package au.gov.dhs.bom.flume;

import java.io.File;
import java.io.FileInputStream;

import org.apache.flume.Event;
import org.junit.Test;

public class XMLEventDeserializerTest {

	@Test
	public void test() throws Exception {
		
		FileInputStream is = new FileInputStream(new File("src/test/xml/IDT60920.xml"));
		XMLEventDeserializer deserializer  = new XMLEventDeserializer(is);
		Event e = deserializer.readEvent();
		while(e != null) {
			System.out.println(e);
			System.out.println(new String(e.getBody()));
			e = deserializer.readEvent();			
		}
		deserializer.close();
	}

}
