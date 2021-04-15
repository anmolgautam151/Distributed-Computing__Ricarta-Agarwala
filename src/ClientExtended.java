import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.util.*;
import java.net.*;

public class ClientExtended {

    public static ArrayList<String> filenames = new ArrayList<String>();
    public static ArrayList<String> filepaths = new ArrayList<String>();
    public static HashMap<String, String> filetokens = new HashMap<String, String>();
    public static HashMap<String, String> filecs = new HashMap<String, String>();
    public static String[] patharray = {"D1/", "D2/", "D3/"};
    //public static String[] patharray = {"/home/013/a/ax/axg190014/aos2/src/D1/", "/home/013/a/ax/axg190014/aos2/src/D2/", "/home/013/a/ax/axg190014/aos2/src/D3/"};

    String defaultpath = "/home/013/a/ax/axg190014/aos2/src/";
    //String defaultpath = "/home/anmol/aos2/src/";

    //public static String[] patharray = {"/home/anmol/aos2/src/D1/", "/home/anmol/aos2/src/D2/", "/home/anmol/aos2/src/D3/"};
    int tempt = 0;
    public int acknow = 0;
    private final List<Integer> syncList = new ArrayList<>();
    int selfId;
    int return_key_flag = 0;
    int timesClient = 0;
    int[] delay = {300, 500, 700, 800, 1100};

    public ClientExtended(int selfId) ////Constructor for ClientExtended
    {
        this.selfId = selfId;
    }

    public void  clienthandler(Message msgrec)  ////Handles the incoming messages provided by listener
    {
        if(msgrec.type.equals("Allowed")) ////Handles the allowed message from server to enter CS
        {
            int temp = acknow + 1;
            System.out.println("Allowed : " + temp);
            acknow = acknow+1;
            timesClient = Math.max(timesClient,(msgrec.timestamp+1));
            //System.out.println("TimeStamp : "+ timesClient);
            String t = "!";
            tempt = 1;
            synchronized (syncList)
            {
                if(acknow == 3)
                {
                    syncList.add(1);
                    syncList.notifyAll(); ///Notifies when keys recieved
                }
            }

        }
        else if(msgrec.type.equals("AllowedRet"))///Handles AllowedRet message from server which sends back the key after exiting CS
        {
            int temp = acknow + 1;
            timesClient = Math.max(timesClient,(msgrec.timestamp+1));
            System.out.println("AllowedRet : "+ temp);
            //System.out.println("TimeStamp : "+ timesClient);
            acknow = acknow+1;
            tempt = 1;
            String t = "!";
            synchronized (syncList)
            {
                if(acknow == 3)
                {
                syncList.add(1);
                syncList.notifyAll();  ////Notifies wait when keys recieved
                }
            }


            return_key_flag = 1;
        }
        else if(msgrec.type.equals("Queried")) ///Handles Queried message which also has the name of the available files
        {
            System.out.println("Queried from Server : "+ msgrec.selfId);
            filenames = msgrec.filenames;
            filepaths = msgrec.filepaths;
            timesClient = Math.max(timesClient,(msgrec.timestamp+1));
            //System.out.println("TimeStamp : "+ timesClient);
            for(int i=0; i<filenames.size();i++)
            {
                filetokens.put(filenames.get(i), "F");
                filecs.put(filenames.get(i), "F");
            }
            acknow = acknow+1;
            synchronized (syncList)
            {
                if(acknow == 1)
                {
                    syncList.add(1);
                    syncList.notifyAll();
                }
            }
        }
        else if(msgrec.type.equals("Request")) ////Handles Request Message  from Server for the keys
        {
            System.out.println("Server Requesting for key : "+msgrec.selfId);
            timesClient = Math.max(timesClient,(msgrec.timestamp+1));
            //System.out.println("TimeStamp : "+ timesClient);
            while(filecs.get(msgrec.filename).equals("T"))
            {
                /////do nothing
            }
            filetokens.put(msgrec.filename, "F");
            returnkeys(msgrec.filename, msgrec.selfId);
        }
    }

