<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%!
public String checkNull(String sCheck)
{
	if (sCheck==null) return("");
	else return sCheck;
}

%>
<%
YFCElement oRep;
YFCElement root = (YFCElement)request.getAttribute("Receipt");
Map oMap=new HashMap();
int counter=0;
String sKey=" ";
	if(root != null)
	{
		YFCElement oItem = root.getChildElement("ReceiptLines"); 
		if(oItem!= null)
			{
			for (Iterator i = oItem.getChildren(); i.hasNext();)
				{
					oRep = (YFCElement) i.next();
					sKey = checkNull(oRep.getAttribute("PalletId"))
								+checkNull(oRep.getAttribute("CaseId"));
					if(!oMap.containsKey(sKey)) {
						String temp = "nothing";
						oMap.put(sKey,temp);
					}
				}
			}
	}
%>
<div style="height:175px;overflow:auto">
<table class="table">
<thead>
    <tr> 
		<td sortable="no" class="checkboxheader">
			<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>  
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@PalletId")%>">
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@CaseId")%>">
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML binding="xml:/Receipt/ReceiptLines/@ReceiptLine" id="ReceiptLine"> 
	<%
	sKey = resolveValue("xml:/ReceiptLine/@PalletId")
					+resolveValue("xml:/ReceiptLine/@CaseId");
	if(!isVoid(sKey)){
	if(oMap.containsKey(sKey)) {
		oMap.remove(sKey);
%>
	<tr> 
	<yfc:makeXMLInput name="palletKey">
		<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/ReceiptLine/@EnterpriseCode">
		</yfc:makeXMLKey>
		<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@PalletId" value="xml:/ReceiptLine/@PalletId"> </yfc:makeXMLKey>
		<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/Receipt/@ReceivingNode"> </yfc:makeXMLKey>
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="caseKey">
		<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/ReceiptLine/@EnterpriseCode">
		</yfc:makeXMLKey>
		<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@CaseId" value="xml:/ReceiptLine/@CaseId"> </yfc:makeXMLKey>
		<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/Receipt/@ReceivingNode"> </yfc:makeXMLKey>
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="receiptKey">
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@CaseId" value="xml:/ReceiptLine/@CaseId"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@PalletId" value="xml:/ReceiptLine/@PalletId"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo" />
		<yfc:makeXMLKey binding="xml:/Receipt/@ShipmentNo" value="xml:/Receipt/Shipment/@ShipmentNo"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode" />
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock" />
		<yfc:makeXMLKey binding="xml:/Receipt/@StatusName" value="xml:/Receipt/Status/@StatusName" />
        <yfc:makeXMLKey binding="xml:/Receipt/@Status" value="xml:/Receipt/@Status"/>
	</yfc:makeXMLInput>
	    <td class="checkboxcolumn">
			<input type="checkbox" value='<%=getParameter("receiptKey")%>' name="containerEntityKey"/>
		</td>
		<td class="tablecolumn">
		<a <%=getDetailHrefOptions("L01", getParameter("palletKey"),"")%> >
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PalletId"/>
		</a>
        </td>
		<td class="tablecolumn">
		<a <%=getDetailHrefOptions("L01", getParameter("caseKey"),"")%> >
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CaseId"/>
		</a>
        </td>
        </tr>
	 	<%}}%>
    </yfc:loopXML> 
</tbody>
	<input type="hidden" name="xml:/Receipt/Audit/@ReasonCode"/>
	<input type="hidden" name="xml:/Receipt/Audit/@ReasonText"/>
</table>
