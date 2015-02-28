
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public abstract class KeyValueDataCenter extends DataCenter{

    public class Content implements Serializable{
        public int value;
        public long timestamp;
		public Content(int v, long t){
			value = v;
			timestamp = t;
		}

    }

    private Map<Integer, Content> key_value_map = new HashMap<Integer, Content>();


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


	protected Content getValue(int key){
		return key_value_map.get(key);
	}

	protected void insertPair(int key, int value, long time){
		key_value_map.put(key, new Content(value, time));
	}

    protected void insertPair(int key, Content content){
		key_value_map.put(key, content);
	}

	protected void updatePair(int key, int value, long time){
		key_value_map.put(key, new Content(value, time));
	}

    protected void updatePair(int key, Content content){
		key_value_map.put(key, content);
	}

	protected void deleteKey(int key){
		key_value_map.remove(key);
	}

    protected boolean containsKey(int key){
        return (key_value_map.get(key)!=null);
    }

    public void printAll(){
        System.out.println("Print out local key-value map below:");
        for(Map.Entry e : key_value_map){
            System.out.println(e.getKey()+"->"+e.getValue());
        }
    }

}

