<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
	<table  class="view">
			<tr >
					<td >
							<input type="hidden" name="xml:/OrderLine/@StatusQryType" value="BETWEEN"  />
							<input type="hidden" name="xml:/OrderLine/@PromisedApptStartDateQryType" value="DATERANGE"  />
							<input type="hidden" name="xml:/OrderLine/Order/@DraftOrderFlag" value="N"  />
							<input type="hidden" name=""  <%=getTextOptions("xml:/OrderLine/Order/@DocumentType","xml:/CurrentEntity/@DocumentType")%> />
							<input type="hidden" name="xml:/OrderLine/@ItemGroupCode" value="PS"  />
					</td>
			</tr>
			<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
				<jsp:param name="DocumentTypeBinding" value="xml:/OrderLine/Order/@DocumentType"/>
				<jsp:param name="EnterpriseCodeBinding" value="xml:/OrderLine/Order/@EnterpriseCode"/>
				<jsp:param name="RefreshOnDocumentType" value="true"/>
				<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
			</jsp:include>
			<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
			   // Statuses are refreshed.
			%>
			<yfc:callAPI apiID="AP1"/>

			<tr >
					<td  class="searchlabel">
							<yfc:i18n>Order_#</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td  class="searchcriteriacell">
							<select name="xml:/OrderLine/Order/@OrderNoQryType" class="combobox"    >
								<yfc:loopOptions  name="QueryTypeDesc" selected="xml:/OrderLine/Order/@OrderNoQryType" value="QueryType" binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" />
							</select>
							<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@OrderNo")%>    />
					</td>
			</tr>
			<tr >
					<td  class="searchlabel">
							<yfc:i18n>Buyer</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td  class="searchcriteriacell">
							<select name="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType" class="combobox"    >
								<yfc:loopOptions  name="QueryTypeDesc" selected="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType" value="QueryType" binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" />
							</select>
							<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@BuyerOrganizationCode")%>    />
							<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
                            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
					</td>
			</tr>
			<tr >
					<td  class="searchlabel">
							<yfc:i18n>Seller</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td  class="searchcriteriacell">
							<select name="xml:/OrderLine/Order/@SellerOrganizationCodeQryType" class="combobox"    >
								<yfc:loopOptions  name="QueryTypeDesc" selected="xml:/OrderLine/Order/@SellerOrganizationCodeQryType" value="QueryType" binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" />
							</select>
							<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@SellerOrganizationCode")%>    />
                            <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
					</td>
			</tr>
			<tr >
					<td  class="searchlabel">
							<yfc:i18n>Item_ID</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td  nowrap="true" class="searchcriteriacell">
							<select name="xml:/OrderLine/Item/@ItemIDQryType" class="combobox"    >
								<yfc:loopOptions  name="QueryTypeDesc" selected="xml:/OrderLine/Item/@ItemIDQryType" value="QueryType" binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" />
							</select>
							<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Item/@ItemID")%>    />
							<img class="lookupicon" name="search" onclick="callServiceItemLookup('xml:/OrderLine/Item/@ItemID','xml:/OrderLine/Item/@ProductClass','xml:/OrderLine/Item/@UnitOfMeasure','item','PS')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
					</td>
			</tr>
			<tr >
					<td  class="searchlabel">
							<yfc:i18n>Appointment</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td >
							<input type="text" class="dateinput" <%=getTextOptions("xml:/OrderLine/@FromPromisedApptStartDate_YFCDATE")%>    />
							<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
							<input type="text" class="dateinput" <%=getTextOptions("xml:/OrderLine/@FromPromisedApptStartDate_YFCTIME")%>    />
							<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
							<yfc:i18n>To</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td >
							<input type="text" class="dateinput" <%=getTextOptions("xml:/OrderLine/@ToPromisedApptStartDate_YFCDATE")%>    />
							<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
							<input type="text" class="dateinput" <%=getTextOptions("xml:/OrderLine/@ToPromisedApptStartDate_YFCTIME")%>    />
							<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
					</td>
			</tr>
			<tr >
					<td  class="searchlabel">
							<yfc:i18n>Order_Line_Status</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td  class="searchcriteriacell">
							<select name="xml:/OrderLine/@FromStatus" class="combobox"    >
								<yfc:loopOptions  name="Description" selected="xml:/OrderLine/@FromStatus" value="Status" binding="xml:/StatusList/@Status" isLocalized="Y"/>
							</select>
							<yfc:i18n>To</yfc:i18n>
					</td>
			</tr>
			<tr >
					<td  class="searchcriteriacell">
							<select name="xml:/OrderLine/@ToStatus" class="combobox"    >
								<yfc:loopOptions  name="Description" selected="xml:/OrderLine/@ToStatus" value="Status" binding="xml:/StatusList/@Status" isLocalized="Y"/>
							</select>
					</td>
			</tr>
	</table>
