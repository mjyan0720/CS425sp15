import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.text.*;

public class ModeDataCenter extends KeyValueDataCenter{
	protected int port;
	protected Socket central_socket;
	protected int ack;
	protected long lastMsgTime;  // used for wait for ack
	protected Object lockAckNum = new Object();
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
		synchronized (lockAckNum){
			if(ack >= lastMsgAckNum){
				return true;
			}
			return false;
		}
	}
	
	public synchronized void setMessageAckNum(int t){
		synchronized (lockAckNum){
			lastMsgAckNum = t;
		}
	}

	public synchronized int getMessageAckNum(){
		synchronized (lockAckNum){
			return lastMsgAckNum;
		}
	}

	public long getLastMessageTime(){
		return lastMsgTime;
	}

	public void setLastMessageTime(long t){
		lastMsgTime = t;
	}

	public synchronized void increaseAck(long t){
		synchronized (lockAckNum){
			if(t != lastMsgTime){
//				System.out.println("Ignoring ACK time is " + t + "; It's not the packet we expect");
				return;
			}
			else if(t == lastMsgTime){
				ack++;
				if(ack == lastMsgAckNum){
					lastMsgAckNum = -1;
					ack = 0;
					lastMsgTime = -1;
					System.out.println("Operation Complete.");
				}
			}
			else{
				DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
				Date t1 = new Date(t);
				Date t2 = new Date(lastMsgTime);
				System.out.println("Impossible! Receving ACK message not for last message, neglected. This ack time is "
				+ df.format(t1) + " while last message time is " + df.format(t2));
			}
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
		Content old = getValue(key);
		if(old != null){
			if(old.timestamp > time){
                System.out.println("It's an old insert message, ignore.");
                return;
            }
		}
		insertPair(key, value,time);
		System.out.println("Successful inserting key " + key);
    }

	public synchronized void insert(int key, Content content){
		Content old = getValue(key);
		if(old != null){
			if(old.timestamp > content.timestamp) return;
		}
		insertPair(key, content);
		System.out.println("Successful inserting key " + key);
	}
	
	public synchronized void update(int key, int value, long time){
		Content old = getValue(key);
		if(old != null){
			if(old.timestamp > time){
                System.out.println("It's an old update message, ignore.");
                return;
            }
		}
		updatePair(key, value, time);
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
		Date t = new Date(time);
		System.out.println("Successful updating key " + key + " value " + value + " at time " + df.format(t));
	}

	public synchronized void update(int key, Content content){
		Content old = getValue(key);
		if(old != null){
			if(old.timestamp > content.timestamp) return;
		}
		updatePair(key, content);
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
		Date t = new Date(content.timestamp);
		System.out.println("Successful updating key " + key + " value " + content.value + " at time " + df.format(t));
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
        Thread server_threads[] = new Thread[TOTAL_NUM];
        for(int i=0; i<TOTAL_NUM; i++){
            if(i!=getId()){
                server_threads[i] = new Thread(new ModeServerThread(this, i));
            } else {
                server_threads[i] = new Thread(new ModeServerThread(this, TOTAL_NUM));
            }
         server_threads[i].start();
        }
        Thread server_thread_listen_to_leader = new Thread(new ModeServerThread(this, getLeaderSocket()));
        server_thread_listen_to_leader.start();
        Thread message_thread = new Thread(new ModeMsgThread(this));
        message_thread.start();
        Thread client_thread = new Thread(new ModeClientThread(this));
        client_thread.start();
	}

	public static void main(String[] args) throws IOException {
    	if (args.length < 1) {
        	System.err.println("Usage: java DataCenter machineID(0,1,2,3) config_file");
	        System.exit(1);
    	}
        //first parameter is ID
		int index = Integer.parseInt(args[0]);
        ModeDataCenter datacenter = new ModeDataCenter(index);
        //second parameter is configuration file
		try{
			datacenter.readConfigFile(args[1]);
		}
		catch(Exception e){
			e.printStackTrace(System.out);
		}

        //third parameter is optional, if provided
        //non-zero means read from file
        if(args[2]!=null && Integer.parseInt(args[2])!=0)//default read from terminal
            DataCenter.ReadFromFile = true;
//        System.out.println("set up read from file "+args[2]+" "+DataCenter.ReadFromFile);
		datacenter.buildConnection();
        datacenter.startThreads();
    }



}





