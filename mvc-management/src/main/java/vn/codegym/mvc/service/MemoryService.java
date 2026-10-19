package vn.codegym.mvc.service;
import vn.codegym.mvc.model.Identified;import java.util.*;import java.util.concurrent.ConcurrentHashMap;import java.util.concurrent.atomic.AtomicInteger;
public class MemoryService<T extends Identified> {
 private final Map<Integer,T> items=new ConcurrentHashMap<>();private final AtomicInteger ids=new AtomicInteger();
 public List<T> findAll(){return items.values().stream().sorted(Comparator.comparingInt(Identified::getId)).toList();}
 public List<T> search(String q){String term=q==null?"":q.trim().toLowerCase(Locale.ROOT);return findAll().stream().filter(x->x.getName().toLowerCase(Locale.ROOT).contains(term)).toList();}
 public T findById(int id){return items.get(id);}public T create(T item){item.setId(ids.incrementAndGet());items.put(item.getId(),item);return item;}
 public boolean update(T item){return items.replace(item.getId(),item)!=null;}public boolean delete(int id){return items.remove(id)!=null;}
}
