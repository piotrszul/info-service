package test.morplines;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.kitesdk.morphline.api.AbstractMorphlineTest;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.avro.ToAvroBuilder;
import org.kitesdk.morphline.base.Fields;

import com.google.common.io.Files;

public class MyMorphlineTest extends AbstractMorphlineTest {

	  @Test
	  public void testDetectMimeTypesWithDefaultMimeTypes() throws Exception {
		  
		new ToAvroBuilder();
	    morphline = createMorphline("test-morphlines/myMorphline");    
	    Record record = new Record();    
	    record.put(Fields.ATTACHMENT_BODY, Files.toByteArray(new File("src/test/json/tweet.json")));
	    startSession();
	    morphline.process(record);
	    System.out.println(collector.getFirstRecord().getFirstValue(Fields.ATTACHMENT_MIME_TYPE));
	    System.out.println(new String((byte[])collector.getFirstRecord().getFirstValue(Fields.ATTACHMENT_BODY)));
	  }

}
