import java.net.*;
public class Client {
    public static Socket enclient(String ipadd, int port) throws InterruptedException
    {
        Socket s1 = null;
        while(s1 == null)
        {
            try
            {
                InetAddress ip = InetAddress.getByName(ipadd);
                s1 = new Socket(ip, port);
                System.out.println(" Connected to : " + ipadd+ " : " + port);
            }
            catch(Exception e)
            {
                s1 = null;
                Thread.sleep(1000);
            }
        }
        return s1;
    }
}
