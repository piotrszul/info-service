package au.gov.dhs.bom;

import java.util.Arrays;

import org.w3c.dom.Document;

import au.gov.dhs.bom.api.BomClient;
import generated.AmocType;

public class RunBomSourceApp {
	public static void main(String[] args) throws Exception {

		BomClient client = new BomClient(Arrays.asList("IDT60920", "IDN60920", "IDS60920"), 30,
				new BomClient.Listener() {
					@Override
					public void onMsg(Document doc, AmocType amoc) {
						System.out.println(doc.getDocumentElement());
						System.out.println(doc.getDocumentElement().getChildNodes().item(3));

					}
				});
		client.start();
	}
}
