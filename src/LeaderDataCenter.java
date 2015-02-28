
import java.io.*;
import java.net.*;
import java.util.*;                                              
import java.util.concurrent.*;

public class LeaderDataCenter extends DataCenter{

    public static final int LEADER_PORT = 9876;

    protected Packet ack_packet;
    protected int ack_received = 0;
    protected Object lock = new Object();

    LeaderDataCenter(){ super(0); }

    public synchronized void setAckPacket(Packet packet){
        synchronized(lock){

            if(ack_received==0)
                ack_packet = packet;
            else{
                //check whether it's the ack packet we expect
                if(ack_packet.getDestination()!=packet.getDestination()){
                    System.out.println("Error. Receive unexpected ack packets. Ignore it.");
                }
            }
            ack_received += 1;
        }
    }

    public synchronized Packet getAckPacket(){
        if(ack_received < DataCenter.TOTAL_NUM)
            //if haven't receive all ack packets
            //doesn't take action
            return null;
        else
            return ack_packet;
    }

    public synchronized void resetAckPacket(){
        synchronized(lock){
            ack_received = 0;
            ack_packet = null;
        }

    }

    @Override
    public void startThreads(){
        //Thread client_thread = new Thread(new ClientThread(this));
        Thread server_threads[] = new Thread[TOTAL_NUM];
        for(int i=0; i<TOTAL_NUM; i++){
            server_threads[i] = new Thread(new ServerThread(this, i));
            server_threads[i].start();
        }
        //Thread message_thread = new Thread(new MessageThread(this));
        //message_thread.start();
    }
   
    @Override
    public void buildConnection() throws IOException{
 
        //for debug, print out port information
   
        ServerSocket server_socket = new ServerSocket(LEADER_PORT);	

        for(int i=0; i<DataCenter.TOTAL_NUM; i++){

    		try{
	    		Socket client_socket = server_socket.accept();
                socket_map[i] = client_socket;
			    System.out.println("Get connection from " + i + ". Connect succeed.");
			} catch (IOException e){
			    System.out.println("2 Cannot open socket between " + i + " and " + index
                    + "; Port is " + ports[i]);
				e.printStackTrace(System.out);
			}

        }
    }

    public static void main(String[] args) throws IOException {
        LeaderDataCenter datacenter = new LeaderDataCenter();
		datacenter.buildConnection();
        //datacenter.startThreads();
    }

}

