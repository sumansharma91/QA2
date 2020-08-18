package com.vsi.oms.api.aurus;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aurus.aesdk.abstractfactory.formfactor.FormFactorHandler;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.integration.adapter.IntegrationAdapter;

public class VSIInitAESDK //extends IntegrationAdapter
{


	private static YFCLogCategory log = YFCLogCategory.instance(VSIInitAESDK.class);
	private static final String TAG = VSIInitAESDK.class.getSimpleName();
	YIFApi api;

	public Document serviceInitAESDKAPI(YFSEnvironment env, Document inXML) throws RemoteException, YIFClientCreationException, ParserConfigurationException{

		printLogs("Printing Input XML :" + SCXmlUtil.getString(inXML));

		if(YFSObject.isVoid(inXML)) {
			inXML=SCXmlUtil.createFromString("<temp/>");
		}else {
			api = YIFClientFactory.getInstance().getApi();
		}
		Document initResponse =initAESDKAPI();

		return initResponse;
	}

	//	public VSIInitAESDK() {
	//		super("");
	//	}
	public static void main(String[] args) {
		try {

			VSIInitAESDK vsiInitAESDK = new VSIInitAESDK();
			vsiInitAESDK.initSDKCall();				

			printLogs("Start Agent "+args);

			//			com.yantra.integration.adapter.IntegrationAdapter.main(args);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			//			try {
			//				IntegrationAdapter.main(args);
			//			}catch (Exception e) {
			//				 e.printStackTrace();
			//			}
		}
	}


	public Document initSDKCall() {
		Document outDoc =null;
		try {
			printLogs("Init AESDK Start");

			VSIInitAESDK initAESDK1= new VSIInitAESDK();
			outDoc =initAESDK1.initAESDKAPI();
			System.out.println("outDoc ="+SCXmlUtil.getString(outDoc));
			printLogs("Init AESDK End");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
		return outDoc;	
	}




	//		public static void main(String[] args) {
	//
	//		try {
	//			VSIInitAESDK initAESDK1= new VSIInitAESDK();
	//			Document outDoc =initAESDK1.initAESDKAPI();
	//	
	//			System.out.println("outDoc ="+SCXmlUtil.getString(outDoc));
	//		}catch (Exception e) {
	//			// TODO: handle exception
	//		}
	//
	//	}

	private Document initAESDKAPI() throws ParserConfigurationException {
		Document initAESDKDoc =createInputInitSDK();

		String initAESDKXML= SCXmlUtil.getString(initAESDKDoc);
		printLogs("initAESDKXML \n"+initAESDKXML);

		FormFactorHandler formFactor = new FormFactorHandler(); 
		String response = formFactor.initAESDK(initAESDKXML);

		Document initResponseDoc=SCXmlUtil.createFromString(response);
		return initResponseDoc; 
	}


	private Document createInputInitSDK() throws ParserConfigurationException {
		Document initAeSDKReqDoc = XMLUtil.createDocument("InitAesdkRequest");
		Element elechangeOrderDoc = initAeSDKReqDoc.getDocumentElement();

		putElementValue(elechangeOrderDoc,"POSID","VSIDEV20");
		//		putElementValue(elechangeOrderDoc,"ConfigFilePath","C:/aesdkprop");
		putElementValue(elechangeOrderDoc,"ConfigFilePath","/Sterling/opt/aesdkprop/");

		return initAeSDKReqDoc;
	}

	private static String getJsonValue(JSONObject jsonObj,String key) {
		String value="";
		if(jsonObj!=null) {
			if (jsonObj.has(key)) {
				value =jsonObj.getString(key);
			}
		}		
		return value;
	}


	private void putElementValue(Element childEle, String key, Object value) {
		Element ele = SCXmlUtil.createChild(childEle, key);
		if(value instanceof String ) {
			ele.setTextContent((String)value);
		}else if(value instanceof Element ) {
			ele.appendChild((Element)value);
		}
	}


	private String getElementValue(Element element, String tagName) {
		String value="";
		if(element!=null) {
			Element ele = (Element) element.getElementsByTagName(tagName).item(0);
			if(ele!=null) {
				value=ele.getTextContent();
			}
		}
		return value;
	}


	private static void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}
}
