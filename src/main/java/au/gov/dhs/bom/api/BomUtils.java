package au.gov.dhs.bom.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BomUtils {

	public static String[] STATE_CODES = {"T", "N", "S", "W","D" ,"V","Q"};
	
	public static Set<String> resolveGlobs(Collection<String> products) {
		Set<String> result = new HashSet<>();
		for(String product:products) {
			result.addAll(resolveGlob(product));
		}
		return result; 
	}
	
	public static Collection<String> resolveGlob(String product) {
		if (product.contains("*")) {
			List<String>  result = new ArrayList<>(STATE_CODES.length);
			for (String code: STATE_CODES) {
				result.add(product.replace("*", code));
			}
			return result;
		} else if (product.startsWith(":")) {
			return resolveGlobs(BOMProducts.getProduct(product.substring(1)));
		} else {
			return Arrays.asList(product);
		}
	}
}
