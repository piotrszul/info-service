package au.gov.dhs.bom.flume.decode;

import org.apache.flume.Context;
import org.w3c.dom.Document;

public class DomEventDeserializerFactory {
	public static DomEventDeserializer newInstance(String name, Context ctx,  Document doc) {
		Class<? extends DomEventDeserializer.DomBuilder> builderClazz = null;
		Class<?> clazz;
		try {
			clazz = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		if (clazz != null && DomEventDeserializer.DomBuilder.class.isAssignableFrom(clazz)) {
			builderClazz = (Class<? extends DomEventDeserializer.DomBuilder>)clazz;
		} else {
			throw new RuntimeException("Cannot use class as builder: " + clazz);
		}
		DomEventDeserializer.DomBuilder builder;
		try {
			builder = builderClazz.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		return builder.build(ctx, doc);
	}
}
