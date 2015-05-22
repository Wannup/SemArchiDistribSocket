package serveur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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

	private String getRepoRacineByHost(String host) {
		Set<String> cles = configs.keySet();
		Iterator<String> it = cles.iterator();

		while (it.hasNext()) {
			String cle = it.next();
			Map<String, String> valeur = configs.get(cle);

			if (host != null) {
				if (valeur.get("name").equals(host.split(":")[0])) {
					return valeur.get("document_root");
				}
			}
		}
		return null;
	}

	public void execute(Request requete) {

		PrintWriter out;
		try {
			out = requete.getWritter();
			out.println("");
			String racineRepo = getRepoRacineByHost(requete.getHost());

			if (racineRepo != null) {
				File fileOrRepo = new File(racineRepo
						+ requete.getRelativeUrl());
				if (fileOrRepo.isDirectory()) {
					out.println(listeRepertoire(
							configs.get("0").get("document_root"), new File(
									racineRepo + requete.getRelativeUrl())));
					out.flush();
					out.close();
				} else {
					if (!fileOrRepo.getAbsolutePath().contains("favicon.ico")) {
						System.out.println(fileOrRepo.getAbsolutePath());
						OutputStream os = requete.getOutputStream();
						if (os != null) {
							 ObjectOutputStream objOS = new ObjectOutputStream(os);
							objOS.writeObject(fileOrRepo);
							objOS.flush();
							objOS.close();
						}
						
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String listeRepertoire(String root, File repertoire) {
		// System.out.println (repertoire.getAbsolutePath());
		// System.out.println (repertoire.getPath());
		String result = "";
		// ajouter le header
		result += "<!DOCTYPE html><html><head></head><body>";

		if (repertoire.isDirectory()) {
			File[] list = repertoire.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					listeRepertoire(root, list[i]);
					if (list[i].isDirectory()) {
						result += "<a href=\""
								+ list[i].getPath().substring(root.length(),
										list[i].getPath().length()) + "\">"
								+ list[i].getName() + "<a></br>";
					} else {
						if (list[i].getName().equals("index.html")) {
							result = "<!DOCTYPE html><html><head></head><body>";
							try (BufferedReader br = new BufferedReader(
									new FileReader(list[i]))) {
								String sCurrentLine;
								while ((sCurrentLine = br.readLine()) != null) {
									result += sCurrentLine;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						} else {
							result += "<a href=\""
									+ list[i].getPath().substring(
											root.length(),
											list[i].getPath().length()) + "\">"
									+ list[i].getName() + "<a></br>";
						}
					}
				}
			} else {
				System.err.println(repertoire + " : Erreur de lecture.");
			}
		}

		result += "</body></html>";

		return result;
	}

	class Accepter_clients implements Runnable {

		private ServerSocket socketserver;
		private Socket socket;
		private Request requete;
		
		public Accepter_clients(ServerSocket s) {
			socketserver = s;
		}

		public void run() {

			try {
				while (true) {
					socket = socketserver.accept();
					System.out.println("user connected to httpServer");
					requete = new Request(socket);
					execute(requete);

					// socket.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
