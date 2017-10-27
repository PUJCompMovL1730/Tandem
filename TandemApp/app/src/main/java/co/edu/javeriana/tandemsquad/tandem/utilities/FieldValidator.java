package co.edu.javeriana.tandemsquad.tandem.utilities;

public class FieldValidator {
    public static boolean validateEmail(String email) {
        return email.matches("[a-zA-Z0-9_.-]{1,50}@[a-zA-Z0-9_-]{1,20}.[a-zA-Z0-9_-]{1,10}[.]{0,1}[a-zA-Z0-9_-]{0,10}");
    }

    public static boolean validatePassword(String password) {
        return password.matches("[a-zA-Z0-9_-]{6,20}");
    }

    public static boolean validateText(String text) {
        return text.matches("[a-zA-Z0-9-_ áéíóúÁÉÍÓÚ]{1,50}");
    }

    public static boolean validateUsername(String username) {
        return username.matches("[a-zA-Z0-9_.-]{1,20}");
    }

    public static boolean validatePhone(String phone) {
        return phone.matches("[0-9]{10}");
    }

    public static boolean confirmatePasswords(String password, String confirmation) {
        return password.equals(confirmation);
    }
}
