package com.codegym.dao;
import com.codegym.model.User;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public final class UserDAO implements IUserDAO {
    private static User read(ResultSet rs) throws SQLException{return new User(rs.getInt("id"),rs.getString("name"),rs.getString("email"),rs.getString("country"));}
    private static void bind(PreparedStatement ps,User user) throws SQLException{ps.setString(1,user.getName());ps.setString(2,user.getEmail());ps.setString(3,user.getCountry());}
    private static void insert(Connection c,User user) throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO users(name,email,country) VALUES(?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            bind(ps,user);ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("No generated user ID");user.setId(rs.getInt(1));}
        }
    }
    @Override public void insertUser(User user) throws SQLException{try(Connection c=Database.open()){insert(c,user);}}
    @Override public User selectUser(int id) throws SQLException{
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("SELECT id,name,email,country FROM users WHERE id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?read(rs):null;}
        }
    }
    @Override public List<User> selectAllUsers(String country,boolean sortName) throws SQLException{
        String pattern="%"+country.replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        String sql="SELECT id,name,email,country FROM users WHERE country LIKE ? ESCAPE '!' ORDER BY "+(sortName?"name,id":"id");
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,pattern);try(ResultSet rs=ps.executeQuery()){List<User> users=new ArrayList<>();while(rs.next())users.add(read(rs));return users;}
        }
    }
    @Override public boolean updateUser(User user) throws SQLException{
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("UPDATE users SET name=?,email=?,country=? WHERE id=?")){
            bind(ps,user);ps.setInt(4,user.getId());return ps.executeUpdate()==1;
        }
    }
    @Override public boolean deleteUser(int id) throws SQLException{
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("DELETE FROM users WHERE id=?")){ps.setInt(1,id);return ps.executeUpdate()==1;}
    }
    @Override public User getUserById(int id) throws SQLException{
        try(Connection c=Database.open();CallableStatement cs=c.prepareCall("{CALL get_user_by_id(?)}")){
            cs.setInt(1,id);try(ResultSet rs=cs.executeQuery()){return rs.next()?new User(id,rs.getString("name"),rs.getString("email"),rs.getString("country")):null;}
        }
    }
    @Override public void insertUserStore(User user) throws SQLException{
        try(Connection c=Database.open();CallableStatement cs=c.prepareCall("{CALL insert_user(?,?,?)}")){
            bind(cs,user);cs.executeUpdate();try(Statement ps=c.createStatement();ResultSet rs=ps.executeQuery("SELECT LAST_INSERT_ID()")){rs.next();user.setId(rs.getInt(1));}
        }
    }
    @Override public List<User> selectAllUsersStore(String country,boolean sortName) throws SQLException{
        try(Connection c=Database.open();CallableStatement cs=c.prepareCall("{CALL select_all_users(?,?)}")){
            cs.setString(1,country);cs.setBoolean(2,sortName);try(ResultSet rs=cs.executeQuery()){List<User> users=new ArrayList<>();while(rs.next())users.add(read(rs));return users;}
        }
    }
    @Override public boolean updateUserStore(User user) throws SQLException{
        try(Connection c=Database.open();CallableStatement cs=c.prepareCall("{CALL update_user(?,?,?,?)}")){
            cs.setInt(1,user.getId());cs.setString(2,user.getName());cs.setString(3,user.getEmail());cs.setString(4,user.getCountry());return cs.executeUpdate()==1;
        }
    }
    @Override public boolean deleteUserStore(int id) throws SQLException{
        try(Connection c=Database.open();CallableStatement cs=c.prepareCall("{CALL delete_user(?)}")){cs.setInt(1,id);return cs.executeUpdate()==1;}
    }
    @Override public void addUserTransaction(User user,int[] permissionIds) throws SQLException{
        try(Connection c=Database.open()){
            boolean original=c.getAutoCommit();c.setAutoCommit(false);
            try{
                insert(c,user);
                if(permissionIds!=null)try(PreparedStatement ps=c.prepareStatement("INSERT INTO user_permission(user_id,permission_id) VALUES(?,?)")){
                    for(int permissionId:permissionIds){ps.setInt(1,user.getId());ps.setInt(2,permissionId);ps.addBatch();}ps.executeBatch();
                }
                c.commit();
            }catch(SQLException|RuntimeException ex){try{c.rollback();}catch(SQLException rollback){ex.addSuppressed(rollback);}user.setId(0);throw ex;}
            finally{c.setAutoCommit(original);}
        }
    }
    public int countByEmail(String email) throws SQLException{
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM users WHERE email=?")){ps.setString(1,email);try(ResultSet rs=ps.executeQuery()){rs.next();return rs.getInt(1);}}
    }
    public int permissionCount(int id) throws SQLException{
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM user_permission WHERE user_id=?")){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){rs.next();return rs.getInt(1);}}
    }
    public static final class Experiment {
        private final boolean transaction,failed; private final int rows; private final BigDecimal salary;
        Experiment(boolean transaction,boolean failed,int rows,BigDecimal salary){this.transaction=transaction;this.failed=failed;this.rows=rows;this.salary=salary;}
        public boolean isTransaction(){return transaction;}public boolean isFailed(){return failed;}public int getRows(){return rows;}public BigDecimal getSalary(){return salary;}
    }
    public Experiment insertUpdateWithoutTransaction(boolean fail) throws SQLException{return experiment(false,fail);}
    public Experiment insertUpdateUseTransaction(boolean fail) throws SQLException{return experiment(true,fail);}
    private Experiment experiment(boolean transaction,boolean fail) throws SQLException{
        String suffix=UUID.randomUUID().toString(),quynh="Quynh-"+suffix,ngan="Ngan-"+suffix;boolean failed=false;
        try(Connection c=Database.open()){
            boolean original=c.getAutoCommit();c.setAutoCommit(!transaction);
            try(PreparedStatement ins=c.prepareStatement("INSERT INTO Employee(name,salary,created_Date) VALUES(?,?,?)");PreparedStatement update=c.prepareStatement("UPDATE Employee SET salary=? WHERE name=?")){
                ins.setString(1,quynh);ins.setBigDecimal(2,BigDecimal.TEN);ins.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now()));ins.executeUpdate();
                ins.setString(1,ngan);ins.setBigDecimal(2,new BigDecimal("20"));ins.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now()));ins.executeUpdate();
                if(fail)update.setBigDecimal(2,new BigDecimal("999.99"));else update.setBigDecimal(1,new BigDecimal("999.99"));
                update.setString(2,quynh);update.executeUpdate();if(transaction)c.commit();
            }catch(SQLException ex){if(!fail)throw ex;failed=true;if(transaction)c.rollback();}
            finally{c.setAutoCommit(original);}
        }
        int rows=0;BigDecimal salary=null;
        try(Connection c=Database.open();PreparedStatement ps=c.prepareStatement("SELECT name,salary FROM Employee WHERE name IN(?,?)")){
            ps.setString(1,quynh);ps.setString(2,ngan);try(ResultSet rs=ps.executeQuery()){while(rs.next()){rows++;if(quynh.equals(rs.getString(1)))salary=rs.getBigDecimal(2);}}
        }
        return new Experiment(transaction,failed,rows,salary);
    }
}
