<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 


<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
				<td class="tablecolumnheader"><yfc:i18n>Modified_By</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Old_Status</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Old_Status_Date</yfc:i18n></td>	    
				<td class="tablecolumnheader"><yfc:i18n>New_Status</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>New_Status_Date</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Reason_Code</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Reason_Text</yfc:i18n></td>
        </tr>
    </thead> 
    <tbody> 
	    <yfc:loopXML binding="xml:/Receipt/ReceiptStatusAudits/@ReceiptStatusAudit" id="ReceiptStatusAudit" >
				<td class="tablecolumn">
					<%
						String sUser = resolveValue("xml:/ReceiptStatusAudit/@CreateUserName");
						if(isVoid(sUser) )
							sUser = resolveValue("xml:/ReceiptStatusAudit/@Createuserid");
					%>
					<%=sUser%>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValueI18NDB binding="xml:/ReceiptStatusAudit/OldStatus/@Description"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ReceiptStatusAudit/@OldStatusDate"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValueI18NDB binding="xml:/ReceiptStatusAudit/NewStatus/@Description"/>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ReceiptStatusAudit/@NewStatusDate"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ReceiptStatusAudit/@ReasonCode"/>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ReceiptStatusAudit/@ReasonText"/>
				</td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
