package au.gov.dhs.flume;

import static org.apache.flume.serialization.AvroEventSerializerConfigurationConstants.STATIC_SCHEMA_URL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;
import org.apache.flume.interceptor.Interceptor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import avro.shaded.com.google.common.base.Throwables;

public class JsonToAvroConverter implements Interceptor, Configurable {

	private static final Logger logger = LoggerFactory.getLogger(JsonToAvroConverter.class);

	/**
	 * These are from AvroEventSerializer which is in flume-ng-sinks. Cannot
	 * import it as its not available in CDH maven repo
	 */
	public static final String AVRO_SCHEMA_URL_HEADER = "flume.avro.schema.url";

	private static DecoderFactory decoderFactory = DecoderFactory.get();
	private static EncoderFactory encoderFactory = EncoderFactory.get();

	private String schemaUrl;
	private Schema schema;
	private boolean addSchemaUrl;
	private GenericDatumReader<GenericRecord> reader;
	private GenericDatumWriter<GenericRecord> writer;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public Event intercept(Event event) {
		GenericRecord genericRecord = null;
		BinaryEncoder binaryEncoder = null;
		try {
			JsonDecoder jsonEncoder = decoderFactory.jsonDecoder(schema, new ByteArrayInputStream(event.getBody()));
			genericRecord = reader.read(genericRecord, jsonEncoder);
			ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream();
			binaryEncoder = encoderFactory.directBinaryEncoder(binaryOutput, binaryEncoder);
			writer.write(genericRecord, binaryEncoder);
			event.setBody(binaryOutput.toByteArray());
			if (addSchemaUrl) {
				event.getHeaders().put(AVRO_SCHEMA_URL_HEADER, schemaUrl);
			}
		} catch (IOException ex) {
			Throwables.propagate(ex);
		}
		return null;
	}

	@Override
	public List<Event> intercept(List<Event> events) {
		GenericRecord genericRecord = null;
		BinaryEncoder binaryEncoder = null;
		for (Event event : events) {
			try {
				JsonDecoder jsonEncoder = decoderFactory.jsonDecoder(schema, new ByteArrayInputStream(event.getBody()));
				genericRecord = reader.read(genericRecord, jsonEncoder);
				ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream();
				binaryEncoder = encoderFactory.directBinaryEncoder(binaryOutput, binaryEncoder);
				writer.write(genericRecord, binaryEncoder);
				event.setBody(binaryOutput.toByteArray());
				if (addSchemaUrl) {
					event.getHeaders().put(AVRO_SCHEMA_URL_HEADER, schemaUrl);
				}
			} catch (IOException ex) {
				Throwables.propagate(ex);
			}
		}
		return events;
	}

	@Override
	public void close() {
		// DO NOTHING
	}

	@Override
	public void configure(Context context) {
		logger.info("Configure");
		schemaUrl = context.getString(STATIC_SCHEMA_URL);
		logger.info("Schema Url: {}", schemaUrl);
		try {
			schema = loadFromUrl(schemaUrl);
		} catch (IOException ex) {
			logger.error("Cannot load schema", ex);
			Throwables.propagate(ex);
		}
		reader = new GenericDatumReader<>(schema);
		writer = new GenericDatumWriter<>(schema);
	}

	/**
	 * Borrowed from AvroEventSerializer
	 * 
	 * @param schemaUrl
	 * @return
	 * @throws IOException
	 */

	private Schema loadFromUrl(String schemaUrl) throws IOException {
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
