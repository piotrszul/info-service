package au.gov.dhs.bom;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import generated.ProductType;


public class ParseBomApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		JAXBElement<ProductType>  productElement = (JAXBElement<ProductType>)unm.unmarshal(new File("src/test/xml/IDT60920.xml"));
		System.out.println(productElement.getValue());
	}

}
