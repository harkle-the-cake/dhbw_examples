package eu.boxwork.example.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Kleiner Beispiel Webserver auf Basis von Sockets
 * @author Patrick Jungk
 * @version 1.0
 * */
public class HTTPWebserver implements Runnable{
	protected static Logger Log = LogManager.getLogger(HTTPWebserver.class.getName());

	/*
	 * private Objekte
	 * */
	public static final int DEFAULT_PORT = 8080;
	private Thread runner = null;
	private boolean active=true;
	private ServerSocket serverSocket = null;
	private static int THROTTLE = 200;
	private MODE modeToRune = MODE.SINGLE_THREADED;

	private static int port = DEFAULT_PORT;

	/*
	 * oeffentliche Einstellungen
	 * */
	public enum MODE {SINGLE_THREADED, MULTI_THREADED};
	 
	/**
	 * initialisiert den Webserver und startet diesen.
	 * */
	public void initialise()
	{
		if (runner==null)
		{
			runner=new Thread(this);
		}

		try {
			Log.info("starting listening on port: "+port);
		    serverSocket = new ServerSocket(port);
		    runner.start();
		}	catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * schlie�t den Server wieder
	 * */
	private void close()
	{
		try {
			serverSocket.close();
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Startet den Server und das "Lauschen" auf einen Port.
	 * */
	@Override
	public void run() {		
		while (active)
		{
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				
				if (modeToRune==MODE.SINGLE_THREADED)
				{
					WebrequestWorker worker = new WebrequestWorker(clientSocket, THROTTLE);
					worker.run();
				}	
				else if (modeToRune==MODE.MULTI_THREADED)
				{
					Thread worker = new Thread( new WebrequestWorker(clientSocket, THROTTLE));
					worker.start();
				}	
				else
				{
					Log.error("Server-Mode not impleemented.");
				}
				
			} catch (Exception e) {	
				Log.error(e.getLocalizedMessage());
			}
		}
		close();
		
	}
		
	/**
	 * @param modeToRune der Modus, wie der Server laufen soll (Single Threaded, Multithreaded TODO)
	 */
	public void setModeToRune(MODE modeToRune) {
		this.modeToRune = modeToRune;
	}

	/**
	 * small rate limiter
	 * */
	public void setThrottle(int val)
	{
		THROTTLE = val;
	}

	/**
	 * Testmain zur Pr�fung der bereitgestellten Methoden
	 * Parameter:
	 * single | multi (String)
	 * port (INT)
	 * throttle (INT)
	 * f�r die Anzahl der Threads
	 * */
	public static void main(String[] args) {
		
		HTTPWebserver ws = new HTTPWebserver();
		if (args.length>0)
		{
			switch (args[0].toLowerCase()) {
			case "multi":
				ws.setModeToRune(MODE.MULTI_THREADED);
				break;
			case "single":
				ws.setModeToRune(MODE.SINGLE_THREADED);
				break;
			default:
				ws.setModeToRune(MODE.SINGLE_THREADED);
				break;
			}
		}
		else
		{
			ws.setModeToRune(MODE.SINGLE_THREADED);
		}


		if (args.length>1)
		{
			port = Integer.parseInt(args[1]);
		}

		if (args.length>2)
		{
			try {
				ws.setThrottle( Integer.parseInt(args[2]) );
			}
			catch (Exception e)
			{
				System.err.println("error parsing throttle: "+e.toString());
			}
		}
		
		ws.initialise();
	}
}
