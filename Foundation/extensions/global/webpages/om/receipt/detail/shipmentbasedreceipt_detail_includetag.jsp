<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<SCRIPT LANGUAGE="JavaScript">
	function changeSerialCaptureView(sCouter){
		var serialCapCtx = document.getElementById("SerialRangeCtx");
		if(serialCapCtx){
			if(serialCapCtx.value && serialCapCtx.value == "Y"){
				toggleSerialScanning(sCounter);
			}
		}		
	}
	function toggleSerialScanning(sCounter) {
		var serialCapCtx = document.getElementById("SerialRangeCtx");
		if(serialCapCtx){
			if(serialCapCtx.value != "N"){
				serialCapCtx.value = "N";
			}else{
				serialCapCtx.value = "Y";
			}
		}
		var tagCapBody = document.getElementById("tagCaptureBody");
		var tagCapFooter = document.getElementById("tagCaptureFooter");
		var docInputs=tagCapBody.getElementsByTagName("tr");		
		for (var i=0;i<docInputs.length;i++) {
			var docInput=docInputs.item(i);	
			var tds = docInput.getElementsByTagName("td");
			for(var j=0;j<tds.length;j++){
				var td = tds.item(j);
				if(td.getAttribute("id") != "deleteRow"){
					if(td.style.display == "none"){
							td.style.display = '';
					}else{
						td.style.display = 'none';
					}
				}
			}
		}
		var tds = ((tagCapFooter.getElementsByTagName("tr"))[0]).getElementsByTagName("td");
		for(var j=0;j<tds.length;j++){
			var td = tds.item(j);
			if(td.getAttribute("id") != "deleteRow"){
				if(td.style.display == "none"){
						td.style.display = '';
				}else{
					td.style.display = 'none';
				}
			}
		}
	}

</SCRIPT>
<%
  int tagtrack=0;
  int NoSecSerials = 0;
  String sNoSecSerials = request.getParameter("NumSecondarySerials");
  if(!isVoid(sNoSecSerials)){
	 NoSecSerials = (new Integer(sNoSecSerials)).intValue();
  }	 
  String sOptionCounter = request.getParameter("optionSetBelongingToLine");
%>
<input type="hidden" id="SerialRangeCtx" name="xml:/Receipt/@SerialRangeCtx" value=""/>
<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
<td width="80%" style="border:1px solid black">
<table class="table" editable="true" border="1" width="100%" cellspacing="0">
<%
	Map identifierAttrMap=null;
	Map descriptorAttrMap=null;
	Map extnIdentifierAttrMap=null;
	Map extnDescriptorAttrMap=null;
	String tagContainer  = request.getParameter("TagContainer");
	if (isVoid(tagContainer)) {
		tagContainer = "TagContainer";
	}
	String tagElement  = request.getParameter("TagElement");
	if (isVoid(tagElement)) {
		tagElement = "Tag";
	}
	%> 
	<yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	<%
		prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
		identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
		descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"DescriptorAttributes");
		extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
		extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnDescriptorAttributes");
	%>
	 </yfc:hasXMLNode>
	<%
	String modifiable = request.getParameter("Modifiable");
	boolean isModifiable = false;
	if (equals(modifiable,"true")) {
		isModifiable = true;
	}%>
   <thead> 
   <tr>
	<% 
	//changing the serial tracked flag for RQ-WMS-158
	if(equals("Y",request.getParameter("SerialCapturedInReceiving"))|| equals("Y",request.getParameter("TimeSensitive"))/*|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))*/){%>
	   <td class="tablecolumnheader">&nbsp;
		</td>
   <%}%>
	<%
		int i = 0;
		while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
		}		
		if ((normalMap != null) || (extnMap != null)) {
			tagtrack=1;%>
					
							<%while (j < 2) {
							    boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}
		i++;
	}%>				
				<% 	if(equals("Y",request.getParameter("SerialCapturedInReceiving"))/*|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))*/){%>
					<td sortable="no" class="tablecolumnheader" colspan="4"> 
						<yfc:i18n>Serial_#</yfc:i18n>
					</td>
					<% for (int s=1; s <= NoSecSerials ; s++){
					  String serLabel= "Secondary_Serial_"+s; %>
						<td sortable="no" class="tablecolumnheader">
							<yfc:i18n><%=serLabel%></yfc:i18n>
						</td>
					<%}%>	
				<%}%>
                
                <% 	if(equals("Y",request.getParameter("TimeSensitive"))){%>
					<td sortable="no" class="tablecolumnheader"> 
						<yfc:i18n>Ship_By_Date</yfc:i18n>
					</td>
				<%}%> 
				<% if(equals("Y",request.getParameter("SerialCapturedInReceiving")) && NoSecSerials==0){ %>
					<td>
						<img class="lookupicon" name="search" onclick="toggleSerialScanning(<%=sOptionCounter%>);return false" <%=getImageOptions(YFSUIBackendConsts.MILESTONE_COLUMN, "Toggle_Serial_Entry") %> />
					</td>
				<% } %>
