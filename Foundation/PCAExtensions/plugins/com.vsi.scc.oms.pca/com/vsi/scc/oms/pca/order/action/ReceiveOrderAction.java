package com.vsi.scc.oms.pca.order.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.vsi.scc.oms.pca.extensions.returnOrderSummary.ReturnOrderSummaryWizardExtensionBehavior;
import com.vsi.scc.oms.pca.order.receive.editor.ReceiveOrderEditor;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;
import com.yantra.yfc.rcp.YRCRelatedTaskAction;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCXmlUtils;
import org.eclipse.swt.widgets.Composite;
//public class ReceiveOrderAction implements IWorkbenchWindowActionDelegate{

public class ReceiveOrderAction extends YRCRelatedTaskAction{

	
	public static String ACTION_ID = "com.vsi.scc.oms.pca.order.action.ReceiveOrderAction";		
	
	/*@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		
				}*/

		@Override
		public void executeTask(IAction arg0, YRCEditorInput arg1,
				YRCRelatedTask arg2) {
			// TODO Auto-generated method stub
			//System.out.println("SUCCESS -----");
			
			/*Element eleSelectedOrder = ((ReturnOrderSummaryWizardExtensionBehavior)((YRCWizard)(YRCDesktopUI.getCurrentPage()))
					.getExtensionBehavior()).getExtentionModel("getCompleteOrderDetails_output");*/
			
		
		Composite curPage = YRCDesktopUI.getCurrentPage();
			
			Element eleSelectedOrder = ((ReturnOrderSummaryWizardExtensionBehavior)((YRCWizard)(YRCDesktopUI.getCurrentPage())).getExtensionBehavior()).getExtentionModel("extn_ReturnOrderOutputModel");
			
			
			/*eleSelectedOrder = ((ReturnOrderSummaryWizardExtensionBehavior)((YRCWizard)(YRCDesktopUI.getCurrentPage()))
					.getExtensionBehavior()).getModel("getCompleteOrderDetails_output");*/
			
			
			if(!YRCPlatformUI.isVoid(eleSelectedOrder)){
				String orderStatus = eleSelectedOrder.getAttribute("Status");
				double dblQtyToAdd=0;
				NodeList eleOrderLineList=eleSelectedOrder.getElementsByTagName("OrderLine");
				if((orderStatus.equalsIgnoreCase("Created") ||orderStatus.equalsIgnoreCase("Partially Receipt Closed") 
						||orderStatus.equalsIgnoreCase("Partially Received")) || orderStatus.equalsIgnoreCase("Refund Issued")){
					
				
				for(int i=0;i<eleOrderLineList.getLength();i++){
					Element eleOrderLine=(Element)eleOrderLineList.item(i);
					if(!YRCPlatformUI.isVoid(eleOrderLine)){
						dblQtyToAdd=dblQtyToAdd+YRCXmlUtils.getDoubleAttribute(eleOrderLine, "OrderedQty");
					}
				}
				if(dblQtyToAdd==0){
					YRCPlatformUI.showError("Error", "Error_RO_Not_Enough_Quantity");
				}else {
					String currentEditor = "com.yantra.pca.ycd.rcp.editors.YCDReturnEditor";
					YRCPlatformUI.closeEditor(arg1,currentEditor, false);
					YRCEditorInput editorIn1=arg1;
					editorIn1.setTaskName("ReceiveOrderTask");
					editorIn1.setInputObject(eleSelectedOrder);
					YRCPlatformUI.openEditor(ReceiveOrderEditor.ID_EDITOR ,editorIn1);
				}}
				else{
					//YRCEditorInput editorIn  = (YRCEditorInput) ((YRCEditorPart) YRCDesktopUI
					//		.getCurrentPart()).getEditorInput();
					YRCPlatformUI.showError("Error", "Order is already Received !");
				}

			
			
		}
	}
		
	@Override
	protected boolean checkForErrors() {
		// TODO Auto-generated method stub
		return false;
	}
	
@Override
protected boolean checkForModifications() {
	// TODO Auto-generated method stub
	return false;
}
	/* @Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
*/
}
