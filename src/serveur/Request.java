package serveur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Request {

	private Map<String, String> dataHeader = new HashMap<String, String>();
	private Socket mySocket;
	
	public Request(Socket socket){
		
		mySocket = socket;
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			String MethodLine = in.readLine();
			if(MethodLine != null){
				dataHeader.put("METHOD", MethodLine.split(" ")[0]);
				dataHeader.put("Ressource", MethodLine.split(" ")[1]);

				String HostLine = in.readLine();
				dataHeader.put("Host", HostLine.split(" ")[1]);
			}
			//in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRelativeUrl(){
		return getHeader("Ressource");
	}
	
	public String getHost(){
		return getHeader("Host");
	}
	
	public String getHeader(String header){
		return dataHeader.get(header);
	}
	
	public PrintWriter getWritter() throws IOException{
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream())));
	}
	
	
}
