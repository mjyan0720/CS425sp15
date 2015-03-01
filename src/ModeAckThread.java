import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeAckThread implements Runnable{
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
        while(true){
            //get the top packet of the queue
			while(!data_center.messageComplete());
			int model = sendOneMessage(false);
			if(model == 3 || model == 4){
			}
        }//end of infinite loop
    }// end of run()

	private int sendOneMessage(boolean flag){
		int model = -1;
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
				if(!flag){
					setPacketVariableInDataCenter(packet);
				}
				model = packet.getModel();
                sendPacket(packet);
            } catch(IOException e){
                System.err.println("error in send packet.");
                System.err.println(e);
            }
        }
		return model;
	}

	private void setPacketVariableInDataCenter(Packet p){
		data_center.setLastMessageTime(p.getTimestamp());
		int ack_num = 0;
		int model = p.getModel();
		Packet.PacketType type = p.getType();
		switch(type){
			case Delete:
				ack_num = 1;
				break;
			case Insert:
			case Update:
			case Get:
				if(model == 1 || model == 2 || model == 3) ack_num = 1;
				else if(model == 4) ack_num = 2;
				break;
			default:
				ack_num = 0;
				break;
		}
		data_center.setMessageAckNum(ack_num);
	}

    private void sendPacket(Packet packet) throws IOException
    {
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
}
