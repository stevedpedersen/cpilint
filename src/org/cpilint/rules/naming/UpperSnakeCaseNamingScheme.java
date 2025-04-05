package org.cpilint.rules.naming;

public class UpperSnakeCaseNamingScheme implements NamingScheme {
    private final String name;
    
    public UpperSnakeCaseNamingScheme() {
        this.name = "upper_snake_case";
    }
    
    @Override
    public boolean test(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        // All characters must be uppercase letters, numbers, or underscores
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
            if (Character.isLetter(c) && !Character.isUpperCase(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
