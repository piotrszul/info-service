package au.gov.dhs.bom;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import generated.AmocType;
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
				//.unmarshal(new File("src/test/xml/IDN11060.xml"));
				.unmarshal(new File("src/test/xml/IDN11060.xml"));
		System.out.println(productElement.getValue());
		AmocType amoc = productElement.getValue().getAmoc();
		List<AreaType> areas = productElement.getValue().getForecast().getArea();
		
		PrintWriter out = new PrintWriter(new File("src/test/json/IDN11060.json"));
		for (AreaType area : areas) {
			if (area.isSetForecastPeriod() && !area.getForecastPeriod().isEmpty()) {
				JSONObject forecast = new JSONObject();
				forecast.put("issue_time_utc", amoc.getIssueTimeUtc().getValue().toXMLFormat());
				JSONObject jsonArea = new JSONObject();
				jsonArea.put("aac", area.getAac());
				jsonArea.put("description", area.getDescription());
				jsonArea.put("parent_aac", area.getParentAac());
				jsonArea.put("type", area.getType());
				forecast.put("area", jsonArea);
				List<JSONObject> forcastPeriods = new ArrayList<JSONObject>();
				for (ForecastPeriodType period : area.getForecastPeriod()) {
					JSONObject jsonPeriod = new JSONObject();
					jsonPeriod.put("start_time_utc", period.getStartTimeUtc().toXMLFormat());
					jsonPeriod.put("end_time_utc", period.getEndTimeUtc().toXMLFormat());

					JSONObject jsonElements = new JSONObject();
					/// System.out.println(period);
					for (ElementType e : period.getElement()) {
						jsonElements.put(e.getType(), e.getValue());
					}
					for (TextElementType te : period.getText()) {
						jsonElements.put(te.getType(), te.getContent().get(0).toString());
					}
					jsonPeriod.put("elements", jsonElements);
					forcastPeriods.add(jsonPeriod);
				}
				forecast.put("periods", forcastPeriods);
				System.out.println(forecast);
				out.println(forecast);
			}
		}
		out.close();
	}

}
