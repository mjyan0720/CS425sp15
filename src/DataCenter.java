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

	public DataCenter(){
	}

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

	public static synchronized void insertMessage(Packet p){
	// Return one message if the message queue is not empty
		message_queue.add(p);
	}

	//@Parameter: machine ID
	public static Socket getSocket(int id){
	// Return one message if the message queue is not empty
		return socket_map[id];
	}

	//@Parameter: current machine ID
	public static void buildConnection() throws IOException{
		for(int i=0; i < 4; i++){
		// Build socket with machines which have bigger IDs than current machine
			if(i > index){
				try{
					Socket client_socket = new Socket(host_name,ports[i]);
					socket_map[i] = client_socket;
					System.out.println("Connection successful between " + i + " and " + index);
				} catch (IOException e){
					System.out.println("Cannot open socket between " + i + " and " + index);
					e.printStackTrace(System.out);
				}
			}
			else{
				try{
					ServerSocket server_socket = new ServerSocket(ports[i]);
					Socket client_socket = server_socket.accept();
					socket_map[i] = client_socket;
				} catch (IOException e){
					System.out.println("Cannot open socket between " + i + " and " + index);
					e.printStackTrace(System.out);
				}
			}
		}
	}

	public static void readConfigFile(String file) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(file));
		try{
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			String values[];
			while(line != null){
				values = line.split(" ");
				int i = 0;
				for(String s: values){
					delay[index][i++] = Integer.parseInt(s);
				}
				line = br.readLine();
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		} finally{
			br.close();
		}
	}
		
	public static void initialize(){
		ports[0] = 28001;
		ports[1] = 28002;
		ports[2] = 28003;
		ports[3] = 28004;
	}

	public static void main(String[] args) throws IOException {
    	if (args.length < 1) {
        	System.err.println("Usage: java DataCenter machineID(0,1,2,3)");
	        System.exit(1);
    	}
		index = Integer.parseInt(args[0]);
		initialize();
		buildConnection();
	}
}
