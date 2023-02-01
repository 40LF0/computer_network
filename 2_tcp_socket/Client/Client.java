import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	
	static String SERVER_IP = "127.0.0.1"; 
	static int SERVER_PORT = 1225;
	static int SERVER_PORT_2 = 1226;
	static String MESSAGE_TO_SERVER = "Hi, Server";
	
	static boolean join_chat = false;
	
	
	static Socket socket = null;
	static OutputStream os = null;
	static InputStream is = null;
	static byte[] data = null;
	static int number;
	static String resultFromServer;
	static String input;
	static String chat_name;
	static String user_name;
	static String path;
	public static String repeat(int count, String with) {
	    return new String(new char[count]).replace("\0", with);
	}
	
	public static void main(String[] args) {
	
		if(args.length == 3) {			
			try{
				SERVER_IP = args[0];
				SERVER_PORT = Integer.parseInt(args[1]);
				SERVER_PORT_2 = Integer.parseInt(args[2]);
			}catch (Exception ex) {
				ex.printStackTrace();
	            System.out.println("Wrong argument format");
				return;
			}
		}
		else {
			System.out.println("Wrong argument format");
			return;
		}

		Scanner sc = new Scanner(System.in);
		// if input argument is correct format,
		// now we should create connection between server and client

		try {
			/** socket communication start */
			socket = new Socket(SERVER_IP,SERVER_PORT);
			System.out.println("socket access");
		
			/**	Path from Client to Server */
			os = socket.getOutputStream();
			/**	Path from Server to Client  */
			is = socket.getInputStream();
			
			os.write( MESSAGE_TO_SERVER.getBytes() );
			os.flush();
			
			data = new byte[512];
			number = is.read(data);
			resultFromServer = new String(data,0,number);
			
			System.out.println(resultFromServer);

			//socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// connection is successfully created.
		System.out.println("connection with server is successfully created.");
		
		// user try to join chat room
		while(!join_chat){
			input = sc.nextLine();
			String[] in = input.split(" ");
			if(input.charAt(0) == "#".charAt(0)) {
        		try {
        			if(in.length ==3) {
            			if(in[0].equals("#JOIN")) {
            				chat_name = in[1];
            				user_name = in[2];
            				os.write(input.getBytes());
            				os.flush();
            				number = is.read(data);
            				resultFromServer = new String(data,0,number);
            				if(resultFromServer.equals("#JOIN SUCCESS")) {
            					System.out.println(resultFromServer);
            					join_chat = true;
            					break;
            				}
            				else {
            					System.out.println(resultFromServer);
            					continue;
            				}
            				
            			}
            			if(in[0].equals("#CREATE")) {
            				chat_name = in[1];
            				user_name = in[2];
            				os.write(input.getBytes());
            				os.flush();
            				number = is.read(data);
            				resultFromServer = new String(data,0,number);
            				if(resultFromServer.equals("#CREATE SUCCESS")) {
            					System.out.println(resultFromServer);
            					join_chat = true;
            					break;
            				}
            				else {
            					System.out.println(resultFromServer);
            					continue;
            				}
            			}
        			}
					if(input.substring(1, 5).equals("EXIT")){
						System.out.println("exit program");
                        System.exit(0);
					}
        			//System.out.println(in.length);
    				System.out.println("Wrong instruction format");
    				continue;

        		}catch(Exception x) {
        			System.out.println("Wrong instruction format");
    				continue;
        		}
			}
			else {
				System.out.println("To chat, you have to join chat_room");
			}
		}
		
		
		
	    (new Thread() {
	        @Override
	        public void run() {

	            while (true) {
	            	
	                try {
	        			number = is.read(data);
	        			resultFromServer = new String(data,0,number);
	        			System.out.println(resultFromServer);
	        			
 
	                }catch (IOException ex) {
	                    ex.printStackTrace();
	                } 	
	            }
	            
	        }


	
	    }).start();
	    try {
			while (true){
				input = sc.nextLine();
				String[] in = input.split(" ");
            	if(input.charAt(0) == "#".charAt(0)) {
            		try {
            			if(input.substring(1, 5).equals("EXIT")) {
            				System.out.println("exit chat room");
            				os.write(input.getBytes());
                			os.flush();
                			socket.close();
                			sc.close();
                            System.exit(0);
							
            			}
            			else if(in[0].equals("#STATUS")) {
            				os.write(input.getBytes());
                			os.flush();
            				continue;
            			}
            			else if(in[0].equals("#PUT")) {
            				String file_name = in[1];
            				file_name = "input/"+file_name;
            				Socket file_socket = new Socket(SERVER_IP,SERVER_PORT_2);
            				//System.out.println("socket access");
            				//System.out.println(input);

            				/**	Path from Server to Client  */
            				OutputStream f_os = file_socket.getOutputStream();
            				
            				
            				os.write(input.getBytes());
            				os.flush();
            				FileInputStream fis = null;
            				try {
            					fis = new FileInputStream(file_name);
            				}catch(FileNotFoundException e){
            					System.out.println("File is not found.");
            	                f_os.close();
            	                file_socket.close();
            	                continue;
            				}
            				Thread.sleep(300);
            				while(!file_socket.isConnected()){
            	                System.out.println("wait for file socket connection.");   
            	            }
            				int readBytes = 0;
            				byte[] buffer = new byte[1024];
            				int totalreadBytes = 0;
            				int index = 0;
            				while ((readBytes = fis.read(buffer)) > 0) {
            	                f_os.write(buffer, 0, readBytes);
            	                totalreadBytes += readBytes;
            	                int num = totalreadBytes/(1024*64);
            	                if(index != num) {
            	                	String out = repeat(1, "#");
                	                System.out.print(out);
                	                index = num;
            	                }
            	                
            	            }
            				System.out.println("");
            				System.out.println("File transfer completed.");
            				try {
            					fis.close();
                				f_os.close();
                				file_socket.close();
            				}
            				catch(Exception x) {
	            				continue;
	                		}
            			}
            			else if(in[0].equals("#GET")) {
            				String file_name = in[1];
            				file_name = "input/"+file_name;
	    					Socket file_socket = new Socket(SERVER_IP,SERVER_PORT_2);
	    					//System.out.println(input);
            				os.write(input.getBytes());
            				os.flush();
            				Thread.sleep(300);
            				FileOutputStream fos = null;
	    					InputStream f_is = file_socket.getInputStream();
	    					byte[] buffer = new byte[512];
	    		            int readBytes;
	    		            int totalreadBytes = 0;
	    		            int index = 0;

	    		            readBytes = f_is.read(buffer);
	    		            String error = new String(buffer,0,readBytes);
	    		            if(error.substring(0,5).equals("#FAIL")) {
	    		            	System.out.println(error);
	    		            	f_is.close();
		    		            file_socket.close();
		    		            System.out.println("File is not found.");
		    		            continue;
	    		            }
	    		            else {
	    		            	fos = new FileOutputStream(file_name);
	    		            	fos.write(buffer, 0, readBytes);
	    		                totalreadBytes += readBytes;
	    		            }
	    		            while ((readBytes = f_is.read(buffer)) != -1) {
	    		                fos.write(buffer, 0, readBytes);
	    		                totalreadBytes += readBytes;
	    		                int num = totalreadBytes/ (1024*64);
            	                if(index != num) {
            	                	String out = repeat(1, "#");
                	                System.out.print(out);
                	                index = num;
            	                }
	    		            } 
	    		            System.out.println("");
	    		            System.out.println("File transfer completed.");
	    		            try {
	    		            	f_is.close();
		    		            fos.close();
		    		            file_socket.close();
	    		            }catch(Exception x) {
	            				continue;
	                		}
	    		            
            				
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
            	os.write(input.getBytes() );
    			os.flush();
			}
	    }	
		catch(IOException ex){
			ex.printStackTrace();
		}
			
		
		
		
		
		
		
	}
}