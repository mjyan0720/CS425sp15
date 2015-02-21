import java.io.*;
import java.util.*;                                              
import java.net.*;

public class Packet{
	private String content;
	private PacketType type;
    private long time;//unit seconds
    private long delay;//unit seconds
    private int destination;
    private String message;
    public enum PacketType {
        Invalid,
        Message,
        Show,
        Search,
        Insert,
        Update
    };


    //initialize Packet with its content, creation time and delay
    public Packet(String s, long t, long d){
        this.content = s;
        this.time = t;
        this.delay = d;
        parsePacket();
    }

    //parse the type of the packet
    private void parsePacket(){

        //check whether it's a send message packet or not
        if(content.length()>4){
            String cmd = content.substring(0, 4);
            if(cmd == new String("Send") ){
                this.type = PacketType.Message;
                this.destination = content.charAt(content.length()-1) - 'A';
                if(this.destination < 4 & this.destination >= 0){
                    System.out.println("Invalid Destination");
                }
                this.message = content.substring(5, content.length()-2);
                return;
            }
        }

        System.out.println("Invalid Command");
        this.type = PacketType.Invalid;

    }
}
