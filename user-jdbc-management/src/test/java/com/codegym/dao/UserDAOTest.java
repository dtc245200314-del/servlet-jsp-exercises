package com.codegym.dao;
import com.codegym.model.User;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class UserDAOTest {
    private final UserDAO dao=new UserDAO();
    @BeforeEach void requiresDedicatedDatabase(){Assumptions.assumeTrue(System.getenv("USER_DB_URL")!=null,"Set USER_DB_URL for integration tests");}
    private User user(String country){return new User("Test-"+UUID.randomUUID().toString().substring(0,8),UUID.randomUUID()+"@example.com",country);}
    @Test void preparedCrud() throws Exception{User u=user("QA");dao.insertUser(u);assertTrue(u.getId()>0);assertEquals(u.getName(),dao.selectUser(u.getId()).getName());User updated=new User(u.getId(),"Updated",u.getEmail(),"New country");assertTrue(dao.updateUser(updated));assertEquals("Updated",dao.selectUser(u.getId()).getName());assertTrue(dao.deleteUser(u.getId()));assertNull(dao.selectUser(u.getId()));}
    @Test void storedProcedureCrud() throws Exception{User u=user("QA-SP");dao.insertUserStore(u);assertEquals(u.getEmail(),dao.getUserById(u.getId()).getEmail());User updated=new User(u.getId(),"SP update",u.getEmail(),"QA-SP");assertTrue(dao.updateUserStore(updated));assertEquals("SP update",dao.getUserById(u.getId()).getName());assertTrue(dao.deleteUserStore(u.getId()));assertNull(dao.getUserById(u.getId()));}
    @Test void countryFilterAndNameSort() throws Exception{String country="Q-"+UUID.randomUUID().toString().substring(0,8);User a=user(country),b=user(country);dao.insertUser(a);dao.insertUser(b);try{var prepared=dao.selectAllUsers(country,true);var stored=dao.selectAllUsersStore(country,true);assertEquals(2,prepared.size());assertEquals(2,stored.size());assertTrue(prepared.get(0).getName().compareTo(prepared.get(1).getName())<=0);assertEquals(prepared.get(0).getId(),stored.get(0).getId());assertTrue(dao.selectAllUsers("' OR 1=1 --",false).isEmpty());}finally{dao.deleteUser(a.getId());dao.deleteUser(b.getId());}}
    @Test void transactionCommitsUserAndPermissions() throws Exception{User u=user("QA-TX");dao.addUserTransaction(u,new int[]{1,4});try{assertEquals(1,dao.countByEmail(u.getEmail()));assertEquals(2,dao.permissionCount(u.getId()));}finally{dao.deleteUser(u.getId());}}
    @Test void invalidPermissionRollsBackUser() throws Exception{User u=user("QA-ROLLBACK");assertThrows(SQLException.class,()->dao.addUserTransaction(u,new int[]{1,9999}));assertEquals(0,dao.countByEmail(u.getEmail()));assertEquals(0,u.getId());}
    @Test void missingBindingWithoutTransactionLeavesPartialWrites() throws Exception{var e=dao.insertUpdateWithoutTransaction(true);assertTrue(e.isFailed());assertEquals(2,e.getRows());assertEquals(new BigDecimal("10.00"),e.getSalary());}
    @Test void missingBindingWithTransactionRollsBack() throws Exception{var e=dao.insertUpdateUseTransaction(true);assertTrue(e.isFailed());assertEquals(0,e.getRows());assertNull(e.getSalary());}
    @Test void successfulTransactionCommitsAllStatements() throws Exception{var e=dao.insertUpdateUseTransaction(false);assertFalse(e.isFailed());assertEquals(2,e.getRows());assertEquals(new BigDecimal("999.99"),e.getSalary());}
}
