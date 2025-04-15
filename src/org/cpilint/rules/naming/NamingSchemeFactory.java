package org.cpilint.rules.naming;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NamingSchemeFactory {
    private static final Map<String, Supplier<NamingScheme>> SCHEMES = new HashMap<>();
    
    static {
        // Register known naming schemes
        SCHEMES.put("camelCase", CamelCaseNamingScheme::new);
        SCHEMES.put("snake_case", SnakeCaseNamingScheme::new);
        SCHEMES.put("kebab-case", KebabCaseNamingScheme::new);
        SCHEMES.put("pascalCase", PascalCaseNamingScheme::new);
        SCHEMES.put("upper_snake_case", UpperSnakeCaseNamingScheme::new);
    }
    
    public static NamingScheme create(String schemeName) {
        Supplier<NamingScheme> supplier = SCHEMES.get(schemeName.toLowerCase());
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown naming scheme: " + schemeName);
        }
        return supplier.get();
    }
    
    public static String[] getAvailableSchemes() {
        return SCHEMES.keySet().toArray(new String[0]);
    }
}
