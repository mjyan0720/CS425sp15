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
                    if(recv_packet.getModel()==1 || recv_packet.getModel()==2)
                        System.out.println("Receive Packet \""+recv_packet.getContent()
                                +"\" from Leader. System time is "+df.format(dateobj));
                    else
                        System.out.println("Receive Packet \""+recv_packet.getContent()
                                +"\" from "+(char)(recv_packet.getSource()+'A')+". System time is "+df.format(dateobj));
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
                long time = packet.getTimestamp();
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
                int key = packet.getKey();
                //perform search operation
                Content res = replica.get(key);
                //create ack packet
				Packet p = buildAckMsg(packet);
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
                long time = packet.getTimestamp();
                //perform operation
 	    		replica.update(key, value, time);
                //create ack packet and insert into ack queue
				Packet p = buildAckMsg(packet);
	            p.setContent("Update Ack For -> \""+packet.getContent()+"\"");
	    		p.setType(Packet.PacketType.Ack);
	    		replica.insertAckMessage(p);
				break;
            }
			case Delete:
            {
				int key = packet.getKey();
                //perform operation
                replica.delete(key);
                //create ack packet and insert to ack queue
				Packet p = buildAckMsg(packet);
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
				Packet p = buildAckMsg(packet);
				p.setType(Packet.PacketType.GetAck);
				p.setValueTimestamp(content);
				p.setKey(key);
				replica.insertAckMessage(p);
				break;
            }
			case GetAck:
            {
                //wait for enough read packet return
                //ignore the following
                //and print out result
				replica.increaseAck(packet.getId());
				processGetAck(packet);	
				break;
            }
			case SearchAck:
            {
                //only 1 search ack will return
                //so print it out
				replica.increaseAck(packet.getId());
				processSearchAck(packet);
				break;
            }
			case Ack:
            {
                //receive enough acks
                //then start original routine -> check queue
				replica.increaseAck(packet.getId());
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
		ModeDataCenter replica = (ModeDataCenter) data_center;
        if(p.getValueTimestamp()==null)
    		System.out.println("Receiving get message: " + p.getKey() + "=>" 
			+ "is not in data center");
        else{
    		System.out.println("Receiving get message: " + p.getKey() + "=>" 
			+ p.getValueTimestamp().value + " Time stamp =>" + p.getValueTimestamp().timestamp);	
			Content t = p.getValueTimestamp();
			Content old = replica.get(p.getKey());
			if(old != null && old.timestamp < t.timestamp){
				replica.updatePair(p.getKey(),t);
			}
		}
	}

/*	private Packet buildAckMsg(int source, int des, String content, int model){
		long current_time = System.currentTimeMillis();
		Packet p = new Packet(content,current_time, source, des, model);
        Random random = new Random();
        long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                    p.getDestination())*1000);
        p.setDelay(delay);
		return p;
	}
*/
	private Packet buildAckMsg(Packet packet){
        Packet p = new Packet(packet);
		long current_time = System.currentTimeMillis();
        //use original packet's timestamp as id
        p.setId(packet.getTimestamp());
        p.setTimestamp(current_time);
        if(packet.getModel()==1 || packet.getModel()==2){
            //No need to reset source and destination
        }else{
            //exchange source and destination
            int s = p.getSource();
            p.setSource(packet.getDestination());
            p.setDestination(s);
        }
        //set corresponding delay
        Random random = new Random();
        long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                    p.getDestination())*1000);
        p.setDelay(delay);
		return p;
	}
}