    public void returnkeys(String filename, int server_number) //Returns key of a file to a server
    {
        timesClient++;
        Message msg = new Message("Release", filename, selfId, timesClient);
        Socket servertosend = Comm.socketlist.get(server_number);
        SendMsg sendingobj = new SendMsg(servertosend);
        try
        {
            sendingobj.send(msg);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    public int generateRand(int min, int max) ////Random Number Generator between a range
    {
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    public void generateReq() throws IOException     ///Generates Request to server
    {
        int rand = 0;
        for(int i=0; i<5; i++)
        {
            try
            {
                Thread.sleep(((selfId+1)*delay[selfId])); ///Sleep before next request is generated
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            if(i == 0)
            {
                System.out.println("Starting Enquiry :" + i);
                timesClient++;
                //System.out.println("TimeStamp : "+ timesClient);

                ///Sends Message for Enquiry
                Message msg = new Message("Enquiry", "None", selfId, timesClient);

                rand = generateRand(0,Comm.socketlist.size());
                Socket servertosend = Comm.socketlist.get(rand);
                //System.out.println("TimeStamp + TO : "+ timesClient + "  " +  rand);
                SendMsg sendingobj = new SendMsg(servertosend);

                try
                {
                    sendingobj.send(msg);
                }
                catch(IOException e)
                {
                    System.out.println(e);
                }

                ///waits till reply recieved from server
                synchronized (syncList) {
                    while(syncList.isEmpty()) {
                        try {
                            syncList.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                syncList.remove(0);
                acknow = 0;
                System.out.println("Finished Enquiry");
            }
            else
            {
                int randaction = generateRand(1,2);
                int randfile = generateRand(0, (filenames.size()));
                rand = generateRand(0,2);
                String tempfile = filenames.get(randfile);
                if(i%2 == 0) //////////READ
                {
                    System.out.println("Inside Read :"+ i);
                    boolean flag = false;
                    if(filetokens.get(tempfile).equals("F"))////If does not have key then request for key
                    {
                        System.out.println("DONT HAVE KEYS TO READ : "+ tempfile);

                        timesClient++;

                        ////Request keys for Read
                        Message msg = new Message("Read", tempfile, selfId,timesClient);
                        //System.out.println("TimeStamp + TO : "+ timesClient + "  ALL" );
                        for(int j=0; j<Comm.socketlist.size(); j++)
                        {
                            Socket servertosend = Comm.socketlist.get(j);
                            SendMsg sendingobj = new SendMsg(servertosend);

                            try {
                                sendingobj.send(msg);
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                        }
                        System.out.println(Comm.socketlist.size());

                        ////Waits till keys are recieved
                        synchronized (syncList) {
                            while(syncList.isEmpty()) {
                                try {
                                    syncList.wait();

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        syncList.remove(0);
                        System.out.println("ALL KEYS RECIEVED");
                        acknow = 0;
                        filetokens.put(tempfile, "T");
                    }
                    //syncList.remove(0);
                    ///Critical Section for Read
                    //if(tempt == 0){timesClient++;}
                    timesClient++;
                    tempt = 0;
                    System.out.println("Reading now : " + tempfile);
                    //System.out.println("TimeStamp : "+ timesClient);
                    filecs.put(tempfile, "T");
                    read(randfile);
                    filecs.put(tempfile, "F");
                    System.out.println("Finished Read");

                }
                else ////////////////////WRITE
                {
                    System.out.println("Inside Write : "+ i);
                    if(filetokens.get(tempfile).equals("F"))////If does not have keys then request for keys
                    {
                        System.out.println("DONT HAVE KEYS TO WRITE : "+ tempfile);
                        timesClient++;
                        //System.out.println("TimeStamp : "+ timesClient);
                        ///Requests for keys
                        Message msg = new Message("Write", tempfile, selfId, timesClient);
                        //System.out.println("TimeStamp + TO : "+ timesClient + "  ALL" );
                        for(int j=0; j<Comm.socketlist.size(); j++)
                        {
                            Socket servertosend = Comm.socketlist.get(j);
                            SendMsg sendingobj = new SendMsg(servertosend);

                            try {
                                sendingobj.send(msg);
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                        }
                        System.out.println(Comm.socketlist.size());

                        ////Waits till all keys are recieved
                        synchronized (syncList) {
                            while(syncList.isEmpty()) {
                                try {
                                    syncList.wait();

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        syncList.remove(0);
                        System.out.println("ALL KEYS RECIEVED");
                        acknow = 0;
                        filetokens.put(tempfile, "T");
                    }
                    ///Critical Section for Write
                    //if(tempt == 0){timesClient++;}
                    timesClient++;
                    tempt = 0;
                    System.out.println("Writing now : " + tempfile);
                    //System.out.println("TimeStamp : "+ timesClient);
                    filecs.put(tempfile, "T");
                    write(randfile);
                    filecs.put(tempfile, "F");
                    System.out.println("Finished Write");
                }

                if(return_key_flag == 1) ///If AllowedRet was recieved then keys are sent after Critical Section here
                {
                    System.out.println("Returning Keys");
                    for(int j=0; j<Comm.socketlist.size();j++) {
                        returnkeys(tempfile, j);
                    }
                    return_key_flag = 0;
                }
            }
        }
    }

    public void write(int index) throws IOException  ////Writes in the files
    {
        for(int x=0; x<patharray.length; x++)
        {
            FileWriter ofile = new FileWriter(defaultpath+patharray[x]+filenames.get(index), true);
            String towrite = "Machine ID : " + String.valueOf(selfId) +" Timestamp : "+ String.valueOf(timesClient) + "\n";
            ofile.write(towrite);
            ofile.close();
        }

    }
    public void read(int index) throws IOException   ////Reads from a file
    {
        String lastLine = "";
        String line;
        BufferedReader input = new BufferedReader(new FileReader(defaultpath+patharray[index]+filenames.get(index)));

        while ((line = input.readLine()) != null) {
            lastLine = line;
        }
        System.out.println("Last Line -> " + lastLine);
        input.close();

    }
}
