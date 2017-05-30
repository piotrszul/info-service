package au.gov.dhs.bom;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.LongDefault;

public class BOMAvroSchema {

	public static void main(String[] args) {

		 Schema schema = SchemaBuilder.record("forecast").namespace("au.gov.bom").fields()
			.name("area").type().record("Area").fields()
				.requiredString("aac")
				.requiredString("description")
				.requiredString("description")				
			.endRecord().noDefault()
		.endRecord();
			
		
		System.out.println(schema);
		
	}

}
