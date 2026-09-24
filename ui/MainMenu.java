package ui;

import exception.EntityNotFoundException;
import exception.MontantInvalideException;
import exception.SoldeInsuffisantException;
import model.*;
import service.*;
import util.DateUtils;
import util.InputValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

public class MainMenu {

    private final ClientService clientService;
    private final CompteService compteService;
    private final TransactionService transactionService;
    private final RapportService rapportService;

    public MainMenu(ClientService clientService, CompteService compteService, 
                    TransactionService transactionService, RapportService rapportService) {
        this.clientService = clientService;
        this.compteService = compteService;
        this.transactionService = transactionService;
        this.rapportService = rapportService;
    }

    public void demarrer() {
        boolean quitter = false;
        while (!quitter) {
            afficherMenuPrincipal();
            int choix = InputValidator.lireEntier("Saisissez votre choix : ");
            System.out.println();

            switch (choix) {
                case 1 -> menuGestionClients();
                case 2 -> menuGestionComptes();
                case 3 -> menuGestionTransactions();
                case 4 -> menuAnalyseEtRapports();
                case 0 -> {
                    System.out.println("👋 Merci d'avoir utilisé l'application Banque Al Baraka. Au revoir !");
                    quitter = true;
                }
                default -> System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
            System.out.println();
        }
    }

    private void afficherMenuPrincipal() {
        System.out.println("==================================================");
        System.out.println("        BANQUE AL BARAKA - MENU PRINCIPAL         ");
        System.out.println("==================================================");
        System.out.println("1. Gestion des Clients");
        System.out.println("2. Gestion des Comptes");
        System.out.println("3. Opérations & Transactions");
        System.out.println("4. Analyses, Alertes & Rapports");
        System.out.println("0. Quitter l'application");
        System.out.println("==================================================");
    }

    // ==========================================
    // 1. MENU GESTION CLIENTS
    // ==========================================
    private void menuGestionClients() {
        System.out.println("--- GESTION DES CLIENTS ---");
        System.out.println("1. Ajouter un client");
        System.out.println("2. Modifier un client");
        System.out.println("3. Supprimer un client");
        System.out.println("4. Rechercher un client par ID");
        System.out.println("5. Rechercher des clients par Nom");
        System.out.println("6. Lister tous les clients");
        int choix = InputValidator.lireEntier("Choix : ");

        try {
            switch (choix) {
                case 1 -> {
                    String nom = InputValidator.lireTexteNonVide("Nom du client : ");
                    String email = InputValidator.lireEmailValide("Email du client : ");
                    Client nouveau = clientService.ajouterClient(nom, email);
                    System.out.println("✅ Client créé avec succès. ID généré : " + nouveau.id());
                }
                case 2 -> {
                    long id = InputValidator.lireLong("ID du client à modifier : ");
                    String nom = InputValidator.lireTexteNonVide("Nouveau nom : ");
                    String email = InputValidator.lireEmailValide("Nouveau email : ");
                    clientService.modifierClient(id, nom, email);
                    System.out.println("✅ Client mis à jour avec succès.");
                }
                case 3 -> {
                    long id = InputValidator.lireLong("ID du client à supprimer : ");
                    clientService.supprimerClient(id);
                    System.out.println("✅ Client supprimé avec succès.");
                }
                case 4 -> {
                    long id = InputValidator.lireLong("ID du client : ");
                    Client c = clientService.rechercherParId(id);
                    System.out.println("👤 ID: " + c.id() + " | Nom: " + c.nom() + " | Email: " + c.email());
                }
                case 5 -> {
                    String nom = InputValidator.lireTexteNonVide("Nom à rechercher : ");
                    List<Client> resultats = clientService.rechercherParNom(nom);
                    if (resultats.isEmpty()) System.out.println("Aucun client trouvé.");
                    else resultats.forEach(c -> System.out.println("👤 ID: " + c.id() + " | Nom: " + c.nom() + " | Email: " + c.email()));
                }
                case 6 -> {
                    List<Client> clients = clientService.listerTous();
                    System.out.println("\n--- Liste des Clients (" + clients.size() + ") ---");
                    clients.forEach(c -> System.out.println("👤 ID: " + c.id() + " | Nom: " + c.nom() + " | Email: " + c.email()));
                }
                default -> System.out.println("❌ Option invalide.");
            }
        } catch (SQLException | EntityNotFoundException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }

    // ==========================================
    // 2. MENU GESTION COMPTES
    // ==========================================
    private void menuGestionComptes() {
        System.out.println("--- GESTION DES COMPTES ---");
        System.out.println("1. Créer un Compte Courant");
        System.out.println("2. Créer un Compte Épargne");
        System.out.println("3. Lister les comptes d'un client");
        System.out.println("4. Rechercher un compte par numéro");
        System.out.println("5. Afficher le compte au solde MAXIMUM");
        System.out.println("6. Afficher le compte au solde MINIMUM");
        int choix = InputValidator.lireEntier("Choix : ");

        try {
            switch (choix) {
                case 1 -> {
                    long idClient = InputValidator.lireLong("ID du client propriétaire : ");
                    String num = InputValidator.lireTexteNonVide("Numéro de compte (ex: CC-1001) : ");
                    double solde = InputValidator.lireDoublePositif("Solde initial : ");
                    double decouvert = InputValidator.lireDoublePositif("Découvert autorisé : ");
                    Compte c = compteService.creerCompteCourant(num, solde, idClient, decouvert);
                    System.out.println("✅ Compte Courant créé avec succès. ID : " + c.getId());
                }
                case 2 -> {
                    long idClient = InputValidator.lireLong("ID du client propriétaire : ");
                    String num = InputValidator.lireTexteNonVide("Numéro de compte (ex: CE-2001) : ");
                    double solde = InputValidator.lireDoublePositif("Solde initial : ");
                    double taux = InputValidator.lireDoublePositif("Taux d'intérêt (%) : ");
                    Compte c = compteService.creerCompteEpargne(num, solde, idClient, taux);
                    System.out.println("✅ Compte Épargne créé avec succès. ID : " + c.getId());
                }
                case 3 -> {
                    long idClient = InputValidator.lireLong("ID du client : ");
                    List<Compte> comptes = compteService.rechercherComptesParClient(idClient);
                    afficherListeComptes(comptes);
                }
                case 4 -> {
                    String num = InputValidator.lireTexteNonVide("Numéro de compte : ");
                    compteService.rechercherParNumero(num)
                            .ifPresentOrElse(
                                    this::afficherCompte,
                                    () -> System.out.println("Aucun compte trouvé avec ce numéro.")
                            );
                }
                case 5 -> compteService.trouverCompteSoldeMax()
                        .ifPresentOrElse(this::afficherCompte, () -> System.out.println("Aucun compte en base."));
                case 6 -> compteService.trouverCompteSoldeMin()
                        .ifPresentOrElse(this::afficherCompte, () -> System.out.println("Aucun compte en base."));
                default -> System.out.println("❌ Option invalide.");
            }
        } catch (SQLException | EntityNotFoundException | MontantInvalideException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }

    // ==========================================
    // 3. MENU GESTION TRANSACTIONS
    // ==========================================
    private void menuGestionTransactions() {
        System.out.println("--- OPÉRATIONS & TRANSACTIONS ---");
        System.out.println("1. Enregistrer une transaction (Versement, Retrait, Virement)");
        System.out.println("2. Consulter l'historique d'un compte");
        System.out.println("3. Filtrer les transactions d'un compte");
        int choix = InputValidator.lireEntier("Choix : ");

        try {
            switch (choix) {
                case 1 -> {
                    long idCompte = InputValidator.lireLong("ID du compte : ");
                    System.out.println("Type : 1. VERSEMENT | 2. RETRAIT | 3. VIREMENT");
                    int typeInt = InputValidator.lireEntier("Choix type : ");
                    TypeTransaction type = switch (typeInt) {
                        case 1 -> TypeTransaction.VERSEMENT;
                        case 2 -> TypeTransaction.RETRAIT;
                        case 3 -> TypeTransaction.VIREMENT;
                        default -> throw new IllegalArgumentException("Type de transaction invalide.");
                    };
                    double montant = InputValidator.lireDoublePositif("Montant : ");
                    String lieu = InputValidator.lireTexteNonVide("Lieu/Pays : ");

                    Transaction t = transactionService.enregistrerTransaction(montant, type, lieu, idCompte);
                    System.out.println("✅ Transaction effectuée à " + DateUtils.formatLocalDateTime(t.date()));
                }
                case 2 -> {
                    long idCompte = InputValidator.lireLong("ID du compte : ");
                    List<Transaction> txs = transactionService.listerParCompte(idCompte);
                    afficherListeTransactions(txs);
                }
                case 3 -> {
                    System.out.println("Filtrer par : 1. Type | 2. Montant Min | 3. Date");
                    int subChoice = InputValidator.lireEntier("Choix : ");
                    if (subChoice == 1) {
                        System.out.println("1. VERSEMENT | 2. RETRAIT | 3. VIREMENT");
                        int tInt = InputValidator.lireEntier("Type : ");
                        TypeTransaction type = (tInt == 1) ? TypeTransaction.VERSEMENT : (tInt == 2) ? TypeTransaction.RETRAIT : TypeTransaction.VIREMENT;
                        afficherListeTransactions(transactionService.filtrerParType(type));
                    } else if (subChoice == 2) {
                        double min = InputValidator.lireDoublePositif("Montant minimal : ");
                        afficherListeTransactions(transactionService.filtrerParMontantMin(min));
                    } else if (subChoice == 3) {
                        LocalDate date = InputValidator.lireDate("Date (JJ/MM/AAAA) : ");
                        afficherListeTransactions(transactionService.filtrerParDate(date));
                    }
                }
                default -> System.out.println("❌ Option invalide.");
            }
        } catch (SQLException | EntityNotFoundException | MontantInvalideException | SoldeInsuffisantException | IllegalArgumentException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }

    // ==========================================
    // 4. MENU ANALYSES ET RAPPORTS
    // ==========================================
    private void menuAnalyseEtRapports() {
        System.out.println("--- ANALYSES, ALERTES & RAPPORTS ---");
        System.out.println("1. Top 5 des clients par solde");
        System.out.println("2. Rapport mensuel (Volumes & Stats par Type)");
        System.out.println("3. Détecter les transactions suspectes (Montant & Lieu)");
        System.out.println("4. Alerte fréquence excessive (> 3 tx / min)");
        System.out.println("5. Identifier les comptes inactifs");
        int choix = InputValidator.lireEntier("Choix : ");

        try {
            switch (choix) {
                case 1 -> {
                    System.out.println("\n🏆 --- TOP 5 CLIENTS ---");
                    List<Client> top5 = rapportService.genererTop5ClientsParSolde();
                    top5.forEach(c -> {
                        try {
                            double total = clientService.calculerSoldeTotalClient(c.id());
                            System.out.println("🥇 " + c.nom() + " (ID: " + c.id() + ") | Solde cumulé: " + total + " €");
                        } catch (Exception ignored) {}
                    });
                }
                case 2 -> {
                    int annee = InputValidator.lireEntier("Année (ex: 2026) : ");
                    int mois = InputValidator.lireEntier("Mois (1-12) : ");
                    Map<TypeTransaction, DoubleSummaryStatistics> stats = rapportService.genererRapportMensuel(annee, mois);
                    
                    System.out.println("\n📊 --- RAPPORT MENSUEL " + mois + "/" + annee + " ---");
                    stats.forEach((type, stat) -> {
                        System.out.println("🔹 Type: " + type);
                        System.out.println("   - Nombre d'opérations: " + stat.getCount());
                        System.out.println("   - Volume total: " + stat.getSum() + " €");
                        System.out.println("   - Moyenne: " + String.format("%.2f", stat.getAverage()) + " €");
                    });
                }
                case 3 -> {
                    double seuil = InputValidator.lireDoublePositif("Seuil de montant suspect (ex: 10000) : ");
                    String pays = InputValidator.lireTexteNonVide("Pays habituel du client : ");
                    List<Transaction> suspectes = rapportService.detecterTransactionsSuspectes(seuil, pays);
                    System.out.println("\n⚠️ --- TRANSACTIONS SUSPECTES DÉTECTÉES (" + suspectes.size() + ") ---");
                    afficherListeTransactions(suspectes);
                }
                case 4 -> {
                    long idCompte = InputValidator.lireLong("ID du compte à analyser : ");
                    List<Transaction> suspectes = rapportService.detecterFrequenceExcessive(idCompte);
                    System.out.println("\n🚨 --- ALERTE FREQUENCE EXCESSIVE (" + suspectes.size() + ") ---");
                    afficherListeTransactions(suspectes);
                }
                case 5 -> {
                    int mois = InputValidator.lireEntier("Mois d'inactivité (ex: 6) : ");
                    List<Compte> inactifs = rapportService.identifierComptesInactifs(mois);
                    System.out.println("\n💤 --- COMPTES INACTIFS DÉTECTÉS (" + inactifs.size() + ") ---");
                    afficherListeComptes(inactifs);
                }
                default -> System.out.println("❌ Option invalide.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
    }

    // Affichage des objets
    private void afficherCompte(Compte c) {
        String typeStr = (c instanceof CompteCourant cc) 
                ? "COURANT (Découvert: " + cc.getDecouvertAutorise() + " €)"
                : "ÉPARGNE (Taux: " + ((CompteEpargne) c).getTauxInteret() + " %)";
        System.out.println("💳 ID: " + c.getId() + " | N°: " + c.getNumero() + " | Solde: " + c.getSolde() + " € | Type: " + typeStr + " | Client ID: " + c.getIdClient());
    }

    private void afficherListeComptes(List<Compte> comptes) {
        if (comptes.isEmpty()) System.out.println("Aucun compte trouvé.");
        else comptes.forEach(this::afficherCompte);
    }

    private void afficherListeTransactions(List<Transaction> txs) {
        if (txs.isEmpty()) System.out.println("Aucune transaction trouvée.");
        else txs.forEach(t -> System.out.println("💸 ID: " + t.id() + " | Date: " + DateUtils.formatLocalDateTime(t.date()) + 
                " | Type: " + t.type() + " | Montant: " + t.montant() + " € | Lieu: " + t.lieu() + " | Compte ID: " + t.idCompte()));
    }
}