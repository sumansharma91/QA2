package com.vsi.scc.oms.pca.order.receive.wizardPages;

import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;


import com.vsi.scc.oms.pca.order.receive.editor.ReceiveOrderEditor;
import com.vsi.scc.oms.pca.order.receive.wizard.ReceiveOrderWizard;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;

import com.yantra.yfc.rcp.YRCWizardPageBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class ReceiveOrderWizardPageBehaviour extends YRCWizardPageBehavior {
    
	public Element input = null;
	public static final String Wizard_ID = ReceiveOrderWizard.Wizard_ID;
	String strOrganizationCode="";
	private String returnDisposition;
	private String returnReason;
	private String strShipNode="";
	boolean isDispositionCodeAvailable=true;
	boolean isCaseIDAvailable=true;
	boolean isQtyAvailable=true;
	private HashMap<String, String> returnDispositionListDescToCode = new HashMap<String, String>();
	private HashMap<String, String> returnDispositionListCodeToDesc = new HashMap<String, String>();
	
	public ReceiveOrderWizardPageBehaviour(Composite parent,String formID,Object inputObject) {
		super(parent);
		init();
		this.input = (Element)inputObject;
		
	}
	
	public void initPage() {
		// TODO Auto-generated method stub
    	Element eleInputModel=getModel("OrderDetails");
    	//System.out.println("OrderDetails "+VSIXmlUtils.getElementXMLString(eleInputModel) );
    	if(YRCPlatformUI.isVoid(eleInputModel)){
    		eleInputModel=getModel("getCompleteOrderDetails_output");
    	}
    	Element eleUserNamespace=getModel("UserNameSpace");
    	strShipNode=eleUserNamespace.getAttribute("Node");
    	if(!YRCPlatformUI.isVoid(eleInputModel)){
    		strOrganizationCode=eleInputModel.getAttribute("EnterpriseCode");
    		setModel("OrderDetailsModel", eleInputModel);
    		callmultiApi(eleInputModel);
    	}
		
	}


	private void callmultiApi(Element eleOrderDetails){
		Document[] inputDocs = new Document[2];
		String[] apiNames = new String[2];
		
		apiNames[0] = "GetReturnDispositionList";
		inputDocs[0] = YRCXmlUtils.createFromString("<ReturnDisposition />");
		Element eleDispositionListInput=inputDocs[0].getDocumentElement();
		eleDispositionListInput.setAttribute("CallingOrganizationCode", strOrganizationCode);
		eleDispositionListInput.setAttribute("DocumentType","0003");
		
		apiNames[1] = "ChangeOrder";
		inputDocs[1] = prepareInputForChangeOrder(eleOrderDetails).getOwnerDocument();

		
		YRCApiContext context = new YRCApiContext();
		context.setApiNames(apiNames);
		context.setInputXmls(inputDocs);
		context.setFormId(Wizard_ID);
		callApi(context);
	}
	
	
	
	
	@Override
	public void handleApiCompletion(YRCApiContext ctx) {
		if(ctx.getInvokeAPIStatus()<0){
			YRCPlatformUI.showError("Error","Invoke API "+ ctx.getApiName()+"Failed");
		}
		if("GetReturnDispositionList".equalsIgnoreCase(ctx.getApiName())){
			Element eleDispositionListOutput=ctx.getOutputXmls()[0].getDocumentElement();
			createMapForReturnDispositions(eleDispositionListOutput);
			Element eleOrderDetailsModel=getModel("OrderDetailsModel");
			setDefaultDispositionCodeToLines(eleOrderDetailsModel,eleDispositionListOutput);
			setModel("OrderDetailsModel",eleOrderDetailsModel);
			setModel("ReturnDispositions",eleDispositionListOutput);
		
		}else if("VSIReturnOrderStartCloseReceiptService".equalsIgnoreCase(ctx.getApiName())){
			
			Element eleInputModel=getModel("OrderDetailsModel");
			String currentEditor = ReceiveOrderEditor.ID_EDITOR;
			String openEditor="com.yantra.pca.ycd.rcp.editors.YCDReturnEditor";
			//String openEditor="com.yantra.pca.ycd.rcp.editors.YCDReturnSearchEditor";
			YRCEditorInput editorIn  = (YRCEditorInput) ((YRCEditorPart) YRCDesktopUI
					.getCurrentPart()).getEditorInput();
			//YRCEditorInput editorIn1=new YRCEditorInput(eleInputModel,new String[] { "" }, "YCD_TASK_RETURN_SUMMARY");
			//YRCEditorInput editorIn1=new YRCEditorInput(eleInputModel,new String[] { "" }, "YCD_TASK_RETURN_SEARCH");
			YRCPlatformUI.closeEditor(editorIn,currentEditor, false);
			
			editorIn.setInputObject(eleInputModel);
			editorIn.setTaskName("YCD_TASK_RETURN_SUMMARY");
			YRCPlatformUI.openEditor(openEditor,editorIn);
			
		}
		// TODO Auto-generated method stub
		super.handleApiCompletion(ctx);
	}
	
	private void createMapForReturnDispositions(Element e) {
		NodeList nodeList = e.getElementsByTagName("ReturnDisposition");
		for(int i=0; i<nodeList.getLength(); i++){
			Element returnDispositionElement = (Element)nodeList.item(i);
			String dispositionCode = returnDispositionElement.getAttribute("DispositionCode");
			String description = YRCPlatformUI.getDBString(returnDispositionElement.getAttribute("Description"));
			returnDispositionListDescToCode.put(description, dispositionCode);
			returnDispositionListCodeToDesc.put(dispositionCode, description);
		}
	}
	private void callReceiveOrderService(Element eleOutput) {
		Element eleOrderDetails=getModel("OrderDetailsModel");
		if(!YRCPlatformUI.isVoid(eleOrderDetails)){
			eleOrderDetails.setAttribute("ShipNode", strShipNode);
			YRCApiContext context = new YRCApiContext();
			context.setApiName("VSIReturnOrderStartCloseReceiptService");
			context.setFormId(Wizard_ID);
			context.setInputXml(eleOrderDetails.getOwnerDocument());
			callApi(context);
		}
		
	}


	public void applyReasonCodes(String strReturnDisposition) {
		//ownerComposite.focusOnApplyButton();
		Element orderElement = getModel("OrderDetailsModel");
		Element e = YRCXmlUtils.getChildElement(orderElement,"OrderLines");
		NodeList nodeList = e.getElementsByTagName("OrderLine");
		for(int i=0;i<nodeList.getLength(); i++){
			Element orderLineElement = (Element)nodeList.item(i);
			orderLineElement.setAttribute("ReturnDisposition", strReturnDisposition);
		}

		refreshTable("tblOrderLines");

	}
	
	
	public void setReturnDisposition(String returnDisposition) {
		this.returnDisposition = returnDisposition;
	}
	
	public void setReturnReason(String returnReason){
		this.returnReason = returnReason;
	}
	
	public void setReturnDispositionToAllSelectedOrderLines(Table orderLinesTable) {
		TableItem[] tableItems = orderLinesTable.getSelection();
		
		for(int i=0;i<tableItems.length; i++){
			Element orderLineElement = (Element)tableItems[i].getData();
			
				if(!YRCPlatformUI.isVoid(returnDisposition)){
					orderLineElement.setAttribute("ReturnDisposition", returnDisposition);
				}
			
		}
	}
	public void setReturnReasonToAllSelectedOrderLines(Table orderLinesTable) {
		TableItem[] tableItems = orderLinesTable.getSelection();
		
		for(int i=0;i<tableItems.length; i++){
			Element orderLineElement = (Element)tableItems[i].getData();
			
				if(!YRCPlatformUI.isVoid(returnReason)){
					orderLineElement.setAttribute("ReturnReason", returnReason);
				
			}
		}
	}
	
	public void doReceive(){
		Element eleOrderDetails=getModel("OrderDetailsModel");
		
		if(checkForError(eleOrderDetails)){
			if(!isDispositionCodeAvailable)
				YRCPlatformUI.showError("Error","Mandatory_Attribute_RO_Disposition_Code");
			else if(!isQtyAvailable)
				YRCPlatformUI.showError("Error","Mandatory_Attribute_RO_Qty");
		}else{
			
			//System.out.println("*****"+ VSIXmlUtils.getElementXMLString(eleOrderDetails));
			callReceiveOrderService(eleOrderDetails);
			
		}
	}
	

	private Element prepareInputForChangeOrder(Element eleOrderDetails) {
		Element eleChangeOrderInput=YRCXmlUtils.createDocument("Order").getDocumentElement();
		eleChangeOrderInput.setAttribute("OrderHeaderKey", eleOrderDetails.getAttribute("OrderHeaderKey"));
		Element eleOrderLines=YRCXmlUtils.createChild(eleChangeOrderInput, "OrderLines");
		NodeList ndlOrderLines=eleOrderDetails.getElementsByTagName("OrderLine");
		for(int i=0;i<ndlOrderLines.getLength();i++){
			Element eleOrderLine=(Element)ndlOrderLines.item(i);
			Element eleChangeOrderLine =YRCXmlUtils.createChild(eleOrderLines,"OrderLine");
			eleChangeOrderLine.setAttribute("ShipNode", strShipNode);
			eleChangeOrderLine.setAttribute("OrderLineKey", eleOrderLine.getAttribute("OrderLineKey"));
			
		}
		// TODO Auto-generated method stub
		return eleChangeOrderInput;
	}

	private Boolean checkForError(Element eleOrderDetails) {
		Boolean blnHasError=false;
		if(!YRCPlatformUI.isVoid(eleOrderDetails)){
			NodeList ndlOrderLineList=eleOrderDetails.getElementsByTagName("OrderLine");
			for(int i=0;i<ndlOrderLineList.getLength();i++){
				isQtyAvailable = true;
				Element eleOrderLine=(Element)ndlOrderLineList.item(i);
				if(!YRCPlatformUI.isVoid(eleOrderLine)){
					String strDispositionCode=eleOrderLine.getAttribute("ReturnDisposition");
					
					String strQty=eleOrderLine.getAttribute("OrderedQty");
					double dblOrderedQty = YRCXmlUtils.getDoubleAttribute(eleOrderLine,"OrderedQty");
					
					
					
					
					if(YRCPlatformUI.isVoid(strDispositionCode)){
						blnHasError=true;
						isDispositionCodeAvailable=false;
						break;
					}
					else if(YRCPlatformUI.isVoid(strQty) ||  dblOrderedQty <= 0 ){
						
						blnHasError=true;
						isQtyAvailable=false; 
						//break;
					}
					else{
						String strDispositionCodeFromHashMap=(String)returnDispositionListDescToCode.get(strDispositionCode);
						eleOrderLine.setAttribute("ReturnDisposition",strDispositionCodeFromHashMap);
						eleOrderLine.setAttribute("OrderedQty",strQty);
					}

				}
			}if(!isQtyAvailable){
				blnHasError=true;
			}
		}
		return blnHasError;
		
	}

	private void setDefaultDispositionCodeToLines(Element eleOrderDetails,Element eleDispostionList) {
		Element eleDisposition=(Element)eleDispostionList.getElementsByTagName("ReturnDisposition").item(0);
		String strDispositionDesc=eleDisposition.getAttribute("Description");
		if(!YRCPlatformUI.isVoid(eleOrderDetails)){
			NodeList ndlOrderLineList=eleOrderDetails.getElementsByTagName("OrderLine");
			for(int i=0;i<ndlOrderLineList.getLength();i++){
				Element eleOrderLine=(Element)ndlOrderLineList.item(i);
				if(!YRCPlatformUI.isVoid(eleOrderLine)){
					eleOrderLine.setAttribute("ReturnDisposition", strDispositionDesc);

				}
			}
		}
		
		
	}
	public void doClose(){
		String currentEditor = ReceiveOrderEditor.ID_EDITOR;
		YRCEditorInput editorIn  = (YRCEditorInput) ((YRCEditorPart) YRCDesktopUI
				.getCurrentPart()).getEditorInput();
		YRCPlatformUI.closeEditor(editorIn,currentEditor, false);
	}

}
