package au.gov.dhs.bom;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import generated.AreaType;
import generated.ElementType;
import generated.ForecastPeriodType;
import generated.ProductType;
import generated.TextElementType;
import twitter4j.internal.org.json.JSONObject;

public class ParseBomForecastApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		JAXBElement<ProductType> productElement = (JAXBElement<ProductType>) unm
				.unmarshal(new File("src/test/xml/IDN11060.xml"));
//				.unmarshal(new File("src/test/xml/IDN11020.xml"));
				System.out.println(productElement.getValue());
		List<AreaType> areas = productElement.getValue().getForecast().getArea();
		for (AreaType area : areas) {
			for (ForecastPeriodType period : area.getForecastPeriod()) {
				JSONObject obj = new JSONObject();
				///System.out.println(period);
				for (ElementType e : period.getElement()) {
					obj.put(e.getType(), e.getValue());
				}
				for (TextElementType te : period.getText()) {
					
					obj.put(te.getType(), te.getContent().get(0).toString());
						
				}
				System.out.println(obj.toString());
			}
		}
	}

}
