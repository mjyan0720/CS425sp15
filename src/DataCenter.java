
import java.io.*;
import java.net.*;
import java.util.*;                                              
import java.util.concurrent.*;


public class DataCenter{
	
    public static final int TOTAL_NUM = 4;
    public static final int base_port = 6000;

    protected  Queue<Packet> message_queue = new LinkedBlockingQueue<Packet>();
	protected  Socket socket_map[] = new Socket[TOTAL_NUM+1]; 
	protected  int ports[] = new int[TOTAL_NUM];
	protected  String host_name = new String();
	protected  int index;
	protected  int delay[][] = new int[TOTAL_NUM][TOTAL_NUM]; 

	public DataCenter(int index){
        this.index = index;
	}

	public  int getId(){
		return index;
	}

	public  int getMaxDelay(int src, int des){
		return delay[src][des];
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
                        if(i==index)
						    socket_map[TOTAL_NUM] = client_socket;
                        else
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
			String values[], delays[];
			while(line != null){
				values = line.split(":");
				int i = 0;
				for(String s: values){
					if(s.equals("IP")){
						host_name = values[1];
						break;
					}
					else if(s.equals("Ports")){
						for(int k=0; k < TOTAL_NUM; k++){
							ports[k] = Integer.parseInt(values[k+1]);
						}	
					}
					else if(s.equals("Delay")){
						for(int k=0; k < TOTAL_NUM; k++){
							line = br.readLine();
							delays = line.split(":");
							for(int h=0; h < TOTAL_NUM; h++){
								delay[k][h] = Integer.parseInt(delays[h]);
							}
						}
					}
				}
				line = br.readLine();
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		} finally{
			br.close();
		}
	}
		
    void startThreads(){
        Thread client_thread = new Thread(new ClientThread(this));
        Thread server_threads[] = new Thread[TOTAL_NUM];
        for(int i=0; i<TOTAL_NUM; i++){
            if(i!=getId()){
                server_threads[i] = new Thread(new ServerThread(this, i));
            } else {
                server_threads[i] = new Thread(new ServerThread(this, TOTAL_NUM));
            }
         server_threads[i].start();
        }
        Thread message_thread = new Thread(new MessageThread(this));
        message_thread.start();
        client_thread.start();
   }

	public static void main(String[] args) throws IOException {
    	if (args.length < 1) {
        	System.err.println("Usage: java DataCenter machineID(0,1,2,3) config_file");
	        System.exit(1);
    	}
		int index = Integer.parseInt(args[0]);
        DataCenter datacenter = new DataCenter(index);
		int mode = Integer.parseInt(args[2]);
		if(mode == 1){
			datacenter = new LinearizableDataCenter(index);		
		}
		else if(mode == 2){
		}
		try{
			datacenter.readConfigFile(args[1]);
		}
		catch(Exception e){
			e.printStackTrace(System.out);
		}
		datacenter.buildConnection();
        datacenter.startThreads();
    }
}


