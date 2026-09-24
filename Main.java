import dao.ClientDAO;
import dao.CompteDAO;
import dao.TransactionDAO;
import service.ClientService;
import service.CompteService;
import service.RapportService;
import service.TransactionService;
import ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        // Initialisation des DAOs
        ClientDAO clientDAO = new ClientDAO();
        CompteDAO compteDAO = new CompteDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        // Initialisation des Services
        ClientService clientService = new ClientService(clientDAO, compteDAO);
        CompteService compteService = new CompteService(compteDAO, clientDAO);
        TransactionService transactionService = new TransactionService(transactionDAO, compteDAO);
        RapportService rapportService = new RapportService(clientDAO, compteDAO, transactionDAO);

        // Lancement de l'UI
        MainMenu menu = new MainMenu(clientService, compteService, transactionService, rapportService);
        menu.demarrer();
    }
}