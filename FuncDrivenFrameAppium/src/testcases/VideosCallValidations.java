package testcases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.openqa.selenium.By;
import mainsources.CharlesProxy;
import mainsources.GeneralLibrary;

public class VideosCallValidations extends GeneralLibrary {

	boolean status;

	public void Verifyomnitureforplayandpause(HashMap<String, String> m, String locators , String url) throws Exception{

		try{

			CharlesProxy cp = new CharlesProxy();
			cp.charlesclear();
			driver.get(urls(url,"webmd","staging"));
			ClickByXpath(By.xpath(locators));
			Thread.sleep(8000);
			cp.charlesclear();
			ClickByXpath(By.xpath(locators));
			Thread.sleep(6000);
			status = cp.CharlesVerify(m);
		}catch(Exception e){
			throw e;
		}
	}

	/* Main test*/
	public void test_Verifyomnitureforplayandpause() throws Exception{

		HashMap<String, String> m = getTdata();
		String url = getTUrl();
		String loc = getTlocators();
		if(m!=null && m.size()>0){
			Verifyomnitureforplayandpause(m,loc,url);
		}
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}
}
