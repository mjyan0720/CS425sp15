
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LinearizableDataCenter extends KeyValueDataCenter{
	int port;
    public LinearizableDataCenter(int index){
        super(index);
    }

    @Override
    //build connection with master server first
    public void buildConnection(){
		try{
			Socket client_socket = new Socket(host_name, 9876);
			System.out.println("Connection successful between server and node " + index);
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
    }


	@Override
	public void readConfigFile(String file){
	}

	@Override
	public void startThreads(){
	}

}





