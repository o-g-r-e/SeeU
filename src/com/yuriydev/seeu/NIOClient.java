package com.yuriydev.seeu;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

public class NIOClient extends Thread {
	private boolean exit;
	public void run()
	{
		//Looper.prepare();
    	
        try {
        	Selector selector = Selector.open();
        	SocketChannel socket = SocketChannel.open();
        	socket.configureBlocking(false);
        	SelectionKey clientKey = socket.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        	
        	//socket.socket().bind(new java.net.InetSocketAddress("192.168.0.104",4444));
        	//socket.socket().connect(new java.net.InetSocketAddress("192.168.0.104",4444));
        	boolean connected = socket.connect(new java.net.InetSocketAddress("192.168.0.104",4444));
        	
			
			while(!exit)
			{
				Log.i("DATA", "select");
				selector.select();
				Log.i("DATA", "select ok");
				Set keys = selector.selectedKeys();
	        
				Iterator it = keys.iterator();
	        
				while(it.hasNext())
				{
					SelectionKey key = (SelectionKey) it.next();
					SocketChannel channel=(SocketChannel)key.channel();
					
					byte[] bytes = new byte[512];
                	ByteBuffer buffer = ByteBuffer.wrap(bytes);
				    NIOSeeuMessage message = new NIOSeeuMessage();
				    
				    if((key.readyOps() & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT)
				    {
						Log.i("DATA", "connect key");
						if(channel.isConnectionPending())channel.finishConnect();
						
						
					    byte[] m = message.create(NIOSeeuMessage.HELLO_MESSAGE, "See U");
					    buffer.put(m);
					    buffer.flip();
					    channel.write(buffer);
					    while (true)
					    {
					    	int bytesNum = channel.read(buffer);
					    	if(bytesNum > 0)
					    	{
					    	bytes = new byte[512];
							buffer = ByteBuffer.wrap(bytes);
						    
						    message = new NIOSeeuMessage();
							
							message = new NIOSeeuMessage(buffer);
							Log.i("DATA", message.getParameter().toString());
					    	}
						}
					    //key.cancel();
					    //continue;
				    }
				    
				    if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ)
				    {
						Log.i("DATA", "read key");
						int bytesNum = channel.read(buffer);
						if(bytesNum <= 0)
						{
							//key.cancel();
							//Log.i("DATA", "read key canceled");
						}
						else
						{
						bytes = new byte[512];
						buffer = ByteBuffer.wrap(bytes);
					    
					    message = new NIOSeeuMessage();
						
						message = new NIOSeeuMessage(buffer);
						Log.i("DATA", message.getParameter().toString());
						}
				    }
				    
				    if((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE)
				    {
						Log.i("DATA", "write key");
				    }
					
				    
					/*if(key == clientKey)
					{
						Log.i("DATA", "connect key");
						if(socket.isConnectionPending())socket.finishConnect();
						
						
					    byte[] m = message.create(SeeuMessage.HELLO_MESSAGE, "See U");
					    buffer.put(m);
					    buffer.flip();
					    socket.write(buffer);
					    
					    
					    //key.cancel();
					}
					
					else///if(key.isReadable())
					{
						Log.i("DATA", "not connect key");
						byte[] bytes = new byte[512];
						ByteBuffer buffer = ByteBuffer.wrap(bytes);
					    
					    SeeuMessage message = new SeeuMessage();
						int bytesNum = socket.read(buffer);
						message = new SeeuMessage(buffer);
						Log.i("DATA", message.getParameter().toString());
					}*/
					//key.cancel();
				}
        	
        	/*Socket fromserver = null;
        	fromserver = new Socket("192.168.0.104",4444);
        	BufferedReader in  = new
        		     BufferedReader(new 
        		      InputStreamReader(fromserver.getInputStream()));
        		    PrintWriter    out = new 
        		     PrintWriter(fromserver.getOutputStream(),true);
        		    BufferedReader inu = new 
        		     BufferedReader(new InputStreamReader(System.in));*/
        		    
        		    
        		    
			}
			
			/*Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //place some code for GUI processing here
                }
            }*/
			//socket.close();
        
        } catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
