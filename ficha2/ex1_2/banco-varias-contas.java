import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {
  
  
  private static class Account {
    private int balance;
    
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
      balance += value;
      return true;
    }
    
    /**
     * Retirar da conta dinheiro value
     * @param value Valor a retirar
     * @return true se sucesso, false caso contrário
     */
    boolean withdraw(int value) {
      if (value > balance)
        return false;
      balance -= value;
      return true;
    }
  }
  
  
  Lock l = new ReentrantLock();
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
    l.lock();
    try{
      return av[id].balance();
    }
    finally{
      l.unlock();
    }

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
    l.lock();
    try{
      return av[id].deposit(value);
    }
    finally{
      l.unlock();
    }

  }

  // Withdraw; fails if no such account or insufficient balance
  /**
   * Retirada de dinheiro value da conta id
   * @param id Conta de onde se vai retirar o dinheiro
   * @param value Valor a retirar
   * @return true se sucesso, false caso contrario
   */
  public boolean withdraw(int id, int value) {
    //podemos alterar este if para antes dos locks para ganhar tempo de execução
    //pois slots é uma variavel imutável (caso contrario nao seria possivel fazer desta maneira)
    if (id < 0 || id >= slots)
      return false;
    l.lock();
    try{
      return av[id].withdraw(value);
    }
    finally{
      l.unlock();
    }
  }

  /**
   * Transfere de from para to. 
   * O valor da transferencia é value.
   * Pressupoe-se que as contas inseridas como input existem.
   * @param from Origem da transferencia
   * @param to Destino da transferencia
   * @param value Valor da transferencia
   * @return true se concluido com sucesso, falso caso contrario
   */
  boolean transfer (int from,int to,int value){
    l.lock();
    try {
      if (!withdraw(from, value))
        return false;
      else 
        return deposit(to, value);
    } finally {
      l.unlock();
    }
  }

  /**
   * Devolve o saldo de todas as contas bancarias
   * Os locks usados são para poder consultar os saldos a qualquer momento e náo so no final do calculo
   * @return Saldo de todas as contas bancarias
   */
  int totalBalance(){
    int r = 0;
    l.lock();
    for (int i = 0; i < av.length; i++)
      r += av[i].balance();
    l.unlock();
      return r;
  }
}