package net.diezus.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionTools {
	
	public final static int TIMEOUT=10; 
	
	/*
	 * I need to ping host to understand if it is still on 
	 */
	public static boolean pingHost(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // timeout | unreachable | failed DNS lookup.
	    }
	}

}
