package mainsources;

import java.text.SimpleDateFormat;
import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.AppiumDriver;

public class GeneralLibrary {

	public String testData[];
	public int inc = 0;
	public static WebDriver driver;
	public String tc_name;
	public String tc_comment = "";
	public boolean isPassed = false;
	SimpleDateFormat dtFormat = new SimpleDateFormat("ddMMHHmmss");
	public long start;
	public long end;
	public long duration;  
	public String baseURL;
	HashMap< String, String> tdata = new HashMap<String, String>();
	WebElement webElement = null;
	public String tUrl;
	// HashMap<String, String> tlocators1 = new HashMap<String, String>();
	String tlocators1;
	

	public String timeStamp(){
		return dtFormat.format(new Date());
	}

	/**--Verify the Title of page--**/
	public void VerifyTitle(String title){
		if (!(title.equalsIgnoreCase(driver.getTitle())))
			fail("Page Title is not expected");
	}

	/**Return test data--**/
	public HashMap<String, String> getTdata() {
		return tdata;
	}

	public void setTurl(String tUrl) {
		this.tUrl = tUrl;
	}

	public String getTUrl(){
		return tUrl;
	}

	/*	public HashMap<String, String> getTlocators(){
		return tlocators1;
	}
	 */	public void setTdata(HashMap<String, String> tdata) {
		 this.tdata = tdata;
	 }


	 /*	public void setTLocators(HashMap<String, String> tlocators1) {
		this.tlocators1 = tlocators1;
	}
	  */
	 public void setTLocators(String tlocators1){
		 this.tlocators1 = tlocators1;		
	 }

	 public String getTlocators(){
		 return tlocators1;
	 }
	 /**--Get Method--**/
	 public WebDriver getWebDriver() {

		 return driver;
	 }

	 /**--Set Method--**/
	 public void setWebDriver(WebDriver driver) {
		 this.driver = driver;
	 }

	 /**--Getting the testdata from the excel file--**/
	 public void setTestData(String test) {

		 StringTokenizer sa = new StringTokenizer(test, ",");
		 testData = new String[sa.countTokens()];
		 inc = 0;
		 while (sa.hasMoreTokens())
			 testData[inc++] = sa.nextToken();
		 inc = 0;
	 }

	 /**Execution start time--**/
	 public void StartTime() {
		 start = System.currentTimeMillis();
	 }

	 /**Execution end time--**/
	 public void EndTime() {
		 end = System.currentTimeMillis();
	 }

	 /**Covert milli to seconds--**/
	 public long duration(){
		 return ((end-start)/1000);
	 }

	 public String[] getTestData(){
		 return testData;
	 }

	 public void fail(String msg){
		 tc_comment = tc_comment + msg + "\n";
	 }

	 /**--Set Flag--**/
	 public String isPassed(){
		 if(isPassed)
			 return "Pass";
		 else
			 return "Fail";
	 }

 
	 /**--Sleep Time to wait for a specific period of Time--**/
	 public void sleep(int time) {
		 try {
			 Thread.sleep(time);
		 } catch (InterruptedException e) {
		 }
	 }

	 /**--Enter Text value using Id--**/
	 public void TypeById(String locator,String text)throws Exception{
		 try{
			 WebElement element  = driver.findElement(By.id(locator));
			 element.clear();
			 element.sendKeys(text);
		 }catch(NoSuchElementException e){
			 throw new Exception("Element : "+ locator+ " Not found");
		 }
	 }

	 /**---Enter Text value using Xpath---**/
	 public void TypeByXpath(By locator, String text) throws Exception{
		 try{
			 WebElement element = driver.findElement(locator);
			 element.clear();
			 element.sendKeys(text);
		 }catch(NoSuchElementException e){
			 throw new Exception("Element : "+ locator+ " Not found");
		 }
	 }

	 /**---Click on Element using Xpath value---**/
	 public void ClickByXpath(By locator) throws Exception{
		 try{
			 WebElement element = driver.findElement(locator);
			 element.click();
		 }catch(NoSuchElementException e){
			 throw new Exception("Element : "+ locator+ " Not found");
		 }
	 }

	 /**--Click on element using name attribute--**/
	 public void ClickByName(By locator) throws Exception{
		 try{
			 WebElement element = driver.findElement(locator);
			 element.click();
		 }catch(NoSuchElementException e){
			 throw new Exception("Element : "+ locator+ "Not found");
		 }
	 }

	 /**---Click on Button using name value--**/
	 public void BtnClick(String locator)throws Exception{
		 try{
			 WebElement element = driver.findElement(By.name(locator));
			 element.click();
		 }catch(NoSuchElementException e){
			 throw new Exception("Element : "+ locator+ " Not found");
		 }
	 }

	 /**--Get Text by using locator--**/
	 public static String Gettext(String locator) throws Exception {
		 String text;
		 try{
			 WebElement element = driver.findElement(By.xpath(locator));
			 text = element.getText();
			 System.out.println(text);
		 }catch(Exception e){
			 throw new Exception("Element not found");
		 }
		 return text;
	 }

