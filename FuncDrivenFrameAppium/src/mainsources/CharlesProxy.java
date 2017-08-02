package mainsources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CharlesProxy extends GeneralLibrary {

	public static Process p;
	public static CloseableHttpClient httpclient = HttpClients.createDefault();
	public static HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
	public static RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	public static HttpGet request =null; 
	public static CloseableHttpResponse verifyResponse = null;
	public static BufferedReader rd = null;
	boolean b=false; 
	public static String charlesStart = "http://control.charles/recording/start";
	public static String charlesExport ="http://control.charles/session/export-csv";
	public static String charlesExportxml = "http://control.charles/session/export-xml";
	public static String charlesExporttrace = "http://control.charles/session/export-trace";
	public static String charlesClear ="http://control.charles/session/clear";
	public static String charlesBreakpointenable = "http://control.charles/tools/breakpoints/enable";
	public static String charlesBreakpointdisable = "http://control.charles/tools/breakpoints/disable";
	public static String charlesMaplocalenable = "http://control.charles/tools/map-local/enable";
	String line= "", actual_URL="";
	String final_URL = "";

	static List<String> CallsFound = new ArrayList<String>();
	static List<String> CallsNotFound = new ArrayList<String>();


	public void KillCharles() throws InterruptedException, IOException{

		p = Runtime.getRuntime().exec("taskkill /IM Charles.exe /F");
		Thread.sleep(2000);
		System.out.println("Charles closed.");
	}


	/**--Start Charles proxy--
	 * @throws IOException **/
	public void StartCharles() throws InterruptedException, IOException {

		KillCharles();

		try {
			CommandLine command = new CommandLine("cmd");
			command.addArgument("/c");
			command.addArgument("C:\\PROGRA~1\\Charles\\Charles.exe");
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			DefaultExecutor executor = new DefaultExecutor();
			executor.setExitValue(1);
			try{
				executor.execute(command, resultHandler);	
				Thread.sleep(5000);
				System.out.println("Charles started.");
			} catch(IOException e){
				e.printStackTrace();
			} catch(InterruptedException e){
				e.printStackTrace();
			}
			request = new HttpGet(charlesStart);
			request.setConfig(config);
			verifyResponse = httpclient.execute(request);

		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**--Charles Map local--**/
	public void CharlesMapLocal()throws Exception{
		request = new HttpGet(charlesMaplocalenable);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
	}


	/**--Charles Breakpoint enable--**/
	public void BreakpointCharlesenable()throws Exception{
		request = new HttpGet(charlesBreakpointenable);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
	}

	/**--Charles Breakpoint disable--**/
	public void BreakpointCharlesdisable()throws Exception{
		request = new HttpGet(charlesBreakpointdisable);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
	}

	/**--Charles clear--**/ 
	public void charlesclear() throws Exception {
		request = new HttpGet(charlesClear);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
		EntityUtils.consume(verifyResponse.getEntity());
	}

	/**--Charles Export with CSV--**/
	public String CharlesExportCSV() throws Exception{
		request = new HttpGet(charlesExport);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
		rd = new BufferedReader(new InputStreamReader(verifyResponse.getEntity().getContent()));
		while((line = rd.readLine()) != null){
			actual_URL = line.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			// actual_URL = actual_URL.replaceAll("\\+", "%2B");
			actual_URL = URLDecoder.decode(actual_URL, "UTF-8");
			final_URL  = final_URL + actual_URL ;
		}
		System.out.println(final_URL);
		return final_URL;
	}

	/**--Charles Export with xml--**/
	public String CharlesExportXML()throws Exception{

		request = new HttpGet(charlesExportxml);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
		rd = new BufferedReader(new InputStreamReader(verifyResponse.getEntity().getContent()));
		while ((line = rd.readLine()) != null) {
			actual_URL = line.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			actual_URL = actual_URL.replaceAll("\\+", "%2B");
			actual_URL = URLDecoder.decode(actual_URL, "UTF-8");
			final_URL  = final_URL + actual_URL ;
		}
		System.out.println(final_URL);
		return final_URL;
	}

	/**--Charles Export with trace--**/
	public String CharlesExportTrace()throws Exception{
		request = new HttpGet(charlesExporttrace);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
		rd = new BufferedReader(new InputStreamReader(verifyResponse.getEntity().getContent()));
		while ((line = rd.readLine()) != null) {
			actual_URL = line.split(",")[0].replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			actual_URL = URLDecoder.decode(actual_URL, "UTF-8");
			final_URL  = final_URL + actual_URL ;
		}
		System.out.println(final_URL);
		return final_URL;
	}

	public String RecordCharles() throws Exception{
		String line= "", actual_URL="";
		request = new HttpGet(charlesExport);
		request.setConfig(config);
		verifyResponse = httpclient.execute(request);
		rd = new BufferedReader(new InputStreamReader(verifyResponse.getEntity().getContent()));
		while ((line = rd.readLine()) != null) {
			actual_URL = line.split(",")[0].replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			actual_URL = URLDecoder.decode(actual_URL, "UTF-8");
			final_URL  = final_URL + actual_URL ;
		}
		return final_URL;
	}

	public boolean CharlesVerify(HashMap<String, String> m) throws Exception{

		boolean allfound;
		int found = 0;
		int notfound = 0;

		try{
			Thread.sleep(5000);
			String xmldata = CharlesExportCSV();
			System.out.println(xmldata);
			for(Entry<String, String> entry: m.entrySet()) {

				String Attribute = entry.getKey();
				String Value     = entry.getValue();						
				String Call = Attribute + "=" + Value;

				System.out.println("Looking for the call "+ "\"" + Call + "\"");
				if(xmldata.contains(Call)){
					System.out.println("\"" + Call + "\""+  " is found");
					found++;
					CallsFound.add(Call);
				} else {
					System.out.println("\""+ Call + "\""+  " is not found");
					notfound++;
					CallsNotFound.add(Call);
				}
			}
			if(m.size()==found){
				System.out.println("List of the calls found: " + CallsFound);
				System.out.println("List of the calls not found" + CallsNotFound);
				allfound =true;
			} else {
				System.out.println("List of the calls not found" + CallsNotFound);
				System.out.println("List of the calls found" + CallsFound );
				allfound = false;
			}
		}catch(Exception e){
			throw e;
		}
		return allfound;
	}
}