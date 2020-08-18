package com.vsi.oms.utils;

import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetSDFProperty extends VSIBaseCustomAPI {
	
	//Properties properties = new Properties();
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetSDFProperty.class);
			
	/**
	 * This method gets the list of configured SDF system arguments and sets them at the root element
	 * of the input XML.
	 * @param env
	 * @param inXML
	 * @return
	 */
	
	public Document getSDFProperty(YFSEnvironment env, Document inXML){
		
		if(log.isDebugEnabled()){
			
			log.debug("Input XML: " + SCXmlUtil.getString(inXML));
		}
		
		Element eleRoot = inXML.getDocumentElement();
		
		Enumeration e = this.getPropertyList();

	    while (e.hasMoreElements()) {
	      
	    	String key = (String) e.nextElement();
	    	if(!YFCObject.isVoid(key)){
	    		
	    		String value = this.getParameter(key);
	    		if(!YFCObject.isVoid(value)){
	    			
	    			SCXmlUtil.setAttribute(eleRoot, key, value);
	    		}
	    	}
	    		      
	    }
		
	    if(log.isDebugEnabled()){
			
			log.debug("Output XML: " + SCXmlUtil.getString(inXML));
		}
		return inXML;
	}
}
