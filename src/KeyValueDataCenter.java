
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public abstract class KeyValueDataCenter extends DataCenter{

    public class Content{
        public double value;
        public double timestamp;
    }

    private Map<String, Content> key_value_map = new HashMap<String, Content>();


    public KeyValueDataCenter(int index){
        super(index);
    }


    //more API to get and store information to the key_value_map
    //
    // -- getValue(key)
    // -- insertPair(key, value)
    // -- updatePair(key, value)
    // -- deleteKey(key)
    // -- getMap() // return the whole map, in search all





}
