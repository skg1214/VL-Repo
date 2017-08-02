package mainsources;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@SuppressWarnings({ "rawtypes", "unchecked"})
public class RunTests extends GeneralLibrary{

	public int failed = 0;
	public int passed = 0;
	public Properties props = new Properties();
	public GeneralLibrary generalLibrary = null;
	public WritableWorkbook result = null;
	public WritableSheet writeSheet = null;
	public WebDriver driver;
	public static String browser;
	public static String deviceName;
	public static String platformVersion;
	public static String os;
	public static String udid;
	public String baseURL;
	public String appFilePath;
	public int ExecuteTest;
	public ArrayList <Integer> testCases =  new ArrayList <Integer>();
	public ArrayList <String> testdataSheet =  new ArrayList <String>();
	public int j = 1;
	public int totalcases;
	public int executed;
	public int skipped;
	String className = "";
	public HashMap<Integer , String> errorlogs = new HashMap<Integer, String>();
	HashMap< Integer,ErrorLogger> errormap = new HashMap<Integer, ErrorLogger>();
	DesiredCapabilities iosappcab = new DesiredCapabilities();
	String resultsFile;
	static String devicedriver;
	SimpleDateFormat timeStamp = new SimpleDateFormat("MMddyy_HHmmss");
	static String SystemPath;

	public static void main(String arp[]) throws Exception{

		SystemPath = System.getProperty("user.dir");

		RunTests rt = new RunTests();
		AppiumServer server = new AppiumServer();
		server.startServer();
		rt.GetTestConfig();
		Thread.sleep(7000);
		rt.InvokeBrowser();
		rt.ReadSheet();
		rt.CreateSheet();
		rt.TestMain();
		rt.TearDown();
		server.stopServer();
	}

