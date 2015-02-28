import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeMsgThread extends MessageThread{
	private ObjectOutputStream leader_obj;// = new ObjectOutputStream();
	public ModeMsgThread(DataCenter c){
		super(c);
	}
    
	@Override
	private void initializeOutput() throws IOException{
    	OutputStream os = data_center.getLeaderSocket().getOutputStream();
    	leader_obj = new ObjectOutputStream(os);
    }

	@Override
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
			while(!data_center.messageComplete());
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
}
