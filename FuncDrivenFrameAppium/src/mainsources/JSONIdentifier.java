package mainsources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="c")
public class JSONIdentifier {

	String jsonData;

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	
	
}
