
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LinearizableDataCenter extends KeyValueDataCenter{

    public LinearizableDataCenter(int index){
        super(index);
    }

//    @Overide
    //build connection with master server first
//    private void buildConnection(){
    
//    }



    public static void main(String[] args) throws IOException{
 
        if (args.length < 1) {
        	System.err.println("Usage: java DataCenter machineID(0,1,2,3)");
	        System.exit(1);
    	}
		int index = Integer.parseInt(args[0]);
        DataCenter datacenter = new LinearizableDataCenter(index);
		datacenter.buildConnection();

        datacenter.startThreads();
    }


}
