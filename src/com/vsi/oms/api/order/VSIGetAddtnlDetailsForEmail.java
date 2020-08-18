package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIGetAddtnlDetailsForEmail {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetAddtnlDetailsForEmail.class);
	
	public Document vsiGetAddtnlDetails(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(inXML != null){
			
			if(log.isDebugEnabled()){
				log.debug("Input for VSIGetAddtnlDetailsForEmail:vsiGetAddtnlDetails : "+ XMLUtil.getXMLString(inXML));
			}
			Element orderEle = inXML.getDocumentElement();
			
				Element orderLineEle = (Element) orderEle.getElementsByTagName("OrderLine").item(0);
				if(orderLineEle != null){
					String shipnode = orderLineEle.getAttribute("ShipNode");
					if(shipnode !=null && !shipnode.trim().equalsIgnoreCase("")){
						try {
							/*String strReqShipDate = orderLineEle.getAttribute("ReqShipDate");
							if(YFCObject.isVoid(strReqShipDate)){
								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								Calendar cal = Calendar.getInstance();
								strReqShipDate = dateFormat.format(cal.getTime());
								orderLineEle.setAttribute("ReqShipDate", strReqShipDate);
							}*/
							Document getCalInDoc = XMLUtil.createDocument("Calendar");
							Element rootEle = getCalInDoc.getDocumentElement();
							/*Calendar cal = Calendar.getInstance();
							String currentMonth = String.valueOf((cal.get(Calendar.MONTH)+1));
							String currentYear = String.valueOf(cal.get(Calendar.YEAR));
							//rootEle.setAttribute("MonthFilter", currentMonth);
							//rootEle.setAttribute("YearFilter", currentYear);*/
							rootEle.setAttribute("CalendarId", shipnode);
							rootEle.setAttribute("OrganizationCode", shipnode);
							
							Document getCalOutDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetCalendarDetails.xml", "getCalendarList", getCalInDoc);
							if(getCalOutDoc != null){
								NodeList calListNL = getCalOutDoc.getElementsByTagName("Calendar");
								int calCnt = calListNL.getLength();
								if(calCnt > 0){
									Element calEle = (Element) calListNL.item(0);
								Node copyCalNode = inXML.importNode(calEle, true);
								orderEle.appendChild(copyCalNode);
								}
								
							}
							
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (YIFClientCreationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}// OrderLine ShipNode attribute null check
					
					NodeList shipNodeNL = orderLineEle.getElementsByTagName("Shipnode");
					if(shipNodeNL.getLength() > 0){
						Element shipNodeEle = (Element) shipNodeNL.item(0);
						Node copyShipNde = inXML.importNode(shipNodeEle, true);
						orderEle.appendChild(copyShipNde);
						
					}
				}//OrderLine Element null Check
			
				NodeList orderLinesNL = orderEle.getElementsByTagName("OrderLine");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				String strReqShipDate = dateFormat.format(cal.getTime());
				for(int k=0; k < orderLinesNL.getLength() ; k ++ ){
					orderLineEle = (Element) orderLinesNL.item(k);
					String reqShipDate = orderLineEle.getAttribute("ReqShipDate");
					if(YFCObject.isVoid(reqShipDate)){
						orderLineEle.setAttribute("ReqShipDate",strReqShipDate);
					}
				}
			
			
			
			
		}// inXML null check
		
		if(log.isDebugEnabled()){
			log.debug("output from VSIGetAddtnlDetailsForEmail:vsiGetAddtnlDetails : "+ XMLUtil.getXMLString(inXML));
		}
		return inXML;
		
    }

	

}
