import java.io.*;
import java.net.*;
import java.util.*;                                              

public class DataCenter{
	private static Queue<Packet> message_queue;
	private static Socket socket_map[] = new Socket[4]; 
	private static int ports[] = new int[4];
	private static String host_name = new String("127.0.0.1");
	private static int index;
	private static int delay[][] = new int[4][4]; 

	public static int getId(){
		return index;
	}

	public static int getMaxDelay(int src, int des){
		return delay[src][des];
	}

	public static synchronized Packet getMessage(){
	// Return one message if the message queue is not empty
		return message_queue.poll();
	}

	//@Parameter: machine ID
	public static Socket getSocket(Integer id){
	// Return one message if the message queue is not empty
		return socket_map[id];
	}

	//@Parameter: current machine ID
	public static void buildConnection(int id) throws IOException{
		for(int i=0; i < 4; i++){
		// Build socket with machines which have bigger IDs than current machine
			if(i >= id){
				Socket server_socket = new Socket(host_name,ports[i]);
				socket_map[i] = server_socket;
			}
		}
	}

	public static void main(String[] args) throws IOException {
    	if (args.length != 2) {
        	System.err.println("Usage: java KKMultiServer <port number>");
	        System.exit(1);
    	}
	}

}
