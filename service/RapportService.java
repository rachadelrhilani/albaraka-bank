package service;

import dao.ClientDAO;
import dao.CompteDAO;
import dao.TransactionDAO;
import model.Client;
import model.Compte;
import model.Transaction;
import model.TypeTransaction;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RapportService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;
    private final TransactionDAO transactionDAO;

    public RapportService(ClientDAO clientDAO, CompteDAO compteDAO, TransactionDAO transactionDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
        this.transactionDAO = transactionDAO;
    }

    /**
     * 1. Générer le top 5 des clients classés par leur solde total (somme de tous leurs comptes)
     */
    public List<Client> genererTop5ClientsParSolde() throws SQLException {
        return clientDAO.findAll().stream()
                .sorted(Comparator.comparingDouble((Client c) -> {
                    try {
                        return compteDAO.findByClientId(c.id()).stream()
                                .mapToDouble(Compte::getSolde)
                                .sum();
                    } catch (SQLException e) {
                        return 0.0;
                    }
                }).reversed())
                .limit(5)
                .toList();
    }

    /**
     * 2. Produire un rapport mensuel : nombre de transactions, moyenne et volume total par TypeTransaction
     */
    public Map<TypeTransaction, DoubleSummaryStatistics> genererRapportMensuel(int annee, int mois) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.date().getYear() == annee && t.date().getMonthValue() == mois)
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        Collectors.summarizingDouble(Transaction::montant)
                ));
    }

    /**
     * 3a. Détecter les transactions suspectes : Montant élevé (> seuil) OU Lieu inhabituel
     */
    public List<Transaction> detecterTransactionsSuspectes(double seuilMontant, String paysHabituel) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.montant() > seuilMontant || !t.lieu().equalsIgnoreCase(paysHabituel))
                .toList();
    }

    /**
     * 3b. Détecter la fréquence excessive : Simulation de plus de 3 opérations en moins de 60 secondes sur le même compte
     */
    public List<Transaction> detecterFrequenceExcessive(Long idCompte) throws SQLException {
        List<Transaction> txs = transactionDAO.findByCompteId(idCompte).stream()
                .sorted(Comparator.comparing(Transaction::date))
                .toList();

        List<Transaction> suspectes = new ArrayList<>();

        for (int i = 0; i < txs.size(); i++) {
            int count = 1;
            for (int j = i + 1; j < txs.size(); j++) {
                long diffSecondes = Duration.between(txs.get(i).date(), txs.get(j).date()).abs().getSeconds();
                if (diffSecondes <= 60) {
                    count++;
                } else {
                    break;
                }
            }
            if (count > 3) { // Seuil de fréquence : plus de 3 opérations par minute
                suspectes.add(txs.get(i));
            }
        }

        return suspectes;
    }

    /**
     * 4. Identifier les comptes inactifs depuis une certaine période (ex: N mois sans aucune transaction)
     */
    public List<Compte> identifierComptesInactifs(int moisInactivite) throws SQLException {
        LocalDateTime dateLimite = LocalDateTime.now().minusMonths(moisInactivite);

        return compteDAO.findAll().stream()
                .filter(compte -> {
                    try {
                        List<Transaction> txs = transactionDAO.findByCompteId(compte.getId());
                        if (txs.isEmpty()) {
                            return true; // Aucun historique de transaction = compte inactif
                        }

                        LocalDateTime dateDerniereTx = txs.stream()
                                .map(Transaction::date)
                                .max(LocalDateTime::compareTo)
                                .orElse(LocalDateTime.MIN);

                        return dateDerniereTx.isBefore(dateLimite);
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .toList();
    }
}