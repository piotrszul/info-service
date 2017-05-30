package au.gov.dhs.bom;

import java.io.ByteArrayOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.specific.SpecificDatumReader;

public class JsonAvroApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Schema schema = SchemaBuilder.builder("au.gov.bom")
			.record("Test").fields()
				.requiredString("name")
				.requiredLong("age")
			.endRecord();
		GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		JsonDecoder in = DecoderFactory.get().jsonDecoder(schema, "{\"name\":\"xxx\", \"age\":1000}");
		GenericRecord rec = reader.read(null, in);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BinaryEncoder out = EncoderFactory.get().directBinaryEncoder(bout, null);
		GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
		writer.write(rec, out);
		System.out.println(rec);
		System.out.println(new String(bout.toByteArray()));
	}
}
