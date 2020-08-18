package com.vsi.scc.oms.pca.order.action;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.SWT;

import com.vsi.scc.oms.pca.order.receive.wizardPages.ReceiveOrderWizardPage;
import com.yantra.yfc.rcp.IYRCRelatedTasksExtensionContributor;
import com.yantra.yfc.rcp.YRCDesktopApplication;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCRelatedTask;

public class createStoreReceiveOrderExtensionContributor  implements IYRCRelatedTasksExtensionContributor{
	
	public createStoreReceiveOrderExtensionContributor() throws Exception {
		}
	
	public boolean acceptTask(YRCEditorInput editorInput, YRCRelatedTask relatedTask) {
		/*if(YRCPlatformUI.equals(relatedTask.getId(),"ReceiveOrderTask")){
			return false;
		}*/
		
		if(YRCPlatformUI.equals(relatedTask.getId(),"ReceiveOrderTask")&& "YFSSYS00011".equalsIgnoreCase(YRCDesktopApplication.getInstance().getApplicationID())){
			return false;
		}
		return true;
		//return true;
	}

	public boolean canExecuteNewTask(YRCEditorInput arg0, YRCRelatedTask arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public Composite createPartControl(Composite arg0, YRCEditorInput arg1, YRCRelatedTask arg2) {
		// TODO Auto-generated method stub
		/* System.out.println("createPartControl");
		 ReceiveOrderWizardPage receiveOrderPage = new ReceiveOrderWizardPage( arg0, SWT.NONE,arg1 );
		  // TODO Auto-generated method stub
		  return receiveOrderPage;*/
		  return null;
		 
		
		
	}

}
