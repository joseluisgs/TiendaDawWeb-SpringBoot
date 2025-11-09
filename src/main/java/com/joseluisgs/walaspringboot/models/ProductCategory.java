package com.joseluisgs.walaspringboot.models;

public enum ProductCategory {
    SMARTPHONES("Smartphones", "📱"),
    LAPTOPS("Laptops", "💻"),
    AUDIO("Audio", "🎧"),
    GAMING("Gaming", "🎮"),
    ACCESSORIES("Accessories", "⚡");
    
    private final String displayName;
    private final String emoji;
    
    ProductCategory(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getEmoji() {
        return emoji;
    }
    
    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }
    
    /**
     * Método para obtener enum desde String (nombre de display)
     * @param categoria String con el nombre de la categoría
     * @return ProductCategory correspondiente o null si no existe
     */
    public static ProductCategory fromString(String categoria) {
        if (categoria == null) {
            return null;
        }
        for (ProductCategory cat : ProductCategory.values()) {
            if (cat.getDisplayName().equalsIgnoreCase(categoria)) {
                return cat;
            }
        }
        return null;
    }
}
