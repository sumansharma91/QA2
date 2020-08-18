<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%	String bshowProductClassColumn = getParameter("showProductClassColumn");
//		System.out.println("bshowProductClassColumn = "+bshowProductClassColumn);
%>
<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0">
	<thead>
		<tr>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("PrimeLineNo")%>">
				<yfc:i18n>Line</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ItemID")%>">
				<yfc:i18n>Item_ID</yfc:i18n>
			</td>
<%		if (equals(bshowProductClassColumn,"true")) {	 %>
				<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ProductClass")%>">
					<yfc:i18n>PC</yfc:i18n>
				</td>
<%		}	%>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("UnitOfMeasure")%>">
				<yfc:i18n>UOM</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ItemID")%>">
				<yfc:i18n>Quantity_To_Schedule</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("Text")%>">
				<yfc:i18n>Reason</yfc:i18n>
			</td>
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML binding="xml:Promise:/Promise/SuggestedOption/UnavailableLines/@UnavailableLine" id="UnavailableLine">
		<%	boolean moreThanOneReason = false;	 %>
				<yfc:makeXMLInput  name="orderLineKey"  >
					<yfc:makeXMLKey  value="xml:/UnavailableLine/@OrderLineKey" binding="xml:/OrderLineDetail/@OrderLineKey" />
				</yfc:makeXMLInput>
				<tr>
					<td class="tablecolumn">
						<a  <%=getDetailHrefOptions("L01", resolveValue("xml:/Order/@DocumentType"), getParameter("orderLineKey"),"")%>   ><yfc:getXMLValue  binding="xml:/UnavailableLine/@PrimeLineNo" /></a>
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/UnavailableLine/@ItemID" /> 
					</td>
<%				if (equals(bshowProductClassColumn,"true")) {	 %>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/UnavailableLine/@ProductClass" /> 
					</td>
<%				}	%>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/UnavailableLine/@UnitOfMeasure" />
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/UnavailableLine/@ToScheduleQty" />
					</td>
					<td class="tablecolumn">
						<yfc:loopXML binding="xml:UnavailableLine:/UnavailableLine/Reasons/@Reason" id="Reason">
						<%	if (moreThanOneReason) {	%>
								<br/>
						<%	}	
								String reasonText=getValue("Reason", "xml:/Reason/@Text");	
								moreThanOneReason=true; %>
							<%=getI18N(reasonText)%>
						</yfc:loopXML>
					</td>
				</tr>
		</yfc:loopXML>
	</tbody>
</table>
