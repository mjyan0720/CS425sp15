
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ModeDataCenter extends KeyValueDataCenter{
	protected int port;
	protected Socket central_socket;
	protected int ack;
	private int ack_num;
    public ModeDataCenter(int index){
        super(index);
    }

	public Socket getLeaderSocekt(){
		return central_socket;
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

	public synchronized boolean messageComplete(){
		if(ack >= ack_num){
			ack = 0;
			return true;
		}
		return false;
	}

	public synchronized void increaseAck(){
		ack++;
	}

	public String show(){
	}

	public boolean search(int key){
		return true; 
	}

	public void insert(){
	}

	public void update(){
	}

	public void delete(){
	}

	public void get(){
	}


//	@Override
//	public void readConfigFile(String file){
//	}

	@Override
	public void startThreads(){
	}

}





