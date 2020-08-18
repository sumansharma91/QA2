package com.vsi.oms.utils;

import java.util.Enumeration;
import java.util.Properties;

import com.yantra.interop.japi.YIFCustomApi;

/**
 * This class is the base custom API class that all CVS custom APIs shall
 * extend.
 * 
 * @author IBM
 * 
 */
public class VSIBaseCustomAPI implements YIFCustomApi {

	Properties mProperties = new Properties();

	/**
	 * This method is overridden from interface.
	 * 
	 * @param properties
	 *            Properties set
	 */
	public void setProperties(Properties properties) {
		if (properties != null) {
			this.mProperties = properties;
		}
	}

	/**
	 * This method is overridden from interface.
	 * 
	 * @return Properties
	 */
	public Properties getProperties() {
		return this.mProperties;
	}
	
	/**
	 * This method will return the value of the SDF property
	 * @param key
	 * @return value
	 */
	protected String getParameter(String key) {
		String result = null;
		if (!mProperties.isEmpty()) {
			result = mProperties.getProperty(key);
		}
		return result;
	}
	
	protected Enumeration getPropertyList(){

		if(mProperties != null && !mProperties.isEmpty()){
			Enumeration e = mProperties.propertyNames();
			return e;
		}

		return null;
	}

}