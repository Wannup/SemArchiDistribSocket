package serveur;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpServer {

	private Map<String, Map<String, String>> configs = new HashMap<String, Map<String, String>>();
	private int Port;

	public HttpServer(int port) {
		try {
			init(port);
			run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void run() {

		ServerSocket socket;
		try {
			socket = new ServerSocket(Port);
			
			Thread t = new Thread(new Accepter_clients(socket));
			t.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	private void init(int port) throws IOException {

		this.Port = port;

		// récupération données config.ini
		Map<String, String> configFileIni = Ini.load("config.ini");

		Set<String> cles = configFileIni.keySet();
		Iterator<String> it = cles.iterator();

		while (it.hasNext()) {
			String cle = it.next();
			String valeur = configFileIni.get(cle);
			String indice = cle.split("\\.")[1];

			if (configs.get(indice) != null) {
				configs.get(indice).put(cle.split("\\.")[2], valeur);
			} else {
				Map<String, String> lineConfig = new HashMap<String, String>();
				lineConfig.put(cle.split("\\.")[2], valeur);
				configs.put(indice, lineConfig);

			}
		}
	}

	class Accepter_clients implements Runnable {

		private ServerSocket socketserver;
		private Socket socket;
    
		public Accepter_clients(ServerSocket s) {
			socketserver = s;
		}
 
		public void run() {

			try {
				while (true) {
					socket = socketserver.accept(); 
												
					Request requete = new Request(socket);	

				
					socket.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
