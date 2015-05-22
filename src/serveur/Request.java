package serveur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

	private Map<String, String> dataHeader = new HashMap<String, String>();
	private Socket mySocket;
	String header = "";
	
	public Request(Socket socket){
		
		
		mySocket = socket;
		BufferedReader in;
		try {
			
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			List<String> socketDataHeaderLines = new ArrayList<String>();
			String inputLine;
			while(!(inputLine = in.readLine()).equals("")){
				socketDataHeaderLines.add(inputLine);
				header += inputLine+"\r\n";
				System.out.println(inputLine);
			}
			
			parseData(socketDataHeaderLines);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseData(List<String> linesHeader){
		for(String line : linesHeader){
			if(line.toUpperCase().startsWith("HOST"))
				dataHeader.put("Host", line.split(" ")[1]);
			if(line.toUpperCase().startsWith("GET"))
				dataHeader.put("Ressource", line.split(" ")[1]);
			if(line.toUpperCase().startsWith("COOKIE"))
				dataHeader.put("Cookie", line.split(" ")[1]);
		}
	}
	
	public String getRelativeUrl(){
		return dataHeader.get("Ressource");
	}
	
	public String getHost(){
		return dataHeader.get("Host");
	}
	
	public String getHeader(){
		return dataHeader.get(header);
	}
	
	public PrintWriter getWritter() throws IOException{
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream())));
	}
	
	public OutputStream getOutputStream(){
		try {
			return mySocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
