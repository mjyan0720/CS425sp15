
import java.io.*;
import java.net.*;
import java.util.*;                                              
import java.util.concurrent.*;


public class DataCenter{
	
    public static final int TOTAL_NUM = 3;
    public static final int base_port = 6000;

    private  Queue<Packet> message_queue = new LinkedBlockingQueue<Packet>();
	private  Socket socket_map[] = new Socket[TOTAL_NUM]; 
	private  int ports[] = new int[TOTAL_NUM];
	private  String host_name = new String("127.0.0.1");
	private  int index;
	private  int delay[][] = new int[TOTAL_NUM][TOTAL_NUM]; 

	public DataCenter(int index){
        this.index = index;
	}

	public  int getId(){
		return index;
	}

	public  int getMaxDelay(int src, int des){
		return 3;
//		return delay[src][des];
	}

	public  synchronized Packet getMessage(){
	// Return one message if the message queue is not empty
    		return message_queue.poll();
	}

	public  synchronized void insertMessage(Packet p){
	// Return one message if the message queue is not empty
		message_queue.add(p);
	}

	//@Parameter: machine ID
	public  Socket getSocket(int id){
	// Return one message if the message queue is not empty
		return socket_map[id];
	}

	//@Parameter: current machine ID
	public  void buildConnection() throws IOException{

        //for debug, print out port information
        for(int i=0; i< TOTAL_NUM; i++){
            System.out.println(i + " -> " + ports[i]);
        }
    
        ServerSocket server_socket = new ServerSocket(ports[index]);	

		for(int i=0; i < TOTAL_NUM; i++){
		// Build socket with machines which have bigger IDs than current machine
			if(i <= index){
				try{
					Socket client_socket = new Socket(host_name,ports[i]);
					socket_map[i] = client_socket;
					System.out.println("Connection successful between " + i + " and " + index);
				} catch (IOException e){
					System.out.println("1 Cannot open socket between " + i + " and " + index
                            + "; Port is " + ports[i]);
					e.printStackTrace(System.out);
				}
			}
			if(i >= index){
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
	}

	public  void readConfigFile(String file) throws Exception{
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
		
	public void initialize(){
        for(int i=0; i<TOTAL_NUM; i++){
		    ports[i] = base_port + i;
        }
	}

    void startThreads(){
        Thread client_thread = new Thread(new ClientThread(this));
        Thread server_threads[] = new Thread[TOTAL_NUM];
        for(int i=0; i<TOTAL_NUM; i++){
            if(i!=getId()){
                server_threads[i] = new Thread(new ServerThread(this, i));
                server_threads[i].start();
            }
        }
        Thread message_thread = new Thread(new MessageThread(this));
        message_thread.start();
        client_thread.start();
   }

	public static void main(String[] args) throws IOException {
    	if (args.length < 1) {
        	System.err.println("Usage: java DataCenter machineID(0,1,2,3)");
	        System.exit(1);
    	}
		int index = Integer.parseInt(args[0]);
        DataCenter datacenter = new DataCenter(index);
		datacenter.initialize();
		datacenter.buildConnection();

        datacenter.startThreads();
    }
}
