package ficha7;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

class ContactManager {
    private HashMap<String, Contact> contacts;
    private ReentrantLock lock;

    public ContactManager() {
        lock = new ReentrantLock();
        contacts = new HashMap<>();
        update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));
    }


    // @TODO
    public void update(Contact c) {
        lock.lock();
        contacts.put(c.name(),c);
        lock.unlock();
    }

    // @TODO
    public ContactList getContacts() {
        try{
            lock.lock();
            ContactList ret = new ContactList();
            ret.addAll(contacts.values());
            return ret;
        }
        finally {
            lock.unlock();
        }
    }

    public void getContacts(DataOutputStream out) throws IOException {
        for (Contact contact : contacts.values()) {
            contact.serialize(out);
            out.flush();
        }
    }
}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactManager manager;

    public ServerWorker (Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    // @TODO
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while (true) {
                int option = in.readInt();
                switch (option) {
                    case 1:
                        System.out.println("Adicionei um contacto...");
                        Contact newContact = Contact.deserialize(in);
                        manager.update(newContact);
                        System.out.println(newContact.toString());
                        break;
                    case 2:
                        manager.getContacts(out);
                        break;
                    default:
                        throw new EOFException();
                }
            }

        } catch (EOFException e) {
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }

}
