package util;

public final class Validation {
    private Validation() { }
    public static int number(String input, int min, int max) throws InvalidChoiceException {
        try {
            int value = Integer.parseInt(input);
            if (value >= min && value <= max) return value;
        } catch (NumberFormatException ignored) { }
        throw new InvalidChoiceException("Enter a whole number from " + min + " to " + max + ".");
    }
    public static char answer(String input) throws InvalidChoiceException {
        if (input != null && input.matches("(?i)[ABCD]")) return Character.toUpperCase(input.charAt(0));
        throw new InvalidChoiceException("Enter exactly A, B, C or D. Try again.");
    }
}
