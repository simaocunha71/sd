package ficha1;
/**
 * Classe do Depositor para a versão com locks (é exatamente igual à versão sem locks, simplesmente copiei)
 */
class DepositorLock implements Runnable {
    final int nDeposits;
    final int value;
    final BankLock b;

    public DepositorLock(int newI, int newV, BankLock newB) {
        this.nDeposits = newI;
        this.value = newV;
        this.b = newB;
    }

    public void run() {
        for (long j = 0; j < nDeposits; j++)
            b.deposit(value);
    }
}

public class Exercicio3 {
    public static void main(String[] args) throws InterruptedException {
        int nThr = 10;
        int valueToDeposit = 100;
        int nDeposits = 1000;
        BankLock b = new BankLock();
        Thread[] threads = new Thread[nThr];

        DepositorLock d = new DepositorLock(nDeposits, valueToDeposit, b);

        for (int i = 0; i < nThr; i++) {
            threads[i] = new Thread(d);
        }

        for (int i = 0; i < nThr; i++) {
            threads[i].start();
        }

        for (int i = 0; i < nThr; i++) {
            threads[i].join();
        }

        //Aparecerá 1 000 000 no saldo
        System.out.println("Saldo -> " + b.balance());

    }
}
