
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class LeaderServerThread extends ServerThread {

    public LeaderServerThread(LeaderDataCenter data_center, int target){
        super(data_center, target);
    }

    @Override
    protected void processPacket(Packet packet){

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
        Date dateobj = new Date();

        switch(packet.getType()){
            case Insert:
            case Update:
            case Delete:
            case Get:
            case Search:
                //put the packet into the queue
                System.out.println("Received \""+packet.getContent()
                        +"\" from "+(char)(target+'A')
                        +", Max delay is "+data_center.getMaxDelay()
                        +" s, system time is "+df.format(dateobj)
                        +"\n Put it into queue.");
                data_center.insertMessage(packet);
                break;
            case Ack:
            case GetAck:
                {
                //don't need to go to the queue
                //just send the ack back to source
                if(! (data_center instanceof LeaderDataCenter))
                    System.err.println("Can't use LeaderSeverThread from non-LeaderDataCenter class");
                System.out.println("Received \"ACK\" from "+(char)(target+'A')
                        +", Max delay is "+data_center.getMaxDelay()
                        +" s, system time is "+df.format(dateobj));
                LeaderDataCenter leader_data_center = (LeaderDataCenter)data_center;
                leader_data_center.setAckPacket(packet);
                break;
                }
            case SearchAck:
                {
                //don't need to go to the queue
                //just send the ack back to source
                if(! (data_center instanceof LeaderDataCenter))
                    System.err.println("Can't use LeaderSeverThread from non-LeaderDataCenter class");
                System.out.println("Received \"SearchAck\" from "+(char)(target+'A')
                        +", Max delay is "+data_center.getMaxDelay()
                        +" s, system time is "+df.format(dateobj));
                LeaderDataCenter leader_data_center = (LeaderDataCenter)data_center;
                leader_data_center.setSearchAckPacket(packet, target);
                break;
                }
            default:
                System.out.println("Can't recognize the packet. ->"+packet.getContent());
        }
    }



}
