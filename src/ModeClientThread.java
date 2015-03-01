
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeClientThread extends ClientThread{

    public ModeClientThread(DataCenter data_center){
        super(data_center);
    }

    @Override
    public void run(){
         System.out.println("Starting Client thread. Read from Terminal...");
        //  open up standard input
        BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(System.in));

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
                Random random = new Random();
                long delay;
                if(packet.getModel() == 1 | packet.getModel() == 2)
                     delay = random.nextInt(data_center.getMaxDelay()*1000);
                else
                    delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                            packet.getDestination())*1000);
                packet.setDelay(delay);
                //used for debug
                //print out the delay
                if(packet.getModel()==2){
                    //in mode 2, get will return local value
                    //don't need to insert to ask for all
                    if(packet.getType()==Packet.PacketType.Get){
                        Content c = ((ModeDataCenter)data_center).get(packet.getKey());
                        if(c==null){
                            System.out.println("Get from local: "+packet.getKey()
                                +"-> Doesn't exist");
                        } else{
                            System.out.println("Get from local: "+packet.getKey()
                                +"->"+c.value+"; Timestamp: "+c.timestamp);
                        }
                        continue;
                    }
                }
                System.out.println("Send the message using delay as " + delay + " Milliseconds");
                data_center.insertMessage(packet);
                printPacket(packet);
           }
        } catch(IOException e){
            System.err.println("Error when reading command from terminal");
            System.exit(1);
        }
        
    }
}
