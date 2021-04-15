import java.io.*;
import java.util.*;
import java.net.*;
public class Server {
    public static ArrayList<Socket> startServer(int port)
    {
        ArrayList<Socket> servers= new ArrayList<Socket>(5);
        ServerSocket svr = null;
        try
        {
            svr = new ServerSocket(port);
            System.out.println(" Server started at port : "+port);
            try
            {
                System.out.println(" Waiting for Clients to Connect ");
                for (int i=0; i < 5; i++)
                {
                    Socket s = svr.accept();
                    servers.add(s);
                }
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return servers;

    }
}
