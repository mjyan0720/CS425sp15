
import java.io.*;
import java.util.*;                                              
import java.net.*;

public class Packet implements Serializable{
    //information provide when packet is created
	private String content;
	private long time;//unit milliseconds
    private long delay;//unit milliseconds

    private int key = -1;
    private int value = -1;
    private int model = -1;

    //pasred result of packets
    private PacketType type = PacketType.Invalid;
    private int destination = -1;
    private int source = -1;
    private String message = null;
    public static enum PacketType {
        Invalid,
        Message,
        Show,
        Search,
        Insert,
        Update,
        Delete,
        Get,
        Ack
    };


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

	// Constructor, mainly used for Ack message
	public Packet(String s, long t, int source, int des){
		this.content = s;
		this.time = t;
		this.source = source;
		this.destination = des;
	}

    //parse the type of the packet
    private void parsePacket(){

        //check whether it's a send message packet or not
        if(content.length()>4){
            String cmd = content.substring(0, 4);
            if(cmd.equals(new String("Send") )){
                //expected format:
                //Send xxxx to A
                //4, ,?, 2, ,1
                this.type = PacketType.Message;
                this.destination = content.charAt(content.length()-1) - 'A';
                //check whether the desitnation is within the range
                if(this.destination >= DataCenter.TOTAL_NUM ||  this.destination < 0){
                    System.out.println("Invalid Destination!!");
                }
                this.message = content.substring(5, content.length()-5);
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
            return;
        } else if(str[0].equals(new String("show-all"))){
            this.type = PacketType.Show;
            return;
        } else if(str[0].equals(new String("search"))){
            this.type = PacketType.Get;
            this.key = Integer.parseInt(str[1]);
            return;
        }

        System.out.println("Invalid Command");
        this.type = PacketType.Invalid;

    }

    public PacketType getType(){
        return type;
    }

    //the send time is in milliseconds
    //use it to compare with System.currentTimeMillis()
    public long getSendTime(){
        return time+delay;
    }

    public String getContent(){
        return content;
    }

    public int getSource(){
        return source;
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

    public long getTimestamp(){
        return time;
    }
}
