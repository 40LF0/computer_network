package chat;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class P2P_chat{
	

	public static void main(String[] args) throws IOException {
		System.out.println(String.valueOf(args.length));
		
		InetAddress target_address;
		int portNumber;
		String user_name;
		String chat_name;
		
		if(args.length == 1) {			
			try{
				portNumber = Integer.parseInt(args[0]);
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
		
		
		String input;
		Scanner sc = new Scanner(System.in);
		System.out.println("To join chat room input like this: #JOIN chat_room_name user_name");
		while(true) {
			input = sc.nextLine();
			String[] in = input.split(" ");
			if(input.charAt(0) == "#".charAt(0)) {
        		try {
        			if(in.length ==3) {
            			if(in[0].equals("#JOIN")) {
            				chat_name = in[1];
            				user_name = in[2];
            				break;
            			}
        			}
					if(input.substring(1, 5).equals("EXIT")){
						System.out.println("exit program");
                        System.exit(0);
					}
        			System.out.println(in.length);
    				System.out.println("Wrong instruction format");
    				continue;

        		}catch(Exception x) {
        			System.out.println("Wrong instruction format");
    				continue;
        		}
			}
			
			System.out.println("To chat, you have to join chat_room");
		}
		
		String hashString = "225";
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-512");
			sh.update(chat_name.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			sb.append(hashString);
			for(int i = 3; i >= 1; i--){
				sb.append(".");
				sb.append(byteData[byteData.length - i] & 0xff);
			}
			hashString = sb.toString();
		}catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.out.println("hashing error");
			sc.close();
			return;
		}
		System.out.println(hashString);

		
		
		target_address = InetAddress.getByName(hashString);
		peer me = new peer();
		me.startServer(target_address, portNumber, user_name);
		me.startSender(target_address, portNumber, user_name);
		
		return;
		
		
	
	}
}