import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeAckThread implements Runnable{
	ModeDataCenter data_center;
	ModeMsgThread message_thread;
	public ModeAckThread(ModeMsgThread t){
		data_center = t.getDataCenter();
		message_thread = t;
	}
    
    public void run(){
        System.out.println("Starting ACK Thread...");
        while(true){
            //get the top packet of the queue
			sendOneMessage();
        }//end of infinite loop
    }// end of run()

	private void sendOneMessage(){
        Packet packet = data_center.getAckMessage();
        if( packet != null ){
            long current_time = System.currentTimeMillis();
            long send_time = packet.getSendTime();

            //check whether it's the time to send the message
            //if not, sleep for required length of time
            if(send_time > current_time){
                try{
                    Thread.sleep(send_time - current_time);
                } catch(InterruptedException e){
                    System.err.println("In ACK thread, message delay is interrupted.");
                    System.err.println(e);
                }
            }
             //send the message
            try{
                message_thread.sendPacket(packet);
            } catch(IOException e){
                System.err.println("error in send packet.");
                System.err.println(e);
            }
        }
	}
}
