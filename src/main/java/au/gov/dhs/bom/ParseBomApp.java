package au.gov.dhs.bom;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import generated.ProductType;


public class ParseBomApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
		System.out.println(au.gov.dhs.bom.flume.decode.XMLDomEventDeserializer.DomBuilder.class.getName());
		System.exit(0);
		
		JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		JAXBElement<ProductType>  productElement = (JAXBElement<ProductType>)unm.unmarshal(new File("src/test/xml/IDT60920.xml"));
		System.out.println(productElement.getValue());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File("src/test/xml/IDT60920.xml"));
		System.out.println(doc);
		
		Element documentElement = (Element)doc.getDocumentElement();
		Element amocElement = (Element)documentElement.getElementsByTagName("amoc").item(0);
		
		for(Node nextSibling = amocElement.getNextSibling();nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
			System.out.println(nextSibling instanceof Element);
		}
				
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression xpathExpr = xpath.compile("//station");
		NodeList result = (NodeList)xpathExpr.evaluate(doc, XPathConstants.NODESET);
		System.out.println(result.getLength());
		//xpathExpr.evaluate();

		
	}

}
