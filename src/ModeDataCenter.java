import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ModeDataCenter extends KeyValueDataCenter{
	protected int port;
	protected Socket central_socket;
	protected int ack;
	protected long lastMsgTime;  // used for wait for ack
	protected int lastMsgAckNum; // the number of ack needed to confirm the last message
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
		if(ack >= lastMsgAckNum){
			ack = 0;
			lastMsgAckNum = 0;
			return true;
		}
		return false;
	}
	
	public synchronized void setMessageAckNum(int t){
		lastMsgAckNum = t;
	}

	public synchronized int getMessageAckNum(){
		return lastMsgAckNum;
	}

	public long getLastMessageTime(){
		return lastMsgTime;
	}

	public void setLastMessageTime(long t){
		lastMsgTime = t;
	}

	public synchronized void increaseAck(long t){
		if(t == lastMsgTime){
			ack++;
			System.out.println("Receving ACK message. New ACK value " + ack);
		}
		else{
			System.out.println("Receving ACK message not for last message, this ack time is " + t + " while last message time is " + lastMsgTime);
		}
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
		lastMsgAckNum = -2;
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





