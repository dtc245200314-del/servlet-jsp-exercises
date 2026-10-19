package vn.codegym.mvc.controller;
import vn.codegym.mvc.model.Identified;import vn.codegym.mvc.service.MemoryService;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;import java.util.UUID;
public abstract class CrudServlet<T extends Identified> extends HttpServlet {
 protected abstract MemoryService<T> service();protected abstract String kind();protected abstract String title();protected abstract T input(HttpServletRequest r);
 protected static String field(HttpServletRequest r,String name,int max){String v=r.getParameter(name);v=v==null?"":v.trim();if(v.isEmpty()||v.length()>max)throw new IllegalArgumentException("Trường "+name+" bắt buộc và tối đa "+max+" ký tự.");return v;}
 private static String value(HttpServletRequest r,String name){String v=r.getParameter(name);return v==null?"":v.trim();}
 private static int id(HttpServletRequest r){int n=Integer.parseInt(value(r,"id"));if(n<1)throw new IllegalArgumentException("ID không hợp lệ.");return n;}
 private void prepare(HttpServletRequest r)throws java.io.UnsupportedEncodingException{r.setCharacterEncoding("UTF-8");var session=r.getSession();if(session.getAttribute("csrf")==null)session.setAttribute("csrf",UUID.randomUUID().toString());r.setAttribute("kind",kind());r.setAttribute("title",title());}
 private void show(HttpServletRequest r,HttpServletResponse s,String view)throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/views/"+kind()+"/"+view+".jsp").forward(r,s);}
 private void fail(HttpServletRequest r,HttpServletResponse s,int code,String message)throws ServletException,IOException{s.setStatus(code);r.setAttribute("error",message);r.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(r,s);}
 @Override protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  prepare(r);String action=value(r,"action");r.setAttribute("action",action);
  try{switch(action){
   case "new" -> show(r,s,"form");
   case "edit","view","delete" -> {T item=service().findById(id(r));if(item==null){fail(r,s,404,"Không tìm thấy dữ liệu.");return;}r.setAttribute("item",item);show(r,s,action.equals("edit")?"form":"detail");}
   case "" -> {String q=value(r,"q");if(q.length()>100)throw new IllegalArgumentException("Từ khóa quá dài.");r.setAttribute("items",service().search(q));show(r,s,"list");}
   default -> fail(r,s,400,"Chức năng không hợp lệ.");
  }}catch(IllegalArgumentException ex){fail(r,s,400,"Dữ liệu hoặc ID không hợp lệ.");}
 }
 @Override protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
  prepare(r);if(!r.getSession().getAttribute("csrf").equals(r.getParameter("csrf"))){fail(r,s,403,"CSRF token không hợp lệ.");return;}
  try{switch(value(r,"action")){
   case "create" -> service().create(input(r));
   case "update" -> {T item=input(r);item.setId(id(r));if(!service().update(item)){fail(r,s,404,"Bản ghi không còn tồn tại.");return;}}
   case "delete" -> {if(!service().delete(id(r))){fail(r,s,404,"Bản ghi không còn tồn tại.");return;}}
   default -> {fail(r,s,400,"Chức năng không hợp lệ.");return;}
  }s.sendRedirect(r.getContextPath()+"/"+kind());}catch(IllegalArgumentException ex){fail(r,s,400,"Dữ liệu không hợp lệ. Kiểm tra các trường và giá sản phẩm.");}
 }
}
