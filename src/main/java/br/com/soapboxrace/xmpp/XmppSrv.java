package br.com.soapboxrace.xmpp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class XmppSrv {

	public static ConcurrentHashMap<String, XmppTalk> xmppClients = new ConcurrentHashMap<String, XmppTalk>();

	public static void addXmppClient(String personaId, XmppTalk xmppClient) {
		xmppClients.put(personaId, xmppClient);
	}

	public static void sendMsg(String personaId, String msg) {
		if (xmppClients.containsKey(personaId)) {
			XmppTalk xTalk = xmppClients.get(personaId);
			if (xTalk != null) {
				xTalk.write(msg);
			} else {
				System.err.println("xmppClient with the personaId " + personaId + " is attached to a null XmppTalk instance!");
			}
		} else {
			System.err.println("xmppClients doesn't contain personaId " + personaId);
		}
	}

	public static void removeXmppClient(String personaId) {
		xmppClients.remove(personaId);
	}

	public static void main(String[] args) throws Exception {
		new XmppSrv();
	}

	public XmppSrv() {
		System.setProperty("jsse.enableCBCProtection", "false");
		new XmppSrvRun().start();
	}

	private static class XmppSrvRun extends Thread {
		public void run() {
			try {
				System.out.println("Xmpp server is running.");
				ServerSocket listener = new ServerSocket(5333);
				try {
					while (true) {
						new Capitalizer(listener.accept()).start();
					}
				} finally {
					listener.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class Capitalizer extends Thread {
		private Socket socket;
		private XmppTalk xmppTalk;

		public Capitalizer(Socket socket) {
			this.socket = socket;
			xmppTalk = new XmppTalk(this.socket);
			System.out.println("New connection at " + socket);
		}

		public void run() {
			try {
				new XmppHandShake(xmppTalk);
				XmppHandler xmppHandler = new XmppHandler(xmppTalk);
				while (true) {
					String input = xmppHandler.read();
					if (input == null || input.contains("</stream:stream>")) {
						XmppSrv.removeXmppClient(xmppTalk.getPersonaId());
						break;
					}
				}
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");
				}
				XmppSrv.removeXmppClient(xmppTalk.getPersonaId());
				System.out.println("Connection with client closed");
			}
		}

	}

	public static XmppTalk get(String personaId) {
		return xmppClients.get(personaId);
	}

}
