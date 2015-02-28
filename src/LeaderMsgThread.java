
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class LeaderMsgThread implements Runnable{

    protected LeaderDataCenter data_center;

    private ObjectOutputStream obj_os[] = new ObjectOutputStream[DataCenter.TOTAL_NUM];

    public LeaderMsgThread(LeaderDataCenter data_center){
        this.data_center = data_center;
    }

    private void initializeOutput() throws IOException{
        for(int i=0; i<DataCenter.TOTAL_NUM; i++){
            OutputStream os = data_center.getSocket(i).getOutputStream();
            obj_os[i] = new ObjectOutputStream(os);
        }
    }

    public void run(){
        
        System.out.println("Starting Leader Message Thread...");

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

                //send the message
                try{
                    sendPacket(packet);
                } catch(IOException e){
                    System.err.println("error in send packet.");
                    System.err.println(e);
                }

                //loop waiting for receive all ack packets
                while(data_center.getAckPacket()==null) ;

                try{
                    sendPacket(data_center.getAckPacket());
                    data_center.resetAckPacket();
                } catch(IOException e){
                    System.err.println("error in send packet.");
                    System.err.println(e);
               }
            }

        }//end of infinite loop
   
    }// end of run()

    private void sendPacket(Packet packet) throws IOException
    {
        switch(packet.getType()){
            case Insert:
            case Update:
                for(int i=0; i<DataCenter.TOTAL_NUM; i++){
                    obj_os[i].writeObject(packet);
                }
                break;
            case Ack:
                obj_os[packet.getDestination()].writeObject(packet);
                break;
            default:
                System.out.println("Can't recognize packet.");
        }

    }



   
}
