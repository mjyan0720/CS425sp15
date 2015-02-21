import java.io.*;
import java.util.*;
import java.net.*;

public class ClientThread implements Runnable{


    private DataCenter data_center;

    public ClientThread(DataCenter data_center){
        this.data_center = data_center;
    }

    public void run(){

        //  open up standard input
        BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(System.in));

        String command = null;
        try{
            while(true){
                //read a line from the terminal
                command = buffer_reader.readLine();
                //System.out.println(command);
                //create a new packet and parse it
                Packet packet = new Packet(command, System.currentTimeMillis());
                //use information of the packet to determine destination
                //and corresponding delay
                Random random = new Random();
                long delay = random.nextInt(data_center.getMaxDelay(data_center.getId(),
                            packet.getDestination())*1000);
                packet.setDelay(delay);
           }
        } catch(IOException e){
            System.err.println("Error when reading command from terminal");
            System.exit(1);
        }
   
    }




}
