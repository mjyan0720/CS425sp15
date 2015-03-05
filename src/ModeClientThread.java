
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeClientThread extends ClientThread{

    public class delayComparator implements Comparator<Packet>{
        @Override
        public int compare(Packet p1, Packet p2){
            if (p1.getSendTime() < p2.getSendTime())
                return 1;
            else if(p1.getSendTime() == p2.getSendTime())
                return 0;
            else
                return -1;
        }
    }

    public ModeClientThread(DataCenter data_center){
        super(data_center);
    }

    @Override
    public void run(){
        //  open up standard input
        BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(System.in));
        try{
            if(DataCenter.ReadFromFile==true)
                buffer_reader = new BufferedReader(new FileReader("input"+data_center.getId()));
        } catch(IOException e){
            System.err.println("Error in reading from input file: "+e);
        }

        System.out.println(DataCenter.ReadFromFile);
        if(DataCenter.ReadFromFile==true)
            System.out.println("Starting Client thread. Read from File \"input"+data_center.getId()+"\"...");
        else
            System.out.println("Starting Client thread. Read from Terminal...");

        String command = null;
        try{
            while(true){
                //read a line from the terminal
                command = buffer_reader.readLine();
                //System.out.println(command);
                //show all will show all local variables
                if(command.equals(new String("show-all"))){
                    if(!(data_center instanceof KeyValueDataCenter)){
                        System.out.println("Can't recognize command. Only support show-all in Part 2");
                    }
                    ((KeyValueDataCenter)data_center).printAll();
                    continue;
                }
                //create a new packet and parse it
                Packet packet = new Packet(command, System.currentTimeMillis(),
                        data_center.getId());
                //use information of the packet to determine destination
                //and corresponding delay
 
                if(packet.getType()==Packet.PacketType.Delay){
                   try{
                       Thread.sleep(packet.getDelay());
                   }catch(InterruptedException e){
                       System.err.println("In processing delay command:"+e);
                   }
                }

               
                //--------------------------------------
                //set destination
                //in mode 1/2, destination is Leader
                //in mode 3/4, we should create 4 packets, insert them into the queue
                //in a sorted order, with the shortest dealy at the head
                //-------------------------------------
                if(packet.getModel()==1 || packet.getModel()==2){
                    //set destination to leader
                    packet.setDestination(DataCenter.TOTAL_NUM);
                    //set dealy, using the constant max dealy
                    Random random = new Random();
                    long delay;
                    delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                                packet.getDestination())*1000);
                    packet.setDelay(delay);

                    //in mode 2, get will return local value
                    //don't need to insert to ask for all
                    if(packet.getModel()==2){
                       if(packet.getType()==Packet.PacketType.Get){
                            Content c = ((ModeDataCenter)data_center).get(packet.getKey());
                            if(c==null){
                                System.out.println("Get from local: "+packet.getKey()
                                    +"-> Doesn't exist");
                            } else{
                                System.out.println("Get from local: "+packet.getKey()
                                    +"->"+c.value+"; Timestamp: "+c.timestamp);
                            }
                            //skip the queue
                            continue;
                        }
                    }
                    //used for debug
                    //print out the delay
                    System.out.println("Send the message using delay as " + delay + " Milliseconds");
                    data_center.insertMessage(packet);
                    printPacket(packet);
                } else {
                    TreeSet<Packet> packets = new TreeSet<Packet>(new delayComparator());

                    Random random = new Random();
  
                    //create 4 packets, with destination and delay properly configured
                    for(int i=0; i<DataCenter.TOTAL_NUM; i++){
                        Packet p = new Packet(packet);
                        p.setDestination(i);
                        long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                                packet.getDestination())*1000);
                        p.setDelay(delay);
                        packets.add(p);
                    }

                    //insert all the packets into queue in order
                    Iterator<Packet> iter = packets.iterator();
                    while(iter.hasNext()){
                        data_center.insertMessage(iter.next());
                    }

                }
           }
        } catch(IOException e){
            System.err.println("Error when reading command from terminal");
            System.exit(1);
        }
        
    }
}
