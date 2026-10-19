package vn.codegym.finalexam.dao;
import org.junit.jupiter.api.*;
import vn.codegym.finalexam.model.Product;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class ProductDAOTest {
    private final ProductDAO dao=new ProductDAO();
    @BeforeEach void requireDedicatedDatabase(){Assumptions.assumeTrue("true".equals(System.getenv("RUN_DB_TESTS"))&&System.getenv("PRODUCT_DB_URL")!=null,"Set RUN_DB_TESTS=true and PRODUCT_DB_URL for MySQL integration tests");}
    private Product sample(){return new Product(0,"QA-"+UUID.randomUUID(),new BigDecimal("123.45"),7);}
    @Test void createsAndReadsDecimalAndUnicode()throws Exception{Product p=dao.create(new Product(0,"Sản phẩm kiểm thử "+UUID.randomUUID(),new BigDecimal("123.45"),7));try{assertTrue(p.getId()>0);Product found=dao.findById(p.getId());assertEquals(p.getName(),found.getName());assertEquals(p.getPrice(),found.getPrice());assertEquals(7,found.getQuantity());}finally{dao.delete(p.getId());}}
    @Test void updatesAndPrefillsValues()throws Exception{Product p=dao.create(sample());try{assertTrue(dao.update(new Product(p.getId(),"Updated",new BigDecimal("45.67"),0)));var found=dao.findById(p.getId());assertEquals("Updated",found.getName());assertEquals(new BigDecimal("45.67"),found.getPrice());assertEquals(0,found.getQuantity());}finally{dao.delete(p.getId());}}
    @Test void deletesAndReportsMissingRow()throws Exception{Product p=dao.create(sample());assertTrue(dao.delete(p.getId()));assertNull(dao.findById(p.getId()));assertFalse(dao.delete(p.getId()));}
    @Test void listContainsCreatedProduct()throws Exception{Product p=dao.create(sample());try{assertTrue(dao.findAll().stream().anyMatch(v->v.getId()==p.getId()));}finally{dao.delete(p.getId());}}
    @Test void boundNameIsStoredAsLiteral()throws Exception{Product p=dao.create(new Product(0,"x'); DROP TABLE products; --",BigDecimal.ONE.setScale(2),1));try{assertEquals("x'); DROP TABLE products; --",dao.findById(p.getId()).getName());assertFalse(dao.findAll().isEmpty());}finally{dao.delete(p.getId());}}
}
