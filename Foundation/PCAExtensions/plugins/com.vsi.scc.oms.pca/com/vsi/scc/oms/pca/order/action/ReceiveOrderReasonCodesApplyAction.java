package com.vsi.scc.oms.pca.order.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;

import com.vsi.scc.oms.pca.order.receive.wizardPages.ReceiveOrderWizardPage;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;

public class ReceiveOrderReasonCodesApplyAction extends YRCAction{

	public static String ACTION_ID = "com.vsi.scc.oms.pca.order.action.ReceiveOrderReasonCodesApplyAction";		
		
	public void execute(IAction action) {
		IYRCComposite currentWizardPage = (IYRCComposite) YRCDesktopUI.getCurrentPage();
				
		if (currentWizardPage instanceof ReceiveOrderWizardPage) {
			ReceiveOrderWizardPage page = (ReceiveOrderWizardPage) currentWizardPage;
            //(page.getMyBehavior()).applyReasonCodes();
        }           
        
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}



}
