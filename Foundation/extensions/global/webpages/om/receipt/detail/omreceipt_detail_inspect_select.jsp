<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<%
	String sViewId="YOMD557";
	String sEntity="return";
	if(equals(resolveValue("xml:/CurrentEntity/@ApplicationCode"),"oms")){
		sViewId="YOMD7557";
		sEntity="omreceipt";
	}
%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript"> 

function proceedClicked(obj) {
   if(!document.all("xml:/NodeInventory/@Node").value){
		alert(YFCMSG077);//var YFCMSG077 = "Node not Passed";
		return;
	}else if(!document.all("xml:/NodeInventory/@EnterpriseCode").value){
		alert(YFCMSG078);//var YFCMSG078 = "Enterprise not Passed";
		return;
	}else if((document.all("DcmIntegrationRealTimeFlag").value == "true") && !(document.all("xml:/NodeInventory/@LocationId").value||document.all("xml:/NodeInventory/Inventory/@CaseId").value||document.all("xml:/NodeInventory/Inventory/@PalletId").value||document.all("xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo").value)){
		alert(YFCMSG132);//var YFCMSG132 = "Location, Case/Pallet or Serial Number must be Passed";
		return;
	}else if((document.all("xml:/NodeInventory/Inventory/@CaseId").value && document.all("xml:/NodeInventory/Inventory/@PalletId").value)){
		alert(YFCMSG101);//var YFCMSG101 = "Both Case ID and Pallet ID cannot be Passed"
		return;
	}else if(!((document.all("xml:/NodeInventory/Inventory/InventoryItem/@ItemID").value&&document.all("xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure").value) || (document.all("xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo").value))){
		alert(YFCMSG102);//var YFCMSG102 = "Either Item and UOM or Serial Number must be passed";
		return;
	}else if(!document.all("xml:/NodeInventory/Inventory/Receipt/@ReceiptNo").value && (document.all("DcmIntegrationRealTimeFlag").value == "false")){
		alert(YFCMSG103);//Receipt No must be passed
		return;
	}else	{
			var tmp = document.all("HaveDataToLoadPopup");
			tmp.value = "Y";
			var sPrepareEntityKey = document.all("hidPrepareEntityKey");
			sPrepareEntityKey.value = "Y";
			yfcChangeDetailView(getCurrentViewId());
			return;
	}	
}



function doNormalWindow()
{
    var sVal = document.all("MyEntityKey");
	if(sVal == null) return;
    var tmp = '<%=HTMLEncode.htmlEscape(getParameter("hidPrepareEntityKey"))%>';
	var oError = document.all("YFCDetailError");
    if(tmp == "Y" && oError.HasError == "N")
    {
		<%if(equals(resolveValue("xml:/CurrentEntity/@ApplicationCode"),"omr")){%>
		entityType = "inspectreceipt";
		<%}else{%>
			entityType = "inspectinbrec";
	   <%}%>

        window.doNotCheck = true;
        oError.HasError="Y";
        showDetailFor(sVal.value);  
    }
}


</script>



<% if(equals(HTMLEncode.htmlEscape(getParameter("hidPrepareEntityKey")),"Y")) { %>
	<script>
		window.attachEvent("onload",doNormalWindow);
	</script>
	<% } %>



<table width="100%" class="view">
<input type="hidden"  name="hidPrepareEntityKey" value="N"/>

	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
			<jsp:param name="ScreenType" value="detail"/>
			<jsp:param name="ShowDocumentType" value="false"/>
			<jsp:param name="ShowNode" value="true"/>
			<jsp:param name="EnterpriseCodeBinding" value="xml:/NodeInventory/@EnterpriseCode"/>
			<jsp:param name="NodeBinding" value="xml:/NodeInventory/@Node"/>
			<jsp:param name="RefreshOnNode" value="true"/>
			<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
			<jsp:param name="EnterpriseListForNodeField" value="false"/>
	</jsp:include>
	<yfc:callAPI apiID="AP2"/>
	<yfc:callAPI apiID="AP3"/>
	<yfc:callAPI apiID="AP6"/>

