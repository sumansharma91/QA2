<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%
    String curAuditLevelKey="";
    String orderAuditLevelKey=getValue("OrderAuditEntity","xml:/OrderAuditEntity/@OrderAuditLevelKey");
	String sDisplayCreditCardOldNo = "";
	String sDisplayCreditCardNewNo = "";
	
	String sDisplaySvcOldNo = "";
	String sDisplaySvcNewNo = "";
	
	String sDisplayCustomerAccountOldNo = "";
	String sDisplayCustomerAccountNewNo = "";
	
	String sDisplayOtherOldNo = "";
	String sDisplayOtherNewNo = "";
	
%>

<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader" sortable="no"">
            <yfc:i18n>Audit_Type</yfc:i18n>
        </td>
        <td class="tablecolumnheader" sortable="no"">
            <yfc:i18n>Identifier</yfc:i18n>
        </td>
        <td class="tablecolumnheader" sortable="no"">
            <yfc:i18n>Name</yfc:i18n>
        </td>
        <td class="tablecolumnheader" sortable="no">
            <yfc:i18n>Old_Value</yfc:i18n>
        </td>
        <td class="tablecolumnheader" sortable="no">
            <yfc:i18n>New_Value</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="OrderAudit" binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel" > 
        <%  
            curAuditLevelKey= getValue("OrderAuditLevel","xml:/OrderAuditLevel/@OrderAuditLevelKey");
        if(equals(curAuditLevelKey, orderAuditLevelKey) || equals("Y", request.getParameter("IgnoreHoldLevel") ) )	{
        %> 

        <yfc:makeXMLInput name="OrderLineKey" >
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderAuditLevel/@OrderLineKey" />
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="OrderReleaseKey" >
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderAuditLevel/@OrderReleaseKey" />
        </yfc:makeXMLInput>


<%
	YFCElement eRoot = getElement("OrderAuditLevel");
	YFCElement eOrderAuditDetails = eRoot.getChildElement("OrderAuditDetails");
	YFCNodeList nlOrderAuditDetail = eOrderAuditDetails.getElementsByTagName("OrderAuditDetail");
	for(int i = 0, listLen = nlOrderAuditDetail.getLength(); i < listLen; i++)	{
		YFCElement eOrderAuditDetail = (YFCElement)nlOrderAuditDetail.item(i);
		request.setAttribute("SingleAuditDetail", eOrderAuditDetail);
%>
		<tr>
			<td class="tablecolumn">
				<yfc:getXMLValue binding="xml:SingleAuditDetail:/OrderAuditDetail/@AuditType"/>
			</td>       
			<td class="tablecolumn">
				<yfc:loopXML binding="xml:SingleAuditDetail:/OrderAuditDetail/IDs/@ID" id="ID" > 
					<yfc:getXMLValue name="ID" binding="xml:/ID/@Name"/>:&nbsp;<yfc:getXMLValue name="ID" binding="xml:/ID/@Value"/> <br/> 
				</yfc:loopXML>
			</td>
			<td/>
			<td/>
			<td/>
		</tr>

		<% sDisplayCreditCardOldNo = "";
		   sDisplayCreditCardNewNo = "";
		%>		
		<yfc:loopXML binding="xml:SingleAuditDetail:/OrderAuditDetail/Attributes/@Attribute" id="Attribute" > 
			<%	String sAttrName = resolveValue("xml:Attribute:/Attribute/@Name"); 
				if (equals(sAttrName, "DisplayCreditCardNo")){
					sDisplayCreditCardOldNo = resolveValue("xml:Attribute:/Attribute/@OldValue");
					sDisplayCreditCardNewNo = resolveValue("xml:Attribute:/Attribute/@NewValue");
				} else if (equals(sAttrName, "DisplaySvcNo")){
					sDisplaySvcOldNo = resolveValue("xml:Attribute:/Attribute/@OldValue");
					sDisplaySvcNewNo = resolveValue("xml:Attribute:/Attribute/@NewValue");
				}else if (equals(sAttrName, "DisplayCustomerAccountNo")){
					sDisplayCustomerAccountOldNo = resolveValue("xml:Attribute:/Attribute/@OldValue");
					sDisplayCustomerAccountNewNo = resolveValue("xml:Attribute:/Attribute/@NewValue");
				}else if(equals(sAttrName, "DisplayPaymentReference1")){
					sDisplayOtherOldNo = resolveValue("xml:Attribute:/Attribute/@OldValue");
					sDisplayOtherNewNo = resolveValue("xml:Attribute:/Attribute/@NewValue");
				}
				%>
		</yfc:loopXML>

		<yfc:loopXML binding="xml:SingleAuditDetail:/OrderAuditDetail/Attributes/@Attribute" id="Attribute" > 
			<%  String sAttrName = resolveValue("xml:Attribute:/Attribute/@Name"); %>
			<% if (!equals(sAttrName, "Modifyts") && !equals(sAttrName, "Createts") && !equals(sAttrName, "Modifyprogid") && !equals(sAttrName, "Createprogid") && !equals(sAttrName, "Modifyuserid") && !equals(sAttrName, "Createuserid")) { 
			%>
			<%	boolean bSkip = false; %>
			<yfc:loopXML binding="xml:SingleAuditDetail:/OrderAuditDetail/IDs/@ID" id="ID" > 
					<%  String sID = resolveValue("xml:/ID/@Name"); 
					if (equals(sID, sAttrName)) { 
						bSkip = true;
					}
					%>
			</yfc:loopXML>
				<% if (!bSkip) { %>
					<tr>
						<td/>
						<td/>
						<td class="tablecolumn">
							<yfc:i18n><yfc:getXMLValue name="Attribute" binding="xml:/Attribute/@Name"/></yfc:i18n>
						</td>
						<%  String sAttrOldVal = resolveValue("xml:Attribute:/Attribute/@OldValue");
							String sAttrNewVal = resolveValue("xml:Attribute:/Attribute/@NewValue");
							if(equals(sAttrName, "SchedFailureReasonCode")){
						%>
							<td class="tablecolumn"><yfc:i18n><%=getLocalizedValue(sAttrName,sAttrOldVal)%></yfc:i18n></td>
							<td class="tablecolumn"><yfc:i18n><%=getLocalizedValue(sAttrName,sAttrNewVal)%></yfc:i18n></td>
						<% }else if(equals(sAttrName, "CreditCardNo")){%>
							<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
							<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>
							<%}else if(equals(sAttrName, "SvcNo")){	%>
								<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
								<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>

							<%}else if(equals(sAttrName, "CustomerAccountNo")){								
								if(userHasDecryptedPaymentAttributesPermissions()){%>
								<yfc:callAPI apiID="AP3"/>
								<%String customerAccountEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("CustomerAccountRule"));
									if(equals(customerAccountEncrypted,"Y")){
										YFCElement inputOldElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+sAttrOldVal+"\"/>").getDocumentElement();
										YFCElement inputNewElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+sAttrNewVal+"\"/>").getDocumentElement();
										YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();%>
										<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputOldElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedOldString"/>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName, resolveValue("xml:GetDecryptedOldString:/GetDecryptedString/@DecryptedString"))%></td>
										<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputNewElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedNewString"/>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName, resolveValue("xml:GetDecryptedNewString:/GetDecryptedString/@DecryptedString"))%></td>									
									<%}else{%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>
									<%}
								}else{
									if(!isVoid(sAttrOldVal)){%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,showEncryptedCreditCardNo(sDisplayCustomerAccountOldNo))%></td>
									<%}else {%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
									<%}if(!isVoid(sAttrNewVal)){%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,showEncryptedCreditCardNo(sDisplayCustomerAccountNewNo))%></td>
									<%}else {%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>
									<%}
								 } 
							}else if(equals(sAttrName, "PaymentReference1")){								
								if(userHasDecryptedPaymentAttributesPermissions()){%>
								<yfc:callAPI apiID="AP4"/>
								<%String otherPaymentTypeEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("OtherPaymentTypeRule"));
									if(equals(otherPaymentTypeEncrypted,"Y")){
										YFCElement inputOldElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+sAttrOldVal+"\"/>").getDocumentElement();
										YFCElement inputNewElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+sAttrNewVal+"\"/>").getDocumentElement();
										YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();%>
										<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputOldElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedOldString"/>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName, resolveValue("xml:GetDecryptedOldString:/GetDecryptedString/@DecryptedString"))%></td>
										<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputNewElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedNewString"/>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName, resolveValue("xml:GetDecryptedNewString:/GetDecryptedString/@DecryptedString"))%></td>									
									<%}else{%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>
									<%}
								}else{
									if(!isVoid(sAttrOldVal)){%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,showEncryptedCreditCardNo(sDisplayOtherOldNo))%></td>
									<%}else {%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
									<%}if(!isVoid(sAttrNewVal)){%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,showEncryptedCreditCardNo(sDisplayOtherNewNo))%></td>
									<%}else {%>
										<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>
									<%}
								 } 
							}else{ %>
								<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrOldVal)%></td>
								<td class="tablecolumn"><%=getLocalizedValue(sAttrName,sAttrNewVal)%></td>
						<% } %>
					</tr>
				<% }
				} %>
            </yfc:loopXML> 
        <%	}
		}	%>
        </yfc:loopXML> 
</tbody>
</table>
