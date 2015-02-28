
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
			System.out.println("Connection successful between leader and node " + index);
		} catch(Exception e){
			e.printStackTrace(System.out);
		}

        super.buildConnection();
    }


//	@Override
//	public void readConfigFile(String file){
//	}

	@Override
	public void startThreads(){
	}

}





