package com.codegym.practice;

public final class Customer {
    private final String name,birthday,address,image;
    public Customer(String name,String birthday,String address,String image){this.name=name;this.birthday=birthday;this.address=address;this.image=image;}
    public String getName(){return name;} public String getBirthday(){return birthday;}
    public String getAddress(){return address;} public String getImage(){return image;}
}
