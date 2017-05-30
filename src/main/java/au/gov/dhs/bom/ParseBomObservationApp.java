package au.gov.dhs.bom;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import au.gov.dhs.bom.flume.ObservationJsonEncoder;
import generated.AmocType;
import generated.AreaType;
import generated.ElementType;
import generated.ForecastPeriodType;
import generated.LevelType;
import generated.ObservationPeriodType;
import generated.ObservationsType;
import generated.ProductType;
import generated.StationType;
import generated.TextElementType;
import twitter4j.internal.org.json.JSONObject;

public class ParseBomObservationApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ObservationJsonEncoder jsonEncoder = new ObservationJsonEncoder();
		
		JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		JAXBElement<ProductType> productElement = (JAXBElement<ProductType>) unm
				//.unmarshal(new File("src/test/xml/IDN11060.xml"));
				.unmarshal(new File("src/test/xml/IDT60920.xml"));
		//System.out.println(productElement.getValue());
		AmocType amoc = productElement.getValue().getAmoc();
		List<StationType> stations = jsonEncoder.getItems(productElement.getValue());
		
		PrintWriter out = new PrintWriter(new File("src/test/json/IDT60920.json"));
		for (StationType station : stations) {
			JSONObject observation = jsonEncoder.encode(amoc, station);
			System.out.println(observation);
			out.println(observation);
		}
		out.close();
	}

}
