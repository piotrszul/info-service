package au.gov.dhs.spark;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.databricks.spark.avro.SchemaConverters;

public class QueryBOMData {

	public static void main(String[] args) throws Exception {
		
		SparkSession  spark = SparkSession.builder().appName("Query").master("local[2]").getOrCreate();
		
		Dataset<Row> bomFeed = spark.read().json("src/test/json/IDN11060.json").toDF();
		bomFeed.printSchema();
		
		bomFeed.registerTempTable("forecast");
		
		List<Row> result = spark.sql("DESC FORMATTED forecast").collectAsList();
		
		for (Row row:result) {
			System.out.println(row);
		}

	    //bomFeed.write().format("com.databricks.spark.avro").save("target/avrooutput");

	    Schema avroSchema = SchemaConverters.convertStructToAvro(bomFeed.schema(), SchemaBuilder.record("forecast").namespace("gov.bom.au") , "gov.bom.au");
	    System.out.println(avroSchema);
	    
	    
	}
}
