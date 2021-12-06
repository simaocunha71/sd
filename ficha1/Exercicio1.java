package ficha1;
public class Exercicio1 {
    public static void main(String[] args) throws InterruptedException{
        //parametrizar as variaveis
        int i = 200;
        int N = 5;

        //Criar array de threads
        Thread[] at = new Thread[N];
        
        //Cada thread vai efetuar Increment
        for (int k = 1; k < N; k++){
            at[k] = new Thread(new Increment(i));
        }

        //Inicializar as N threads
        for (int k = 1; k < N; k++){
            at[k].start();
        }

        //Esperar que as N threads terminem
        for (int k = 1; k < N; k++){
            at[k].join();
        }

        //Irá imprimir esta mensagem no fim da execução das threads
        System.out.println("Fim");
    }    
}

