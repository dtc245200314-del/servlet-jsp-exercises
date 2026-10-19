package com.codegym.dao;
import com.codegym.model.User;
import java.sql.SQLException;
import java.util.List;
public interface IUserDAO {
    void insertUser(User user) throws SQLException;
    User selectUser(int id) throws SQLException;
    List<User> selectAllUsers(String country,boolean sortName) throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    User getUserById(int id) throws SQLException;
    void insertUserStore(User user) throws SQLException;
    List<User> selectAllUsersStore(String country,boolean sortName) throws SQLException;
    boolean updateUserStore(User user) throws SQLException;
    boolean deleteUserStore(int id) throws SQLException;
    void addUserTransaction(User user,int[] permissionIds) throws SQLException;
}
