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

	public Socket getLeaderSocket(){
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
		if(ack > 0){
			ack = 0;
			return true;
		}
		return false;
	}

	public synchronized void increaseAck(){
		ack++;
		System.out.println("Receving ACK message. New ACK value " + ack);
	}

	public String show(){
		return new String("Show value successful!");
	}

	public synchronized boolean search(int key){
		if(containsKey(key))
			return true; 
		return false;
	}

	public synchronized void insert(int key, int value, long time){
		insertPair(key, value,time);
		System.out.println("Successful inserting key " + key);
    }

	public synchronized void insert(int key, Content content){
		insertPair(key, content);
		System.out.println("Successful inserting key " + key);
	}
	
	public synchronized void update(int key, int value, long time){
		updatePair(key, value, time);
		System.out.println("Successful updating key " + key + " value " + value + " at time " + time);
	}

	public synchronized void update(int key, Content content){
		updatePair(key, content);
		System.out.println("Successful updating key " + key + " value " + content.value + " at time " + content.timestamp);
	}
	
	public synchronized void delete(int key){
		deleteKey(key);
	}

	public synchronized Content get(int key){
		return  getValue(key);
	}

	@Override
	public void startThreads(){
        Thread client_thread = new Thread(new ClientThread(this));
        Thread server_threads[] = new Thread[TOTAL_NUM];
/*        for(int i=0; i<TOTAL_NUM; i++){
            if(i!=getId()){
                server_threads[i] = new Thread(new ModeServerThread(this, i));
            } else {
                server_threads[i] = new Thread(new ModeServerThread(this, TOTAL_NUM));
            }
         server_threads[i].start();
        }
*/        Thread server_thread_listen_to_leader = new Thread(new ModeServerThread(this, getLeaderSocket()));
        server_thread_listen_to_leader.start();
        Thread message_thread = new Thread(new ModeMsgThread(this));
        message_thread.start();
        client_thread.start();
	}
}





