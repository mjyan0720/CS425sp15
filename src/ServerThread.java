
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ServerThread implements Runnable{

    protected Socket socket; 
    protected int target;
    protected DataCenter data_center;

    public ServerThread(DataCenter data_center, int target){
        this.target = target == DataCenter.TOTAL_NUM ? data_center.getId() : target;
        this.data_center = data_center;
        this.socket = data_center.getSocket(target);
    }

    public ServerThread(DataCenter data_center, Socket s){
        this.data_center = data_center;
        this.socket = s;
        this.target = -1;
    }

    public void run(){

        System.out.println("Starting Server Thread listen to message from "+(char)(target+'A'));
        try{
            ObjectInputStream obj_is = new ObjectInputStream(socket.getInputStream());
 
            while(true){
                //blocking to read from the socket
                Packet recv_packet = (Packet)obj_is.readObject();
                if(recv_packet!=null){
                    processPacket(recv_packet);
                }
            }//end of infinite loop

        }catch(IOException e){
            System.err.println(e);
            System.exit(1);
        } catch(ClassNotFoundException e){
            System.err.println(e);
            System.exit(1);
        }// end of catch class not found exception
          
    }

    protected void processPacket(Packet packet){
        switch(packet.getType()){
            case Message:
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
                Date dateobj = new Date();
                System.out.println("Received \""+packet.getMessage()
                        +"\" from "+(char)(target+'A')
                        +", Max delay is "+data_center.getMaxDelay(
                            data_center.getId(), packet.getDestination())
                        +" s, system time is "+df.format(dateobj));
                break;
            default:
                System.out.println("Can't recognize the packet.");
        }
    }

}
