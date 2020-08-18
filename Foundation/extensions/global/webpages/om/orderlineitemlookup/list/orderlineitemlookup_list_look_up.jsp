<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<% 
	YFCElement multiApi = null;
	YFCElement inputElem = ((YFCElement) request.getAttribute("OrderLineStatus"));
	if(inputElem != null){
		 multiApi = YFCDocument.parse("<MultiApi><API Name=\"getOrderLineStatusList\" OutputNode=\"OrderLineStatusList\" ><Input></Input></API></MultiApi>").getDocumentElement();
		 YFCElement elem = (YFCElement)(multiApi.getElementsByTagName("Input")).item(0);
		 elem.appendChild(elem.importNode(inputElem));
	}

	YFCElement templateElem = YFCDocument.parse("<MultiApi><API Name=\"getOrderLineStatusList\" ><Template></Template></API></MultiApi>").getDocumentElement();

	YFCElement outTemplate = YFCDocument.parse("<OrderLineStatusList TotalNumberOfRecords=\"\"><OrderStatus OrderHeaderKey=\"\" OrderLineKey=\"\" OrderLineScheduleKey=\"\" OrderReleaseKey=\"\" OrderReleaseStatusKey=\"\" PipelineKey=\"\" ReceivingNode=\"\" ShipNode=\"\" Status=\"\" StatusDate=\"\" StatusDescription=\"\" StatusReason=\"\" TotalQuantity=\"\"><OrderStatusTranQuantity StatusQty=\"\" TotalQuantity=\"\" TransactionalUOM=\"\" /><OrderRelease OrderHeaderKey=\"\" OrderReleaseKey=\"\" ReleaseNo=\"\" ShipNode=\"\" /><OrderLine OrderHeaderKey=\"\" OrderLineKey=\"\" OrderedQty=\"\" OriginalOrderedQty=\"\" PrimeLineNo=\"\" ShipNode=\"\" StatusQuantity=\"\" SubLineNo=\"\"><OrderLineTranQuantity OrderedQty=\"\" OriginalOrderedQty=\"\" StatusQuantity=\"\" TransactionalUOM=\"\" /><Order BuyerOrganizationCode=\"\" DocumentType=\"\" DraftOrderFlag=\"\" OrderHeaderKey=\"\" OrderNo=\"\" PaymentRuleId=\"\" PaymentStatus=\"\" SellerOrganizationCode=\"\" /><Item ItemID=\"\" ItemShortDesc=\"\" ProductClass=\"\" UnitOfMeasure=\"\" /></OrderLine></OrderStatus></OrderLineStatusList>").getDocumentElement();

	YFCElement outelement = (YFCElement)(templateElem.getElementsByTagName("Template")).item(0);
	outelement.appendChild(outelement.importNode(outTemplate));

%>


<yfc:callAPI apiName="multiApi" inputElement="<%=multiApi%>" templateElement="<%=templateElem%>" outputNamespace="multiapi"/>
<%
	int recordCount = 0;
	YFCElement multiapiElem = (YFCElement)request.getAttribute("multiapi");
	if(multiapiElem != null){
		YFCElement statusListElem = multiapiElem.getChildElement("API").getChildElement("Output").getChildElement("OrderLineStatusList");
		request.setAttribute("OrderLineStatusList", statusListElem);
		recordCount = statusListElem.getIntAttribute("TotalNumberOfRecords");

	}
%>
<script language="javascript">
    setRetrievedRecordCount(<%=recordCount%>);
</script>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Order_Line_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
            <tr>
				<td class="tablecolumn">
					<!-- Pass DB values for UOM and Qty instead of uom/quantity used for ordering -->
					<img class="icon" onclick="setOrderLineItemLookupValue('<%=resolveValue("xml:/OrderStatus/OrderLine/@PrimeLineNo")%>','<%=resolveValue("xml:/OrderStatus/OrderRelease/@ReleaseNo")%>','<%=resolveValue("xml:/OrderStatus/OrderLine/Item/@ItemID")%>','<%=resolveValue("xml:/OrderStatus/OrderLine/Item/@ProductClass")%>','<%=resolveValue("xml:/OrderStatus/OrderLine/Item/@UnitOfMeasure")%>', '<%=resolveValue("xml:/OrderStatus/OrderLine/OrderLineTranQuantity/@StatusQuantity")%>')"  value="<%=resolveValue("xml:/OrderStatus/OrderLine/Item/@ItemID")%>" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />
				</td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemID"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ProductClass"/></td>
                <!-- User should be able to see the quantity/uom that he/she used for ordering -->
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@TransactionalUOM"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty"/></td>	
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
