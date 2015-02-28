import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class ModeMsgThread extends MessageThread{
	private ObjectOutputStream leader_obj;// = new ObjectOutputStream();
	public ModeMsgThread(DataCenter c){
		super(c);
	}
}
