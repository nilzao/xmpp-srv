package br.com.soapboxrace.xmpp;

public class XmppHandShake {

	private XmppTalk xmppTalk;
	private int pkgCount = 0;

	public XmppHandShake(XmppTalk xmppTalk) {
		this.xmppTalk = xmppTalk;
		handShakeBeforeSsl();
		handShakeAfterSsl();
	}

	private void handShakeBeforeSsl() {
		String[] packets = new String[2];
		packets[0] = "<stream:stream xmlns='jabber:client' xml:lang='en' xmlns:stream='http://etherx.jabber.org/streams' from='127.0.0.1' id='174159513' version='1.0'><stream:features><starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
		packets[1] = "<proceed xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";
		do {
			xmppTalk.read();
			xmppTalk.write(packets[pkgCount]);
			pkgCount++;
		} while (pkgCount < packets.length);
		TlsWrapper.wrapXmppTalk(xmppTalk);
		pkgCount = 0;
	}

	private void handShakeAfterSsl() {
		String personaId = "0";
		String domain = "127.0.0.1";
		String[] packets = new String[3];
		packets[0] = "";
		packets[1] = "";
		packets[2] = "";
		do {
			String read = xmppTalk.read();
			if (pkgCount == 0) {
				domain = XmppParser.parseDomainFromUser(read);
				packets[0] = "<stream:stream xmlns='jabber:client' xml:lang='en' xmlns:stream='http://etherx.jabber.org/streams' from='" + domain
						+ "' id='5000000000000A' version='1.0'><stream:features/>";
			}
			if (pkgCount == 1) {
				personaId = XmppParser.parseUserNameFromIQ(read);
				packets[1] = "<iq id='EA-Chat-1' type='result' xml:lang='en'><query xmlns='jabber:iq:auth'><username>" + personaId
						+ "</username><password/><digest/><resource/><clientlock xmlns='http://www.jabber.com/schemas/clientlocking.xsd'/></query></iq>";
				System.out.println("parse personaId: " + personaId);
			}
			if (pkgCount == 2) {
				packets[2] = "<iq id='EA-Chat-2' type='result' to='" + personaId + "@" + domain + "/EA-Chat'/>";
			}
			xmppTalk.write(packets[pkgCount]);
			pkgCount++;
		} while (pkgCount < 3);
		for (int i = 0; i < 3; i++) {
			xmppTalk.read();
		}

		xmppTalk.setPersonaId(personaId);
		xmppTalk.setDomain(domain);
		XmppSrv.addXmppClient(personaId, xmppTalk);
		getPresenceResponse(xmppTalk);
	}

	public static void getPresenceResponse(XmppTalk xmppTalk) {
		String channelName = xmppTalk.getCurrentChannelName();
		Integer channelNumber = xmppTalk.getCurrentChannelNumber();
		String personaId = xmppTalk.getPersonaId();
		String xmppIp = xmppTalk.getDomain();

		String formatString = "<presence from='channel.%s__%d@conference.%s/%s' to='%s@%s/EA-Chat' xml:lang='en'>"
				+ "<x xmlns='http://jabber.org/protocol/muc#user'><item affiliation='none' role='none'/></x></presence>";
		xmppTalk.write(String.format(formatString, channelName, channelNumber, xmppIp, personaId, personaId, xmppIp));
	}
}