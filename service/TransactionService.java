package service;

import dao.CompteDAO;
import dao.TransactionDAO;
import exception.EntityNotFoundException;
import exception.MontantInvalideException;
import exception.SoldeInsuffisantException;
import model.Compte;
import model.CompteCourant;
import model.Transaction;
import model.TypeTransaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final CompteDAO compteDAO;

    public TransactionService(TransactionDAO transactionDAO, CompteDAO compteDAO) {
        this.transactionDAO = transactionDAO;
        this.compteDAO = compteDAO;
    }

    public Transaction enregistrerTransaction(double montant, TypeTransaction type, String lieu, Long idCompte) 
            throws SQLException, EntityNotFoundException, MontantInvalideException, SoldeInsuffisantException {
        
        if (montant <= 0) {
            throw new MontantInvalideException("Le montant de la transaction doit être supérieur à zéro.");
        }

        Compte compte = compteDAO.findById(idCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable avec l'ID : " + idCompte));

        double soldeActuel = compte.getSolde();
        double nouveauSolde = soldeActuel;

        if (type == TypeTransaction.RETRAIT) {
            double limite = (compte instanceof CompteCourant cc) ? -cc.getDecouvertAutorise() : 0.0;
            if (soldeActuel - montant < limite) {
                throw new SoldeInsuffisantException("Solde insuffisant pour réaliser ce retrait.");
            }
            nouveauSolde -= montant;
        } else if (type == TypeTransaction.VERSEMENT) {
            nouveauSolde += montant;
        }

        compteDAO.updateSolde(idCompte, nouveauSolde);
        Transaction transaction = new Transaction(null, LocalDateTime.now(), montant, type, lieu, idCompte);
        return transactionDAO.save(transaction);
    }

    public List<Transaction> listerParCompte(Long idCompte) throws SQLException {
        return transactionDAO.findByCompteId(idCompte).stream()
                .sorted((t1, t2) -> t2.date().compareTo(t1.date()))
                .toList();
    }

    public List<Transaction> listerParClient(Long idClient) throws SQLException {
        List<Long> comptesIds = compteDAO.findByClientId(idClient).stream()
                .map(Compte::getId)
                .toList();

        return transactionDAO.findAll().stream()
                .filter(t -> comptesIds.contains(t.idCompte()))
                .sorted((t1, t2) -> t2.date().compareTo(t1.date()))
                .toList();
    }

    public List<Transaction> filtrerParMontantMin(double montantMin) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.montant() >= montantMin)
                .toList();
    }

    public List<Transaction> filtrerParType(TypeTransaction type) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.type() == type)
                .toList();
    }

    public List<Transaction> filtrerParDate(LocalDate date) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.date().toLocalDate().equals(date))
                .toList();
    }

    public List<Transaction> filtrerParLieu(String lieu) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.lieu().equalsIgnoreCase(lieu))
                .toList();
    }

    public Map<TypeTransaction, List<Transaction>> regrouperParType() throws SQLException {
        return transactionDAO.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::type));
    }

    public Map<String, List<Transaction>> regrouperParPeriode() throws SQLException {
        return transactionDAO.findAll().stream()
                .collect(Collectors.groupingBy(t -> t.date().getYear() + "-" + String.format("%02d", t.date().getMonthValue())));
    }

    public double calculerMoyenneParCompte(Long idCompte) throws SQLException {
        return transactionDAO.findByCompteId(idCompte).stream()
                .mapToDouble(Transaction::montant)
                .average()
                .orElse(0.0);
    }

    public double calculerTotalParCompte(Long idCompte) throws SQLException {
        return transactionDAO.findByCompteId(idCompte).stream()
                .mapToDouble(Transaction::montant)
                .sum();
    }

    public List<Transaction> detecterSuspectesBasique(double seuilMontant) throws SQLException {
        return transactionDAO.findAll().stream()
                .filter(t -> t.montant() > seuilMontant)
                .toList();
    }
}