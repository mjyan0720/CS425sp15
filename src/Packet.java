
import java.io.*;
import java.util.*;                                              
import java.net.*;

public class Packet implements Serializable{
    //information provide when packet is created
	private String content;
	private long time;//unit milliseconds
    private long id;
    private long delay;//unit milliseconds

    private int key = -1;
    private int value = -1;
    //default model is 1, used for search and delete
    private int model = 1;
    private Content value_timestamp;

    //pasred result of packets
    private PacketType type = PacketType.Invalid;
    //default destination is leader
    private int destination = DataCenter.TOTAL_NUM;
    //invalid to uninitialize source
    private int source = -1;
    private String message = null;

    public static enum PacketType {
        Invalid,
        Message,
        Search,
        Insert,
        Update,
        Delete,
        Get,
        Ack,
        GetAck,
        SearchAck,
        Delay
    };

	public Packet(){
	}
    
    public Packet(Packet p){
        this.content = p.content;
        this.time = p.time;
        this.delay = p.delay;
        this.value = p.value;
        this.key = p.key;
        this.model = p.model;
        this.value_timestamp = p.value_timestamp;
        this.type = p.type;
        this.message = p.message;
        this.destination = p.destination;
        this.source = p.source;
        this.id = p.id;
    }
    
    //initialize Packet with its content, creation time and delay
    public Packet(String s, long t){
        this.content = s;
        this.time = t;
        parsePacket();
    }

    public Packet(String s, long t, int source){
        this.content = s;
        this.time = t;
        this.source = source;
        parsePacket();
    }

    public void setKey(int key){
        this.key = key;
    }

    public Content getValueTimestamp(){
        return value_timestamp;
    }

    public void setValueTimestamp(Content c){
        this.value_timestamp = c;
    }

	// Constructor, mainly used for Ack message
	public Packet(String s, long t, int source, int des, int model){
		this.content = s;
		this.time = t;
		this.source = source;
		this.destination = des;
        this.model = model;
        parsePacket();
	}

    //parse the type of the packet
    private void parsePacket(){

        //check whether it's a send message packet or not
        if(content.length()>4){
            String cmd = content.substring(0, 4);
            if(cmd.equalsIgnoreCase(new String("Send") )){
                //expected format:
                //Send xxxx to A
                //4, ,?, 2, ,1
                this.type = PacketType.Message;
                this.destination = content.charAt(content.length()-1) - 'A';
                //check whether the desitnation is within the range
                if(this.destination >= DataCenter.TOTAL_NUM ||  this.destination < 0){
                    System.out.println("Invalid Destination!!");
                }
                this.message = content.substring(5, content.length()-2);
                return;
            }
        }

        String str[] = content.split(" ");

        if(str[0].equals(new String("insert"))){
            this.type = PacketType.Insert;
            this.key = Integer.parseInt(str[1]);
            this.value = Integer.parseInt(str[2]);
            this.model = Integer.parseInt(str[3]);
            if(this.model == 1 || this.model == 2){
                this.destination = DataCenter.TOTAL_NUM;
            }
            return;
        } else if(str[0].equals(new String("update"))){
            this.type = PacketType.Update;
            this.key = Integer.parseInt(str[1]);
            this.value = Integer.parseInt(str[2]);
            this.model = Integer.parseInt(str[3]);
            if(this.model == 1 || this.model == 2){
                this.destination = DataCenter.TOTAL_NUM;
            }
            return;
        } else if(str[0].equals(new String("delete"))){
            this.type = PacketType.Delete;
            this.key = Integer.parseInt(str[1]);
            return;
        } else if(str[0].equals(new String("get"))){
            this.type = PacketType.Get;
            this.key = Integer.parseInt(str[1]);
            this.model = Integer.parseInt(str[2]);
            if(this.model == 1 || this.model == 2){
                this.destination = DataCenter.TOTAL_NUM;
            }
            return;
        } else if(str[0].equals(new String("search"))){
            this.type = PacketType.Search;
            this.key = Integer.parseInt(str[1]);
            return;
        } else if(content.equals(new String("ACK"))){
            this.type = PacketType.Ack;
            return;
        } else if(str[0].equals(new String("delay"))){
            this.type = PacketType.Delay;
            this.delay = Integer.parseInt(str[1])*1000;
            return;
       }

        System.out.println("Invalid Command");
        this.type = PacketType.Invalid;

    }

    public PacketType getType(){
        return type;
    }

    public void setType(PacketType type){
        this.type = type;
    }

    public long getDelay(){
        return delay;
    }
    //the send time is in milliseconds
    //use it to compare with System.currentTimeMillis()
    public long getSendTime(){
        return time+delay;
    }

    public void setContent(String s){
        this.content = s;
    }


    public String getContent(){
        return content;
    }

    public int getSource(){
        return source;
    }

    public void setSource(int s){
        this.source = s;
    }

    public void setDestination(int dest){
        this.destination = dest;
    }

    public int getDestination(){
        return destination;
    }

    public String getMessage(){
        return message;
    }

    public void setDelay(long d){
        this.delay = d;
    }

    public int getKey(){
        return key;
    }

    public int getValue(){
        return value;
    }

    public int getModel(){
        return model;
    }

    public void setTimestamp(long t){
        this.time = t;
    }

    public long getTimestamp(){
        return time;
    }

    public void setId(long t){
        this.id = t;
    }

    public long getId(){
        return id;
    }
}
