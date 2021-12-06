package ficha8.ex2;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private Lock sendLock = new ReentrantLock();
    private Lock receiveLock = new ReentrantLock();

    public static class Frame {
        public final int tag;
        public final byte[] data;
        public Frame(int tag,byte[] data) {
            this.tag = tag;
            this.data = data; 
        }
    }
    
    public TaggedConnection(Socket socket) throws IOException { 
        this.s = socket;
        in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }
    
    public void send(Frame frame) throws IOException {
        sendLock.lock();
        try {
            out.writeInt(frame.tag);
            out.writeInt(frame.data.length);
            out.write(frame.data);
            out.flush();
        } finally {
            sendLock.unlock();
        }
    }
    
    public void send(int tag,byte[] data) throws IOException {
        send(new Frame(tag, data));
    }
    
    public Frame receive() throws IOException {
        receiveLock.lock();
        try {
            int tag = in.readInt();
            int size = in.readInt();
            byte[] data = new byte[size];
            in.readFully(data);
            return new Frame(tag, data);
        } finally {
            receiveLock.unlock();
        }
    }
    
    public void close() throws IOException {
        in.close();
        out.close();
    }
    
}
