import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor
 */
public class EchoServer {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345); //servidor nunca fecha

            while (true) {
                Socket socket = ss.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String n;
                int acc = 0, nEnviados = 0, media;
                while ((n = in.readLine()) != null) {
                    System.out.println(n);
                    acc+=Integer.parseInt(n);
                    nEnviados++;
                    out.println(acc);
                    out.flush();
                }
                media = acc/nEnviados;
                out.println(media);
                out.flush();

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}