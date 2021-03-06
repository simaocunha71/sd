package ficha3.ex3;

import java.util.Random;


class Mover implements Runnable {
    BankEx3 b;
    int s; // Number of accounts

    public Mover(BankEx3 b, int s) {
        this.b = b;
        this.s = s;
    }

    public void run() {
        final int moves = 100000;
        int from, to;
        Random rand = new Random();

        for (int m = 0; m < moves; m++) {
            from = rand.nextInt(s); // Get one
            while ((to = rand.nextInt(s)) == from); // Slow way to get distinct
            b.transfer(from, to, 1);
            System.out.println("Enviei de #"+from+" para #"+to+" com o valor de transferencia 1€");
        }
    }
}

class BankTest {
    public static void main(String[] args) throws InterruptedException {
        final int N = 10;

        BankEx3 b = new BankEx3();
        System.out.println("-----------------------Exercicio 2-----------------------");
        System.out.println("Criando "+N+" contas com "+1000+"€ cada...");
        for (int i = 0; i < N; i++)
            b.createAccount(1000);
        
        int[] v = new int[]{0,1,2,3,4,5,6,7,8,9};
        System.out.println("Saldo total antes das threads: "+b.totalBalance(v)+"€");
        
        Thread t1 = new Thread(new Mover(b, 10));
        Thread t2 = new Thread(new Mover(b, 10));
        
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Fechando as "+N+" contas...");
        for (int i = 0; i < N; i++)
            b.closeAccount(i);

        System.out.println("Saldo total depois das threads: " + b.totalBalance(v)+"€");
        System.out.println("Expectável: 0€");
        System.out.println("--------------------------------------------------");
    }
}
