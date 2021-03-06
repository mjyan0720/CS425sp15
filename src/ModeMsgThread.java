import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeMsgThread implements Runnable{
	private ObjectOutputStream leader_obj;// = new ObjectOutputStream();
    private ObjectOutputStream obj_os[] = new ObjectOutputStream[DataCenter.TOTAL_NUM];
	ModeDataCenter data_center;
	public ModeMsgThread(ModeDataCenter c){
		data_center = c;
	}
    
	public void initializeOutput() throws IOException{
    	OutputStream os = data_center.getLeaderSocket().getOutputStream();
    	leader_obj = new ObjectOutputStream(os);
        for(int i=0; i<DataCenter.TOTAL_NUM; i++){
            os = data_center.getSocket(i).getOutputStream();
            obj_os[i] = new ObjectOutputStream(os);
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
		Thread ack_thread = new Thread(new ModeAckThread(this));
		ack_thread.start();
        while(true){
            //get the top packet of the queue
			while(!data_center.messageComplete());
			// If the mode is 3 or 4, continue to send 4 messages 
			sendMessage();
        }//end of infinite loop
    }// end of run()

	private void sendMessage(){
        Packet packet = data_center.getMessage();
        if( packet != null ){
			if(packet.getModel() == 3 || packet.getModel() == 4){
	            int i = 0;	
				// send 4 messages
	    		setPacketVariableInDataCenter(packet);
		    	do{
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
					
                    i += 1;

					while((packet = data_center.getMessage()) == null && i<DataCenter.TOTAL_NUM);

                }while(i < DataCenter.TOTAL_NUM);
			}
			else{
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
					setPacketVariableInDataCenter(packet);
        	        sendPacket(packet);
            	} catch(IOException e){
                	System.err.println("error in send packet.");
	                System.err.println(e);
    	        }
			}
//			System.out.println("After sending message, new ack_num is " + data_center.getMessageAckNum());
        }
	}

	private void setPacketVariableInDataCenter(Packet p){
		int ack_num = 0;
		int model = p.getModel();
		Packet.PacketType type = p.getType();
		switch(type){
			case Search:
			case Delete:
				ack_num = 1;
				break;
			case Insert:
			case Update:
			case Get:
				if(model == 1 || model == 2 || model == 3) ack_num = 1;
				else if(model == 4) ack_num = 2;
				else ack_num = 0;
				break;
			default:
				ack_num = 0;
				break;
		}
		data_center.setLastMessageTime(p.getTimestamp());
		data_center.setMessageAckNum(ack_num);
	}

    protected synchronized void sendPacket(Packet packet) throws IOException
    {
//        System.out.println("A packet with desitination "+packet.getDestination() + ", lastMsgAckNum " + data_center.getMessageAckNum());
        if(packet.getDestination()<0 || packet.getDestination() > DataCenter.TOTAL_NUM){
            System.out.println("A packet with invalid desitination "+packet.getDestination()+", drop it!");
            return;
        }
		else if (packet.getDestination() == DataCenter.TOTAL_NUM){
			leader_obj.writeObject(packet);
		}
        else{
			obj_os[packet.getDestination()].writeObject(packet);
		}
    }

	public ModeDataCenter getDataCenter(){
		return data_center;
	}
}
