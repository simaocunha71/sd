package ex1_2;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    
    //É mais eficiente ter os locks só no banco do que nas contas+banco, 
    //pois estaremos adquirir locks a mais, 
    //tornando o programa mais ineficiente
    
    private static class Account {
        Lock l = new ReentrantLock(); //necessario para os objetos Account nulos
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
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    Lock l = new ReentrantLock();
    

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance); //novo objeto de Account, por isso não é necessário sujeitá-lo aos locks
        l.lock();
        try{
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        }
        finally{
            l.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;

        l.lock();
        try{
            c = map.remove(id);
            if (c == null)
                return 0;
            c.l.lock(); //caso c não seja null   
        }
        finally{
            l.unlock();
        }

        try{
            return c.balance();
        }
        finally{
            c.l.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c;
        l.lock();
        try{
            c = map.get(id);
            if (c == null)
                return 0;
            c.l.lock();              
        }
        finally{
            l.unlock();
        }

        try{
            return c.balance();
        }
        finally{
            c.l.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c;
        l.lock();
        try{
            c = map.get(id);
            if (c == null)
                return false;
            c.l.lock();
        }
        finally{
            l.unlock();
        }
        try{
            return c.deposit(value);
        }
        finally{
            c.l.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c;
        l.lock();
        try{
            c = map.get(id);
            if (c == null)
                return false;
            c.l.lock();
        }
        finally{
           l.unlock(); 
        }
        try{
            return c.withdraw(value);
        }
        finally{
            c.l.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        l.lock();
        try{
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto ==  null)
                return false;
            if (from < to){
                cfrom.l.lock();
                cto.l.lock();
            }
            else{
                cto.l.lock();
                cto.l.unlock();
            }
        }
        finally{
            l.unlock();
        }
        return cfrom.withdraw(value) && cto.deposit(value);
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        int total = 0;
        Account[] arr = new Account[ids.length];
        Arrays.sort(arr); //ordenar os locks para evitar deadlocks
        l.lock();
        try{
            for (int i = 0; i < ids.length; i++){
                arr[i] = map.get(ids[i]);
                if (arr[i] == null)
                    return 0;
            }
            for (Account a : arr) {
                a.l.lock();
            }
        }
        finally{
            l.unlock();
        }

        for (Account acc : arr){
            total += acc.balance();
            acc.l.unlock();
        }
        return total;
    }
    public static void main(String[] args) {
        System.out.println("Fazer o exercicio 2 aqui neste método");
    }
}