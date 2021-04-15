import java.util.ArrayList;

public class Message implements java.io.Serializable
{
    int selfId;
    String type;
    String filename;
    ArrayList<String> filepaths = new ArrayList<String>();
    ArrayList<String> filenames = new ArrayList<String>();
    int timestamp;
    public Message(int s)
    {
        this.selfId = s;
    }

    public Message(String type, String filename, int selfId, int timestamp)
    {
        this.type = type;
        this.filename = filename;
        this.selfId = selfId;
        this.timestamp = timestamp;
    }

    public Message(String type, ArrayList<String> filenames, ArrayList<String> filepaths, int selfId, int timestamp)
    {
        this.type = type;
        this.filepaths = filepaths;
        this.filenames = filenames;
        this.selfId = selfId;
        this.timestamp = timestamp;
    }
}
