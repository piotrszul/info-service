package au.gov.dhs.bom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

import au.gov.dhs.kafka.Message;
import generated.ElementType;

public class AvroSchemaApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	    // get the reflected schema for packets
	    Schema schema = ReflectData.get().getSchema(ElementType.class);
	    System.out.println(schema);	    
	 
	    Message msg = new Message("dsdsd-e32323-3232", System.currentTimeMillis(), "Hellp".getBytes());
	    msg.getHeaders().add(new Message.Header("content-type", "text/asci"));
	    DatumWriter<Message> writer = new ReflectDatumWriter<Message>(Message.class);
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    BinaryEncoder enc = EncoderFactory.get().directBinaryEncoder(out, null);
	    writer.write(msg, enc);
	    System.out.println(new String(out.toByteArray()));
	    
	    DatumReader<Message> reader = new ReflectDatumReader<>(Message.class);
	    Message inMsg = new Message(null,0L,null);
	    reader.read(inMsg, DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(out.toByteArray()), null));
	    System.out.println(inMsg);
	    System.out.println("JSON NOW:");
	    
	    ByteArrayOutputStream jsonOut = new ByteArrayOutputStream();
	    JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(schema, jsonOut);
	    writer.write(inMsg, jsonEncoder);
	    System.out.println(new String(jsonOut.toByteArray()));
	    
	}	
}
