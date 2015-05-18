package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Request {

	private Map<String, String> dataHeader = new HashMap<String, String>();
	
	public Request(Socket socket){
		
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String MethodLine = in.readLine();
			dataHeader.put("METHOD", MethodLine.split(" ")[0]);
			dataHeader.put("Ressource", MethodLine.split(" ")[1]);

			String HostLine = in.readLine();
			//System.out.println(HostLine);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getHeader(String header){
		return dataHeader.get(header);
	}
}
