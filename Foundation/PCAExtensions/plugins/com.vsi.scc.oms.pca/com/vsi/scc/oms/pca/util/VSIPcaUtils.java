package com.vsi.scc.oms.pca.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.internal.YRCApiCaller;

public class VSIPcaUtils {
	
	public static IYRCComposite getCurrentPage(){
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if (comp instanceof YRCWizard) {
			YRCWizard wizard = (YRCWizard) YRCDesktopUI.getCurrentPage();
			currentPage = (IYRCComposite) wizard.getCurrentPage();
		}else if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}
		return currentPage;
	}
	
		
	public static String getCurrentStore()
	{
		Element userElement = YRCPlatformUI.getUserElement();
		String strStoreId="";
	
		if (!YRCPlatformUI.isVoid(userElement)) {
			strStoreId=userElement.getAttribute("ShipNode");
		}
		
		return strStoreId;
	}

	public static String getStringTrace(Exception ex)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return(sw.toString());
	}
	
	public static ToolTip getToolTip() {
		
		Display display = new Display();
		Shell shell = new Shell(display);
		Image image = null;
		final ToolTip tip = new ToolTip(shell, SWT.BALLOON);
		return tip;	
	}

	/**
	 * <Description>
	 * This method is used to invoke an Api.
	 * 
	 * @param Api to be invoked
	 * @param Input document
	 * @param Form Id
	 * @return Output document.
	 */
	public static Document invokeApi(final String strApiName, final Document docInput, final String strFormId) {

		// create Api Context object.
		final YRCApiContext yrcApiContext = new YRCApiContext();

		// set Api name.
		yrcApiContext.setApiName(strApiName);

		// set input document.
		yrcApiContext.setInputXml(docInput);

		// set Form Id.
		yrcApiContext.setFormId(strFormId);

		// create Api Caller object.
		final YRCApiCaller yrcApiCaller = new YRCApiCaller(yrcApiContext, true);

		// invoke api.
		yrcApiCaller.invokeApi();
		
		if(yrcApiContext.getInvokeAPIStatus() < 1) {
			
			YRCPlatformUI.trace("API exception in " + strFormId + " page, ApiName " + yrcApiContext.getApiName() + ",Exception : ", yrcApiContext.getException());
			return yrcApiContext.getOutputXml();
		
		}
			
			return yrcApiContext.getOutputXml();
			
	}


	/**
	 * <Description>
	 * This method is used to invoke an multi Api.
	 * 
	 * @param List of APIs to be invoked
	 * @param List of Input document
	 * @param Form Id
	 * @return List of Output document
	 */
	public static Document[] invokeApi(final String strApiNames[], final Document docInputs[], final String strFormId) {

		// create Api Context object.
		final YRCApiContext yrcApiContext = new YRCApiContext();

		// set Api name.
		yrcApiContext.setApiNames(strApiNames);

		// set input document.
		yrcApiContext.setInputXmls(docInputs);

		// set Form Id.
		yrcApiContext.setFormId(strFormId);

		// create Api Caller object.
		final YRCApiCaller yrcApiCaller = new YRCApiCaller(yrcApiContext, true);

		// invoke api.
		yrcApiCaller.invokeApi();

		// get the output.
		final Document docOutputs[] = yrcApiContext.getOutputXmls();

		return docOutputs;

	}


}
