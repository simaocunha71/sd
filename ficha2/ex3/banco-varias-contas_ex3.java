package ex3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {
  
  private static class Account {
    private int balance;
    Lock l = new ReentrantLock();
    /**
     * Construtor parametrizado de uma conta bancária
     * @param balance Saldo inicial da conta bancária
     */
    Account(int balance){ 
      this.balance = balance; 
    }
    
    /**
     * Devolve saldo da conta bancária
     * @return Saldo da conta
     */
    int balance(){ 
      return balance; 
    }
    
    /**
     * Depositar na conta dinheiro value
     * @param value Valor a depositar
     * @return true se sucesso, falso caso contrário
     */
    boolean deposit(int value) {
      l.lock();
      try {
        balance += value;
        return true;
      } finally {
        l.unlock();
      }
    }
    
    /**
     * Retirar da conta dinheiro value
     * @param value Valor a retirar
     * @return true se sucesso, false caso contrário
     */
    boolean withdraw(int value) {
      l.lock();
      try {
        if (value > balance)
          return false;
        balance -= value;
        return true;
      } finally {
        l.unlock();
      }
    }
  }
  
  // Bank slots and vector of accounts
  private int slots;
  private Account[] av; 
  
  /**
   * Criação de um banco com n contas
   * @param n nº de contas de um banco
   */
  public Bank(int n)
  {
    slots=n;
    av=new Account[slots];
    for (int i=0; i<slots; i++) 
      av[i]=new Account(0);
  }

  // Account balance
  /**
   * Saldo da conta id
   * @param id Conta id
   * @return Saldo da conta id
   */
  public int balance(int id) {
    //podemos alterar este if para antes dos locks para ganhar tempo de execução
    //pois slots é uma variavel imutável (caso contrario nao seria possivel fazer desta maneira)
    if (id < 0 || id >= slots)
      return 0;
    return av[id].balance();
  }

  // Deposit
  /**
   * Deposito de dinheiro value na conta id
   * @param id Conta a depositar
   * @param value Valor a depositar
   * @return true se sucesso, false caso contrario
   */
  boolean deposit(int id, int value) {
    //podemos alterar este if para antes dos locks para ganhar tempo de execução
    //pois slots é uma variavel imutável (caso contrario nao seria possivel fazer desta maneira)
    if (id < 0 || id >= slots)
      return false;
    return av[id].deposit(value);
  }

  // Withdraw; fails if no such account or insufficient balance
  /**
   * Retirada de dinheiro value da conta id
   * @param id Conta de onde se vai retirar o dinheiro
   * @param value Valor a retirar
   * @return true se sucesso, false caso contrario
   */
  public boolean withdraw(int id, int value){
    if (id < 0 || id >= slots)
      return false;
    return av[id].withdraw(value);
  }

  /**
   * Transfere de from para to. 
   * O valor da transferencia é value.
   * Pressupoe-se que as contas inseridas como input existem.
   * Não importa a ordem de libertar os locks, mas importa a ordem de adquirir os locks 
   * @param from Origem da transferencia
   * @param to Destino da transferencia
   * @param value Valor da transferencia
   * @return true se concluido com sucesso, falso caso contrario
   */
  boolean transfer (int from,int to,int value){
    if (from < 0 || from >= slots || to < 0 || to >= slots || from == to || value < 0)
      return false;
    Account cfrom = av[from];
    Account cto = av[to];
    //ordenar os locks para evitar deadlocks
    if (from < to){
      cfrom.l.lock();
      cto.l.lock();
    }
    else{
      cto.l.lock();
      cto.l.unlock();
    }
    
    try {
      try {
        if (!cfrom.withdraw(value))
          return false;
      } finally {
        cfrom.l.unlock();
      }
      return cto.deposit(value);
    } finally {
      cto.l.unlock();
    }

    /* minha resolução
    try {
      if (!cfrom.withdraw(value)) 
        return false;
      try{
        return cto.deposit(value);
      } finally{
        cto.l.unlock();
      }
    }finally{
      cfrom.l.unlock();
    }
    */
    
  }

  /**
   * Devolve o saldo de todas as contas bancarias
   * Sempre que se somar o saldo total, vai-se libertando essa conta da thread que a usou
   * @return Saldo de todas as contas bancarias
   */
  int totalBalance(){
    int r = 0;
    for (int i = 0; i < av.length; i++)
      av[i].l.lock();
    for (int i = 0; i < av.length; i++){
      r += av[i].balance();
      av[i].l.unlock();
    }
  return r;
  }
}