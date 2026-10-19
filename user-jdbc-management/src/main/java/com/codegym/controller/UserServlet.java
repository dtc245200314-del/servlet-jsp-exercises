package com.codegym.controller;
import com.codegym.dao.UserDAO;
import com.codegym.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/users")
public final class UserServlet extends HttpServlet {
    private final UserDAO dao=new UserDAO();
    private static String param(HttpServletRequest r,String key){String s=r.getParameter(key);return s==null?"":s.trim();}
    private static int id(HttpServletRequest r){int id=Integer.parseInt(param(r,"id"));if(id<1)throw new IllegalArgumentException("ID không hợp lệ.");return id;}
    private static User input(HttpServletRequest r){
        String name=param(r,"name"),email=param(r,"email"),country=param(r,"country");
        if(name.isEmpty()||name.length()>50||country.isEmpty()||country.length()>50||email.length()>100||!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))throw new IllegalArgumentException("Nhập tên/quốc gia tối đa 50 ký tự và email hợp lệ.");
        return new User(name,email,country);
    }
    private static void prepare(HttpServletRequest r){
        HttpSession session=r.getSession();if(session.getAttribute("csrf")==null)session.setAttribute("csrf",UUID.randomUUID().toString());
        r.setAttribute("mode","sql".equals(param(r,"mode"))?"sql":"sp");
    }
    private static void view(HttpServletRequest r,HttpServletResponse s,String jsp) throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/"+jsp+".jsp").forward(r,s);}
    private static void error(HttpServletRequest r,HttpServletResponse s,int status,String message) throws ServletException,IOException{s.setStatus(status);r.setAttribute("error",message);view(r,s,"error");}
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException{
        r.setCharacterEncoding("UTF-8");prepare(r);boolean sp=!"sql".equals(r.getAttribute("mode"));
        try{
            String action=param(r,"action");
            switch(action){
                case "new" -> view(r,s,"form");
                case "edit","delete","view" -> {
                    User user=sp?dao.getUserById(id(r)):dao.selectUser(id(r));
                    if(user==null){error(r,s,404,"Không tìm thấy user.");return;}
                    r.setAttribute("user",user);r.setAttribute("action",action);view(r,s,"edit".equals(action)?"form":"detail");
                }
                case "" -> {String country=param(r,"country");if(country.length()>50)throw new IllegalArgumentException("Từ khóa quốc gia quá dài.");boolean sort="name".equals(param(r,"sort"));r.setAttribute("users",sp?dao.selectAllUsersStore(country,sort):dao.selectAllUsers(country,sort));view(r,s,"list");}
                default -> error(r,s,400,"Chức năng không hợp lệ.");
            }
        }catch(IllegalArgumentException ex){error(r,s,400,"Dữ liệu hoặc ID không hợp lệ.");}
        catch(SQLException ex){getServletContext().log("User database request failed",ex);error(r,s,503,"Không thể truy cập cơ sở dữ liệu. Kiểm tra cấu hình server.");}
    }
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException{
        r.setCharacterEncoding("UTF-8");prepare(r);
        if(!r.getSession().getAttribute("csrf").equals(r.getParameter("csrf"))){error(r,s,403,"CSRF token không hợp lệ.");return;}
        boolean sp=!"sql".equals(r.getAttribute("mode"));
        try{
            switch(param(r,"action")){
                case "create" -> {
                    User user=input(r);
                    if("transaction".equals(param(r,"creation"))){String[] values=r.getParameterValues("permissions");int[] permissions=values==null?new int[0]:new int[values.length];for(int i=0;i<permissions.length;i++){permissions[i]=Integer.parseInt(values[i]);if(permissions[i]<1||permissions[i]>4)throw new IllegalArgumentException("Quyền không hợp lệ.");}dao.addUserTransaction(user,permissions);}
                    else if(sp)dao.insertUserStore(user);else dao.insertUser(user);
                }
                case "update" -> {User user=input(r);user.setId(id(r));if(!(sp?dao.updateUserStore(user):dao.updateUser(user))){error(r,s,404,"User không còn tồn tại.");return;}}
                case "delete" -> {if(!(sp?dao.deleteUserStore(id(r)):dao.deleteUser(id(r)))){error(r,s,404,"User không còn tồn tại.");return;}}
                case "without-failure","with-failure","with-success" -> {
                    String action=param(r,"action");var result="without-failure".equals(action)?dao.insertUpdateWithoutTransaction(true):dao.insertUpdateUseTransaction("with-failure".equals(action));
                    r.setAttribute("experiment",result);view(r,s,"experiment");return;
                }
                case "user-rollback" -> {
                    String email="rollback-"+UUID.randomUUID()+"@example.com";boolean failed=false;
                    try{dao.addUserTransaction(new User("Rollback demo",email,"QA"),new int[]{1,9999});}catch(SQLException expected){failed=true;}
                    r.setAttribute("message","SQL lỗi FK: "+failed+"; số user còn lại sau rollback: "+dao.countByEmail(email));view(r,s,"experiment");return;
                }
                default -> {error(r,s,400,"Chức năng không hợp lệ.");return;}
            }
            s.sendRedirect(r.getContextPath()+"/users?mode="+r.getAttribute("mode"));
        }catch(IllegalArgumentException ex){error(r,s,400,ex.getMessage());}
        catch(SQLException ex){getServletContext().log("User database mutation failed",ex);error(r,s,409,"Không lưu được dữ liệu. Email có thể đã tồn tại hoặc ràng buộc dữ liệu bị vi phạm.");}
    }
}
