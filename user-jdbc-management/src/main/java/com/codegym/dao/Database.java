package com.codegym.dao;
import java.sql.*;
public final class Database {
    private Database(){}
    public static Connection open() throws SQLException {
        try{Class.forName("com.mysql.cj.jdbc.Driver");}catch(ClassNotFoundException ex){throw new SQLException("Missing MySQL JDBC driver",ex);}
        String url=System.getenv().getOrDefault("USER_DB_URL","jdbc:mysql://127.0.0.1:3306/codegym_java?characterEncoding=UTF-8");
        String user=System.getenv().getOrDefault("DB_USER","root"), password=System.getenv("DB_PASSWORD");
        if(password==null) throw new SQLNonTransientConnectionException("Configure DB_PASSWORD in the server environment.");
        return DriverManager.getConnection(url,user,password);
    }
}
