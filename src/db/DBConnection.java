package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    // Private constructor (singleton pattern)
    private DBConnection() throws ClassNotFoundException, SQLException {
        // Load MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Connect to your MySQL database
        this.connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/InventoryManagement", // database name
                "root",  // username
                "Ema73#35"   // password
        );
    }

    // Singleton instance
    public static DBConnection getInstance() throws SQLException, ClassNotFoundException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    // Return the connection
    public Connection getConnection() {
        return this.connection;
    }
}
