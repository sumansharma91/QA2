<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>


<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
		<td width="80%" style="border:1px solid black">
			<table class="table" ID="OptionTable" >
				<thead>
					<tr>
						<td class="tablecolumnheader" sortable="no">
							&nbsp;
						</td>
						<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Option_ID</yfc:i18n></td>
						<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLine/Item/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
						<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@LineTotal")%>"><yfc:i18n>Price</yfc:i18n></td>
					</tr>
				</thead>
				<tbody>
					<yfc:loopXML name="ItemOptions" binding="xml:/ItemOptions/@ItemOption" id="ItemOption">
				<%	String checkBoxKeyValue=getValue("ItemOption","xml:/ItemOption/@ItemID");
						String checkBoxKeyName=	"xml:/OrderLine/OrderLineOptions/OrderLineOption_"+ItemOptionCounter+"/@OptionItemID" ;
						String hiddenInputUOMName=	"xml:/OrderLine/OrderLineOptions/OrderLineOption_"+ItemOptionCounter+"/@OptionUOM"; %>
						<tr>
							<td class="checkboxcolumn"><input type="checkbox" ID="chkEntityDSOPT" yfcSuppressInitCheckBox="true"  <%=yfsGetCheckBoxOptions(checkBoxKeyName,"",checkBoxKeyValue,"xml:/Order/AllowedModifications")%>
							<%
							/*		System.out.println("TLID = " +ItemOptionCounter);
									System.out.println("OptionID = " +getValue("ItemOption","xml:/ItemOption/@ItemID"));
									System.out.println("OptionUOM = " +getValue("ItemOption","xml:/ItemOption/@UnitOfMeasure"));
									System.out.println("OptionBelongsToLineNo = " +getParameter("optionSetBelongingToLine")); */
							%>
							/></td>
							<td class="tablecolumn">
								<yfc:getXMLValue binding="xml:/ItemOption/@ItemID" />
								<input type="hidden" name="<%=hiddenInputUOMName%>" value='<%=getValue("ItemOption", "xml:/ItemOption/@UnitOfMeasure")%>'/>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue binding="xml:/ItemOption/PrimaryInformation/@ShortDescription" />
							</td>
							<td class="tablecolumn">
								<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/ItemOption/@OptionPrice" />&nbsp;<%=curr0[1]%>
							</td>
						</tr>
					</yfc:loopXML> 
				</tbody>
			</table>
		</td>
		<td width="10%">
			&nbsp;
		</td>
	</tr>
</tbody>
</table>	
