package vn.codegym.mvc.model;
public final class Product implements Identified {
 private int id;private String name;
private java.math.BigDecimal price;
private String description;
private String manufacturer;
 public Product(){} public Product(String name,java.math.BigDecimal price,String description,String manufacturer){this.name=name;this.price=price;this.description=description;this.manufacturer=manufacturer;}
 public int getId(){return id;} public void setId(int id){this.id=id;}
 public String getName(){return name;}public void setName(String value){this.name=value;}
public java.math.BigDecimal getPrice(){return price;}public void setPrice(java.math.BigDecimal value){this.price=value;}
public String getDescription(){return description;}public void setDescription(String value){this.description=value;}
public String getManufacturer(){return manufacturer;}public void setManufacturer(String value){this.manufacturer=value;}
}
