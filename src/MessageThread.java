
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class MessageThread implements Runnable{

    private DataCenter data_center;

    public MessageThread(DataCenter data_center){
        this.data_center = data_center;
    }

    public void run(){

        while( true){

            //get the top packet of the queue
            Packet packet = data_center.getMessage();
            if( packet != null ){

                long current_time = System.currentTimeMillis();
                long send_time = packet.getSendTime();

                printPacket(packet);

                if(send_time > current_time){
                    try{
                        Thread.sleep(send_time - current_time);
                    } catch(InterruptedException e){
                        System.err.println("In message thread, message delay is interrupted.");
                        System.err.println(e);
                    }
                }

                try{
                    sendPacket(packet);
                } catch(IOException e){
                    System.err.println("error in send packet.");
                    System.err.println(e);
                }
            }

        }//end of infinite loop
   
    }// end of run()

    private void sendPacket(Packet packet) throws IOException
    {

        OutputStream os = data_center.getSocket(packet.getDestination()).getOutputStream();
        ObjectOutputStream obj_os = new ObjectOutputStream(os);
        obj_os.writeObject(packet);
        obj_os.close();
        os.close();

    }


    private void printPacket(Packet packet){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();

        switch(packet.getType()){
            case Message:
                System.out.println("Sent \""+packet.getMessage()
                        +"\" to "+(packet.getDestination()+'A')
                        +", system time is "+df.format(dateobj));
                break;
            default:
                System.out.println("Can't recognize the packet.");
        }
    }

}
