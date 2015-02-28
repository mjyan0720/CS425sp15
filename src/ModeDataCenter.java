
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ModeDataCenter extends KeyValueDataCenter{
	int port;
	Socket central_socket;
    public ModeDataCenter(int index){
        super(index);
    }

    @Override
    //build connection with master server first
    public void buildConnection() throws IOException{
		try{
			central_socket = new Socket("127.0.0.1", 9876);
			System.out.println("Connection successful between server and node " + index);
		} catch(Exception e){
			e.printStackTrace(System.out);
		}

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


	@Override
	public void readConfigFile(String file){
	}

	@Override
	public void startThreads(){
	}

}





