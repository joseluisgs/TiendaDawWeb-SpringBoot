package dev.joseluisgs.walaspringboot.config;

/**
 * Application-wide constants for WalaDaw
 * These constants are used throughout the application for branding and configuration
 */
public class AppConstants {

    /**
     * Application name
     */
    public static final String APP_NAME = "WalaDaw";

    /**
     * Application description/tagline
     */
    public static final String APP_DESCRIPTION = "Tu tienda de confianza";

    /**
     * Application version
     */
    public static final String APP_VERSION = "1.0.0";

    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new IllegalStateException("Constants class");
    }
}
