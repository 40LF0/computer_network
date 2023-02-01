package chat;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class peer {
	
	public void startSender(InetAddress target_address,int portNumber,String user_name) throws UnknownHostException{
    //InetAddress target_address = InetAddress.getByName("225.1.1.1");

    	String input;
    	Scanner sc = new Scanner(System.in);
    	DatagramSocket socket = null;
    	System.out.println("# chat to others");
		Loop1:
        while (true) {
            try {
            	
            	input = sc.nextLine();
            	if(input.charAt(0) == "#".charAt(0)) {
            		try {
            			if(input.substring(1, 5).equals("EXIT")) {
            				System.out.println("exit chat room");
                            System.exit(0);
							
            			}
            			else {
            				System.out.println("you cannnot chat starting with # character");
            				continue;
            			}
            		}catch(Exception x) {
            			System.out.println("you cannnot chat starting with # character");
        				continue;
            		}

            	}
            	
            	input = user_name+" : "+ input;
                byte data[] = (input).getBytes();
 
                try {
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                } catch (SocketException ex) {
                    ex.printStackTrace();

                }

                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        target_address,
                        portNumber);
                
                System.out.println(new String(packet.getData()));
                //System.out.println("we send- "+new String(packet.getData()));
                socket.send(packet);
                
                // reset packet
                StringBuffer sb = new StringBuffer();
                for(int i = 0 ; i < input.length() ;i++) {
                	sb.append(" ");
                }
                data = sb.toString().getBytes();
                try {
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                } catch (SocketException ex) {
                    ex.printStackTrace();

                }

                packet = new DatagramPacket(
                        data,
                        data.length,
                        target_address,
                        portNumber);
                socket.send(packet);
                
                
                    
            } catch (IOException ex) {
                ex.printStackTrace();
            }

    	}
                 

    }


	public void startServer(InetAddress target_address,int portNumber,String user_name) {
	    (new Thread() {
	        @Override
	        public void run() {

	
	        	MulticastSocket socket = null;
	    		try {
	    			 InetSocketAddress group = new InetSocketAddress(target_address, 0);
	    			 NetworkInterface netIf = NetworkInterface.getByName("chat");
	    			 socket = new MulticastSocket(portNumber);
	    			 socket.joinGroup(group, netIf);

	    		} catch (IOException ex) {
	                ex.printStackTrace();
                }
	            
	            DatagramPacket packet = new DatagramPacket(new byte[512], 512);
	             
	            
	            String temp;
	            while (true) {
	            	
	                try {
	                    socket.receive(packet);
	                    temp=new String(packet.getData());
	                    
	                    String[] in = temp.split(" ");
	                    if(!in[0].equals(user_name)) {
	                    	System.out.println(temp);
	                    }
	                    //System.out.println("received -"+temp);
 
	                }catch (IOException ex) {
	                    ex.printStackTrace();
	                } 	
	            }
	            
	        }


	
	    }).start();
	} 

}

