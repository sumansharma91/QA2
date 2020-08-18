package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIRdyForPickupOrdDtlsEmail {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIRdyForPickupOrdDtlsEmail.class);
	
	public Document vsiGetOrderDetails(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(inXML != null){
			Element orderLineEle = (Element) inXML.getElementsByTagName("OrderLine").item(0);
			if(orderLineEle != null){
				String shipnode = orderLineEle.getAttribute("ShipNode");
				if(shipnode !=null && !shipnode.trim().equalsIgnoreCase("")){
					try {
						Document getCalInDoc = XMLUtil.createDocument("Calendar ");
						Element rootEle = getCalInDoc.getDocumentElement();
						Calendar cal = Calendar.getInstance();
						String currentMonth = String.valueOf((cal.get(Calendar.MONTH)+1));
						String currentYear = String.valueOf(cal.get(Calendar.YEAR));
						rootEle.setAttribute("MonthFilter", currentMonth);
						rootEle.setAttribute("YearFilter", currentYear);
						rootEle.setAttribute("CalendarKey", shipnode);
						rootEle.setAttribute("OrganizationCode", "VSI.com");
						
						Document getCalOutDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetCalendarDetails.xml", "getCalendarDetails", getCalInDoc);
						if(getCalOutDoc != null){
							Element calEle = getCalOutDoc.getDocumentElement();
							Node copyCalNode = inXML.importNode(calEle, true);
							Element ordEle = (Element) inXML.getElementsByTagName("Order").item(0);
							ordEle.appendChild(copyCalNode);
							
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
				}
			}
		}
		return inXML;
		
    }

}
