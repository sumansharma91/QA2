<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/om/orderschedule/detail/orderschedule_detail_productline_oneinteraction_include.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%	String DelMethodSentFromAnchor = getParameter("DeliveryMethod"); %>
<table class="view" width="100%">
<tr>
	<td class="detaillabel" nowrap="true" ><yfc:i18n>Ship_Node</yfc:i18n></td>	
	<td class="protectedtext">
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/@Node"/>&nbsp;
	</td>
	<td class="detaillabel" nowrap="true" ><yfc:i18n>Ship_To_Address</yfc:i18n></td>	
	<td class="protectedtext" nowrap="true" colspan="3">
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/PersonInfoShipTo/@AddressLine1"/>&nbsp;
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/PersonInfoShipTo/@AddressLine2"/>&nbsp;
</tr>
<tr>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td class="protectedtext" nowrap="true" >	
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/PersonInfoShipTo/@City"/>&nbsp;
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/PersonInfoShipTo/@State"/>&nbsp;
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/PersonInfoShipTo/@Country"/>&nbsp;
	<yfc:getXMLValue name="Interaction" binding="xml:/Interaction/PersonInfoShipTo/@ZipCode"/>&nbsp;
	</td>
</tr>
<%  
	YFCElement interactionElement = (YFCElement)request.getAttribute("Interaction");
//	System.out.println(interactionElement.getString());

	YFCDocument timeLineInputDocument = formTimeLineInputDocumentForAvailability(interactionElement,getParameter("DeliveryMethod"));
//	System.out.println(timeLineInputDocument.getString());

	YFCElement timeLineInputElement = timeLineInputDocument.getDocumentElement();
    request.setAttribute("TimeLineInput", timeLineInputElement);
%>
<tr>
	<td >
	<%	if (equals(DelMethodSentFromAnchor,"SHP")) {	 %>
				<input type="hidden" name="<%=getDefaultEntityKeyName()%>"/>
				<yfc:loopXML name="Interaction" binding="xml:/Interaction/PromiseLines/@PromiseLine" id="PromiseLine">
							<input type="hidden" name="xml:/<%=getDefaultEntityKeyName()%>/PromiseLine_<%=PromiseLineCounter%>/@OrderLineKey" value='<%=getValue("PromiseLine", "xml:/PromiseLine/@OrderLineKey")%>'/>
							<input type="hidden" name="xml:/<%=getDefaultEntityKeyName()%>/PromiseLine_<%=PromiseLineCounter%>/@ShipNode" value='<%=getValue("Interaction", "xml:/Interaction/@Node")%>'/>
							<input type="hidden" name="xml:/<%=getDefaultEntityKeyName()%>/PromiseLine_<%=PromiseLineCounter%>/@DeliveryDate" value='<%=getValue("Interaction", "xml:/Interaction/@DeliveryDate")%>'/>
							<input type="hidden" name="xml:/<%=getDefaultEntityKeyName()%>/PromiseLine_<%=PromiseLineCounter%>/@ShipDate" value='<%=getValue("Interaction", "xml:/Interaction/@ShipDate")%>'/>
							<input type="hidden" name="xml:/<%=getDefaultEntityKeyName()%>/PromiseLine_<%=PromiseLineCounter%>/@Quantity" value='<%=getValue("PromiseLine", "xml:/PromiseLine/Assignment/@Quantity")%>'/>
				</yfc:loopXML> 
	<%	}	%>
	</td>
</tr>
<tr>
	<td colspan="4">
		<jsp:include page="/yfsjspcommon/timelineview.jsp" flush="true" />
	</td>
</tr>
</table>
