package testcases;

import java.util.HashMap;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import mainsources.GeneralLibrary;
import mainsources.Strings;

public class ArticlePageheader extends GeneralLibrary {

	private static boolean status;
	private static WebElement titlearticlepage;
	private static WebElement mastHead;
	private static WebElement WebmdImage;
	private static WebElement text;

	public static void VerifyifByline(HashMap<String, String> m, String loc, String url) throws Exception {

		try {
			
			String Bylinetext = m.get("Bylinetext"); 
			String Bylineyear = m.get("year");
			String Byline = Bylinetext + "," + " "+ Bylineyear;
					
			driver.get(url);
			if(Gettext(loc).contentEquals(Byline)){
				System.out.println("Byline text is displayed in the article page");
				status = true;
			} else {
				System.out.println("Byline text is NOT displayed in the article page");
				status = false;
			}
		}catch(Exception e){
			throw e;
		}
	}

	public void VerifyTitleArticlepage(HashMap<String, String> m, String loc, String url) throws Exception {

		try{
			driver.get(url);
			System.out.println(loc);
			String titleArticlePage = m.get("titleArticlePage");
			System.out.println(titleArticlePage);
			if(IsElementPresent(loc)){
				System.out.println("Title of the article/video page is displayed");
				if(Gettext(loc).equals(titleArticlePage)){
					System.out.println("Title is displayed as " + titlearticlepage.getText());
					status = true;
				} else {
					System.out.println("Title is displayed as " + titlearticlepage.getText());
					status = false;
				}
			} else{
				System.out.println("Title of in the article page is NOT displayed");
				status = false;				
			}

		} catch(Exception e){
			throw e;
		}
	}

	public void VerifyMastHeadArticlepage(HashMap<String, String> m, String loc, String url) throws Exception{

		try {
			String link 			= m.get("WebMdImageSrc");
			String text				= m.get("textPresents");

			driver.get(url);

			if (IsElementPresent(loc)){
				System.out.println("Masthead is displayed at the top of the Article/video");
			} else {
				System.out.println("Masthead is NOT displayed at the top of the Article/video");
				status = false;
			}
			System.out.println("Searching for WebMd image on the Masthead");
			if(IsElementPresent(loc)){
				System.out.println("WebMd image is displayed, verifyinig for the Link attribute");
				if(WebmdImage.getAttribute("src").equals(link)){
					System.out.println("Link is displayed properly");
				} else {
					System.out.println("Link is NOT displayed properly");
					status = false;
				}
			}
			System.out.println("Looking for the text: " +"\"" + Strings.textPresents  +"\"");
			if(Gettext(loc).equalsIgnoreCase(text)){
				System.out.println("\"" + Strings.textPresents  +"\""+ " text found below WebMd image");
				status = true;
			} else {
				System.out.println("\"" + Strings.textPresents  +"\""+ " text NOT found below WebMd image");
				status = false;
			}
		} catch(Exception e){
			throw e;
		}
	}


	/*Main test*/
	public void test_VerifyifByline() throws Exception{

		HashMap<String, String> m = getTdata();
		String loc = getTlocators();
		String url = getTUrl();
		
		VerifyifByline(m, loc, url);
		
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}

	/*Main test*/
	public void test_VerifyTitleArticlepage() throws Exception{
		HashMap<String, String> m = getTdata();
		String loc = getTlocators();
		String url = getTUrl();
		VerifyTitleArticlepage(m, loc, url);
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}
	/* Main test*/
	public void test_VerifyMastHeadArticlepage() throws Exception{
		HashMap<String, String> m 	=  getTdata();
		String loc =  getTlocators();
		String url = getTUrl();
		VerifyMastHeadArticlepage(m, loc, url);
		if (status){
			isPassed = true;
		} else {
			isPassed = false;
		}
	}
}
