package com.vsi.oms.api;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICalculateRewards implements YIFCustomApi{
	Document srchResponse=null;
	int iretryCount = 0;
	
public Document calculateReward(YFSEnvironment env, Document inXML) throws SocketTimeoutException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, ParserConfigurationException, SAXException, IOException 
	
	{
		YIFApi api=null;
		
		
		YFCLogCategory log = YFCLogCategory
				.instance(VSICalculateRewards.class);
		
		try {
			api = YIFClientFactory.getInstance().getApi();
			Document soapCallinpudoc=api.executeFlow(env,"VSICalculateReward", inXML);
			srchResponse=InvokeWebservice.invokeCalculateRewards(soapCallinpudoc);
		
			
			
		} catch (Exception e) {


			e.printStackTrace();
			 String _xml=null;
			if(e instanceof SocketTimeoutException)
			{
				if(iretryCount < 3){
					
					if(log.isDebugEnabled()){
						log.debug("** Printing retry count : "+iretryCount);
					}
					iretryCount ++;
					Document soapCallinpudoc=api.executeFlow(env, "VSICalculateReward", inXML);
					srchResponse=InvokeWebservice.invokeCalculateRewards(soapCallinpudoc);
					
					if(log.isDebugEnabled()){
						log.debug("Request to ISE CalculateRewards:" + soapCallinpudoc);
						log.debug("Response from ISE CalculateRewards:" + srchResponse);
					}
					}
				
			}
			
	 YFCDocument ifcallFailed=YFCDocument.getDocumentFor("<Customer IsCallSuccessfull=\"N\" />");
		return ifcallFailed.getDocument(); 
 
		}
		
		 if(srchResponse != null){
				String bonusPoints=null;
				String regularPoints=null;
				double totalpoint=0.00;
				DecimalFormat df = new DecimalFormat("#.##");
				Node rewardTotal = srchResponse.getElementsByTagName("CalculateRewardResult").item(0);
	    		NodeList list = rewardTotal.getChildNodes();
	    		for (int i = 0; i < list.getLength(); i++) {
	    			 
	                Node node = list.item(i);
	                
	                    	   if ("RegularPoints".equals(node.getNodeName())) {
	    		             regularPoints=node.getTextContent();
	    	   }
	                    	 
	                    	   if ("BonusPoints".equals(node.getNodeName())) {
	                    		    bonusPoints=node.getTextContent();
	                       	   }
	                    	   
	    		}
				totalpoint=Double.parseDouble(regularPoints)+Double.parseDouble(bonusPoints);
				
				Document getRewardInput = XMLUtil.createDocument("RewardTotal");
				Element rewardEle = getRewardInput.getDocumentElement();
				
				rewardEle.setAttribute("TotalRewardsPoint",df.format(totalpoint));	
				return getRewardInput;
				}
		return srchResponse;
		
	}
	
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
