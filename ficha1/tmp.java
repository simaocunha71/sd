class MyThread extends Thread{
    public void run(){
        System.out.println("OLA!");
    }
}

class MyRunnable implements Runnable {
    public void run() {
        System.out.println("OLA!");
    }
}

public class tmp {
    public static void main(String[] args) throws InterruptedException{
        //Correr uma thread com uma classe que extende Thread
        MyThread t = new MyThread();
        // t.run(); <- o que não fazer
        t.start();

        //Correr uma thread que implementa a interface Runnable
        Thread th = new Thread(new MyRunnable());
        th.start();
    
        t.join();//espera até que a thread t termine
    }    
}
