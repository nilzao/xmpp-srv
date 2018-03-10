package br.com.soapboxrace.xmpp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;

public class XmppParser {

	public static Message parseMessageXmlStr(String packetXmlStr) {
		return new Message(parseElement(packetXmlStr), true);
	}

	private static Element parseElement(String packetXmlStr) {
		Element element = null;
		InputStream stream = new ByteArrayInputStream(packetXmlStr.getBytes(StandardCharsets.UTF_8));
		SAXReader saxReader = new SAXReader();
		try {
			Document read = saxReader.read(stream);
			element = read.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return element;
	}

	public static IQ parseIQXmlStr(String packetXmlStr) {
		return new IQ(parseElement(packetXmlStr), true);
	}

	public static String parseUserNameFromIQ(String packetXmlStr) {
		IQ iq = parseIQXmlStr(packetXmlStr);
		Element childElement = iq.getChildElement();
		Element object = (Element) childElement.content().get(1);
		return object.getText();
	}

	public static String parseDomainFromUser(String packetXmlStr) {
		Element parseElement = parseElement(packetXmlStr + "</stream:stream>");
		Attribute attribute = parseElement.attribute("to");
		return attribute.getValue();
	}

}
