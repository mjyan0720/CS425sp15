
import java.io.*;
import java.net.*;
import java.util.*;                                              
import java.util.concurrent.*;

public class LeaderDataCenter extends DataCenter{

    LeaderDataCenter(){ super(0); }
    @Override
    public void buildConnection() throw IOException{
 
        //for debug, print out port information
        for(int i=0; i< TOTAL_NUM; i++){
            System.out.println(i + " -> " + ports[i]);
        }
    
        ServerSocket server_socket = new ServerSocket(ports[index]);	

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

