package com.vsi.oms.api.aurus;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;

import com.aurus.aesdk.abstractfactory.formfactor.FormFactorHandler;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class VSIInitAESDKServlet extends HttpServlet{


	private static final String TAG = VSIInitAESDKServlet.class.getSimpleName();
	
	private static final long serialVersionUID = 8808616055836017631L;

	  public synchronized void init(ServletConfig config)
	    throws ServletException
	  {
	    
			String outDoc =initAESDKAPI();
			printLogs(outDoc);
		  
	  }
	

//	public static void main(String[] args) throws ParserConfigurationException {
//
//		VSIInitAESDKServlet initAESDK1= new VSIInitAESDKServlet();
//		String outDoc =initAESDK1.initAESDKAPI();
//
//		System.out.println("outDoc ="+outDoc);
//
//	}
	
	


	private String initAESDKAPI()  {
		
		JSONObject configJson = new JSONObject(); 
		configJson.put("ConfigFilePath", "/Sterling/opt/aesdkprop/"); 
//		configJson.put("ConfigFilePath", "C:/aesdkprop"); 
		configJson.put("POSID", "VSIDEV20");

		JSONObject initAESDKJSON = new JSONObject(); 
		initAESDKJSON.put("InitAesdkRequest", configJson);

		System.out.println("jsonRequest \n"+initAESDKJSON.toString());

		FormFactorHandler formFactor = new FormFactorHandler(); 
		//		JSONObject jsonResponse = formFactor.initAESDK(jsonRequest);
		String response = formFactor.initAESDK(initAESDKJSON.toString());

		
		
		return response; 
	}



	
	private void printLogs(String mesg) {
		System.out.println(mesg);
	}
}
