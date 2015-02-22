
import java.io.*;
import java.util.*;                                              
import java.net.*;

public class Packet implements Serializable{
    //information provide when packet is created
	private String content;
	private long time;//unit milliseconds
    private long delay;//unit milliseconds

    //pasred result of packets
    private PacketType type = PacketType.Invalid;
    private int destination = -1;
    private String message = null;
    public static enum PacketType {
        Invalid,
        Message,
        Show,
        Search,
        Insert,
        Update
    };


    //initialize Packet with its content, creation time and delay
    public Packet(String s, long t){
        this.content = s;
        this.time = t;
        parsePacket();
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

    public int getDestination(){
        return destination;
    }

    public String getMessage(){
        return message;
    }

    public void setDelay(long d){
        this.delay = d;
    }
}
