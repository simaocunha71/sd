package ficha8.ex1;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.*;

public class FramedConnection implements AutoCloseable {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private Lock sendLock = new ReentrantLock();
    private Lock receiveLock = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException {
        this.s = socket;
        in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }
    

    public void send(byte[] data) throws IOException {
        sendLock.lock();
        try{
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally{
            sendLock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        receiveLock.lock();
        try {
            int size = in.readInt();
            byte[] data = new byte[size];
            in.readFully(data);
            return data;
        } finally {
            receiveLock.unlock();
        }
    }

    public void close() throws IOException {
        s.close();
    } 
}
