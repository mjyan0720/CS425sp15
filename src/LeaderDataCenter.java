
import java.io.*;
import java.net.*;
import java.util.*;                                              
import java.util.concurrent.*;

public class LeaderDataCenter extends DataCenter{

    public static final int LEADER_PORT = 9876;

    LeaderDataCenter(){ super(0); }
    
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

