<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<%
    String sCheckedValue=""; 
%> 
<table class="view" width="100%">
		<yfc:makeXMLInput name="receiptLineKey">
        <yfc:makeXMLKey binding="xml:/Receipt/ReceiptLines/ReceiptLine/@ReceiptLineKey" value="xml:/ReceiptLineList/ReceiptLine/@ReceiptLineKey"> </yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="receiptHeaderKey">
        <yfc:makeXMLKey binding="xml:/Receipt/ReceiptLines/ReceiptLine/@ReceiptHeaderKey" value="xml:/ReceiptLineList/ReceiptLine/@ReceiptHeaderKey"> </yfc:makeXMLKey>
		</yfc:makeXMLInput>
<tr>	
	<td class="detaillabel" ><yfc:i18n>Lot_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@LotNumber"/></td>
	<td class="detaillabel" ><yfc:i18n>Serial_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@SerialNo"/></td>
	<td class="detaillabel" ><yfc:i18n>Quantity</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@Quantity"/></td>
</tr>
</table> 
<table class="view" width=100%">
<tr>
    <td>
      
	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="Radio" class="radiobutton" 		onclick="disableAndClearFields('xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveQuantity','xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveQuantity','xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveQuantity');" 
        <%=getRadioOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveEntireLine", sCheckedValue ,"Y" )%> />
        <yfc:i18n>UnReceive_Entire_Line</yfc:i18n>
	</td>
<tr>
</tr>
	<td>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="Radio" class="radiobutton" 		onclick="enableFields('xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveQuantity','xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveQuantity');"  
        <%=getRadioOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveEntireLine", sCheckedValue ,"N" )%> />
        <yfc:i18n>UnReceive_Quantity</yfc:i18n>
		<input type="text" class="numericunprotectedinput" 
		<%if(sCheckedValue=="Y"){%> disabled="true" <%}%>
		 <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@UnreceiveQuantity")%>/>
	</td>
</tr>
</table> 
		<input type="hidden" value='<%=getTextOptions("xml:/ReceiptLineList/ReceiptLine/@ReceiptHeaderKey")%>'/>
		<input type="hidden" value='<%=getTextOptions("xml:/ReceiptLineList/ReceiptLine/@ReceiptNo")%>'/>
		<input type="hidden" value='<%=getTextOptions("xml:/ReceiptLineList/ReceiptLine/@ReceivingNode")%>'/>
		<input type="hidden" value='<%=getTextOptions("xml:/ReceiptLineList/ReceiptLine/@ReceivingDock")%>'/>
		<input type="hidden"
		value='<%=getTextOptions("xml:/ReceiptLineList/ReceiptLine/@ReceiptLineKey")%>'/>
