package com.vsi.scc.oms.pca.order.giftCard.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

import com.vsi.scc.oms.pca.order.giftCard.screens.VSISendGCMailComposite;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCDialog;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCRelatedTask;
import com.yantra.yfc.rcp.YRCRelatedTaskAction;

public class VSISendGCMailAction extends YRCRelatedTaskAction {

	public static String ACTION_ID = "com.vsi.scc.oms.pca.actions.SendGCMailAction";

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

	@Override
	public void executeTask(IAction act, YRCEditorInput arg1,
			YRCRelatedTask task) {
		// TODO Auto-generated method stub

		

		// YRCEditorInput edIn = arg1;
		// edIn.setTaskName("SendGCMail");
		// edIn.setName("Email Details");

		Composite comp = (Composite) YRCDesktopUI.getCurrentPage();

		VSISendGCMailComposite mailComp = new VSISendGCMailComposite(comp, comp
				.getStyle());

		YRCDialog oDialog = new YRCDialog(mailComp, 415, 250, "Send Gift Card E-Mail",
				null);
		oDialog.open();
	}

}
