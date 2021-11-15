import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    
    private Lock l = new ReentrantLock();
    
    private class Product { 
        Condition c = l.newCondition(); //cada produto fica associado ao lock do armazem
        int quantity = 0; 
    }
    
    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) 
            return p;
        p = new Product();
        map.put(item, p);
        return p;
    }
    
    //------------------------------------Versao egoista--------------------//
    public void supplyEgoista(String item, int quantity) {
        l.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            p.c.signalAll();
        } finally {
            l.unlock();
        }
    }
    
    public void consumeEgoista(Set<String> items) {
        l.lock();
        try {
            for (String s : items) {
                get(s).quantity--;
            }
        } finally {
            l.unlock();
        }
    }

    // -----------------------------------Versao cooperativa------------------------//

    public void supplyCooperativa(String item, int quantity) {
        l.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
        } finally {
            l.unlock();
        }
    }

    public Product falta(String[] prods){
        for (String s:prods){
            Product p = get(s);
            if(p.quantity == 0)
                return p;
        }
        return null; 
    }
    
    public void consumeCooperativa(String[] prods) throws InterruptedException {
        l.lock();
        for (String s : prods) {
            Product p = get(s);
            while (p.quantity == 0)
                p.c.await();
            p.quantity--;
        }
        l.unlock();
    }

}