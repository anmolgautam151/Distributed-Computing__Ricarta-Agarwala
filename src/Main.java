import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int is_server = Integer.parseInt(args[0]);
        int is_client = Integer.parseInt(args[1]);
        int machineId = Integer.parseInt(args[2]);
        String hostname1 = String.valueOf(args[3]);
        int portId = Integer.parseInt(args[4]);
        String hostname2 = String.valueOf(args[5]);
        int port2 = Integer.parseInt(args[6]);
        String hostname3 = String.valueOf(args[7]);
        int port3 = Integer.parseInt(args[8]);


        Comm objcomm = new Comm(is_server, is_client, machineId, portId, hostname1, hostname2, port2, hostname3, port3);

        if(is_client > 0)
        {
            System.out.println("CLIENT : "+ machineId);
            ClientExtended client = new ClientExtended(machineId);
            objcomm.clienthandler(client);
            client.generateReq();
        }
        else
        {
            System.out.println("SERVER : "+ machineId);
            ServerExtended server = new ServerExtended(objcomm, machineId);
            objcomm.handler(server);
        }

    }
}
