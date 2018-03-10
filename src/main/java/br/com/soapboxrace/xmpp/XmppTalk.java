package br.com.soapboxrace.xmpp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class XmppTalk {

	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String personaId;
	private String clientIp;
	private String currentChannelName = "GN";
	private Integer currentChannelNumber = 1;
	private String domain;

	public XmppTalk(Socket socket) {
		this.socket = socket;
		this.clientIp = socket.getInetAddress().getHostAddress();
		System.out.println("inet [" + socket.getInetAddress() + "]");
		System.out.println("local [" + socket.getLocalAddress() + "]");
		System.out.println("remotesock [" + socket.getRemoteSocketAddress() + "]");
		System.out.println("localsock [" + socket.getLocalSocketAddress() + "]");
		setReaderWriter();
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		setReaderWriter();
	}

	public Socket getSocket() {
		return socket;
	}

	private void setReaderWriter() {
		try {
			reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String read() {
		String msg = null;
		char[] buffer = new char[8192];
		int charsRead = 0;
		try {
			if ((charsRead = reader.read(buffer)) != -1) {
				msg = new String(buffer).substring(0, charsRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("C->S [" + msg + "]");
		return msg;
	}

	public void write(String msg) {
		try {
			System.out.println("S->C [" + msg + "]");
			writer.write(msg);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPersonaId() {
		return personaId;
	}

	public void setPersonaId(String personaId) {
		this.personaId = personaId;
	}

	public String getCurrentChannelName() {
		return currentChannelName;
	}

	public void setCurrentChannelName(String currentChannelName) {
		this.currentChannelName = currentChannelName;
	}

	public Integer getCurrentChannelNumber() {
		return currentChannelNumber;
	}

	public void setCurrentChannelNumber(Integer currentChannelNumber) {
		this.currentChannelNumber = currentChannelNumber;
	}

	public String getClientIp() {
		return clientIp;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}