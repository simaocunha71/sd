import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Barrier {
    private final int nThreads;
    private Lock l = new ReentrantLock();
    private Condition c = l.newCondition();
    private int counter = 0; //conta quantas threads já invocaram await
        
    Barrier (int nThreads) {
        this.nThreads = nThreads;
    }

    /**
     * Refere-se à versão do exercicio 1
     * @throws InterruptedException
     */
    void await11() throws InterruptedException {
        l.lock();
        try {
            counter++;  
            
            while(counter < nThreads){
                c.await();
            }
            c.signal();
            
            /*
            if (counter < nThreads){
                while(counter < nThreads)
                    c.await();
            }
            else
                c.signalAll();
            */

        } finally {
            l.unlock();
        }
    }

    private boolean open = true;

    /**
     * Refere-se à versão do exercicio 2
     * @throws InterruptedException
     */
    void await12() throws InterruptedException{
        l.lock();
        try {
            counter++;  
            if (counter < nThreads){
                while(open){
                    c.await();
                    open = false;
                }
            }
            else{
                c.signalAll();
            }
            

        } finally {
            l.unlock();
        }
    }

}

class Tester extends Thread{
    Barrier b;
    Tester (Barrier b){
        this.b = b;
    }
    
    public void run(){
        Random r = new Random();
        while (true) {
            try {
                Thread.sleep( 1000 + r.nextInt(4000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Vou invocar await");

            try {
                //b.await11();
                b.await12();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Await done");
        }
    }
}

class Main{
    public static void main(String[] args) throws InterruptedException {
        int nt = 3;
        Barrier b = new Barrier(nt);
        Thread[] at = new Thread[nt];

        for (int i = 0; i < nt; i++)
            at[i] = new Tester(b);

        for (int i = 0; i < nt; i++)
            at[i].start();

    }    
}
