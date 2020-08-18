<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

	<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0" >
		<thead>
			<tr>
				<td class="checkboxheader" sortable="no">
						<input type="checkbox"     onclick='doCheckAll(this)'   ></input>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Line_#</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Item_ID</yfc:i18n>
				</td>

				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>UOM</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Description</yfc:i18n>
				</td>


			</tr>
		</thead>

		<tbody>
			<yfc:loopXML binding="xml:AdditionalProductLines:/OrderLine/AdditionalOrderLines/@OrderLine" id="AddlOrderLine">
				<tr>
					<td class="checkboxcolumn">

						<yfc:makeXMLInput  name="orderLineKey"  >
							<yfc:makeXMLKey value="xml:AdditionalProductLines:/OrderLine/@OrderLineKey" binding="xml:/OrderLineDetail/@DeliveryOrderLineKey" />
							<yfc:makeXMLKey value="xml:AddlOrderLine:/OrderLine/@OrderLineKey" binding="xml:/OrderLineDetail/@OrderLineKey" />
						</yfc:makeXMLInput>

						<input type="checkbox" name="AddlOrderLineEntityKey" value='<%=getParameter("orderLineKey")%>'/>
					</td>

					<td class="tablecolumn">
						<a   <%=getDetailHrefOptions("L01",getParameter("orderLineKey"),"ShowReleaseNo=Y")%>   >
							<yfc:getXMLValue  binding="xml:AddlOrderLine:/OrderLine/@PrimeLineNo" />
						</a>
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:AddlOrderLine:/OrderLine/Item/@ItemID" />
					</td>

					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:AddlOrderLine:/OrderLine/Item/@UnitOfMeasure" />
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:AddlOrderLine:/OrderLine/Item/PrimaryInformation/@ShortDescription" />
					</td>
				</tr>
			</yfc:loopXML>
		</tbody>
	</table>
