import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

class Contact {
    private String name;
    private int age;
    private long phoneNumber;
    private String company;     
    private ArrayList<String> emails;

    public Contact (String name, int age, long phoneNumber, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String name(){ 
        return name; 
    }
    public int age(){ 
        return age; 
    }
    public long phoneNumber(){ 
        return phoneNumber; 
    }
    public String company(){ 
        return company; 
    }
    public List<String> emails() { 
        return new ArrayList<>(emails); 
    }

    //Guarda contact em binário
    public void serialize (DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeInt(age);
        out.writeLong(phoneNumber);
        if(company != null) {
            out.writeBoolean(true);
            out.writeUTF(company);
        }
        else
            out.writeBoolean(false);
        out.writeInt(emails.size()); //tamanho para arraylist emails
        for(String email : emails)
            out.writeUTF(email);
        }

    //Cria contact através de ficheiro binário
    public static Contact deserialize (DataInputStream in) throws IOException {
        String name = in.readUTF();
        int age = in.readInt();
        long phoneNumber = in.readLong();
        boolean existeCompany = in.readBoolean();
        String company;
        if(existeCompany)
            company = in.readUTF();
        else
            company = null;
        int emailsSize = in.readInt();
        List<String> emails = new ArrayList<>(emailsSize);
        for(int i = 0; i < emailsSize; i++){
            String email = in.readUTF();
            emails.add(email);
        }
        return new Contact (name,age,phoneNumber,company,emails);

    }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append(this.emails.toString());
        builder.append("}");
        return builder.toString();
    }

}
