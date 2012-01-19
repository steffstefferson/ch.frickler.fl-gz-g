package simulation.model;

import java.util.HashMap;
import java.util.Map;

import simulation.eventHandlers.ProcessQueuesHandler;

/**
 * at certain circumstances it is not possible to create all antimessages only
 * by calculation. the RollbackVariables class allows us, to store some
 * timestamps or other values to properly rollback an event. have a look at the
 * {@link ProcessQueuesHandler} too see an example of such stored values. <br />
 * Every {@link Event} can store a rollback variable.
 * 
 * @see Event
 * @see ProcessQueuesHandler
 * 
 */
public class RollBackVariables {

	private Map<String, Object> values = new HashMap<String, Object>();

	/**
	 * Initializes the rollback variable and stores the given value
	 * 
	 * @param key
	 *            the key for storing this value
	 * @param value
	 *            the value to store
	 */
	public RollBackVariables(String key, Object value) {
		values.put(key, value);
	}

	/**
	 * 
	 * @param key
	 *            which identifies the value
	 * @return the value as an Object
	 */
	public Object getValue(String key) {
		Object value = values.get(key);
		if (value == null)
			throw new RuntimeException("value not set");
		return value;
	}

	/**
	 * get value and cast it to a long
	 * 
	 * @param key
	 * @return the stored value as a long
	 */
	public long getLongValue(String key) {
		Long l = (Long) getValue(key);
		return l;
	}

	/**
	 * get the value and cast it to a boolean
	 * 
	 * @param key
	 * @return the stored value as a long
	 */
	public boolean getBooleanValue(String key) {
		Boolean b = (Boolean) getValue(key);
		return b;
	}

	/**
	 * adds or <u>replaces</u> a variable.
	 * 
	 * @param key
	 *            the identifier for this value
	 * @param value
	 *            the new value to store
	 */
	public void setValue(String key, Object value) {
		this.values.put(key, value);
	}

}
