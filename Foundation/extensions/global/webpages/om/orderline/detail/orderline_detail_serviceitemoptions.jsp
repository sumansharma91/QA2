<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
		<td width="80%" style="border:1px solid black">
			<table class="table" >
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
						<tr>
							<td class="checkboxcolumn"><input type="checkbox" name="chkEntityPSOPT" disabled=true yfcMultiSelectOptionCounter='<%=ItemOptionCounter%>' 
							yfcMultiSelectOptionValue1='<%=getValue("ItemOption","xml:/ItemOption/@ItemID")%>' yfcMultiSelectOptionValue2='<%=getValue("ItemOption","xml:/ItemOption/@UnitOfMeasure")%>'
							yfcOptionBelongsToLineNo='<%=getParameter("optionSetBelongingToLine")%>'
							<%
							/*		System.out.println("TLID = " +ItemOptionCounter);
									System.out.println("OptionID = " +getValue("ItemOption","xml:/ItemOption/@ItemID"));
									System.out.println("OptionUOM = " +getValue("ItemOption","xml:/ItemOption/@OUnitOfMeasure"));
									System.out.println("OptionBelongsToLineNo = " +getParameter("optionSetBelongingToLine")); */
							%>
							/></td>
							<td class="tablecolumn">
								<yfc:getXMLValue binding="xml:/ItemOption/@ItemID" />
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
