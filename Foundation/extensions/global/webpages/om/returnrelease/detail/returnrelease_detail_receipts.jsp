<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%boolean bContainsReceipt = false;%>
<table class="table" width="100%">
    <thead>
        <tr>
            <td class="checkboxheader" sortable="no">
                <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Open</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>No_Of_Expected_Cartons</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>No_Of_Expected_Pallets</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/ReceiptList/@Receipt" id="Receipt">
		<%bContainsReceipt = true;%>
            <tr>
                <yfc:makeXMLInput name="receiptKey">
                    <yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey"/>
                </yfc:makeXMLInput>
                <td class="checkboxcolumn"><input type="checkbox" value='<%=getParameter("receiptKey")%>' name="chkEntityKey"/></td>
                <td class="tablecolumn">
                    <a <%=getDetailHrefOptions("L01", getParameter("receiptKey"), "")%>>
                    <yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/></a>
                </td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Receipt/@ReceiptDate")%>">
                    <yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"/>
                </td>
                <td class="tablecolumn" >
                    <yfc:getXMLValue binding="xml:/Receipt/@OpenReceiptFlag"/>
                </td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Receipt/@NumOfCartons")%>">
                    <yfc:getXMLValue binding="xml:/Receipt/@NumOfCartons"/>
                </td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Receipt/@NumOfPallets")%>">
                    <yfc:getXMLValue binding="xml:/Receipt/@NumOfPallets"/>
                </td>
            </tr>
        </yfc:loopXML> 
		<%if(!bContainsReceipt){%>
		<yfc:callAPI apiID="AP2"/>
        <yfc:loopXML binding="xml:ReceiptList1:/ReceiptList/@Receipt" id="Receipt">
			<tr>
                <yfc:makeXMLInput name="receiptKey">
                    <yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey"/>
                </yfc:makeXMLInput>
                <td class="checkboxcolumn"><input type="checkbox" value='<%=getParameter("receiptKey")%>' name="chkEntityKey"/></td>
                <td class="tablecolumn">
                    <a <%=getDetailHrefOptions("L01", getParameter("receiptKey"), "")%>>
                    <yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/></a>
                </td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Receipt/@ReceiptDate")%>">
                    <yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"/>
                </td>
                <td class="tablecolumn" >
                    <yfc:getXMLValue binding="xml:/Receipt/@OpenReceiptFlag"/>
                </td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Receipt/@NumOfCartons")%>">
                    <yfc:getXMLValue binding="xml:/Receipt/@NumOfCartons"/>
                </td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Receipt/@NumOfPallets")%>">
                    <yfc:getXMLValue binding="xml:/Receipt/@NumOfPallets"/>
                </td>
            </tr>
        </yfc:loopXML> 
		<%}%>
    </tbody>
</table>
