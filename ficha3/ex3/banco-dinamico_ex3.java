package ficha3.ex3;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BankEx3 {

    private static class Account {
        private int balance;
        ReentrantReadWriteLock al = new ReentrantReadWriteLock();
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

    ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    Lock rl = l.readLock();
    Lock wl = l.writeLock();
    
    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        wl.lock();
        int id = nextId;
        nextId += 1;
        map.put(id, c);
        wl.unlock();
        return id;
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        wl.lock();
        try {
            c = map.remove(id);
            if (c == null) return 0;
            c.al.readLock().lock();
        } finally {
            wl.unlock();
        }

        try {
            return c.balance();
        } finally {
            c.al.readLock().unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c;
        rl.lock();
        try {
            c = map.get(id);
            if (c == null) return 0;
            c.al.readLock().lock();
        } finally {
            rl.unlock();
        }

        try {
            return c.balance();
        } finally {
            c.al.readLock().unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c;
        rl.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.al.writeLock().lock();
        } finally {
            rl.unlock();
        }

        try {
            return c.deposit(value);
        } finally {
            c.al.writeLock().unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c;
        rl.lock();
        try {
            c = map.get(id);
            if (c == null) return false;
            c.al.writeLock().lock();
        } finally {
            rl.unlock();
        }

        try {
            return c.withdraw(value);
        } finally {
            c.al.writeLock().unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        rl.lock();
        try {
            cfrom = map.get(from);
            cto   = map.get(to);
            if (cfrom == null || cto == null)
                return false;
                //ordenar locks
            if (from < to) {
                cfrom.al.writeLock().lock();
                cto.al.writeLock().lock();
            } else {
                cto.al.writeLock().lock();
                cfrom.al.writeLock().lock();
            }
        } finally {
            rl.unlock();
        }

        try {
            try {
                if (!cfrom.withdraw(value)) return false;
            } finally {
                cfrom.al.writeLock().unlock();
            }
            return cto.deposit(value);
        } finally {
            cto.al.writeLock().unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        List<Account> cs = new ArrayList<>();
        rl.lock();
        try {
            for (int i : Arrays.stream(ids).sorted().toArray()) {
                Account c = map.get(i);
                if (c == null)
                    return 0;
                cs.add(c);
            }
            for (Account c : cs)
                c.al.readLock().lock();
        } finally {
            rl.unlock();
        }
        int total = 0;
        for (Account c : cs) {
            total += c.balance();
            c.al.readLock().unlock();
        }
        return total;
    }

}