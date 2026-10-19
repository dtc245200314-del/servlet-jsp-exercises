package vn.codegym.finalexam.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.codegym.finalexam.model.Product;

public final class ProductDAO {
    private static Product read(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("id"),rs.getString("name"),rs.getBigDecimal("price"),rs.getInt("quantity"));
    }
    private static void bind(PreparedStatement ps,Product p) throws SQLException {
        ps.setString(1,p.getName());ps.setBigDecimal(2,p.getPrice());ps.setInt(3,p.getQuantity());
    }
    public List<Product> findAll() throws SQLException {
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("SELECT id,name,price,quantity FROM products ORDER BY id");ResultSet rs=ps.executeQuery()){
            List<Product> products=new ArrayList<>();while(rs.next())products.add(read(rs));return products;
        }
    }
    public Product findById(int id) throws SQLException {
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("SELECT id,name,price,quantity FROM products WHERE id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?read(rs):null;}
        }
    }
    public Product create(Product p) throws SQLException {
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("INSERT INTO products(name,price,quantity) VALUES(?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            bind(ps,p);ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){
                if(!rs.next())throw new SQLException("No generated product ID");
                return new Product(rs.getInt(1),p.getName(),p.getPrice(),p.getQuantity());
            }
        }
    }
    public boolean update(Product p) throws SQLException {
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("UPDATE products SET name=?,price=?,quantity=? WHERE id=?")){
            bind(ps,p);ps.setInt(4,p.getId());return ps.executeUpdate()==1;
        }
    }
    public boolean delete(int id) throws SQLException {
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("DELETE FROM products WHERE id=?")){
            ps.setInt(1,id);return ps.executeUpdate()==1;
        }
    }
}
