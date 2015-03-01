
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;
public class ClientThread implements Runnable{


    private DataCenter data_center;

    public ClientThread(DataCenter data_center){
        this.data_center = data_center;
    }

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
                if(packet.getModel() ==1)
                     delay = random.nextInt(data_center.getMaxDelay()*1000);
                else
                    delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                            packet.getDestination())*1000);
                packet.setDelay(delay);
                //used for debug
                //print out the delay
                System.out.println("Send the message using delay as " + delay + " Milliseconds");
                data_center.insertMessage(packet);
                printPacket(packet);
           }
        } catch(IOException e){
            System.err.println("Error when reading command from terminal");
            System.exit(1);
        }
   
    }

    private void printPacket(Packet packet){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
        Date dateobj = new Date();

        switch(packet.getType()){
            case Message:
                System.out.println("Sent \""+packet.getMessage()
                        +"\" to "+(char)(packet.getDestination()+'A')
                        +", system time is "+df.format(dateobj));
                break;
            case Insert:
            case Update:
            case Get:
            case Delete:
            case Search:
                if(packet.getModel()==1)
                    System.out.println("Sent \""+packet.getContent()
                        +"\" to Leader, system time is "+df.format(dateobj));
                break;
            default:
                System.out.println("Can't recognize the packet.");
                System.out.println("Still send it.");
        }
    }



}
