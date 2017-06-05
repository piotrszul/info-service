package au.gov.dhs.bom;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class JsonAvroApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Schema schema = loadFromUrl("file:///Users/szu004/contract/dhs/info-services/src/main/avro/tweet.avsc");
		GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		
		System.out.println(SchemaBuilder.record("au.gov").
				fields().optionalString("dsdsds").nullableString("yyy","xxxx").endRecord());
		
		JsonDecoder in = DecoderFactory.get().jsonDecoder(schema, new FileInputStream("src/test/json/tweet.json"));
		//ExtendedJsonDecoder in = new ExtendedJsonDecoder(schema, new FileInputStream("src/test/json/tweet.json"));
		GenericRecord rec = reader.read(null, in);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BinaryEncoder out = EncoderFactory.get().directBinaryEncoder(bout, null);
		GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
		writer.write(rec, out);
		System.out.println(rec);
		System.out.println(new String(bout.toByteArray()));
	}
	
	private static Schema loadFromUrl(String schemaUrl) throws IOException {
		Configuration conf = new Configuration();
		Schema.Parser parser = new Schema.Parser();
		if (schemaUrl.toLowerCase(Locale.ENGLISH).startsWith("hdfs://")) {
			FileSystem fs = FileSystem.get(conf);
			FSDataInputStream input = null;
			try {
				input = fs.open(new Path(schemaUrl));
				return parser.parse(input);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} else {
			InputStream is = null;
			try {
				is = new URL(schemaUrl).openStream();
				return parser.parse(is);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
	}

}
