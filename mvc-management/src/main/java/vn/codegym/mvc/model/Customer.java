package vn.codegym.mvc.model;
public final class Customer implements Identified {
 private int id;private String name;
private String email;
private String address;
 public Customer(){} public Customer(String name,String email,String address){this.name=name;this.email=email;this.address=address;}
 public int getId(){return id;} public void setId(int id){this.id=id;}
 public String getName(){return name;}public void setName(String value){this.name=value;}
public String getEmail(){return email;}public void setEmail(String value){this.email=value;}
public String getAddress(){return address;}public void setAddress(String value){this.address=value;}
}
