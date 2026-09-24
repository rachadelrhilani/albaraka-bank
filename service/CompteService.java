package service;

import dao.ClientDAO;
import dao.CompteDAO;
import exception.EntityNotFoundException;
import exception.MontantInvalideException;
import exception.SoldeInsuffisantException;
import model.Compte;
import model.CompteCourant;
import model.CompteEpargne;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CompteService {

    private final CompteDAO compteDAO;
    private final ClientDAO clientDAO;

    public CompteService(CompteDAO compteDAO, ClientDAO clientDAO) {
        this.compteDAO = compteDAO;
        this.clientDAO = clientDAO;
    }

    public Compte creerCompteCourant(String numero, double soldeInitial, Long idClient, double decouvertAutorise) 
            throws SQLException, EntityNotFoundException, MontantInvalideException {
        if (soldeInitial < 0) {
            throw new MontantInvalideException("Le solde initial ne peut pas être négatif.");
        }
        clientDAO.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable avec l'ID : " + idClient));

        CompteCourant cc = new CompteCourant(null, numero, soldeInitial, idClient, decouvertAutorise);
        return compteDAO.save(cc);
    }

    public Compte creerCompteEpargne(String numero, double soldeInitial, Long idClient, double tauxInteret) 
            throws SQLException, EntityNotFoundException, MontantInvalideException {
        if (soldeInitial < 0) {
            throw new MontantInvalideException("Le solde initial ne peut pas être négatif.");
        }
        clientDAO.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable avec l'ID : " + idClient));

        CompteEpargne ce = new CompteEpargne(null, numero, soldeInitial, idClient, tauxInteret);
        return compteDAO.save(ce);
    }

    public void mettreAJourSolde(Long idCompte, double nouveauSolde) 
            throws SQLException, EntityNotFoundException, SoldeInsuffisantException {
        Compte compte = compteDAO.findById(idCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable avec l'ID : " + idCompte));

        if (compte instanceof CompteCourant cc) {
            if (nouveauSolde < -cc.getDecouvertAutorise()) {
                throw new SoldeInsuffisantException("Le solde dépasse le découvert autorisé de " + cc.getDecouvertAutorise() + " €.");
            }
        } else if (nouveauSolde < 0) {
            throw new SoldeInsuffisantException("Un compte épargne ne peut pas être à découvert.");
        }

        compteDAO.updateSolde(idCompte, nouveauSolde);
    }

    public List<Compte> rechercherComptesParClient(Long idClient) throws SQLException {
        return compteDAO.findByClientId(idClient);
    }

    public Optional<Compte> rechercherParNumero(String numero) throws SQLException {
        return compteDAO.findAll().stream()
                .filter(c -> c.getNumero().equalsIgnoreCase(numero))
                .findFirst();
    }

    public Optional<Compte> trouverCompteSoldeMax() throws SQLException {
        return compteDAO.findAll().stream()
                .max(Comparator.comparingDouble(Compte::getSolde));
    }

    public Optional<Compte> trouverCompteSoldeMin() throws SQLException {
        return compteDAO.findAll().stream()
                .min(Comparator.comparingDouble(Compte::getSolde));
    }
}