package service;

import dao.ClientDAO;
import dao.CompteDAO;
import exception.EntityNotFoundException;
import model.Client;
import model.Compte;

import java.sql.SQLException;
import java.util.List;

public class ClientService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;

    public ClientService(ClientDAO clientDAO, CompteDAO compteDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
    }

    public Client ajouterClient(String nom, String email) throws SQLException {
        Client client = new Client(null, nom, email);
        return clientDAO.save(client);
    }

    public void modifierClient(Long id, String nom, String email) throws SQLException, EntityNotFoundException {
        clientDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable avec l'ID : " + id));
        clientDAO.update(new Client(id, nom, email));
    }

    public void supprimerClient(Long id) throws SQLException, EntityNotFoundException {
        clientDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable avec l'ID : " + id));
        clientDAO.delete(id);
    }

    public Client rechercherParId(Long id) throws SQLException, EntityNotFoundException {
        return clientDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable avec l'ID : " + id));
    }

    public List<Client> rechercherParNom(String nom) throws SQLException {
        return clientDAO.findAll().stream()
                .filter(c -> c.nom().toLowerCase().contains(nom.toLowerCase()))
                .toList();
    }

    public List<Client> listerTous() throws SQLException {
        return clientDAO.findAll();
    }

    public double calculerSoldeTotalClient(Long idClient) throws SQLException, EntityNotFoundException {
        rechercherParId(idClient);
        return compteDAO.findByClientId(idClient).stream()
                .mapToDouble(Compte::getSolde)
                .sum();
    }

    public long compterComptesClient(Long idClient) throws SQLException, EntityNotFoundException {
        rechercherParId(idClient);
        return compteDAO.findByClientId(idClient).size();
    }
}