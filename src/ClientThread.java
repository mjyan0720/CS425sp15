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
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   
    }




}
