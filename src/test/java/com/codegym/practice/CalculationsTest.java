package com.codegym.practice;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class CalculationsTest {
    @Test void currencyUsesExactDecimalArithmetic(){assertEquals(new BigDecimal("250000.00"),Calculations.currency("25000","10.00"));}
    @Test void discountBoundaries(){assertEquals(0,Calculations.discount(new BigDecimal("200"),BigDecimal.ZERO).signum());assertEquals(new BigDecimal("200.00"),Calculations.discount(new BigDecimal("200"),new BigDecimal("100")));}
    @Test void discountRejectsInvalidPercent(){assertThrows(IllegalArgumentException.class,()->Calculations.discount(BigDecimal.TEN,new BigDecimal("101")));}
    @Test void arithmetic(){assertEquals(new BigDecimal("7"),Calculations.calculate(new BigDecimal("3"),new BigDecimal("4"),"+"));assertEquals(new BigDecimal("-1"),Calculations.calculate(new BigDecimal("3"),new BigDecimal("4"),"-"));assertEquals(new BigDecimal("12"),Calculations.calculate(new BigDecimal("3"),new BigDecimal("4"),"*"));assertEquals(new BigDecimal("0.75"),Calculations.calculate(new BigDecimal("3"),new BigDecimal("4"),"/"));}
    @Test void rejectsZeroDivisionAndUnknownOperator(){assertThrows(IllegalArgumentException.class,()->Calculations.calculate(BigDecimal.ONE,BigDecimal.ZERO,"/"));assertThrows(IllegalArgumentException.class,()->Calculations.calculate(BigDecimal.ONE,BigDecimal.ONE,"?"));}
    @Test void malformedAndOversizedNumbers(){for(String s:new String[]{"NaN","Infinity","","1e1000000","999999999999999999999999999999999"})assertThrows(IllegalArgumentException.class,()->Calculations.number(s));assertThrows(IllegalArgumentException.class,()->Calculations.nonNegative("-1"));}
    @Test void dictionaryNormalizesAndHandlesUnknown(){assertEquals("Xin chào",Calculations.translate(" HELLO "));assertNull(Calculations.translate("unknown"));assertNull(Calculations.translate(null));}
    @Test void htmlEscapesUntrustedInput(){assertEquals("&lt;script&gt;&amp;&quot;&#39;",Html.escape("<script>&\"'"));}
}
