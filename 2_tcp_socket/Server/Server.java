import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread {
	
	static int SERVER_PORT = 1225;
	static int SERVER_PORT_2 = 1226;
	final static String MESSAGE_TO_SERVER = "Hello, Client";
	static AtomicInteger last_id = new AtomicInteger();
	static HashMap<String,Integer> chat_list = new HashMap<String,Integer>();
	static HashMap<Integer,Vector<Integer>> join_member = new HashMap<Integer,Vector<Integer>>();
	static HashMap<Integer,OutputStream> os_list = new HashMap<Integer,OutputStream>();
	static HashMap<Integer,String> member_name = new HashMap<Integer,String>();
	static ServerSocket FileSocket = null;
	
	public static void main(String[] args) {

		if(args.length == 2) {			
			try{
				SERVER_PORT = Integer.parseInt(args[0]);
				SERVER_PORT_2 = Integer.parseInt(args[1]);
	        }
	        catch (NumberFormatException ex){
	            ex.printStackTrace();
	            System.out.println("Wrong argument format");
				return;
	        }
		}
		else {
			System.out.println("Wrong argument format");
			return;
		}

		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			FileSocket = new ServerSocket(SERVER_PORT_2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
		try {
			while (true) {
				System.out.println("socket wait for connection");
				Socket socket = serverSocket.accept();
				// we should create a thread which will deal with the accepted client.
				
				
				System.out.println("host : "+socket.getInetAddress()+" | Communication connection success");
				
				/**	Path from Client to Server */
				InputStream is = socket.getInputStream();
				/**	Path from Server to Client */
				OutputStream os = socket.getOutputStream();
				
				byte[] data = new byte[256];
				int n = is.read(data);
				String messageFromClient = new String(data,0,n);
				
				System.out.println(messageFromClient);
				
				os.write( MESSAGE_TO_SERVER.getBytes() );
				os.flush();
				
				(new Thread() {
			        @Override
			        public void run() {
			        	boolean join_chat = false;
			        	String chat_name = null;
			        	String user_name = null;
			        	int id = last_id.getAndIncrement();
			            while (!join_chat) {
			            	
			                try {
			                	int n = is.read(data);
			    				String input = new String(data,0,n);
			    				String[] in = input.split(" ");
			    				if(in[0].equals("#JOIN")) {
		            				chat_name = in[1];
		            				user_name = in[2];
		            				if(chat_list.containsKey(chat_name)){
		            					int index = chat_list.get(chat_name);
		            					join_member.get(index).add(id);
		            					os_list.put(id, os);
		            					member_name.put(id, user_name);
		            					join_chat = true;
		            					String output = "#JOIN SUCCESS";
		            					os.write( output.getBytes() );
		            					os.flush();
		            					System.out.println(input + " SUCCESS");
		            					break;
		            				}
		            				else {
		            					String output = "#JOIN FAIL";
		            					os.write( output.getBytes() );
		            					os.flush();
		            					System.out.println(input + " FAIL");
		            					continue;
		            				}
		            				
		            			}
		            			if(in[0].equals("#CREATE")) {
		            				chat_name = in[1];
		            				user_name = in[2];
		            				if(chat_list.containsKey(chat_name)){
		            					String output = "#CREATE FAIL";
		            					os.write( output.getBytes() );
		            					os.flush();
		            					System.out.println(input + " FAIL");
		            					continue;
		            				}
		            				else {
		            					chat_list.put(chat_name, id);
		            					Vector<Integer> a = new Vector<Integer>();
		            					a.add(id);
		            					join_member.put(id, a);
		            					os_list.put(id, os);
		            					member_name.put(id, user_name);
		            					join_chat = true;
		            					String output = "#CREATE SUCCESS";
		            					os.write( output.getBytes() );
		            					os.flush();
		            					System.out.println(input + " SUCCESS");
		            					break;
		            				}

		            			}
			        			
		 
			                }catch (IOException ex) {
			                    ex.printStackTrace();
			                } 	
			            }
			            ///////// join chat room
			            while (true) {
			            	try {
			            		int n = is.read(data);
			    				String input = new String(data,0,n);
			    				String[] in = input.split(" ");
			    				if(in[0].equals("#EXIT")) {
			    					int index = chat_list.get(chat_name);
	            					join_member.get(index).removeElement(id);
	            					member_name.remove(id);
	            					if(join_member.get(index).isEmpty()) {
	            						join_member.remove(index);
	            						chat_list.remove(chat_name);
	            					}
	            					socket.close();
			    					break;
			    				}
			    				else if(in[0].equals("#STATUS")) {
			    					String output = "Current chat room name is "+ chat_name;
			    					os.write(output.getBytes());
	            					os.flush();
			    					int index = chat_list.get(chat_name);
			    					Vector<Integer> member_id = join_member.get(index);	 
			    					Iterator<Integer> value = member_id.iterator();
			    					output = "These are member of our char room.";
			    					os.write(output.getBytes());
	            					os.flush();
			    					int i = 0;
			    					while (value.hasNext()) {
			    						i++;	
	            						int target_id = value.next();
	            						output = " " + Integer.toString(i) + ": " + member_name.get(target_id);
	            						os.write(output.getBytes());
		            					os.flush();
	            					}
	            					
			    				}
			    				else if(in[0].equals("#PUT")) {
			    					String file_name = in[1];
			    					file_name = "output/"+file_name;
			    					System.out.println(input);
			    					Socket socket = FileSocket.accept();
			    					FileOutputStream fos = new FileOutputStream(file_name);
			    					InputStream f_is = socket.getInputStream();
			    					byte[] buffer = new byte[512];
			    		            int readBytes;
			    		            while ((readBytes = f_is.read(buffer)) != -1) {
			    		                fos.write(buffer, 0, readBytes);
			    		 
			    		            } 
			    		            f_is.close();
			    		            fos.close();
			    		            socket.close();
			    					

			    				}
			    				else if(in[0].equals("#GET")) {
			    					String file_name = in[1];
			    					file_name = "output/"+file_name;
		            				Socket file_socket = FileSocket.accept();
		            				System.out.println(input);

		            				/**	Path from Server to Client  */
		            				OutputStream f_os = file_socket.getOutputStream();
		            				
		            				FileInputStream fis = null;
		            				try {
		            					fis = new FileInputStream(file_name);
		            				}catch(FileNotFoundException e){
		            					System.out.println("File is not found.");
		            					String out = "#FAIL";

		            					f_os.write(out.getBytes() );
		            					f_os.flush();
		            	                f_os.close();
		            	                file_socket.close();
		            	                continue;
		            				}
		            				

		            				int readBytes = 0;
		            				byte[] buffer = new byte[1024];
		            				while ((readBytes = fis.read(buffer)) > 0) {
		            	                f_os.write(buffer, 0, readBytes);
		            	            }
		            				
		            				System.out.println("File transfer completed.");
		            				fis.close();
		            				f_os.close();
		            				file_socket.close();
			    				}
			    				else {
			    					int index = chat_list.get(chat_name);
	            					Vector<Integer> member_list = join_member.get(index);
	            					Iterator<Integer> value = member_list.iterator();
	            					while (value.hasNext()) {
	            						int target_id = value.next();
	            						if(target_id == id) {
	            							continue;
	            						}
	            						OutputStream target_os = os_list.get(target_id);
	            						target_os.write(input.getBytes() );
	            						target_os.flush();
	            					}
	            					
			    				}
			    				
			            	}catch (IOException ex) {
			                    ex.printStackTrace();
			                } 
			            }
			        }
			    }).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

