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
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Release_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receiving_Node</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_Open</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
	    <yfc:loopXML binding="xml:/ReceiptList/@Receipt" id="Receipt" >
            <tr>
				<yfc:makeXMLInput name="receiptKey">
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey" />
				</yfc:makeXMLInput>
	            <td class="checkboxcolumn">
					<input type="checkbox" value='<%=getParameter("receiptKey")%>' name="EntityKey"/>
				</td>
	            <td class="tablecolumn">
					<a href="javascript:showDetailFor('<%=getParameter("receiptKey")%>');">
						<yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/>
					</a>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Order/@OrderNo"/>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/OrderRelease/@ReleaseNo"/>
				</td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Receipt/@ReceiptDate")%>">
					<yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"/>
				</td>
                <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Order/OrderLines/OrderLine/@ReceivingNode"/>
				</td>
                <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/@OpenReceiptFlag"/>
				</td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
