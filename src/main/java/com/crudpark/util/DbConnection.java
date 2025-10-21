// util/DbConnection.java - MEJORADO
package com.crudpark.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {
    private static final Properties PROPS = new Properties();
    private static boolean initialized = false;

    static {
        try (InputStream input = DbConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("❌ ERROR: db.properties file not found in classpath");
            }
            PROPS.load(input);
            
            // Validar que existan las propiedades necesarias
            if (PROPS.getProperty("db.url") == null || 
                PROPS.getProperty("db.username") == null || 
                PROPS.getProperty("db.password") == null) {
                throw new RuntimeException("❌ ERROR: db.properties is missing required properties (db.url, db.username, db.password)");
            }
            
            // Load JDBC driver
            String driver = PROPS.getProperty("db.driver", "org.postgresql.Driver");
            Class.forName(driver);
            
            initialized = true;
            System.out.println("✅ Database configuration loaded successfully");
            System.out.println("📍 URL: " + PROPS.getProperty("db.url"));
            
        } catch (ClassNotFoundException ex) {
            System.err.println("❌ ERROR: PostgreSQL JDBC Driver not found!");
            System.err.println("Make sure postgresql dependency is in your pom.xml");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        } catch (Exception ex) {
            System.err.println("❌ ERROR: Error loading database configuration: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException("Database connection not properly initialized");
        }
        
        try {
            Connection conn = DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.username"),
                PROPS.getProperty("db.password")
            );
            
            // Configurar la conexión para PostgreSQL ENUMs
            // Esto ayuda con el manejo de ENUMs
            conn.setAutoCommit(true);
            
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ ERROR: Cannot connect to database");
            System.err.println("URL: " + PROPS.getProperty("db.url"));
            System.err.println("Username: " + PROPS.getProperty("db.username"));
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    public static void testConnection() {
        System.out.println("\n🔍 Testing database connection...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection successful!");
                System.out.println("📊 Database: " + conn.getCatalog());
                System.out.println("🔗 URL: " + conn.getMetaData().getURL());
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection FAILED!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método útil para debugging
    public static void printConnectionInfo() {
        System.out.println("\n📋 Database Configuration:");
        System.out.println("  URL: " + PROPS.getProperty("db.url"));
        System.out.println("  Username: " + PROPS.getProperty("db.username"));
        System.out.println("  Driver: " + PROPS.getProperty("db.driver"));
        System.out.println("  Initialized: " + initialized);
    }
}