import java.io.*;
import java.net.*;

public class SendMsg {
    public Socket s;
    public SendMsg(Socket t)
    {
        this.s = t;
    }
    public Message recieve() throws IOException, ClassNotFoundException
    {
        ObjectInputStream obj = new ObjectInputStream(s.getInputStream());
        Message msg = (Message)obj.readObject();
        System.out.println(" Incoming Message ");
        return msg;
    }
    public void send(Message Msg) throws IOException
    {
        ObjectOutputStream obj = new ObjectOutputStream(s.getOutputStream());
        obj.writeObject(Msg);
        System.out.println(" Outgoing Message ");
        obj.flush();
    }
}