	 /**--Text Verification by using name value--**/
	 public static void VerifyText(String locator, String expected)throws Exception{
		 try{
			 String actualvalue = driver.findElement(By.xpath(locator)).getText();
			 System.out.println(actualvalue);
			 if(actualvalue.contentEquals(expected))
			 {
				 System.out.println("Text verification is passed");
			 }
			 else{
				 System.out.println("Text verification is failed");
			 }
		 }catch(NoSuchElementException e){
			 throw new Exception("Text verification value : "+ locator+ " Not found");
		 }
	 }

	 /**--Verify Element is present or not--**/
	 public boolean IsElementPresent(String locator) throws Exception {
		 boolean isPresent;
		 WebElement element;

		 try {
			 element = driver.findElement(By.xpath(locator));
			 if(element.isDisplayed()){
				 isPresent =true;
			 } else {
				 isPresent = false;
			 }
		 }catch (NoSuchElementException e) {
			 throw new Exception("Text verification value : "+ locator+" not found");
		 }
		 return isPresent;
	 }

	 /**--Swipe vertical using the dimensions--**/
	 public void SwipeDown(){
		 Dimension dimensions = driver.manage().window().getSize();
		 System.out.println("Size of screen= " +dimensions);
		 int Startpoint = (int) (dimensions.getHeight() * 0.6);
		 System.out.println("Size of scrollStart= " +Startpoint );
		 int scrollEnd = (int) (dimensions.getHeight() * 0.2);
		 System.out.println("Size of cscrollEnd= " + scrollEnd);             
		 ((AppiumDriver) driver).swipe(0, Startpoint,0,scrollEnd,1000);           
	 }

	 /**--Search for element using the swipe--**/
	 public void IsElementPresentSwipe(By search) throws Exception {
		 try {
			 driver.findElement(search);
		 }
		 catch (NoSuchElementException e) {
			 SwipeDown();
			 if(driver.findElements(search).size() == 0){
				 SwipeDown();
			 }
			 if(driver.findElements(search).size() == 0){
				 SwipeDown();
			 }
			 if(driver.findElements(search).size() == 0){
				 SwipeDown();
			 }
		 }
	 }

	 /**--Select Text by Visible--**/
	 public void SelectByVisibleText(WebElement element, String selectValue){
		 new Select(element).selectByVisibleText(selectValue);
	 }

	 /**--Select by Index Value--**/
	 public void SelectByIndex(WebElement element,int index){
		 new Select(element).selectByIndex(index);
	 }

	 /**--Click by link--**/
	 public void ClickLink(String link){
		 driver.findElement(By.linkText(link)).click();
	 }

	 public int getRandom(int i) {
		 int r = 0;
		 while (r == 0) {
			 r = (int) ((Math.random()) * i) + 1;
		 }
		 return r;
	 }

	 /**--Explicit Wait condition to find presence of Element--**/
	 public WebElement WaitForElement(By searchBy)  {

		 WebElement webElement = null;

		 Wait<WebDriver> wait = new WebDriverWait(driver, 10000);
		 try{
			 webElement = wait.until(ExpectedConditions.presenceOfElementLocated((searchBy)));	
		 } catch(Exception e){
			 e.printStackTrace();
		 }
		 return webElement;
	 }

	 /**Swipe operations--**/
	 public static void swipeScreenUp() throws Exception
	 {
		 try{
			 Dimension size = driver.manage().window().getSize();
			 int startx = size.width / 2;
			 int endx = startx;
			 int starty = ( int )( size.height * 0.80 );
			 int endy   = ( int )( size.height * 0.10 );
			 ((AppiumDriver) driver).swipe( startx , starty , endx , endy , 1000 );
		 }catch(NoSuchElementException e){
			 Dimension size = driver.manage().window().getSize();
			 int startx = size.width / 2;
			 int endx = startx;
			 int starty = ( int )( size.height * 0.80 );
			 int endy   = ( int )( size.height * 0.10 );
			 ((AppiumDriver) driver).swipe( startx , starty , endx , endy , 1000 );
		 }
	 }
	 /**
	  * Single swipe down
	  * @param driver
	  */
	 public static void swipeScreenDown()
	 {
		 Dimension size = driver.manage().window().getSize();
		 int startx = size.width / 2;
		 int endx = startx;
		 int starty = ( int )( size.height * 0.10 );
		 int endy   = ( int )( size.height * 0.80 );
		 ((AppiumDriver) driver).swipe( startx , starty , endx , endy , 1000 );
	 }

	 public static String urls(String url, String username, String password){
		 String toAppend = username +":"+password+"@";
		 return (url.substring(0, url.indexOf("w")).concat(toAppend).concat(url.substring(url.indexOf("w"))));
	 }

	 public static void isAlertPresent() throws Exception {

	 }	
}