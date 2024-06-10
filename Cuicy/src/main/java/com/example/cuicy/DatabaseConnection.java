package com.example.cuicy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {
    public Connection databaselink;
    public Connection getConnection() {
        String databaseUsername = "root";
        String databasePassword = "trinhvosinh123";
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaselink = DriverManager.getConnection(url, databaseUsername, databasePassword);

        } catch(Exception e){
            e.printStackTrace();
        }
        return databaselink;
    }

}
