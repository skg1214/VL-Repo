package testcases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import mainsources.CharlesProxy;
import mainsources.GeneralLibrary;

public class SlideshowModulecallsValidation extends GeneralLibrary {
	private static boolean status;

	public void VerifyomnitureCallsNext(HashMap<String, String> m) throws Exception{
		
		int  found    = 0;
		int  notfound = 0;
		List<String> CallsFound = new ArrayList<String>();
		List<String> CallsNotFound = new ArrayList<String>();
		CharlesProxy cp = new CharlesProxy();
		cp.charlesclear();
		driver.get("http://www.staging.webmd.com/epilepsy/treat-epilepsy-seizures-16/slideshow-epilepsy-overview");
		List<WebElement> slides = driver.findElements(By.xpath("//div[@class='slide']"));
		int num = slides.size();
		WebElement rightarrow = driver.findElement(By.xpath("//*[@id='dyn-ss']/div[2]/a[2]/i")); 
		
		for(Entry<String, String> entry : m.entrySet()){
			String Attribute = entry.getKey();
			String Value 	 = entry.getValue();
			String Call 	 = Attribute + "=" + Value;
			
			String Xmlexport = cp.CharlesExportXML();
			if(Xmlexport.contains(Call)){
				System.out.println("Call: " + Call + "found");
			}
			 cp.charlesclear();
		
		for (int i=1; i<=num;i++){
					rightarrow.click();
					 Xmlexport = cp.CharlesExportXML();
					
		}
		
		 Xmlexport = cp.CharlesExportXML();
			if(Xmlexport.contains(Call)){
				found++;
				System.out.println("Call: " + Call + "found");
				CallsFound.add(Call);
			}else {
				notfound++;
				System.out.println("Call: " + Call + "not found");
				CallsNotFound.add(Call);
			}
		}
		if(m.size() == found){
			System.out.println("");

		}
	}

	/* Main test */
	public void test_VerifyomnitureCallsNext() throws Exception{
		HashMap<String, String> m = getTdata();
		if(m!=null && m.size()>0){
			VerifyomnitureCallsNext(m);
		}
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}
}
