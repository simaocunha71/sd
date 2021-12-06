package ficha7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class ContactList extends ArrayList<Contact> {


    public void serialize (DataOutputStream out) throws IOException {
        out.writeInt(this.size());
        for(Contact i : this)
            i.serialize(out);
    }

    public static ContactList deserialize (DataInputStream in) throws IOException {
        ContactList cl = new ContactList();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            cl.add(Contact.deserialize(in));
        }
        return cl;
    }

}
