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

	
	public static DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static String formatTimestamp(XMLGregorianCalendar xmlCal) {
		GregorianCalendar cal = xmlCal.toGregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return TIMESTAMP_FORMAT.format(cal.getTime());
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		JAXBElement<ProductType> productElement = (JAXBElement<ProductType>) unm
				//.unmarshal(new File("src/test/xml/IDN11060.xml"));
				.unmarshal(new File("src/test/xml/IDT60920.xml"));
		//System.out.println(productElement.getValue());
		AmocType amoc = productElement.getValue().getAmoc();
		List<StationType> stations = productElement.getValue().getObservations().getStation();
		
		PrintWriter out = new PrintWriter(new File("src/test/json/IDT60920.json"));
		for (StationType station : stations) {
			JSONObject observation = new JSONObject();
			observation.put("issue_time", formatTimestamp(amoc.getIssueTimeUtc().getValue()));
			observation.put("issue_time_utc", amoc.getIssueTimeUtc().getValue().toXMLFormat());
			observation.put("issue_time_local", amoc.getIssueTimeLocal().getValue().toXMLFormat());
			// wmo-id="94970" bom-id="094029" tz="Australia/Hobart" stn-name="HOBART (ELLERSLIE ROAD)" stn-height="50.50" type="AWS" lat="-42.8897" lon="147.3278" forecast-district-id="TAS_PW006" description="Hobart"
			JSONObject jsonStation = new JSONObject();
			jsonStation.put("wmo_id", station.getWmoId());
			jsonStation.put("bom_id", station.getBomId());
			jsonStation.put("tz", station.getTz());
			jsonStation.put("stn_name", station.getStnName());
			jsonStation.put("description", station.getDescription());
			jsonStation.put("forecast_district_id", station.getForecastDistrictId());
			jsonStation.put("lat", String.valueOf(station.getLat()));
			jsonStation.put("lon", String.valueOf(station.getLon()));
			observation.put("station", jsonStation);
			
			ObservationPeriodType period = station.getPeriod().get(0);
			LevelType level = period.getLevel().get(0);
			
			JSONObject jsonPeriod = new JSONObject();
			jsonPeriod.put("time", formatTimestamp(period.getTimeUtc()));
			jsonPeriod.put("time_utc", period.getTimeUtc());
			jsonPeriod.put("time_local", period.getTimeLocal());	
			observation.put("period", jsonPeriod);
			
			JSONObject jsonElements = new JSONObject();
			/// System.out.println(period);
			for (ElementType e : level.getElement()) {
				jsonElements.put(e.getType().replace('-', '_'), e.getValue());
			}
			observation.put("elements", jsonElements);
				
			System.out.println(observation);
			out.println(observation);
		}
		out.close();
	}

}