	/**--Read the properties from the Config file--**/
	public void GetTestConfig(){

		try {
			InputStream is = new FileInputStream(SystemPath+File.separator+"Config.ini");
			System.out.println("Getting properties from the config file");
			props.load(is);
			is.close(); 
		}catch(Exception e){
			System.out.println("Unable to load properties from config file");
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		os			=  props.getProperty("OS");
		browser	    = props.getProperty("Browser").toLowerCase();
		deviceName  = props.getProperty("deviceName");
		platformVersion = props.getProperty("platformVersion");
		baseURL = props.getProperty("testURL");
		appFilePath = props.getProperty("appfilepath");
		ExecuteTest = props.getProperty("ExecuteTest").length();

		if(os == "iOS"){
			udid = props.getProperty("UDID");
			if(udid.length() == 0){
				System.out.println("Please provide the specifc UDID");
				System.exit(1);
			}
			if(udid == null){
				System.out.println("UDID to test is not provided in ConfigFile");
				System.exit(1);
			}
		}		
		if (browser.length() == 0){
			System.out.println("Please provide the specific browser needed");
			System.exit(1);
		}
		if(deviceName.length() == 0){
			System.out.println("Please provide the specific device name");
			System.exit(1);
		}
		if(deviceName == null){
			System.out.println("Device name to test is not provided in ConfigFile");
			System.exit(1);
		}
		if(platformVersion.length() == 0){
			System.out.println("Please provide the specific platform version");
			System.exit(1);
		}
		if(platformVersion == null){
			System.out.println("platform Version to test is not provided in ConfigFile");
			System.exit(1);
		}
		if(baseURL == null){
			System.out.println("BaseURL to test is not provided in ConfigFile");
			System.exit(1);
		}
		if(ExecuteTest == 0){
			System.out.println("No of tests to be executed is not provided in ConfigFile");
			System.exit(1);
		}
		
		String[] tokens = props.getProperty("ExecuteTest").split(",");

		for (int i = 0; i < tokens.length; i++) {

			System.out.println("TestCases: " + tokens[i]);

			if (!tokens[i].contains("-"))
				testCases.add(Integer.parseInt(tokens[i]));
			else {
				String[] range = tokens[i].split("-");
				int f = Integer.parseInt(range[0]);
				int t = Integer.parseInt(range[1]);
				testCases.add(f);
				while (f != t) {
					f = f + 1;
					testCases.add(f);
				}
			}
		}
	}

	/**--Calling Browser Initiation--
	 * @throws Exception **/
	public void InvokeBrowser() throws Exception{
		if (browser.equalsIgnoreCase("ie") || browser.equalsIgnoreCase("iexplore"))
		{
			System.setProperty("webdriver.ie.driver", SystemPath + "\\drivers\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		}
		else if(browser.equalsIgnoreCase("chrome"))
		{
			System.setProperty("webdriver.chrome.driver", SystemPath + "\\drivers\\chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("test-type");
			driver = new ChromeDriver(options);
			driver.get("http://www.google.com");
		}
		else if (browser.equalsIgnoreCase("firefox"))
		{
			driver = new FirefoxDriver();
		}
		else if(browser.equalsIgnoreCase("iosnativeapp"))
		{
			devicedriver = "iOS";
			DesiredCapabilities iosappcab = new DesiredCapabilities();
			iosappcab.setCapability(CapabilityType.BROWSER_NAME, "iOS");
			iosappcab.setCapability("browserName", "iOS");
			iosappcab.setCapability(CapabilityType.VERSION, "8.4");
			iosappcab.setCapability("deviceName", "iPhone 6");
			iosappcab.setCapability("app", appFilePath);
			iosappcab.setCapability("automationName","appium");
			iosappcab.setCapability("autoAcceptAlerts", true);
			iosappcab.setCapability("newCommandTimeout", 120);
			try{
				System.out.println("Invoking browser " +browser);
				driver = new IOSDriver(new URL(baseURL), iosappcab);
			}
			catch(MalformedURLException e1){
				e1.printStackTrace();
			}
		}
		else if(browser.equalsIgnoreCase("iosnativerealdevice"))
		{
			File appDir = new File("/Users/phaneendra/Downloads/Users-2/Shared/Jenkins/Home/workspace/webmdrx-ios_automation/build");
			File app = new File(appDir, "WebMDRx.app");
			DesiredCapabilities iosappcab = new DesiredCapabilities();
			iosappcab.setCapability(CapabilityType.BROWSER_NAME, "iOS");
			iosappcab.setCapability("deviceName", "iPhone6");
			iosappcab.setCapability("udid", "dac7004f8b63aa7a6de8754c110c0798ef41c409");
			iosappcab.setCapability("platformName", "iOS");
			iosappcab.setCapability("platformVersion", "8.0.2");
			iosappcab.setCapability("app", app.getAbsolutePath());
			try{

				driver = new RemoteWebDriver(new URL(baseURL),iosappcab);
				System.out.println("Invoking browser");
			}
			catch(MalformedURLException e1){
				e1.printStackTrace();
			}
		}
		else if (browser.equalsIgnoreCase("androidnativeapp"))
		{
			devicedriver = "Android";
			File app = new File(SystemPath+File.separator+"app"+File.separator+"com.whatsapp_v2.16.361-451514_Android-2.3.4.apk");
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("deviceName", "AtomEmulator-5554");
			capabilities.setCapability("browserName", "Android");
			capabilities.setCapability("platformVersion", "5.0.2");
			capabilities.setCapability("api", "21");
			capabilities.setCapability("platformName", "Android");
			capabilities.setCapability("app", app.getAbsolutePath());
			capabilities.setCapability("appPackage", "com.whatsapp.");
			capabilities.setCapability("appActivity", "com.whatsapp.Main");

			try{
				System.out.println("Invoking browser "+browser);
				driver = new AndroidDriver(new URL(baseURL), capabilities);
				driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
			}
			catch(MalformedURLException e1){
				e1.printStackTrace();
			}
		}
		else if (browser.equalsIgnoreCase("realandroiddevice"))
		{
			devicedriver = "Android";
			File appDir = new File("/Users/phaneendra/Downloads/");
			File app = new File(appDir, "WebMDRX-Android-1_2_1-159-switcher.apk");
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("deviceName", "Android");
			capabilities.setCapability("browserName", "Android");
			capabilities.setCapability("platformVersion", "4.4.4");
			capabilities.setCapability("platformName", "Android");
			capabilities.setCapability("app", app.getAbsolutePath());
			capabilities.setCapability("appPackage", "com.webmd.webmdrx");
			capabilities.setCapability("appActivity", "com.webmd.webmdrx.HomeActivity");

			try{
				System.out.println("Invoking browser");
				driver =  new AndroidDriver(new URL(baseURL), capabilities);
				driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
			}
			catch(MalformedURLException e1){
				e1.printStackTrace();
			}
		}
		else if(browser.equalsIgnoreCase("genyemulator")){
			File appDir = new File("/Users/phaneendra/Downloads/");
			File app = new File(appDir, "WebMDRX-Android-1_2_1-159-switcher.apk");
			DesiredCapabilities capabilitiesgeny = new DesiredCapabilities();
			capabilitiesgeny.setCapability("deviceName", "Google Nexus 5");
			capabilitiesgeny.setCapability("browserName", "Android");
			capabilitiesgeny.setCapability("api", "21");
			capabilitiesgeny.setCapability("platformName", "Android");
			capabilitiesgeny.setCapability("app", app.getAbsolutePath());
			capabilitiesgeny.setCapability("appPackage", "com.webmd.webmdrx");
			capabilitiesgeny.setCapability("appActivity", "com.webmd.webmdrx.HomeActivity");
			try{
				System.out.println("Invoking browser");
				driver = new AndroidDriver(new URL(baseURL), capabilitiesgeny);
				driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
			}
			catch(MalformedURLException e1){
				e1.printStackTrace();
			}
		}
		else if(browser.equalsIgnoreCase("ChromeAndroid")){

			File appDir = new File(SystemPath+File.separator+"app"+File.separator);
			File app =  new File(appDir, "com.android.chrome_49.apk");
			DesiredCapabilities capabilities= new DesiredCapabilities();
			capabilities.setCapability("browserName", "Chrome");
			capabilities.setCapability("device", "Android");
			capabilities.setCapability("deviceName", "TestingDevice:5554");
			capabilities.setCapability("platformVersion", "5.1.1");
			capabilities.setCapability("platformName", "Android");
			capabilities.setCapability("app", app.getAbsolutePath());
			capabilities.setCapability("appPackage", "com.android.chrome");
			capabilities.setCapability("appActivity", "org.chromium.chrome.browser.document.ChromeLauncherActivity");
			try{
				System.out.println("Invoking "+browser);
				driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
				driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
			}catch(MalformedURLException e1){
				e1.printStackTrace();
			}
		}

		else if(browser.equalsIgnoreCase("AndroidChrome")){

			DesiredCapabilities capabilities= new DesiredCapabilities();
			// capabilities.setCapability("platformName", "Android");
			capabilities.setCapability("platformName", os);
			capabilities.setCapability("platformVersion", platformVersion);
			capabilities.setCapability("device", "Android");
			capabilities.setCapability("deviceName", deviceName);
			capabilities.setCapability("app", "Chrome");
			capabilities.setCapability("appPackage", "com.android.chrome");
			capabilities.setCapability("appActivity", "org.chromium.chrome.browser.document.ChromeLauncherActivity");
			capabilities.setCapability("newCommandTimeout", 120);
			capabilities.setCapability("launchTimeout", "100000");
			System.out.println("Invoking "+browser);
			driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
		}

		else if(browser.equalsIgnoreCase("iOSRealDeviceIpad")){

			DesiredCapabilities capabilities = new DesiredCapabilities();
			// capabilities.setCapability("platformName", "iOS");
			capabilities.setCapability("platformName", os);
			capabilities.setCapability("platformVersion", "9.3"); //Replace this with your iOS version
			// capabilities.setCapability("udid","845377832185a49ee2585a5fa3fed8a27838f327");
			capabilities.setCapability("udid",udid);
			capabilities.setCapability("deviceName", "iPad Air");
			capabilities.setCapability("browserName", "Safari");
			System.out.println("Invoking "+browser);
			driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
		}

		else if(browser.equalsIgnoreCase("iOSRealDeviceIphone5s")){

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("platformName", "iOS");
			capabilities.setCapability("platformVersion", "9.3"); 
			// capabilities.setCapability("udid","a87bc7ce634b9f0a853e022d6c0fc81d98c03285");
			capabilities.setCapability("udid",udid);
			capabilities.setCapability("deviceName", "iPhone 5");
			capabilities.setCapability("browserName", "Safari");
			System.out.println("Invoking "+browser);
			driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
		}
	}

	public void setNoResetSetToTrue() throws Throwable {
		if (browser.equals("iOS")){
			iosappcab.setCapability("noReset",true);
			driver = new IOSDriver(new URL(baseURL),iosappcab);
		}
	}

	public void setNoResetToFalse() throws Throwable {
		if (browser.equals("iOS")){
			iosappcab.setCapability("noReset",false);
			driver = new IOSDriver(new URL(baseURL),iosappcab);
		}
	}

	/**--Reading the Testcases from the excel file--**/
	public void ReadSheet() throws Exception{

		String FilePath = SystemPath+File.separator+"TestCasesWebMD.xls";
		FileInputStream fs = new FileInputStream(FilePath);
		Workbook input = Workbook.getWorkbook(fs); 
		Sheet sheet = input.getSheet(0);

		System.out.println("Reading the testcases from excel file");

		int rows   = sheet.getRows();
		totalcases = rows-1;

		for(int i=1;i<rows;i++)

		{
			int caseid = Integer.parseInt(sheet.getCell(0,i).getContents().replace(".0",""));

			if(testCases.contains(caseid)){

				String title 	= sheet.getCell(1,i).getContents();
				String executor = sheet.getCell(2,i).getContents();
				String testData = sheet.getCell(3,i).getContents();
				String Url 		= sheet.getCell(4,i).getContents();
				String Locators = sheet.getCell(5,i).getContents();

				if(executor == null){
					System.out.println("Skipping case ::" + caseid + " :: Executor is null");
				}else{
					testdataSheet.add(caseid+"");
					testdataSheet.add(title);
					testdataSheet.add(executor);
					testdataSheet.add(testData);
					testdataSheet.add(Url);
					testdataSheet.add(Locators);
					ErrorLogger log =new ErrorLogger();
					log.setCno(caseid);
					log.setCaseName(title);
					errormap.put(caseid, log);
				}
			}
		}
		input.close();
	}

	/**--Creating empty excel file in the results folder--**/
	public void CreateSheet() throws Exception{

		System.out.println("Creating a empty excel sheet in the results folder");

		resultsFile =  SystemPath+File.separator+"Results"+File.separator+"TestResults_"+ timeStamp.format(new Date()) + ".xls";

		result = Workbook.createWorkbook(new File(resultsFile));
		writeSheet = result.createSheet("Results", 0);
		Label lb = new Label(0, 0, "TestCase_No.");
		writeSheet.addCell(lb);
		lb = new Label(1, 0, "TestCase_Name");
		writeSheet.addCell(lb);
		lb = new Label(2, 0, "Test Function_Name");
		writeSheet.addCell(lb);
		lb = new Label(3, 0, "Test_Data");
		writeSheet.addCell(lb);
		lb = new Label(4, 0, "TestCase_Status");
		writeSheet.addCell(lb);
		lb = new Label(5, 0, "Error_Message");
		writeSheet.addCell(lb);
		lb = new Label(6,0,"Calls Found");
		writeSheet.addCell(lb);
		lb = new Label(7,0,"Calls Not Found");
		writeSheet.addCell(lb);
		lb = new Label(8, 0, "Execution Time(Sec)");
		writeSheet.addCell(lb);
		lb = new Label(9, 0, "Execution Date & Time");
		writeSheet.addCell(lb);
		System.out.println("Excel Sheet created");
	}

	/**--Update the excel sheet with the final results--**/
	public void UpdateSheet(List<String > dataSheet) throws Exception{

		Label lb = new Label(0, j, dataSheet.get(0));
		writeSheet.addCell(lb);
		lb = new Label(1, j, dataSheet.get(1));
		writeSheet.addCell(lb);
		lb = new Label(2, j, dataSheet.get(2));
		writeSheet.addCell(lb);
		lb = new Label(3, j, dataSheet.get(3));
		writeSheet.addCell(lb);
		lb = new Label(4, j, generalLibrary.isPassed());
		writeSheet.addCell(lb);
		lb = new Label(5, j, generalLibrary.tc_comment);
		writeSheet.addCell(lb);
		lb = new Label(6,j,CharlesProxy.CallsFound.toString());
		writeSheet.addCell(lb);
		CharlesProxy.CallsFound.clear();
		lb = new Label(7,j,CharlesProxy.CallsNotFound.toString());
		writeSheet.addCell(lb);
		CharlesProxy.CallsNotFound.clear();
		lb = new Label(8, j, String.valueOf(duration()));
		writeSheet.addCell(lb);
		lb = new Label(9, j, new Date().toString());
		writeSheet.addCell(lb);
		j++;
	}

	/**--Calling the TestCases Methods and executing--**/
	public void TestMain() throws Exception{

		org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RunTests.class.getName());
		int count = testdataSheet.size();
		List <String> dataSheet =  new ArrayList <String>();
		String methodName = "";
		String testUrl= "";
		String testData = "";
		String testlocators = "";
		for(int i =0; i < count-3; i = i+6) {

			try{
				if (browser.equalsIgnoreCase("iosnativeapp") || browser.equalsIgnoreCase("androidnativeapp")){
					((AppiumDriver) driver).closeApp();

					((AppiumDriver) driver).launchApp();
				}else if (browser.equalsIgnoreCase("realandroiddevice")){
					((AndroidDriver)driver).closeApp();
					((AndroidDriver)driver).launchApp();
				} 
				generalLibrary = new GeneralLibrary();
				dataSheet =  new ArrayList <String>();
				dataSheet = testdataSheet.subList(i, i+6);
				StringTokenizer sa = new StringTokenizer(dataSheet.get(2),"||");
				className = sa.nextToken();
				methodName = sa.nextToken();
				testData = dataSheet.get(3);
				testUrl  = dataSheet.get(4);
				testlocators = dataSheet.get(5);
				System.out.print("Executing Test Case - NO: " + dataSheet.get(0) + "-"+ className + " - "	+ dataSheet.get(1) + " - ");
				StartTime();
				Class cobj = Class.forName("testcases." + className);
				generalLibrary = (GeneralLibrary) cobj.newInstance();
				generalLibrary.setWebDriver(driver);
				generalLibrary.baseURL = baseURL;
				generalLibrary.setTestData(dataSheet.get(3));
				if(dataSheet.get(3)!=null && dataSheet.get(3).length()>0)
				{
					HashMap<String, String> maps = new HashMap<String, String>();
					// String[] data =dataSheet.get(3).split(",");
					String[] data =dataSheet.get(3).split(";");
					for(String d : data){
						String[] x = d.split("=");
						if(x.length > 1 && x[1] != null && x[1].length() > 0 && x[0] != null && x[0].length() > 0){
							maps.put(x[0],x[1]);

						}else {
							throw new Exception("Data is not provided for " + x[0]);
						}
					}
					generalLibrary.setTdata(maps);
				}
				generalLibrary.setTurl(testUrl);

				/*				if(dataSheet.get(5)!=null && dataSheet.get(5).length()>0)
				{
					HashMap<String, String> testlocators1 = new HashMap<String, String>();
					String[] locators =dataSheet.get(5).split(",");

					for(String l:locators){				
						String[] locarr = l.split("=", 2);
						if(locarr.length > 1 && locarr[1] != null && locarr[1].length() > 0 && locarr[0] != null && locarr[0].length() > 0){
							testlocators1.put(locarr[0],locarr[1]);
						} else {
							throw new Exception("Data is not provided for " + locarr[0]);
						}
					}
					generalLibrary.setTLocators(testlocators1);
				}
				 */
				generalLibrary.setTLocators(testlocators);

				Method mobj = cobj.getDeclaredMethod("test_" + methodName);
				mobj.invoke(generalLibrary);

				// driver = generalLibrary.getWebDriver();

			} catch (ClassNotFoundException e) {
				generalLibrary.tc_comment = new String("Class "+className	+ ": not found");
			} catch (NoSuchMethodException e) {
				generalLibrary.tc_comment = new String("Method "+methodName+ " : not found");
			} 
			catch (NoSuchElementException e) {
				generalLibrary.tc_comment = "[["+ "Element is not visible" + "]]\n"+ e.getMessage();
			}catch (Exception e) {
				if (e.getCause() != null)
					generalLibrary.tc_comment = "[["+  e.getCause().getMessage() + "]]\n";
				else
					generalLibrary.tc_comment = "[["+  e.getMessage() + "]]\n";
			}

			finally {
				if (generalLibrary.isPassed) {
					System.out.println("Pass");
					// screenshot();
					int x =Integer.parseInt(dataSheet.get(0));
					errormap.get(x).setStatus("Passed");
					passed++;
				} else {
					System.out.println("Fail");
					// screenshot();
					System.out.print(generalLibrary.tc_comment);
					//System.out.println(errormap.get(2));
					int x =Integer.parseInt(dataSheet.get(0));
					errormap.get(x).setStatus("Failed");
					errormap.get(x).setComment(generalLibrary.tc_comment);
					failed++;
				}
				System.out.println();
				System.out.println("**********************************************************************\n");
				EndTime();
				System.out.println("Duration of the test is: " + duration());
				System.out.println("Updating the data sheet");
				UpdateSheet(dataSheet);
				System.out.println("Updated the data sheet");
			}
		}
	}

	/**--Take screenshot--**/
	public void screenshot(){
		try{
			TakesScreenshot ts = (TakesScreenshot)driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(source, new File("./Screenshots/"+source.getName()));
			// FileUtils.copyFile(source, new File("./Screenshots/"+source.getName()+".png"));
			System.out.println("Screenshot taken");
		}catch(Exception e){
			System.out.println("Exception while taking the screenshot"+e.getMessage());
		}
	}

	/**--Creating the HTML Report for the executed Testcases--**/
	public String CreateHtmlReport(){

		try{
			executed = testCases.size();
			String htmlFile = SystemPath+File.separator+"Results"+File.separator+"TestResults_"+ timeStamp.format(new Date()) + ".html";
			File f=new File(htmlFile);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			String content ="<b>Help</b>";
			String ln = "\n";
			Date d = new Date();
			String headContent = "<html> "
					+ ln
					+ " <head>"
					+ ln
					+ " <style>"
					+ ln
					+ "	td.header {"
					+ ln
					+ " background-color:#3399FF;border-top:0px solid #333333;border-bottom:1px dashed #000000;"
					+ ln
					+ "	}"
					+ " td.testDetails { "
					+ ln
					+ " background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:1px dashed #000000;"
					+ ln
					+ "	}"
					+ ln
					+ " span.testDetails {"
					+ ln
					+ " font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;"
					+ ln
					+ "}"
					+ ln
					+ "td.execDetails { "
					+ ln
					+ " background-color:#3399FF;border-top:5px solid #3399FF;border-bottom:0px dashed #000000;"
					+ ln
					+ "}"
					+ ln
					+ " span.execDetails {"
					+ ln
					+ " font-size:12px;font-weight:bold;color:#000000;line-height:200%;font-family:verdana;text-decoration:none;"
					+ ln
					+ "}"
					+ ln
					+ "span.pass { "
					+ ln
					+ " font-size: 14px;font-weight:bold;line-height:100%;color:#00FF00;font-family:arial; "
					+ ln
					+ "	}"
					+ ln
					+ " span.fail { "
					+ ln
					+ " font-size: 14px;font-weight:bold;color:#FF0000;line-height:100%;font-family:arial; "
					+ ln
					+ " } "
					+ ln
					+ " span.skip { "
					+ ln
					+ " font-size: 14px;font-weight:bold;color:#0000FF;line-height:100%;font-family:arial; "
					+ ln
					+ " } "
					+ ln
					+ " span.title { "
					+ " font-size: 14px;font-weight:normal;color:#000000;line-height:100%;font-family:arial; "
					+ ln
					+ " } "
					+ ln
					+ " td.reqDetails { "
					+ ln
					+ " font-size:12px;font-weight:bold;color:#000000;line-height:100%;font-family:verdana;text-decoration:none; "
					+ ln
					+ " } "
					+ ln
					+ " td.reqData {  "
					+ ln
					+ " font-size:12px;color:#000000;line-height:100%;font-family:verdana;text-decoration:none; "
					+ ln
					+ " } "
					+ ln
					+"table {"
					+"border-collapse: collapse;"
					+"}"
					+ ln
					+"table, td, th {"
					+"   border: 1px solid black;"
					+"}"
					+ ln
					+ " </style> "
					+ ln
					+ " </head> "
					+ ln
					+ "<body leftmargin=\"0\" marginwidth=\"0\" topmargin=\"0\" marginheight=\"0\" offset=\"0\" bgcolor='#FFFFFF'>";

			String header = "<div id=\"header\"> "
					+ ln
					+ " <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
					+ ln
					+ " <tr> "
					+ ln
					+ "<td align=\"left\" valign=\"middle\" class=\"header\"> "
					+ ln
					+ "</td>"
					+ ln
					+ "<td align=\"middle\""
					+ " valign=\"middle\" class=\"header\">"
					+ ln
					+ "<span style=\"font-size:14px;font-weight:bold;color:#000000;line-"
					+ "height:200%;font-family:verdana;text-decoration:none;\">"
					+ ln
					+ "AUTOMATION TEST RESULTS"
					+ ln
					+ "</span>"
					+ ln
					+ "</td>"
					+ ln
					+ "<td align=\"\" valign=\"middle\" style=\"background-color:#3399FF;border-top:0px solid #000000;border-bottom:"
					+ "1px dashed #000000;\">"
					+ ln
					+ " <span style=\"font-size:15px;font-weight:bold;color:#000000;line-height:100%;font-family:verdana;"
					+ "text-decoration:none;\">" + ln + "</span>" + ln + "</td>"
					+ ln + " </tr>" + ln + "</table>" + ln + "</div>";

			String [] p = appFilePath.split("/");
			int l =p.length;
			String appName = p[l-1];
			String testDetails = "<div id=\"testDetails\">"
					+ ln
					+ "<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> "
					+ ln
					+ "<tr> "
					+ ln
					+ " <td align=\"left\" valign=\"middle\" class=\"testDetails\"> "
					+ ln + "<span  class=\"testDetails\">" + ln + " Date &amp; Time : "
					+ d.toString() + ln + "</span>" + ln + "</td>" + ln
					+ "<td align=\"left\" "
					+ "valign=\"middle\" class=\"testDetails\" colspan=\"2\"> " + ln
					+ "<span  class=\"testDetails\"> " + ln
					+ "Application : <font color=\"#FFFFFF\">" + appName
					+ " </font> " + ln + " </span>" + ln + " </td> " + ln
					+ " </tr>" + ln + " </table> " + ln + "</div>";

			String execDetails = "<div id=\"execDetails\"> "
					+ ln
					+ "<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\"> "
					+ ln
					+ "  <tr> "
					+ ln
					+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
					+ ln
					+ "<span class=\"execDetails\">"
					+ ln
					+ "Test Cases: "
					+ totalcases
					+ "</span>"
					+ ln
					+ "</td>"
					+ ln
					+ "	<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
					+ ln + "<span class=\"execDetails\">" + ln + "Passed : "
					+ passed + "</span>" + ln + "</td>" + ln
					+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
					+ ln + "<span class=\"execDetails\">" + ln + "Failed :"
					+ failed + "</span>" + ln + "</td>" + ln
					+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
					+ ln + "<span class=\"execDetails\">" + ln + "Skipped : "
					+ (totalcases-executed) + "</span>" + ln + "</td>" + ln
					+ "<td align=\"left\" valign=\"middle\" class=\"execDetails\">"
					+ ln + "<span class=\"execDetails\">" + ln + "Browser: "
					+ browser + "</span>" + ln + "</td>" + ln + "</tr>" + ln
					+ "</table>" + ln + "</div> <br/>";

			String testCaseDetails = "<div id=\"testcaseDetails\" style=\"padding-left:15px\">"
					+ ln
					+ " <p> "
					+ "<span style=\"font-size: 15px;font-weight:bold;color:#000000;font-family:verdana;\">Items Tested:</span> </p>"
					+ ln;

			String errorlog = "<div><table b><td><span style=\"font-size: 15px;font-weight:bold;color:#000000;font-family:verdana;\">Case_No</span></td>"
					+ "<td><span style=\"font-size: 15px;font-weight:bold;color:#000000;font-family:verdana;\">Case_Name</span></td>"
					+ "<td><span style=\"font-size: 15px;font-weight:bold;color:#000000;font-family:verdana;\">Status</span></td>"
					+ "<td><span style=\"font-size: 15px;font-weight:bold;color:#000000;font-family:verdana;\">Comment</span></td></div>";

			Set<Integer> sets =errormap.keySet();
			for(int x: sets){
				ErrorLogger log= errormap.get(x);
				if( log != null){
					errorlog += "<tr >";
				}else{
					errorlog += "<tr>";
				}
				errorlog += "<td>"+x+"</td>";
				errorlog += "<td>"+log.getCaseName()+"</td>";
				if(log.getStatus() != null){
					if(log.getStatus().equalsIgnoreCase("failed")){
						errorlog += "<td style='background-color:red'>Failed</td><td>"+log.getComment()+"</td></tr>";
					}else{
						errorlog += "<td style='background-color:green'>Passed</td><td></td></tr>";
					}
				}
			}
			errorlog +="</table>";

			writer.write(headContent);
			writer.write(header);
			writer.write(testDetails);
			writer.write(execDetails);
			writer.write(testCaseDetails);
			writer.write(errorlog);
			writer.flush();
			writer.close();
			return htmlFile;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	/**--Closing all the sessions and sending the mail--**/
	public void TearDown() throws Exception {
		result.write();
		result.close();
		driver.close();
		driver.quit();
		// SendMail();
	}


	/**--Getting the properties from the email file and configuring the mail body--**/
	public void SendMail() {

		try {

			System.out.println("Sending mail..");
			InputStream is = new FileInputStream(SystemPath+File.separator+"Email.properties");
			props.load(is);
			Message msg;
			String smtpHostName = props.getProperty("SMTP_HOST_NAME");
			String recipient = props.getProperty("To");
			String from = props.getProperty("From");
			String displayName = props.getProperty("DisplayName");
			String subject = props.getProperty("Subject");
			String smtp = props.getProperty("SMTP_PROTOCOL");
			String port = props.getProperty("SMTP_PORT");
			props.put("mail.smtp.host", smtpHostName);
			props.put("mail.smtp.auth", "true");
			Properties propsnew = new Properties();
			propsnew.setProperty("mail.transport.protocol", smtp);
			propsnew.setProperty("mail.host", smtpHostName);
			propsnew.setProperty("mail.port", port);

			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(propsnew, auth);

			msg = new MimeMessage(session);

			InternetAddress addressFrom = new InternetAddress(from, displayName);
			msg.setFrom(addressFrom);

			msg.setSentDate(new Date());

			String[] recipients = recipient.split(";");
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			//			Address[] cc = new Address[] {InternetAddress.parse("preddy@webmd.net"),
			//											InternetAddress.parse("preddy@webmd.net")};
			//			msg.addRecipients(Message.RecipientType.CC, cc);

			//			msg.addRecipient(RecipientType.CC, new InternetAddress("rsauther@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("sdharmapuri@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("nkodumuri@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("abhavsar@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("kpaluri@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("BGaddam@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("sriyaz@webmd.net"));
			//			msg.addRecipient(RecipientType.CC, new InternetAddress("preddy@webmd.net"));

			msg.addRecipient(RecipientType.CC, new InternetAddress("jchukka@webmd.net"));

			subject = subject + " - " + (new java.util.Date()).toString();
			if (failed != 0)
				subject = subject + " -- Contains Failed Cases";
			msg.setSubject(subject);

			String message = "<b>Hi All,</b><br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "Please find the Automation Test Summary Report for<b> WebMD - RX Mobile "+devicedriver
					+ " </b> executed on : <b>"
					+ browser
					+ " </b> is as follows: <br/> <ul><h3> "
					+ " Test summary for WebMDRx Mobile" + "</h3><table border=3 cellpadding=5 ><tr><th align=left>Passed:</th><td align=center width=80 style=color:#149106 ><b>"
					+ passed
					+ "</b></td></tr><tr><th align=left >Failed:</th><td align=center width=80 style=color:red ><b>"
					+ failed
					+ "</b></td></tr><tr><th align=left>Total Test Cases:</th><td align=center width =80 style=color:#000001><b>"
					+ (failed + passed)
					+ "</b></td></tr></table></ul><br/>&nbsp;&nbsp;&nbsp;Please find Test Automation Report attached with this email.</br></br><b><p>Note:</b> This is an automated Email generated by Appium. <br/><br/>Thanks,<br/>Automation Team<br/><STYLE>BODY{color:#000001;font-size:10pt; font-family:TrebuchetMS }</STYLE><BODY>";

			msg.setContent(message, "text/html");

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			String file = "Path of your file";
			messageBodyPart = new MimeBodyPart();
			DataSource tResult = new FileDataSource(resultsFile);
			messageBodyPart.setFileName(resultsFile.substring(resultsFile.lastIndexOf('/')+ 1, resultsFile.length()));
			messageBodyPart.setDataHandler(new DataHandler(tResult));
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			String html = CreateHtmlReport();
			DataSource htmlResult = new FileDataSource(html);
			messageBodyPart.setFileName(html.substring(html.lastIndexOf('/')+ 1, html.length()));
			messageBodyPart.setDataHandler(new DataHandler(htmlResult));
			multipart.addBodyPart(messageBodyPart);

			msg.setContent(multipart);
			Transport.send(msg);
			System.out.println("Mail sent..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**--Authentication for mail--**/
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String username = props.getProperty("SMTP_AUTH_USER");
			String password = props.getProperty("SMTP_AUTH_PWD");
			return new PasswordAuthentication(username, password);
		}
	}
}
