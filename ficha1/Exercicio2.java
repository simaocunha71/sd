package ficha1;

class Bank {

  private static class Account {
    private int balance;

    Account(int balance) {
      this.balance = balance;
    }

    int balance() {
      return balance;
    }

    boolean deposit(int value) {
      balance += value;
      return true;
    }
  }

  // Our single account, for now
  private Account savings = new Account(0);

  // Account balance
  public int balance() {
    return savings.balance();
  }

  // Deposit
  boolean deposit(int value) {
    return savings.deposit(value);
  }
}

/**
 * Classe de um depositor
 */
class Depositor implements Runnable{
    final int nDeposits;
    final int value;
    final Bank b;

    public Depositor (int newI,int newV, Bank newB){
      this.nDeposits = newI;
      this.value = newV;
      this.b = newB;
    }

    public void run() {
        for (long j = 0; j < nDeposits; j++)
            b.deposit(value);
    }
}

public class Exercicio2 {    
    public static void main(String[] args) throws InterruptedException{
        int nThr = 10;
        int valueToDeposit = 100;
        int nDeposits = 1000;
        Bank b = new Bank();
        Thread [] threads = new Thread[nThr];

        Depositor d = new Depositor(nDeposits, valueToDeposit,b);

        for(int i = 0; i < nThr; i++) {
            threads[i] = new Thread(d);
        }

        for(int i = 0; i < nThr; i++) {
            threads[i].start();
        }

        for(int i = 0; i < nThr; i++) {
            threads[i].join();
        }
        //É suposto não aparecer 1 000 000 no saldo
        System.out.println("Saldo -> " + b.balance());
    }
}
