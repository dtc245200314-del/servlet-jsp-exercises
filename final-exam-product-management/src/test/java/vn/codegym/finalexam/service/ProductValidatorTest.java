package vn.codegym.finalexam.service;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class ProductValidatorTest {
    @Test void parsesAndTrimsValidInput(){var p=ProductValidator.parse(0,"  Bàn phím  ","850000.50","12");assertEquals("Bàn phím",p.getName());assertEquals(new BigDecimal("850000.50"),p.getPrice());assertEquals(12,p.getQuantity());}
    @Test void allowsZeroAndUpperBound(){assertEquals(0,ProductValidator.parse(0,"Zero","0","0").getQuantity());assertEquals(Integer.MAX_VALUE,ProductValidator.parse(0,"Max","9999999999999.99","2147483647").getQuantity());}
    @Test void rejectsEmptyAndLongNames(){for(String s:new String[]{"","   ","x".repeat(256)})assertThrows(IllegalArgumentException.class,()->ProductValidator.parse(0,s,"1","1"));}
    @Test void rejectsInvalidPrices(){for(String s:new String[]{"-1","NaN","Infinity","1.999","1e9","10000000000000",""})assertThrows(IllegalArgumentException.class,()->ProductValidator.parse(0,"Test",s,"1"));}
    @Test void rejectsInvalidQuantities(){for(String s:new String[]{"-1","1.5","2147483648","", "abc"})assertThrows(IllegalArgumentException.class,()->ProductValidator.parse(0,"Test","1",s));}
}
