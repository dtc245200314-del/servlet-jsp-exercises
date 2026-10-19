package vn.codegym.finalexam.dao;

import java.sql.*;

public final class Database {
    private Database(){}
    public static Connection open() throws SQLException {
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch(ClassNotFoundException ex){throw new SQLException("Missing MySQL Connector/J",ex);}
        String url=System.getenv().getOrDefault("PRODUCT_DB_URL","jdbc:mysql://127.0.0.1:3306/codegym_final?characterEncoding=UTF-8");
        String user=System.getenv().getOrDefault("DB_USER","root");
        String password=System.getenv("DB_PASSWORD");
        if(password==null)throw new SQLNonTransientConnectionException("Set DB_PASSWORD in the Tomcat process environment.");
        return DriverManager.getConnection(url,user,password);
    }
}
