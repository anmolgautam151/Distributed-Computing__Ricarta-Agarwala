import java.io.*;
import java.util.*;
import java.net.*;
public class Comm {

    public static ArrayList<Socket> socketlist = new ArrayList<Socket>();
    //public static int[] serverports = {5001, 5002, 5003};
    public static int[] serverports = new int[3];
    //public static String[] servernames = {"dc01","dc02","dc03"};
    //public static String[] servernames = {"localhost","localhost","localhost"};
    public static String[] servernames = new String[3];
    public static HashMap<Integer, Socket> socket_node_map = new HashMap<Integer, Socket>();
    ServerExtended handlerobj = null;
    ClientExtended chandler = null;
    public static String type;
    public Comm(int no_servers, int no_clients, int machineId, int portId, String host1, String host2, int port2, String host3, int port3)
    {
        if (no_servers == 1) ////If the machine is a server
        {
            socketlist = startserver(portId);
            servernames[0] = host1;
            type = "Server";
        }
        else if (no_clients == 1) /////If the machine is a client
        {
            servernames[0] = host1;
            servernames[1] = host2;
            servernames[2] = host3;
            serverports[0] = portId;
            serverports[1] = port2;
            serverports[2] = port3;
            socketlist = startclient();
            type = "Client";
        }

        /////Create hasmap of sockets with their respective Ids
        for (int i = 0; i < socketlist.size(); i++) {
            Socket temp = socketlist.get(i);
            Message mobj = new Message(machineId);
            int recieveId = 0;
            SendMsg msg_obj = new SendMsg(temp);
            try {
                msg_obj.send(mobj);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Message temp_rmsg = msg_obj.recieve();
                recieveId = temp_rmsg.selfId;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            socket_node_map.put(recieveId, temp);
        }

        ////Fix ordering of sockets for future reference
        for (int i = 0; i < socketlist.size(); i++)
        {
            socketlist.set(i, socket_node_map.get(i));
        }

        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
    }

    public static ArrayList<Socket> startclient() //////Connect Client to all the servers
    {
        ArrayList<Socket> clients = new ArrayList<Socket>(3);

        for (int i = 0; i<3; i++) {
            Socket each_client = null;
            try {
                each_client = Client.enclient(servernames[i], serverports[i]);
            } catch (Exception e) {
                System.out.println(e);
            }
            clients.add(each_client);
        }
        return clients;
    }

    public static ArrayList<Socket> startserver(int port_number)///// Start server
    {
        ArrayList<Socket> server_sockets = new ArrayList<Socket>(5);
        server_sockets = Server.startServer(port_number);
        return server_sockets;
    }

    public void handler(ServerExtended obj) ////Handler for Server
    {
        this.handlerobj = obj;
        for(int i =0; i<socketlist.size(); i++)
        {
            Thread t = new Listener(socketlist.get(i), obj, chandler, type);
            t.start();
        }

    }

    public void clienthandler(ClientExtended obj)  /////Handler for Client
    {
        this.chandler = obj;
        for(int i =0; i<socketlist.size(); i++)
        {
            Thread t = new Listener(socketlist.get(i), handlerobj, obj, type);
            t.start();
        }
    }
}
