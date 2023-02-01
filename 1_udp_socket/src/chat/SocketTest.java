package chat;
import java.io.*;
import java.net.*;
import java.net.MulticastSocket;


public class SocketTest {


  public static void main(String[] args) throws IOException {

    startServer();

    startSender();
  }

  public static void startSender() throws UnknownHostException{
    InetAddress aHost = InetAddress.getByName("225.1.1.4");
    (new Thread() {
        @Override
        public void run() {

            int i=0;
            while (i<10) {
                try {
                	i++;
                	System.out.println(i);

                	
                	//Scanner sc = new Scanner(System.in);
                	//String input;
                	//input = sc.nextLine();
                    //byte data[] = (input).getBytes();
                	
                    byte data[] = (String.valueOf(i)).getBytes();
                    DatagramSocket socket = null;
                    try {
                        socket = new DatagramSocket();
                        socket.setBroadcast(true);
                    } catch (SocketException ex) {
                        ex.printStackTrace();

                    }

                    DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            aHost,
                            9090);
                    

                    System.out.println("send : "+new String(packet.getData()));
                    socket.send(packet);
                    Thread.sleep(50);
                    
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();

                }
            }
        }}).start();
    }


  public static void startServer() {
    (new Thread() {
        @Override
        public void run() {


        	MulticastSocket socket = null;
    		try {
				
				InetAddress mcastaddr = InetAddress.getByName("225.1.1.4");
				InetSocketAddress group = new InetSocketAddress(mcastaddr, 0);
				NetworkInterface netIf = NetworkInterface.getByName("bge0");
				socket = new MulticastSocket(9090);

				socket.joinGroup(group, netIf);
    		}catch (IOException ex) {
                ex.printStackTrace();
            }
            
            
            DatagramPacket packet = new DatagramPacket(new byte[512], 512);
            
            String temp;
            while (true) {
                try {
                    socket.receive(packet);
                    temp=new String(packet.getData());
                    System.out.println("received: "+temp);


                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
            
        }

    }).start();
 }
 
}