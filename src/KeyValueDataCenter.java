
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public abstract class KeyValueDataCenter extends DataCenter{

    public class Content{
        public double value;
        public double timestamp;
		public Content(double v, double t){
			value = v;
			timestamp = t;
		}

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


	protected Content getValue(String key){
		return key_value_map.get(key);
	}

	protected void insertPair(String key, double value, double time){
		key_value_map.put(key, new Content(value, time));
	}

	protected void updatePair(String key, double value, double time){
		key_value_map.put(key, new Content(value, time));
	}

	protected void deleteKey(String key){
		key_value_map.remove(key);
	}

}

