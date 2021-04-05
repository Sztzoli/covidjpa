package covid;

public class DataValidator {

    public static final int TAJ_LAST_INDEX = 8;

    private String emailFirst;

    public boolean validateName(String name) {
        return validateString(name);
    }

    public boolean validateZip(String zip) {
        return validateString(zip);
    }

    public boolean validateAge(int age) {
        return (age > 10 && age < 150);
    }

    public boolean validateEmail(String email) {
        if (email.length() >= 3 && email.contains("@")) {
            this.emailFirst = email;
            return true;
        }
        return false;
    }

    public boolean validateSecondEmail(String secondEmail) {
        return (emailFirst.equals(secondEmail));
    }

    public boolean validateTaj(String taj) {
        return (validateTajNumber(taj) && tajCDV(taj));
    }


    private boolean tajCDV(String taj) {
        int result = 0;
        int lastDigit = taj.charAt(TAJ_LAST_INDEX) - '0';
        for (int i = 0; i < TAJ_LAST_INDEX; i++) {
            int digit = taj.charAt(i) - '0';
            if (i % 2 == 0) {
                result += (digit * 3);
            } else {
                result += (digit * 7);
            }
        }
        return (result % 10 == lastDigit);
    }

    private boolean validateTajNumber(String taj) {
        return (taj.length() == 9 && taj.matches("[0-9]+"));
    }

    private boolean validateString(String text) {
        return text != null && !text.isBlank();
    }
}
