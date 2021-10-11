class Increment implements Runnable {
  final long i; 

  public Increment (int newI){
    this.i = newI;
  }
  public void run() {
    final long i=100;

    for (long j = 0; j < i; j++)
      System.out.println(j);
  }
}