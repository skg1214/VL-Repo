package mainsources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONProcessor {
	
	public  static LinkedHashSet<String> process(String jsondata)throws Exception{
		LinkedHashSet<String> drug_Names = new LinkedHashSet<String>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsondata);

		// get a String from the JSON object
		long Code = (Long) jsonObject.get("code");
		// handle a structure into the json object
		JSONObject data = (JSONObject) jsonObject.get("data");
		JSONObject rXSearch = (JSONObject) data.get("rxsearch");
	//	JSONObject rXPricing = (JSONObject) data.get("rxpricing");
		JSONArray data1 = (JSONArray) rXSearch.get("data");
		int ArrayLength = data1.size();
		Iterator i = data1.iterator();
		Integer key1 = 2;
		while (i.hasNext()) {
		JSONObject innerObj = (JSONObject) i.next();
			ArrayList a1 = new ArrayList();
			String drugName = (String) innerObj.get("drug_name");
			a1.add(drugName);
			String id = (String) innerObj.get("id");
			a1.add(id);
			Boolean Generic = (Boolean) innerObj.get("is_generic");
		    a1.add(Generic);
			JSONArray otherName = (JSONArray) innerObj.get("other_name");
			Iterator i1 = otherName.iterator();
			int size_othernames = 0;
            String oNames="";
          	while (i1.hasNext()) {
				JSONObject innerObj2 = (JSONObject) i1.next();
				if (Generic == true && otherName.size() == 1){
						oNames =  "(generic "+innerObj2.get("name")+")";
					}else if(otherName.size() == 1){
						oNames=oNames+"("+innerObj2.get("name")+")";
					}
			}
			a1.add(oNames);
			if(oNames != null && oNames.length() > 0)
			drug_Names.add(drugName+" "+oNames);	
			else
			drug_Names.add(drugName);
			Object[] values = new Object[a1.size()];
			a1.toArray(values);
			key1++;
		}
		return drug_Names;
	}

}
