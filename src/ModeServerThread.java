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
		int source = packet.getSource();
		int des = packet.getDestination();
		long time = packet.getTimestamp();
		int key = packet.getKey();
		int value = packet.getValue();
		int model = packet.getModel();
		ModeDataCenter replica = (ModeDataCenter) data_center;
		Packet p;
		if(model == 1) des = DataCenter.TOTAL_NUM;
        switch(packet.getType()){
			case Insert:
				replica.insert(key, value, time);
                p = buildAckMsg(source, des, packet.getModel());
                //if not in mode 1, should set destination to other
                //if(packet.getModel() == 1)
    			//	p = buildAckMsg(source, DataCenter.TOTAL_NUM);
				replica.insertMessage(p);
				break;
			case Show:
				replica.show();
				p = buildAckMsg(source, des, packet.getModel());
				replica.insertMessage(p);
				break;
			case Search:
				replica.search(packet.getKey());
				p = buildAckMsg(source, des, packet.getModel());
				p.setType(Packet.PacketType.SearchAck);
				replica.insertMessage(p);
				break;
			case Update:
				replica.update(key, value, time);
				p = buildAckMsg(source, des, packet.getModel());
				replica.insertMessage(p);
				break;
			case Delete:
				replica.delete(key);
				p = buildAckMsg(source, des, packet.getModel());
				replica.insertMessage(p);
				break;
			case Get:
				Content content = replica.get(key);
				p = buildAckMsg(source, des, packet.getModel());
				p.setType(Packet.PacketType.GetAck);
				p.setValueTimestamp(content);
				p.setKey(key);
				replica.insertMessage(p);
				break;
			case GetAck:
				processGetAck(packet);	
				break;
			case SearchAck:
				processSearchAck(packet);
				break
			case Ack:
				replica.increaseAck();
				break;
            default:
                System.out.println("Can't recognize the packet.");
        }
	}

	private void processSearchAck(Packet p){
		System.out.println("Replicas with " + p.getKey() + " exists in replicas " + p.getContent());	
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

	private Packet buildAckMsg(int source, int des, int model){
		long current_time = System.currentTimeMillis();
		Packet p = new Packet(new String("ACK"),current_time, source, des, model);
        Random random = new Random();
        long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                    p.getDestination())*1000);
        p.setDelay(delay);
		return p;
	}
}