<%	// Call getShipNodeList API to know whether the Node is DcmIntegrationRealTime or not.
		boolean DcmIntegrationRealTimeFlag=false;
		YFCElement nodeInput=null;
		boolean canCallgetShipNodeListAPI=false;
		if (!isVoid(getValue("CommonFields", "xml:/CommonFields/@Node"))) {
//			System.out.println("Node is selected ++++++++++++++++++++++++++++++++"); 
			nodeInput = YFCDocument.parse("<ShipNode ShipNode=\""+ getValue("CommonFields", "xml:/CommonFields/@Node")  + "\"/>").getDocumentElement();
			canCallgetShipNodeListAPI=true;		
		} else{ 
//			System.out.println("User not Node user and Node not selected ++++++++++++++++++++++++++++++++"); 
		}	
		YFCElement nodeTemplate = YFCDocument.parse("<ShipNodeList TotalNumberOfRecords=\"\"><ShipNode ShipNode=\"\" DcmIntegrationRealTime=\"\" /></ShipNodeList>").getDocumentElement();
		
		if (canCallgetShipNodeListAPI) {	%>
			<yfc:callAPI apiName="getShipNodeList" inputElement="<%=nodeInput%>" templateElement="<%=nodeTemplate%>" outputNamespace="ShipNode"/>
<%	}	
		if (equals(resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"),"Y")) {
				DcmIntegrationRealTimeFlag=true;	
		}	
%>
	<input type="hidden" name="DcmIntegrationRealTimeFlag" value='<%=DcmIntegrationRealTimeFlag%>' />
	
<%	if (DcmIntegrationRealTimeFlag) {	 %>
			<tr>
				<td class="detaillabel" >
					<yfc:i18n>Location</yfc:i18n>
				</td>
				<td nowrap="true">
					<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/@LocationId","xml:/NodeInventory/@LocationId")%> />
					<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/NodeInventory/@Node")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
				</td>
				<td class="detaillabel" >
					<yfc:i18n>Pallet_ID</yfc:i18n>
				</td>
				<td nowrap="true">
					<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/Inventory/@PalletId","xml:/NodeInventory/Inventory/@PalletId")%> />
				</td>

				<td class="detaillabel" >
					<yfc:i18n>Case_ID</yfc:i18n>
				</td>
				<td nowrap="true">
					<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/Inventory/@CaseId","xml:/NodeInventory/Inventory/@CaseId")%> />
				</td>
			</tr>
<%	}	%>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Item_ID</yfc:i18n>
		</td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/Inventory/InventoryItem/@ItemID","xml:/NodeInventory/Inventory/InventoryItem/@ItemID")%> />
			 <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<img class="lookupicon" name="search"onclick="callItemLookup('xml:/NodeInventory/Inventory/InventoryItem/@ItemID','xml:/NodeInventory/Inventory/InventoryItem/@ProductClass','xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Product_Class</yfc:i18n>
		</td>
		<td nowrap="true">
			<select name="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass" class="combobox" >
				<yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
					name="CodeValue" value="CodeValue" selected="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass" />
			</select>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Unit_Of_Measure</yfc:i18n>
		</td>
		<td nowrap="true">
			<select name="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure" class="combobox">
				<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"/>
			</select>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Receipt_#</yfc:i18n>
		</td>
		<%	String sLookup="receiptlookup";
			if(equals("omr", resolveValue("xml:/CurrentEntity/@ApplicationCode")) ){
				sLookup="returnreceiptlookup";
		}%>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" 
			<%=getTextOptions("xml:/NodeInventory/Inventory/Receipt/@ReceiptNo","xml:/NodeInventory/Inventory/Receipt/@ReceiptNo")%> />
			<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receipt") %> />
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Serial_#</yfc:i18n>
		</td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo","xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo")%> />
		</td>
		<%	if (DcmIntegrationRealTimeFlag) {	 %>
			<td class="detaillabel" >
				<yfc:i18n>Inventory_Status</yfc:i18n>
			</td>
			<td nowrap="true">
				<select name="xml:/NodeInventory/Inventory/@InventoryStatus" class="combobox" >
					<yfc:loopOptions binding="xml:InventoryStatusList:/InventoryStatusList/@InventoryStatus" 
					name="InventoryStatus" value="InventoryStatus" selected="xml:/NodeInventory/Inventory/@InventoryStatus"/>
				</select>
			</td>
		<%	} else { %>
			<td></td>
		<%	} %>

	</tr>
	<tr>
		<td colspan="6" align="center">
			<input type="hidden"  name="HaveDataToLoadPopup" 
<%		if(equals(getParameter("HaveDataToLoadPopup"),"Y"))  {	 %>			
				Value="Y"
<%		}else{	%>
				Value="N"
<%		}	%>/>
			<input class="button" type="button" Value="<%=getI18N("Proceed")%>" onclick="proceedClicked(this);" />
		</td>
	</tr>
</table>

<%
//	System.out.println("SELECTED NODE ----------------------------"+resolveValue("xml:/NodeInventory/@Node"));
//	System.out.println("USER NODE ----------------------------"+resolveValue("xml:CurrentUser:/User/@Node"));
//	System.out.println("NODE FROM COMMONFIELDS ----------------------------"+getValue("CommonFields", "xml:/CommonFields/@Node"));
	if(equals(getParameter("HaveDataToLoadPopup"),"Y"))  {
//Only if DcmIntegrationRealTimeFlag is true, we should call getNodeInventory. Otherwise, just load the inspect screen for the passed in value
		if (DcmIntegrationRealTimeFlag) {
			// temporary fix for CR 69983.
		 if (!isVoid(resolveValue("xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo"))) {
			String sSerialNo = resolveValue("xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo"); //CR 72787
			String sItemID = resolveValue("xml:/NodeInventory/Inventory/InventoryItem/@ItemID");
			String sOrgCode = resolveValue("xml:/NodeInventory/@EnterpriseCode");
			String sUOM = resolveValue("xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure");
			String sPC = resolveValue("xml:/NodeInventory/Inventory/InventoryItem/@ProductClass");
			YFCElement getSerialListInput = YFCDocument.parse("<Serial SerialNo=\""+sSerialNo+"\"><InventoryItem ItemID=\""+sItemID+"\" UnitOfMeasure=\""+sUOM+"\" OrganizationCode=\""+sOrgCode+"\" ProductClass=\"" +sPC+"\"/> </Serial>").getDocumentElement();
			YFCElement getSerialListTemplate = YFCDocument.parse("<Serial AtNode=\"\" ReceiptHeaderKey=\"\" LocationId=\"\" InventoryItemKey=\"\" InventoryStatus=\"\" SerialNo=\"\" CaseId=\"\" PalletId=\"\" ShipNode=\"\" TagNumber=\"\"> <InventoryItem ItemID=\"\" OrganizationCode=\"\" ProductClass=\"\" UnitOfMeasure=\"\"/> </Serial>").getDocumentElement();
			
			%>
				<yfc:callAPI apiName="getSerialList" inputElement="<%=getSerialListInput%>" templateElement="<%=getSerialListTemplate%>" outputNamespace="SerialList"/>
			<%
					YFCElement serialListElem = (YFCElement) request.getAttribute ("SerialList");
				//System.out.println ("output from getSerialList######### "+request.getAttribute("SerialList"));
				YFCNodeList serial = (YFCNodeList) serialListElem.getElementsByTagName("Serial");
			
				if (isVoid (serial) || serial.getLength() < 1) {		
					%>
						<script>
							alert(YFCMSG104);//var YFCMSG104 = "No receipt found matching input criteria";
						</script>
					<%
				} else if(serial.getLength() > 1) {
						%>
						<script>
						alert(YFCMSG080);//var YFCMSG080 = "Item and UOM must be Passed";
						</script>
					<%
					}else {
							String sReceiptHdrKey = getValue("SerialList","xml:SerialList:/SerialList/Serial/@ReceiptHeaderKey");
					if(isVoid(sReceiptHdrKey)){
						%>
							<script>
								alert(YFCMSG104);//var YFCMSG104 = "No receipt found matching input criteria";
							</script>
						<%
					}else {
			%>
				<yfc:makeXMLInput name="MyEntityKey">
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:SerialList:/SerialList/Serial/@ReceiptHeaderKey"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@LocationId" value="xml:SerialList:/SerialList/Serial/@LocationId"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:SerialList:/SerialList/Serial/InventoryItem/@ItemID"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:SerialList:/SerialList/Serial/InventoryItem/@ProductClass"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:SerialList:/SerialList/Serial/InventoryItem/@UnitOfMeasure"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@InventoryStatus" value="xml:SerialList:/SerialList/Serial/@InventoryStatus"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@SerialNo" value="xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@CaseId" value="xml:/NodeInventory/Inventory/@CaseId"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@PalletId" value="xml:/NodeInventory/Inventory/@PalletId"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@Quantity" value="1"/>
				</yfc:makeXMLInput>
				
				<input type="hidden" name="MyEntityKey" value='<%=HTMLEncode.htmlEscape(getParameter("MyEntityKey"))%>' />	
				<script>
					window.attachEvent("onload",doNormalWindow);
				</script>
			<%
						}
					}
		} else {
	%>
			<yfc:callAPI apiID='AP1'/>
<%		boolean validLPN = true;
		YFCElement outputNodeInventoryElem = (YFCElement) request.getAttribute("OutputNodeInventory"); 
		if (!isVoid(resolveValue("xml:/NodeInventory/Inventory/@CaseId")) || !isVoid(resolveValue("xml:/NodeInventory/Inventory/@PalletId"))) { %>
			<yfc:callAPI apiID='AP4'/> 
<%			if (YFCCommon.isVoid(resolveValue("xml:LPNDetails:/GetLPNDetails/@Node"))) {
				validLPN = false;
			}
		}
		if (validLPN) {
			String NoOfLocinvlist = 								getValue("OutputNodeInventory","xml:/NodeInventory/LocationInventoryList/@TotalNumberOfRecords");
			if (equals("0",NoOfLocinvlist)){ 
					boolean locationLPNErrorThrown=false;
					if (!isVoid(resolveValue("xml:/NodeInventory/Inventory/@CaseId")) || !isVoid(resolveValue("xml:/NodeInventory/Inventory/@PalletId"))) { %>
						<yfc:callAPI apiID='AP4'/>
				<%	String LPNLocation = resolveValue("xml:LPNDetails:/GetLPNDetails/LPN/LPNLocation/@LocationId" );	
						if (!YFCCommon.isVoid(resolveValue("xml:/NodeInventory/@LocationId")) && !equals(LPNLocation,resolveValue("xml:/NodeInventory/@LocationId"))) {	
							locationLPNErrorThrown=true; %>
							<script>
								alert(YFCMSG083);//Invalid LPN Location Combination
							</script>					
	<%				}
					} 
					if (!locationLPNErrorThrown) {%>
							<script>
								alert(YFCMSG104);//var YFCMSG104 = "No receipt found matching input criteria";
							</script>
	<%			}
			} else if (equals("1",NoOfLocinvlist)) 
				{
					String NoOfItemInventoryDetaillist = getValue("OutputNodeInventory","xml:/NodeInventory/LocationInventoryList/LocationInventory/ItemInventoryDetailList/@TotalNumberOfRecords");

					if (equals("1",NoOfItemInventoryDetaillist)&&(!isVoid(resolveValue("xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/@ReceiptHeaderKey"))) ) {	%>
						<yfc:makeXMLInput name="MyEntityKey">
							<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/@ReceiptHeaderKey"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@LocationId" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@InventoryStatus" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/@InventoryStatus"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@SerialNo" value="xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@CaseId" value="xml:/NodeInventory/Inventory/@CaseId"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@PalletId" value="xml:/NodeInventory/Inventory/@PalletId"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@Quantity" value="xml:OutputNodeInventory:/NodeInventory/LocationInventoryList/LocationInventory/@Quantity"/>
						</yfc:makeXMLInput>
						<input type="hidden" name="MyEntityKey" value='<%=HTMLEncode.htmlEscape(getParameter("MyEntityKey"))%>' />						
						<script>
							window.attachEvent("onload",doNormalWindow);
						</script>
	<%			} else{	%>
						<script>
							alert(YFCMSG105);
						</script>		
	<%			}
			} else {	 %>
					<script>
						alert(YFCMSG105);
					</script>		
	<%	}
		}
		}
		} else{	// this section is for non WMS Node.%>
				<yfc:callAPI apiID='AP5'/>
	<%	YFCElement ReceiptListElem = (YFCElement) request.getAttribute("ReceiptList");
			String NoOfReceiptsFound = getValue("ReceiptList","xml:/ReceiptList/@TotalNumberOfRecords");

			if (equals("0",NoOfReceiptsFound)){ 
	%>
				<script>
					alert(YFCMSG104);
				</script>
<%		} else if (equals("1",NoOfReceiptsFound)) {	%>
					<yfc:callAPI apiID='AP7'/>
				<yfc:makeXMLInput name="MyEntityKey">
						<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/ReceiptList/Receipt/@ReceiptHeaderKey"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:/ReceiptLineList/ReceiptLine/@ItemID"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:/ReceiptLineList/ReceiptLine/@ProductClass"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:/ReceiptLineList/ReceiptLine/@UnitOfMeasure"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@SerialNo" value="xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@CaseId" value="xml:/NodeInventory/Inventory/@CaseId"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@PalletId" value="xml:/NodeInventory/Inventory/@PalletId"/>
					</yfc:makeXMLInput>
					<input type="hidden" name="MyEntityKey" value='<%=HTMLEncode.htmlEscape(getParameter("MyEntityKey"))%>' />						
					<script>
						window.attachEvent("onload",doNormalWindow);
					</script>
<%		} else{	%>
					<script>
						alert(YFCMSG105);
					</script>
<%		}
		}
	}else{
//		System.out.println("BBBBBBBBBB");
	}

%>
<input type="hidden" value='<%=getTextOptions("xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo","xml:/NodeInventory/Inventory/SerialList/SerialDetail/@SerialNo")%>' />	
