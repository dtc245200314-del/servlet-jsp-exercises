package vn.codegym.finalexam.controller;

import vn.codegym.finalexam.dao.ProductDAO;
import vn.codegym.finalexam.model.Product;
import vn.codegym.finalexam.service.ProductValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/products")
public final class ProductServlet extends HttpServlet {
    private final ProductDAO dao=new ProductDAO();
    private static String param(HttpServletRequest r,String key){String value=r.getParameter(key);return value==null?"":value;}
    private static int id(HttpServletRequest r){
        int id=Integer.parseInt(param(r,"id"));
        if(id<1)throw new IllegalArgumentException("ID không hợp lệ.");return id;
    }
    private static void prepare(HttpServletRequest r){
        HttpSession session=r.getSession();
        if(session.getAttribute("csrf")==null)session.setAttribute("csrf",UUID.randomUUID().toString());
    }
    private static void view(HttpServletRequest r,HttpServletResponse s,String name) throws ServletException,IOException{
        r.getRequestDispatcher("/WEB-INF/views/"+name+".jsp").forward(r,s);
    }
    private static void error(HttpServletRequest r,HttpServletResponse s,int status,String message) throws ServletException,IOException{
        s.setStatus(status);r.setAttribute("error",message);view(r,s,"error");
    }
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException {
        r.setCharacterEncoding("UTF-8");prepare(r);
        try{
            switch(param(r,"action")){
                case "" -> {
                    r.setAttribute("products",dao.findAll());
                    r.setAttribute("notice",switch(param(r,"success")){case "created"->"Đã thêm sản phẩm.";case "updated"->"Đã cập nhật sản phẩm.";case "deleted"->"Đã xóa sản phẩm.";default->"";});
                    view(r,s,"list");
                }
                case "new" -> {r.setAttribute("editing",false);r.setAttribute("form",Map.of("name","","price","","quantity",""));view(r,s,"form");}
                case "edit","delete" -> {
                    Product p=dao.findById(id(r));
                    if(p==null){error(r,s,404,"Không tìm thấy sản phẩm.");return;}
                    r.setAttribute("product",p);
                    if("edit".equals(param(r,"action"))){
                        r.setAttribute("editing",true);r.setAttribute("productId",p.getId());
                        r.setAttribute("form",Map.of("name",p.getName(),"price",p.getPrice().toPlainString(),"quantity",Integer.toString(p.getQuantity())));
                        view(r,s,"form");
                    }else view(r,s,"delete");
                }
                default -> error(r,s,400,"Chức năng không hợp lệ.");
            }
        }catch(IllegalArgumentException ex){error(r,s,400,"ID sản phẩm không hợp lệ.");}
        catch(SQLException ex){getServletContext().log("Product read failed",ex);error(r,s,503,"Chưa kết nối được cơ sở dữ liệu. Vui lòng kiểm tra cấu hình server.");}
    }
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException {
        r.setCharacterEncoding("UTF-8");prepare(r);
        if(!r.getSession().getAttribute("csrf").equals(r.getParameter("csrf"))){error(r,s,403,"Phiên biểu mẫu không hợp lệ. Hãy mở lại trang và thử lại.");return;}
        String action=param(r,"action");
        try{
            switch(action){
                case "create","update" -> {
                    boolean editing="update".equals(action);int productId=editing?id(r):0;
                    r.setAttribute("editing",editing);r.setAttribute("productId",productId);
                    r.setAttribute("form",Map.of("name",param(r,"name"),"price",param(r,"price"),"quantity",param(r,"quantity")));
                    Product product;
                    try{product=ProductValidator.parse(productId,param(r,"name"),param(r,"price"),param(r,"quantity"));}
                    catch(IllegalArgumentException ex){s.setStatus(400);r.setAttribute("error",ex.getMessage());view(r,s,"form");return;}
                    if(editing){if(!dao.update(product)){error(r,s,404,"Sản phẩm không còn tồn tại.");return;}}
                    else dao.create(product);
                    s.sendRedirect(r.getContextPath()+"/products?success="+(editing?"updated":"created"));
                }
                case "delete" -> {
                    if(!dao.delete(id(r))){error(r,s,404,"Sản phẩm không còn tồn tại.");return;}
                    s.sendRedirect(r.getContextPath()+"/products?success=deleted");
                }
                default -> error(r,s,400,"Chức năng không hợp lệ.");
            }
        }catch(IllegalArgumentException ex){error(r,s,400,"ID sản phẩm không hợp lệ.");}
        catch(SQLException ex){getServletContext().log("Product write failed",ex);error(r,s,503,"Không lưu được sản phẩm. Vui lòng thử lại sau.");}
    }
}
