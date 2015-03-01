import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeServerThread extends ServerThread{
	public ModeServerThread(DataCenter data_center, int target){
		super(data_center, target);
	}

    public ModeServerThread(DataCenter data_center, Socket s){
        super(data_center, s);
    }

	@Override
	public void run(){
        System.out.println("Starting Server Thread listen to message from "+(char)(target+'A'));
        try{
            ObjectInputStream obj_is = new ObjectInputStream(socket.getInputStream());
            while(true){
                //blocking to read from the socket
                Packet recv_packet = (Packet)obj_is.readObject();
                if(recv_packet!=null){
                    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
                    Date dateobj = new Date();
                    if(recv_packet.getModel()==1)
                        System.out.println("Receive Packet \""+recv_packet.getContent()
                                +"\" from Leader. System time is "+df.format(dateobj));
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
		ModeDataCenter replica = (ModeDataCenter) data_center;
        switch(packet.getType()){
			case Insert:
            {
                int key = packet.getKey();
                int value = packet.getValue();
                int time = packet.getTime();
                //perform insert operation
				replica.insert(key, value, time);
                //create ack packet and insert it into ack queue
                Packet p = buildAckMsg(packet);
                p.setContent("Insert Ack For -> \""+packet.getContent()+"\"");
                p.setType(Packet.PacketType.Ack);
				replica.insertAckMessage(p);
				break;
            }
			case Search:
            {
                //perform search operation
                Content res = replica.get(key);
                //create ack packet
				p = buildAckMsg(packet);
	            p.setContent("Search Ack For -> \""+packet.getContent()+"\"; res = "+
                        (res!=null));
	    		p.setType(Packet.PacketType.SearchAck);
                p.setValueTimestamp(res);
				replica.insertAckMessage(p);
				break;
            }
			case Update:{
	            int key = packet.getKey();
                int value = packet.getValue();
                int time = packet.getTime();
                //perform operation
 	    		replica.update(key, value, time);
                //create ack packet and insert into ack queue
				p = buildAckMsg(packet);
	            p.setContent("Update Ack For -> \""+packet.getContent()+"\"");
	    		p.setType(Packet.PacketType.Ack);
	    		replica.insertMessage(p);
				break;
            }
			case Delete:
            {
				int key = packet.getKey();
                //perform operation
                replica.delete(key);
                //create ack packet and insert to ack queue
				p = buildAckMsg(packet);
	            p.setContent("Delete Ack For -> \""+packet.getContent()+"\"");
	    		p.setType(Packet.PacketType.Ack);
		    	replica.insertAckMessage(p);
				break;
            }
			case Get:
            {
				int key = packet.getKey();
                //perform operation
	    		Content content = replica.get(key);
                //create ack packet and insert to ack queue
				p = buildAckMsg(this);
				p.setType(Packet.PacketType.GetAck);
				p.setValueTimestamp(content);
				replica.insertAckMessage(p);
				break;
            }
			case GetAck:
            {
                //wait for enough read packet return
                //ignore the following
                //and print out result
				processGetAck(packet);	
				break;
            }
			case SearchAck:
            {
                //only 1 search ack will return
                //so print it out
				processSearchAck(packet);
				break;
            }
			case Ack:
            {
                //receive enough acks
                //then start original routine -> check queue
				replica.increaseAck(time);
				break;
            }
            default:
                System.out.println("Can't recognize the packet.");
        }
	}


    //search will always operate at model 1
    //so we will only receive 1 such packet
    //print out directly
	private void processSearchAck(Packet p){
        if(p.getContent().length()==0)
		    System.out.println("Key " + p.getKey() + " doesn't exist in any replicas.");	
        else
		    System.out.println("Key " + p.getKey() + " exists in replicas " + p.getContent());	
	}

	private void processGetAck(Packet p){
		System.out.println("Receiving get message: " + p.getKey() + "=>" + p.getValueTimestamp().value + " Time stamp =>" + p.getValueTimestamp().timestamp);			
	}

	private Packet buildAckMsg(int source, int des, String content, int model){
		long current_time = System.currentTimeMillis();
		Packet p = new Packet(content,current_time, source, des, model);
        Random random = new Random();
        long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                    p.getDestination())*1000);
        p.setDelay(delay);
		return p;
	}

	private Packet buildAckMsg(Packet packet){
        Packet p = new Packet(packet);
		long current_time = System.currentTimeMillis();
        //use original packet's timestamp as id
        p.setTimestamp(current_time);
        p.setId(packet.getTimestamp());
        if(packet.getModel()==1 || packet.getModel()==2){
            //No need to reset source and destination
        }else{
            //exchange source and destination
            p.setSource(packet.getDestination());
            p.setDestination(p.getSource());
        }
        //set corresponding delay
        Random random = new Random();
        long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                    p.getDestination())*1000);
        p.setDelay(delay);
		return p;
	}
}
