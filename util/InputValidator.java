package util;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private InputValidator() {}

    public static int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Erreur : Veuillez saisir un nombre entier valide.");
            }
        }
    }

    public static long lireLong(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Erreur : Veuillez saisir un identifiant numérique valide.");
            }
        }
    }

    public static double lireDoublePositif(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().replace(",", ".");
            try {
                double val = Double.parseDouble(input);
                if (val >= 0) {
                    return val;
                }
                System.out.println("❌ Erreur : Le montant doit être supérieur ou égal à zéro.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Erreur : Veuillez saisir un montant numérique valide.");
            }
        }
    }

    public static String lireTexteNonVide(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("❌ Erreur : Ce champ ne peut pas être vide.");
        }
    }

    public static String lireEmailValide(String message) {
        while (true) {
            String email = lireTexteNonVide(message);
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }
            System.out.println("❌ Erreur : Format d'adresse email invalide (ex: exemple@banque.ma).");
        }
    }

    public static LocalDate lireDate(String message) {
        while (true) {
            String dateStr = lireTexteNonVide(message);
            LocalDate date = DateUtils.parseLocalDate(dateStr);
            if (date != null) {
                return date;
            }
            System.out.println("❌ Erreur : Format de date invalide. Utilisez le format JJ/MM/AAAA.");
        }
    }
}