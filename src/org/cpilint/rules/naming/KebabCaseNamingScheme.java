package org.cpilint.rules.naming;

public class KebabCaseNamingScheme implements NamingScheme {
    private final String name;
    
    public KebabCaseNamingScheme() {
        this.name = "kebab-case";
    }
    
    @Override
    public boolean test(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        // All characters must be lowercase letters, numbers, or hyphens
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '-') {
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
