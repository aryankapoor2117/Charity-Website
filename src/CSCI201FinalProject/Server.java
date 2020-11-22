package CSCI201FinalProject;

/* 
* big credit for helping figure out socket networking:
* https://www.geeksforgeeks.org/socket-programming-in-java/
*
*/


import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

public class Server {
    private ServerSocket serverSocket;
	 
    public void start(int port) {
    	try {
            serverSocket = new ServerSocket(port);
            while (true)
                new ClientHandler(serverSocket.accept()).start();
    	} catch(IOException e) {
    		e.printStackTrace();
    	} finally {
    		stop();
    	}
    }
 
    public void stop() {
        try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
    private static class ClientHandler extends Thread{
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;
        private int start;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
 
        public void run(){
        	try {
	            out = new PrintWriter(clientSocket.getOutputStream(), true);
	            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	            objIn= new ObjectInputStream(clientSocket.getInputStream());
	            objOut=new ObjectOutputStream(clientSocket.getOutputStream());
	            start=SQLutil.last_id();
	            
	            String inputLine;
	            String userName;
	            String password;
	            while ((inputLine = in.readLine()) != null) {
	            	//Code in user command handling
	            	
	            	//TODO: test User.java interaction / command sending
	    			
	            	if("n".equals(inputLine)) {
	            		if(start>=0) {
	            			Post in=SQLutil.getPost(start);
	            			objOut.writeObject(in);
	            			start=start-1;
	            		}else {
	            			objOut.writeObject(new Post(null,null,null,null,null));
	            		}
	            		
					}else if ("l".equals(inputLine)){
						//user login
						userName=in.readLine();
						password=in.readLine();
						if(SQLutil.loginUser(userName,password)!=null) {
							out.println(userName);
						}else {
							out.println("");
						}
						
					}else if ("r".equals(inputLine)) {
						//user registration
						userName=in.readLine();
						password=in.readLine();
						if(SQLutil.registerUser(userName,password)!=null) {
							out.println(userName);
						}else {
							out.println("");
						}
						
					}else if ("u".equals(inputLine)) {
						//update posts
						
			            start=SQLutil.last_id();
			            
					}else if("p".equals(inputLine)) {
						//post a post
	            		try {
							Post post=(Post) objIn.readObject();
							
							if(SQLutil.makePost(post)) {
								out.println("Your post has been successfully published.");
							}else {
								out.println("Failed to post");
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
	            		
	            	}else if("e".equals(inputLine)) {
	            		//echo
	            		out.println(inputLine);
	            	}
	            	
	            	inputLine="";
	            }
	            
	            in.close();
	            out.close();
	            objIn.close();
	            objOut.close();
	            clientSocket.close();
        	}catch(IOException e) {
        		e.printStackTrace();
        	} catch (ParseException e1) {
				//TODO: figure out what to do at parse exception
				e1.printStackTrace();
			}
        }
    }
    
    public static void main(String[] args) {
    	SQLutil.loginDB();
    	Server server = new Server();
    	server.start(7777);
    }
}
