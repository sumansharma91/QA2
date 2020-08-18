<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
    prepareOrderElement((YFCElement) request.getAttribute("Order"),null, null );
%>
<table cellSpacing=0 class="anchor" cellpadding="7px">
<%  

YFCElement myorder = (YFCElement) request.getAttribute("Order");
YFCElement paymentmethod = myorder.getChildElement("PaymentMethods").getChildElement("PaymentMethod");
String currentPaymentKey = paymentmethod.getAttribute("PaymentKey");
%>
    <yfc:callAPI apiID="AP1"/>

<%
ArrayList paymentMethodsList = getLoopingElementList("xml:/Order/PaymentMethods/@PaymentMethod");
	for (int PaymentMethodCounter = 0; PaymentMethodCounter < paymentMethodsList.size(); PaymentMethodCounter++) {
		
		YFCElement singlePaymentMethod = (YFCElement) paymentMethodsList.get(PaymentMethodCounter);
		pageContext.setAttribute("PaymentMethod", singlePaymentMethod);
		
		/*The line directly below has been added to work around a bug in innerpanel.jsp.
		  Need to assess the impact of the proposed change to innerpanel.jsp before making 
		  the fix and removing the line below.*/
		  
		request.removeAttribute(YFCUIBackendConsts.YFC_CURRENT_INNER_PANEL_ID);

		String paymentType = getValue("PaymentMethod", "xml:/PaymentMethod/@PaymentType");
		String paymentTypeGroup = "";
		if(!isVoid(paymentType)){
			YFCElement paymentTypeListElem = (YFCElement)request.getAttribute("PaymentTypeList");
			for (Iterator i = paymentTypeListElem.getChildren(); i.hasNext();) {
				YFCElement childElem = (YFCElement) i.next();
				if(equals(paymentType, childElem.getAttribute("PaymentType"))){
					paymentTypeGroup = childElem.getAttribute("PaymentTypeGroup");
					break;
				}
			}
		}
		request.setAttribute("PaymentMethod", pageContext.getAttribute("PaymentMethod"));

		preparePaymentMethodElement((YFCElement) request.getAttribute("PaymentMethod"), paymentTypeGroup);
	%>
	<% if(!isVoid(resolveValue("xml:/PaymentMethod/@PaymentKey")) && equals(currentPaymentKey, resolveValue("xml:/PaymentMethod/@PaymentKey"))){%>
		<tr>
			<td addressip="true">
                <input type="hidden" <%=getTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@PaymentKey","xml:/PaymentMethod/@PaymentKey")%>/>
				<input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
				<input type="hidden" name="xml:/Order/@ModificationReasonText"/>
				<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
				<input type="hidden" name="xml:/Order/@Override" value="N"/>
				<input type="hidden" name="<%="xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@PaymentTypeGroup"%>" value="<%=HTMLEncode.htmlEscape(paymentTypeGroup)%>"/>
				<input type="hidden" name="xml:/Order/@PaymentMethodCounter" value='<%=String.valueOf(PaymentMethodCounter)%>'/>
				<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:/Order/@DraftOrderFlag")%>'/>
				<jsp:include page="/yfc/innerpanel.jsp" flush="true">
					<jsp:param name="CurrentInnerPanelID" value="I01"/>
					<jsp:param name="PaymentMethodCounter" value='<%=String.valueOf(PaymentMethodCounter)%>'/>  
					<jsp:param name="Title" value='<%=getLocalizedPaymentType(paymentTypeGroup)%>'/>
					<jsp:param name="PaymentTypeGroup" value='<%=paymentTypeGroup%>'/>
					<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
					<jsp:param name="AllowedModValue" value='N'/>
					<jsp:param name="DataXML" value="Order"/>
				</jsp:include>
			</td>
		</tr>
	<%break;}
}%>

</table>
