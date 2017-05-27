package au.gov.dhs.bom;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

import generated.ElementType;
import twitter4j.Status;

public class AvroSchemaApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	    // get the reflected schema for packets
	    Schema schema = ReflectData.get().getSchema(Status.class);
	    System.out.println(schema);
	}
}
