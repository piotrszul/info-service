package au.gov.dhs.bom;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;


public class GetBomDataApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		URL bomFtpUrl = new URL("ftp://ftp.bom.gov.au/anon/gen/fwo/IDT60920.xml");
		URLConnection connection = bomFtpUrl.openConnection();
		InputStream is = connection.getInputStream();
		IOUtils.copy(is, System.out);
		is.close();
	}
}
