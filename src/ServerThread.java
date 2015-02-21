
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ServerThread implements Runnable{

    private Socket socket; 
    private DataCenter data_center;

    public ServerThread(DataCenter data_center, Socket socket){
        this.data_center = data_center;
        this.socket = socket;
    }

    public void run(){

        while(true){
            try{
                ObjectInputStream obj_is = new ObjectInputStream(socket.getInputStream());
                Packet recv_packet = (Packet)obj_is.readObject();
                if(recv_packet!=null){
                    processPacket(recv_packet);
                }
            } catch(IOException e){
                System.err.println(e);
                System.exit(1);
            } catch(ClassNotFoundException e){
                System.err.println(e);
                System.exit(1);
           }
        }//end of inifinite loop
   
    }

    private void processPacket(Packet packet){
        switch(packet.getType()){
            case Message:
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();
                System.out.println("Received \""+packet.getMessage()
                        +"\" from "+(packet.getDestination()+'A')
                        +", Max delay is "+data_center.getMaxDelay(
                            data_center.getId(), packet.getDestination())
                        +" s, system time is "+df.format(dateobj));
                break;
            default:
                System.out.println("Can't recognize the packet.");
        }
    }

}
