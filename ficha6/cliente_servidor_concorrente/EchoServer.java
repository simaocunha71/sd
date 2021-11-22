import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Register {
    private int sum = 0;
    private int count = 0;
    Lock l = new ReentrantLock();

    public void add(int n) {
        l.lock();
        try {
            sum += n;
            count += 1;
        } finally {
            l.unlock();
        }
    }

    public int media() {
        l.lock();
        try {
            return sum / count;
        } finally {
            l.unlock();
        }
    }
}

class ClientHandler implements Runnable{
    private Socket s;
    private Register r;
    public ClientHandler(Socket s,Register r){
        this.s = s;
        this.r = r;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream());

            String n;
            int acc = 0, nEnviados = 0, media;
            while ((n = in.readLine()) != null) {
                System.out.println(n);
                acc += Integer.parseInt(n);
                nEnviados++;
                out.println(acc);
                out.flush();
            }
            media = acc / nEnviados;
            out.println(media);
            out.flush();

            s.shutdownOutput();
            s.shutdownInput();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Servidor
 */
public class EchoServer {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            while(true){
                Socket s = ss.accept();
                Register r = new Register();
                new Thread(new ClientHandler(s,r)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}