package info.kmichel.lobster;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

	private final String name;
	private final String value;
	private final Map<String, String> properties;

	public Cookie(final String name, final String value) {
		this.name = name;
		this.value = value;
		this.properties = new HashMap<String, String>();
	}

	public void setProperty(final String name, final String value) {
		properties.put(name, value);
	}

	public String getProperty(final String name) {
		return properties.get(name);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
