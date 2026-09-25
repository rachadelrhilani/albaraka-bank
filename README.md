# SoluBank / Albaraka Bank

## 📖 Description du Projet
SoluBank (ou Albaraka Bank) est une application bancaire développée en Java permettant de gérer les clients, les comptes bancaires et les transactions associées. L'application est dotée d'une interface console interactive et repose sur une architecture multicouche (DAO, Service, Modèle, UI) garantissant une séparation claire des responsabilités.

## 🗂️ Structure du Projet

```text
com.solubank/
│
├── config/          # Fichiers de configuration (ex: connexion base de données)
├── dao/             # Data Access Objects (communication avec la base de données)
├── exception/       # Classes d'exceptions personnalisées
├── lib/             # Bibliothèques externes (ex: driver JDBC)
├── model/           # Classes modèles ou entités (Client, Compte, Transaction...)
├── service/         # Logique métier (règles et traitements)
├── target/          # Fichiers compilés (.class) générés
├── ui/              # Interface utilisateur (Menus console)
├── util/            # Utilitaires divers (helpers)
└── Main.java        # Point d'entrée principal de l'application
```

## ✅ Checklist des Fonctionnalités

### Gestion des Clients
- [ ] Ajouter un nouveau client
- [ ] Mettre à jour les informations d'un client
- [ ] Supprimer un client
- [ ] Rechercher un client et afficher ses détails

### Gestion des Comptes Bancaires
- [ ] Créer un nouveau compte pour un client
- [ ] Consulter les détails et le solde d'un compte
- [ ] Mettre à jour le statut du compte (Actif, Suspendu, etc.)
- [ ] Supprimer ou clôturer un compte

### Gestion des Transactions
- [ ] Effectuer un dépôt sur un compte
- [ ] Effectuer un retrait d'un compte
- [ ] Effectuer un virement/transfert entre deux comptes
- [ ] Consulter l'historique des transactions d'un compte

### Génération de Rapports
- [ ] Générer le relevé de compte d'un client
- [ ] Afficher des statistiques globales (nombre de clients, transactions, etc.)

## 🚀 Comment Lancer le Projet

### Prérequis
- Java Development Kit (JDK) 8 ou version ultérieure installé sur votre machine.
- Configuration de la base de données prête (si applicable) et drivers JDBC dans le dossier `lib/`.

### Utilisation avec un IDE (Recommandé)
1. Ouvrez le projet dans votre IDE préféré (VS Code, IntelliJ IDEA, Eclipse).
2. Assurez-vous que le dossier `lib` est bien ajouté au *Build Path* (classpath) de votre projet.
3. Exécutez le fichier [`Main.java`](file:///d:/com.solubank/Main.java).

### Compilation et exécution via la ligne de commande (Windows)

1. **Compilation** (depuis le dossier racine du projet) :
```bash
javac -d target -cp "lib/*" Main.java dao/*.java model/*.java service/*.java ui/*.java util/*.java exception/*.java config/*.java
```

2. **Exécution** :
```bash
java -cp "target;lib/*" Main
```
