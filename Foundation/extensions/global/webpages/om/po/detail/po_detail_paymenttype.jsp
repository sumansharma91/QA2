<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@page  import="com.yantra.yfc.ui.backend.util.*" %>

<table class="anchor">
<tr>
    <td valign="top">
        <table class="view" width="100%">
            <tr>
                <td class="detaillabel" ><yfc:i18n>Payment_Rule</yfc:i18n></td>
                <td>
                    <select <%=yfsGetComboOptions("xml:/Order/@PaymentRuleId", "xml:/Order/AllowedModifications")%>>
                        <yfc:loopOptions binding="xml:/PaymentRuleList/@PaymentRuleId" name="Description" value="PaymentRuleId" selected="xml:/Order/@PaymentRuleId" isLocalized="Y"/>
                    </select>
                </td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>            
        </table>
    </td>
</tr>
<tr>
    <td valign="top">
        <table class="view" width="100%" cellSpacing="10">
        <%  ArrayList paymentMethodsList = getLoopingElementList("xml:/Order/PaymentMethods/@PaymentMethod");
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

                preparePaymentMethodElement((YFCElement) request.getAttribute("PaymentMethod"));
        %>
		<% if(!isVoid(resolveValue("xml:/PaymentMethod/@PaymentKey"))){%>
            <tr>
                <td addressip="true">
                <jsp:include page="/yfc/innerpanel.jsp" flush="true">
                    <jsp:param name="CurrentInnerPanelID" value="I03"/>
                    <jsp:param name="PaymentMethodCounter" value='<%=String.valueOf(PaymentMethodCounter)%>'/>  
					<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
					<jsp:param name="AllowedModValue" value='N'/>
					<jsp:param name="DataXML" value="Order"/>
                   <jsp:param name="Title" value='<%=getLocalizedPaymentType(paymentTypeGroup)%>'/>
                </jsp:include>
                </td>
            </tr>
		<%}%>
        <% } %>
        </table>
    </td>
</tr>
</table>
