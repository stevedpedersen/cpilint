package org.cpilint.rules.naming;

public class SnakeCaseNamingScheme implements NamingScheme {
    private final String name;
    
    public SnakeCaseNamingScheme() {
        this.name = "snake_case";
    }
    
    @Override
    public boolean test(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        // All characters must be lowercase letters, numbers, or underscores
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
            if (Character.isLetter(c) && !Character.isLowerCase(c)) {
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
