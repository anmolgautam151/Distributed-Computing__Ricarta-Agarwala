import java.io.*;
import java.net.*;

public class Listener extends Thread
{
    Socket Soc_obj;
    ServerExtended msh_hand_obj = null;
    ClientExtended chandler = null;
    String type;

    public Listener(Socket Soc_obj, ServerExtended msh_hand_obj, ClientExtended chandler, String type)
    {
        this.Soc_obj = Soc_obj;
        this.msh_hand_obj = msh_hand_obj;
        this.chandler = chandler;
        this.type = type;
    }

    @Override
    public void run()
    {
        SendMsg soc_msg = new SendMsg(Soc_obj);
        while (true)
        {
            try
            {
                Message listener_msg = soc_msg.recieve();
                //System.out.println(" Handling Message ");
                if(type.equals("Client"))     ////If the machine is a Client, send to Reciever in ClientExtended
                {
                    chandler.clienthandler(listener_msg);
                }
                else                    ////If the machine is a Server, send to Reciever in ServerExtended
                {
                   msh_hand_obj.serverreciever(listener_msg);
                }

            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }

    }
}
