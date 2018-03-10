package br.com.soapboxrace.xmpp;

import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class XmppHandler {

	private XmppTalk xmppTalk;

	public XmppHandler(XmppTalk xmppTalk) {
		this.xmppTalk = xmppTalk;
	}

	public String read() {
		String read = xmppTalk.read();
		if (read != null && !read.isEmpty()) {
			if (read.contains("<message")) {
				Message message = XmppParser.parseMessageXmlStr(read);
				JID from = message.getFrom();
				JID to = message.getTo();
				String body = message.getBody();
				String subject = message.getSubject();
				if (from != null && "sbrw.engine.engine".equals(from.getNode())) {
					XmppTalk xmppTalkTo = XmppSrv.get(to.getNode());
					String personaId = xmppTalkTo.getPersonaId();
					System.out.println("personaId: [" + personaId + "]");
					xmppTalkTo.write(read.replace("sbrw.engine.engine@127.0.0.1", "sbrw.engine.engine@" + xmppTalkTo.getDomain() + "/EA_Chat"));
					System.out.println(from.getNode());
				}
				System.out.println(to.getNode());
				System.out.println(body);
				System.out.println(subject);
			}

		}
		return read;
	}

}
