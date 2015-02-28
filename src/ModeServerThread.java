import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeServerThread extends ServerThread{

	public ModeServerThread(DataCenter data_center, int target){
		super(data_center,target);
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

	private void processPacket(Packet packet){
        switch(packet.getType()){
            case Message:
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
                Date dateobj = new Date();
                System.out.println("Received \""+packet.getMessage()
                        +"\" from "+(char)(target+'A')
                        +", Max delay is "+data_center.getMaxDelay(
                            data_center.getId(), packet.getDestination())
                        +" s, system time is "+df.format(dateobj));
                break;
			case Show:
				data_center.show();
				break;
			case Search:
				data_center.search(packet.getKey());
				break;
			case Insert:
				data_center.insert();
				break;
			case Update:
				data_center.update();
				break;
			case Delete:
				data_center.delete();
				break;
			case Get:
				data_center.get();
				break;
			case Ack:
				data_center.increaseAck();
				break;
            default:
                System.out.println("Can't recognize the packet.");
        }
	}
}
