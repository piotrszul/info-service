package test.morplines;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class TestJsonFlattener {

	@Test
	public void test() throws Exception {
		String tweet = Files.toString(new File("src/test/json/tweet.json"), Charsets.UTF_8);
		System.out.println(tweet);
		Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(tweet);
		for(Entry<String, Object> kv:flattenJson.entrySet()) {
			System.out.println(kv.getKey() + " : " + kv.getValue());
		}
		
	}

}
