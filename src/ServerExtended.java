import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerExtended {

    Comm objcomm;
    int timestamp = 0;
    int reqsgenerated;
    public static String[] filekeys = {"T", "T"};
    public static String[] filenames = {"F1.txt", "F2.txt"};
    public static int[] filelentto = {99,99,99,99};
    public int selfId;
    public static HashMap<String, ArrayList<Integer>> waiting_queue = new HashMap<String, ArrayList<Integer>>();
    public static HashMap<Integer, Integer> pid_tmestamp = new HashMap<Integer, Integer>();
    String defaultpath = "/home/013/a/ax/axg190014/aos2/src/";
    //String defaultpath = "/home/anmol/aos2/src/";
    String[] machinedir = {"D1", "D2", "D3"};

    public ServerExtended(Comm comobj, int selfId) // Constructor for ServerExtended
    {
        this.objcomm = comobj;
        this.timestamp = 0;
        this.reqsgenerated = 0;
        this.selfId = selfId;
    }

    public static int findIndex(String arr[], String t) // Used to find index of a string in an array
    {
        int index = Arrays.binarySearch(arr, t);
        return (index < 0) ? -1 : index;
    }

    public void serverreciever(Message msgrec) //Recieves message from listener and handles it
    {
        if(msgrec.type.equals("Release"))
        {

            timestamp = Math.max(timestamp,(msgrec.timestamp+1));
            System.out.println("Key recieved from Client : "+msgrec.selfId);
            int index = findIndex(filenames, msgrec.filename);
            filekeys[index] = "T";
            filelentto[index] = 99;
            int index_to_remove=0;

            ///Checks if there is any other processes request for the same file
            if(!waiting_queue.isEmpty() && waiting_queue.containsKey(msgrec.filename))
            {
                ArrayList<Integer> temp = waiting_queue.get(msgrec.filename);
                if(temp.size() > 0)
                {
                    System.out.println("Tending to Queued Requests");
                    int pid_to_send = 0;
                    int min_time = 1000;

                    ///Iterates through the request to reply to the one with lower timestamp
                    for (int i = 0; i < temp.size(); i++)
                    {
                        if (pid_tmestamp.get(temp.get(i)) < min_time)
                        {
                            min_time = pid_tmestamp.get(temp.get(i));
                            pid_to_send = temp.get(i);
                            index_to_remove = i;
                        }
                        else if (pid_tmestamp.get(temp.get(i)) == min_time)
                        {
                            if (temp.get(i) < pid_to_send) {
                                min_time = pid_tmestamp.get(temp.get(i));
                                pid_to_send = temp.get(i);
                                index_to_remove = i;
                            }
                        }
                    }

                    ////Removes the processes req from the queue
                    temp.remove(index_to_remove);
                    waiting_queue.put(msgrec.filename, temp);
                    pid_tmestamp.remove(pid_to_send);

                    timestamp++;
                    Message msg;
                    //System.out.println("Msg to queued process : "+ timestamp);
                    ////Allows the client to enter CS and piggybacks if there is another req and the key needs to be returned
                    if (waiting_queue.get(msgrec.filename).size() > 0) {
                        msg = new Message("AllowedRet", "None", selfId, timestamp);
                    } else {
                        msg = new Message("Allowed", "None", selfId, timestamp);
                    }

                    filekeys[index] = "F";
                    filelentto[index] = pid_to_send;

                    Socket servertosend = Comm.socketlist.get(pid_to_send);
                    SendMsg sendingobj = new SendMsg(servertosend);
                    try
                    {
                        sendingobj.send(msg);
                    }
                    catch (IOException e)
                    {
                        System.out.println(e);
                    }
                }

            }
        }
        else /// Sends the message further to reptoReq to handle the other types of request
        {
            reptoReq(msgrec);
        }
    }
    public void reptoReq(Message msgrec) //Handles Enquiry and Read/Write requests
    {
        if(msgrec.type.equals("Enquiry")) ///Handles enquiry request from clients
        {
            System.out.println("Inside Server-Enquiry : Sending File Names to Client : " + msgrec.selfId);
            timestamp = Math.max(timestamp,(msgrec.timestamp+1));
            //System.out.println("Timestamp : " + timestamp);
            String[] pathnames;
            ArrayList<String> retfilepaths = new ArrayList<String>();
            ArrayList<String> retfilenames = new ArrayList<String>();
            //File f = new File("/home/013/a/ax/axg190014/aos2/src/D1");
            String temppath = defaultpath + machinedir[selfId];
            File f = new File(temppath);
            pathnames = f.list();

            ///makes a list of files that are available
            for (String pathname : pathnames)
            {
                retfilepaths.add("./D1" + pathname);
                retfilenames.add(pathname);
                System.out.println("FILE : " + pathname);
            }

            ///Sends the list of files  back to the client
            timestamp++;
            //System.out.println("Timestamp : " + timestamp);
            Message msg = new Message("Queried", retfilenames, retfilepaths, selfId, timestamp);
            Socket servertosend = Comm.socketlist.get(msgrec.selfId);
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

        else if(msgrec.type.equals("Read") || msgrec.type.equals("Write")) /// Handles the Read/Write Requests
        {
            System.out.println("Request from Client : " + msgrec.selfId);
            int index = findIndex(filenames, msgrec.filename);
            timestamp = Math.max(timestamp,(msgrec.timestamp+1));
            //System.out.println("Timestamp : " + timestamp);
            if(filekeys[index].equals("T")) ////If the key is available then provide the key
            {
                System.out.println("Key found : Sending Key : "+ msgrec.selfId);

                ///updates the keys to keep track which process has the key
                filelentto[index] = msgrec.selfId;
                filekeys[index] = "F";

                ///Sends the key to the process
                timestamp++;
                //System.out.println("Timestamp : " + timestamp);
                Message msg = new Message("Allowed", "None", selfId, timestamp);
                Socket servertosend = Comm.socketlist.get(msgrec.selfId);
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

            else  ///If the key is not available then req the key from the process that have it and put the req in the queue
            {
                System.out.println("Key not found : Queuing Request of Client: " + msgrec.selfId);
                System.out.println("Requesting for Key from Client : " + filelentto[index]);

                ///Entering request in the queue before requesting for key
                if(waiting_queue.containsKey(msgrec.filename))
                {
                    ArrayList<Integer> temp= waiting_queue.get(msgrec.filename);
                    temp.add(msgrec.selfId);
                    waiting_queue.put(msgrec.filename, temp);
                    pid_tmestamp.put(msgrec.selfId, msgrec.timestamp);
                }
                else
                {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    temp.add(msgrec.selfId);
                    waiting_queue.put(msgrec.filename, temp);
                    pid_tmestamp.put(msgrec.selfId, msgrec.timestamp);
                }

                ///Sends request for the key to the process that has it
                timestamp++;
                //System.out.println("Timestamp : " + timestamp);
                Message msg = new Message("Request", filenames[index], selfId, timestamp);
                Socket servertosend = Comm.socketlist.get(filelentto[index]);
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
        }
    }
}
