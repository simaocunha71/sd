package ex3;

import java.util.Random;

class Watcher implements Runnable{
  Bank b;
  int s; // Number of accounts

  public Watcher(Bank b, int s) { 
    this.b=b; 
    this.s=s; 
  }
  public void run() {
    final int moves = 100000;
    for (int m = 0; m < moves; m++) {
      int t = b.totalBalance();
      System.out.println(m +" : "+ t);
    }
  } 
}

class Mover implements Runnable {
  Bank b;
  int s; // Number of accounts

  public Mover(Bank b, int s) { 
    this.b=b; 
    this.s=s; 
  }

  public void run() {
    final int moves=100000;
    int from, to;
    Random rand = new Random();

    for (int m=0; m<moves; m++)
    {
      from=rand.nextInt(s); // Get one
      while ((to=rand.nextInt(s))==from); // Slow way to get distinct
      b.transfer(from,to,1);
    }
  }
}

class BankTest {
  public static void main(String[] args) throws InterruptedException {
    final int N=10;
    int valueToDeposit = 1000;

    Bank b = new Bank(N);
    System.out.println("--------------------------------------------------");
    System.out.println("Depositando "+valueToDeposit+"€ em "+N+" contas...");
    for (int i=0; i<N; i++) 
      b.deposit(i, valueToDeposit);

    System.out.println("Saldo total no banco antes das threads -> " + b.totalBalance());

    Thread t1 = new Thread(new Mover(b,10)); 
    Thread t2 = new Thread(new Mover(b,10));
    Thread t3 = new Thread(new Watcher(b,10));

    t1.start(); t2.start(); t3.start(); 
    t1.join(); t2.join(); t3.join();

    System.out.println("Saldo total no banco depois das threads-> " + b.totalBalance());
    System.out.println("--------------------------------------------------");
  }
}