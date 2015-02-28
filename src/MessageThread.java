
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class MessageThread implements Runnable{

    protected DataCenter data_center;

    private ObjectOutputStream obj_os[] = new ObjectOutputStream[DataCenter.TOTAL_NUM];
    public MessageThread(DataCenter data_center){
        this.data_center = data_center;
    }

    public void initializeOutput() throws IOException{
        for(int i=0; i<DataCenter.TOTAL_NUM; i++){
            //if(i!=data_center.getId()){
            OutputStream os = data_center.getSocket(i).getOutputStream();
            obj_os[i] = new ObjectOutputStream(os);
            //}
        }
    }

    public void run(){
        
        System.out.println("Starting Message Thread...");

        try{
            initializeOutput();
        } catch(IOException e){
            System.err.println("error occur when initialize all object output stream");
            System.err.println(e);
        }

        while( true){

            //get the top packet of the queue
            Packet packet = data_center.getMessage();
            if( packet != null ){

                long current_time = System.currentTimeMillis();
                long send_time = packet.getSendTime();

                //check whether it's the time to send the message
                //if not, sleep for required length of time
                if(send_time > current_time){
                    try{
                        Thread.sleep(send_time - current_time);
                    } catch(InterruptedException e){
                        System.err.println("In message thread, message delay is interrupted.");
                        System.err.println(e);
                    }
                }

                //send the message
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
        if(packet.getDestination()<0 || packet.getDestination() >= DataCenter.TOTAL_NUM){
            System.out.println("A packet with invalid desitination "+packet.getDestination()+", drop it!");
            return;
        }
/*        if(packet.getDestination()==data_center.getId()){
            System.out.println("Send packet to myself. This case is not handled yet. Drop packet.");
            return;
        }
 */       //if the destination is valid, just send it
        obj_os[packet.getDestination()].writeObject(packet);

    }



}
