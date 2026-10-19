package vn.codegym.finalexam.service;

import java.math.BigDecimal;
import vn.codegym.finalexam.model.Product;

public final class ProductValidator {
    private ProductValidator(){}
    public static Product parse(int id,String name,String price,String quantity){
        String productName=name==null?"":name.strip();
        if(productName.isEmpty()||productName.length()>255)
            throw new IllegalArgumentException("Tên sản phẩm phải có từ 1 đến 255 ký tự.");
        String priceText=price==null?"":price.strip();
        if(!priceText.matches("[0-9]{1,13}(\\.[0-9]{1,2})?"))
            throw new IllegalArgumentException("Giá phải không âm, tối đa 13 chữ số phần nguyên và 2 chữ số thập phân.");
        String quantityText=quantity==null?"":quantity.strip();
        if(!quantityText.matches("[0-9]{1,10}"))
            throw new IllegalArgumentException("Số lượng phải là số nguyên không âm.");
        long count=Long.parseLong(quantityText);
        if(count>Integer.MAX_VALUE)throw new IllegalArgumentException("Số lượng không được vượt quá 2147483647.");
        return new Product(id,productName,new BigDecimal(priceText).setScale(2),(int)count);
    }
}
