package testcases;

import java.util.HashMap;
import org.openqa.selenium.By;
import mainsources.CharlesProxy;
import mainsources.GeneralLibrary;

public class OmnitureCallValidations extends GeneralLibrary{

	boolean status;

	public void verifyModuleCall(HashMap<String, String> m, String locators, String url) throws Exception{
		try{
			CharlesProxy cp = new CharlesProxy();
			cp.charlesclear();
			if(url.substring(url.indexOf(".")+1).startsWith("staging")){
				driver.get(urls(url,"webmd","staging"));
			}else {
				driver.get(url);
			}
			cp.charlesclear();
			ClickByXpath(By.xpath(locators));
			status = cp.CharlesVerify(m);
		}catch(Exception e){
			throw e;
		}
	}

	public void verifyPageviewCall(HashMap<String, String> m, String url) throws Exception {
		try{
			CharlesProxy cp = new CharlesProxy();
			cp.charlesclear();
			if(url.substring(url.indexOf(".")+1).startsWith("staging")){
				driver.get(urls(url,"webmd","staging"));
			}else {
				driver.get(url);
			}
			status = cp.CharlesVerify(m);
		}catch(Exception e){
			throw e;
		}
	}

	/* Main test */
	public void test_verifyModuleCall() throws Exception{
		HashMap<String, String> m = getTdata();
		String url = getTUrl();
		String loc = getTlocators();
		if(m!=null && m.size()>0){
			verifyModuleCall(m,loc,url);
		}
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}

	/* Main test */
	public void test_verifyPageviewCall() throws Exception{
		HashMap<String, String> m = getTdata();
		String url = getTUrl();
		if(m!=null && m.size()>0){
			verifyPageviewCall(m,url);
		}
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}	
}