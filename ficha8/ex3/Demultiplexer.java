package ficha8.ex3;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ficha8.ex2.TaggedConnection;
import ficha8.ex2.TaggedConnection.Frame;

public class Demultiplexer implements AutoCloseable {

     private Lock l = new ReentrantLock();
     private TaggedConnection tg;
     private Map<Integer,Entrada> ents = new HashMap<>();
     private IOException exc = null;
     
     private class Entrada {
         Condition cond = l.newCondition();
         private ArrayDeque<byte[]> dq = new ArrayDeque<>();
         private int waiters = 0;
     }
     

     public Demultiplexer(TaggedConnection conn) {
        this.tg = conn;
     }
    
     public void start() {
        new Thread(() -> {
            try {
                Frame f = tg.receive();
                l.lock();
                try{
                    Entrada e = get(f.tag);
                    e.dq.add(f.data);
                    e.cond.signal();
                } finally{
                    l.unlock();
                }
            } 
            
            catch (IOException ep) {
                l.lock();
                try {
                    exc = ep;
                    ents.forEach((k,v) -> v.cond.signalAll());

                } finally {
                    l.unlock();
                }
            }

        }).start();
     }
    
     public void send(Frame frame) throws IOException {
         tg.send(frame);
     }
    
     public void send(int tag,byte[] data) throws IOException {
       tg.send(tag, data);
     }
    
     public byte[] receive(int tag) throws IOException, InterruptedException {
        l.lock();
        try {
            Entrada e = get(tag);
            e.waiters++;
            while(true){
                if(!e.dq.isEmpty()){
                    e.waiters--;
                    if (e.waiters == 0 && e.dq.isEmpty())
                        ents.remove(tag);
                    return e.dq.poll(); //return byte[]
                }
                if (exc != null){
                    throw exc;
                }
                e.cond.await();
            }
        } finally {
            l.unlock();
        }
     }
    
     private Entrada get(int tag) { 
        Entrada e = ents.get(tag);
        if (e == null){
            e = new Entrada();
            ents.put(tag, e);
        }
        return e;
    }

    public void close() throws IOException {
        tg.close();
     }
}