</tr>	
</thead>
<tbody id="tagCaptureBody">
					<%
								if (equals("Y", request.getParameter("SerialTracked"))
								|| equals("Y", request.getParameter("TimeSensitive"))
								|| (equals("Y", request.getParameter("Serialized")) && equals(
								"omr", request.getParameter("applicationCode")))) {
							if (equals("Y", request.getParameter("sReceieveOrderFailed"))) {
								YFCElement root = getRequestDOM();
								YFCElement oReceiptElement = root
								.getChildElement("Receipt");
								YFCElement oReceiptLines = oReceiptElement
								.getChildElement("ReceiptLines");
								String bindingForTagSerElem = "xml:/Receipt/ReceiptLines/ReceiptLine_";
								String sCounter = request
								.getParameter("optionSetBelongingToLine");
								//System.out.println("Receipt Lines :" + oReceiptLines);
								if (oReceiptLines != null) {
							for (Iterator iter1 = oReceiptLines.getChildren(); iter1
									.hasNext();) {
								YFCElement oReceiptLine = (YFCElement) iter1.next();
								if (equals(oReceiptLine
								.getAttribute("YFC_NODE_NUMBER"), sCounter)) {
									YFCNodeList tagSerialList = oReceiptLine
									.getElementsByTagName("TagSerial");
									//System.out.println("tagSerialList " + tagSerialList.getLength());
									for (int x = 0; x < tagSerialList.getLength(); x++) {
								YFCElement oTagSerElem = (YFCElement) tagSerialList
										.item(x);
								YFCNodeList serialDetailList = oReceiptLine
										.getElementsByTagName("SerialDetail");
								if (serialDetailList != null) {
									String sNodeNumber = oTagSerElem
									.getAttribute("YFC_NODE_NUMBER");
									for (int y = 0; y < serialDetailList
									.getLength(); y++) {
										YFCElement oSerialDetail = (YFCElement) serialDetailList
										.item(y);
										if (equals(
										sNodeNumber,
										oSerialDetail
										.getAttribute("YFC_NODE_NUMBER"))) {
									//These two elements are for one line
									oTagSerElem
											.setAttributes(oSerialDetail
											.getAttributes());
									break;
										}
									}
								}
					%>
					<tr>
						<td id="deleteRow" class="checkboxcolumn"><img class="icon" onclick="deleteRow(this)"
							<%=getImageOptions(YFSUIBackendConsts.DELETE_ICON, "Remove_Row")%> />
						</td>
					<%
								i = 0;
								while (i < 2) {
									int j = 0;
									Map normalMap = null;
									Map extnMap = null;
									Map currentMap = null;
									if (i == 0) {
										normalMap = identifierAttrMap;
										extnMap = extnIdentifierAttrMap;
									} else {
										normalMap = descriptorAttrMap;
										extnMap = extnDescriptorAttrMap;
									}
									if ((normalMap != null)
									|| (extnMap != null)) {
					%>

					<%
									while (j < 2) {
									boolean isExtn = false;
									if (j == 0) {
										currentMap = normalMap;
										isExtn = false;
									} else {
										currentMap = extnMap;
										isExtn = true;
									}
									if (currentMap != null) {
										if (!currentMap.isEmpty()) {
											for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
												String currentAttr = (String) k.next();
												String currentAttrValue = (String) currentMap.get(currentAttr);
												String sTagSerialExtnBinding = "/@";
												if (isExtn) {
													sTagSerialExtnBinding = "/Extn/@";
												}
					%>
						<td nowrap="true" class="tablecolumn"><input type="text"
							class="unprotectedinput"
							<%=getTextOptions(bindingForTagSerElem + sCounter + "/TagSerial_"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")+ sTagSerialExtnBinding + currentAttr, "", oTagSerElem.getAttribute(currentAttr)) %> />
						<%
												if (currentAttr == "ManufacturingDate") {
						%> <img class="lookupicon"
							name="search" onclick="invokeCalendar(this);return false"
							<%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
						<%
												}
						%>
						</td>

						<%
											}
										}
									}
									j++;
								}
							}
							i++;
						}
						if (equals("Y", request.getParameter("SerialTracked"))) {
						%>
						<td><input type="text" class="unprotectedinput" id='<%="singleserialtext"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")%>'
							<%=getTextOptions(bindingForTagSerElem+sCounter+ "/TagSerial_"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")+"/@SerialNo","", oTagSerElem.getAttribute("SerialNo"))%> />
						</td>
						<td class="detaillabel" id='<%="serialrangefromtext"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")%>' style='display:none'>
						<yfc:i18n>From_Serial_#</yfc:i18n>
						</td>
						<td nowrap="true" id='<%="serialrangefromlabel"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")%>' class="tablecolumn" style='display:none' >
							<input type="text" class="unprotectedinput"
							<%=getTextOptions(bindingForTagSerElem+sCounter+ "/TagSerial_"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")+"/SerialRange/@FromSerialNo","", oTagSerElem.getChildElement("SerialRange", true).getAttribute("FromSerialNo"))%> />
						</td>
						<td class="detaillabel" id='<%="serialrangetotext"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")%>' style='display:none'>
						<yfc:i18n>To_Serial_#</yfc:i18n>
						</td>
						<td nowrap="true" id='<%="serialrangetolabel"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")%>' class="tablecolumn" style='display:none' >
						<input type="text" class="unprotectedinput"
							<%=getTextOptions(bindingForTagSerElem+sCounter+ "/TagSerial_"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")+"/SerialRange/@ToSerialNo","", oTagSerElem.getChildElement("SerialRange", true).getAttribute("ToSerialNo"))%> />
						</td>
						<%
						for (int s = 1; s <= NoSecSerials; s++) {
						%>
						<td><input type="text" class="unprotectedinput"
							<%=getTextOptions(bindingForTagSerElem +sCounter+"/TagSerial_"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")+"/@SecondarySerial"+s,"",oTagSerElem.getAttribute("SecondarySerial" + s))%> />
						</td>
						<%
						}
					}
					if (equals("Y", request.getParameter("TimeSensitive"))) {
						%>
						<td nowrap="true" class="tablecolumn"><input type="text"
							class="unprotectedinput"
							<%=getTextOptions(bindingForTagSerElem+sCounter+ "/TagSerial_"+oTagSerElem.getAttribute("YFC_NODE_NUMBER")+"/@ShipByDate","", oTagSerElem.getAttribute("ShipByDate"))%> />
						<img class="lookupicon" name="search"
							onclick="invokeCalendar(this);return false"
							<%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
						</td>
						<%
					}
						%>
					</tr>
					<%
				}
			}
		}
	}
}
}
					%>
</tbody>
<% if( equals("Y",request.getParameter("SerialCapturedInReceiving"))|| equals("Y",request.getParameter("TimeSensitive"))/*|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))*/){%>
 <tfoot id="tagCaptureFooter">
	 <tr style='display:none' TemplateRow="true">
		<td id="deleteRow" class="checkboxcolumn" >
	    </td>
<%}%> 
	<%
	String binding ="xml:/Receipt/ReceiptLines/ReceiptLine";
	sOptionCounter = request.getParameter("optionSetBelongingToLine");
	
	i = 0;
	while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
	
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
	
		}		
		if ((normalMap != null) || (extnMap != null)) {%>

						<%while (j < 2) {
								
                                boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
							
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);
											String sTagSerialExtnBinding = "/@" ;
											if(isExtn){
												sTagSerialExtnBinding = "/Extn/@" ;
											}
%>
	    <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_"+sOptionCounter+"/TagSerial_"+sTagSerialExtnBinding +currentAttr,"xml:/Receipt/ReceiptLines/ReceiptLine"+sTagSerialExtnBinding+currentAttr) %>/>
			<%	if(currentAttr=="ManufacturingDate"){%>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<%}%>
		</td>
											
									    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }}
		i++;
}%> 
    
	<%if(equals("Y",request.getParameter("SerialCapturedInReceiving"))/*|| (equals("Y",request.getParameter("Serialized")) && equals("omr", request.getParameter("applicationCode")))*/){%>
	   <td nowrap="true" id='<%="singleserialtext"+sOptionCounter%>' class="tablecolumn" style='display:' colspan="4">
           <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/@SerialNo")%>/> 
       </td>
		
		<td class="detaillabel" id='<%="serialrangefromtext"+sOptionCounter%>' style='display:none'>
            <yfc:i18n>From_Serial_#</yfc:i18n>
		</td>

		<td nowrap="true" id='<%="serialrangefromlabel"+sOptionCounter%>' class="tablecolumn" style='display:none' >
			<input type="text" class="unprotectedinput"   <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/SerialRange"+"/@FromSerialNo")%>/>		     
		</td>

		<td class="detaillabel" id='<%="serialrangetolabel"+sOptionCounter%>' style='display:none'>
            <yfc:i18n>To_Serial_#</yfc:i18n>
		</td> 

		<td nowrap="true" id='<%="serialrangetotext"+sOptionCounter%>' class="tablecolumn" style='display:none' >
		   <input type="text" class="unprotectedinput"   <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/SerialRange"+"/@ToSerialNo")%>/>
		</td> 

	  <% for (int s=1; s <= NoSecSerials; s++){%>
			<td>
				<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/SerialDetail_"+"/@SecondarySerial"+s)%>/>
			</td>
		<%}%>
	<%}%>
	<%if(equals("Y",request.getParameter("TimeSensitive"))){%>
	   <td nowrap="true" class="tablecolumn">
           <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/@ShipByDate")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
       </td>
	 <%}%>
	   <!--
	   <td nowrap="true" class="tablecolumn">
      		<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@IsNewRow","Y")%>/> 
	      	<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ItemID", request.getParameter("ItemID"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ProductClass", request.getParameter("ProductClass"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@UnitOfMeasure", request.getParameter("UnitOfMeasure"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@InventoryStatus", request.getParameter("InventoryStatus"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@DispositionCode")%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@OrderLineKey", request.getParameter("OrderLineKey"))%>/>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ShipmentLineKey", request.getParameter("ShipmentLineKey"))%>/>
		</td>
	 -->
	 </tr>
	<% if(equals("Y",request.getParameter("SerialCapturedInReceiving"))|| equals("Y",request.getParameter("TimeSensitive"))/*|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))*/){%>
	 <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
</tfoot>
<%}%>
</table>
</td>
</tr>
</tbody>
</table>	
<script>
	changeSerialCaptureView(<%=sOptionCounter%>);
</script>
