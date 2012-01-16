package simulation.model;

import java.util.HashMap;
import java.util.Map;

public class RollBackVariables{

	private Map<String, Object> values = new HashMap<String, Object>();

	public RollBackVariables(String key, Object value) {
		values.put(key, value);
	}

	public Object getValue(String key) {
		Object value = values.get(key);
		if (value == null)
			throw new RuntimeException("value not set");
		return value;
	}

	/**
	 * 
	 * @return the stored value as a long
	 */
	public long getLongValue(String key) {
		Long l = (Long) getValue(key);
		return l;
	}

	public boolean getBooleanValue(String key){
		Boolean b = (Boolean) getValue(key);
		return b;
	}
	
	public void setValue(String key, Object value) {
		this.values.put(key, value);
	}

}
