package org.cpilint.rules.naming;

public class PascalCaseNamingScheme implements NamingScheme {
    private final String name;
    
    public PascalCaseNamingScheme() {
        this.name = "pascalCase";
    }
    
    @Override
    public boolean test(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        // First character must be uppercase
        if (!Character.isUpperCase(name.charAt(0))) {
            return false;
        }
        
        // Subsequent characters must be alphanumeric or underscore
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
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
