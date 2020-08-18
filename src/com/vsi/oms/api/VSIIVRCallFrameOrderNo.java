package com.vsi.oms.api;


import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIIVRCallFrameOrderNo{

	private YFCLogCategory log = YFCLogCategory.instance(VSIIVRCallFrameOrderNo.class);
	YIFApi api;

	public Document frameOrderNo(YFSEnvironment env, Document inXML) throws RemoteException, YIFClientCreationException{
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIIVRCallFrameOrderNo================================");
		}
		Element eleOrder = inXML.getDocumentElement();

		String strOrderNo = eleOrder
		.getAttribute(VSIConstants.ATTR_ORDER_NO);
		Integer intOrderSize=strOrderNo.length();
		api = YIFClientFactory.getInstance().getApi();
		if (9==intOrderSize){
			StringBuilder sb= new StringBuilder("C");
			sb.append(strOrderNo);

			String newOrderNo=sb.toString();
			eleOrder.setAttribute(VSIConstants.ATTR_ORDER_NO, newOrderNo);
			eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, VSIConstants.VSICOM_ENTERPRISE_CODE);
			//call getOrderList

			return VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_IVR, VSIConstants.API_GET_ORDER_LIST,
					inXML);

		}
		else if (8==intOrderSize){
			StringBuilder sb= new StringBuilder("WO");
			sb.append(strOrderNo);

			String newOrderNo=sb.toString();
			eleOrder.setAttribute(VSIConstants.ATTR_ORDER_NO, newOrderNo);
			eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, VSIConstants.VSICOM_ENTERPRISE_CODE);
			//call getOrderList

			Document docGetOrderListOutpt= VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_IVR, VSIConstants.API_GET_ORDER_LIST,
					inXML);

			if(!YFCCommon.isVoid(docGetOrderListOutpt)&&Integer.parseInt(docGetOrderListOutpt.getDocumentElement().getAttribute(VSIConstants.ATTR_TOTAL_ORDER_LIST))>0)
			{
				return docGetOrderListOutpt;
			}
			else{
				eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, VSIConstants.ENT_ADP);
				return VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_IVR, VSIConstants.API_GET_ORDER_LIST,
						inXML);
			}


		}
		else{
			return VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_IVR, VSIConstants.API_GET_ORDER_LIST,
					inXML);
		}
	}	


}
