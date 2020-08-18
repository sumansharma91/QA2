package com.vsi.isccs.mashups.order.customerappeasement;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIAuthorizationHelper;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.Element;

public class VSICustomerAppeasementOffersUEMashup
extends SCCSBaseMashup
{
  private static YFCLogCategory cat = YFCLogCategory.instance(VSICustomerAppeasementOffersUEMashup.class.getName());
  
  public Element massageInput(Element inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext)
  {
    if (!SCUtil.isVoid(inputEl))
    {
      if (cat.isDebugEnabled()) {
        cat.debug("input to SCCSCustomerAppeasementOffersUEMashup is:" + SCXmlUtil.getString(inputEl));
      }
      Element tempInputEl = SCXmlUtil.getCopy(inputEl);
      inputEl.removeAttribute(VSIConstants.ATTR_APPEASEMENT_TYPE);
      if (!SCUtil.isVoid(SCXmlUtil.getChildElement(inputEl, VSIConstants.ELE_APPEASEMENT_REASON))) {
        inputEl.removeChild(SCXmlUtil.getChildElement(inputEl,VSIConstants.ELE_APPEASEMENT_REASON));
      }
      Element response = (Element)SCUIMashupHelper.invokeMashup("customerAppeasement_getCompleteOrderDetails", inputEl, uiContext);
      if (!SCUtil.isVoid(response))
      {
        if (!SCUtil.isVoid(SCXmlUtil.getChildElement(response,VSIConstants.ELE_NOTES))) {
          response.removeChild(SCXmlUtil.getChildElement(response,VSIConstants.ELE_NOTES));
        }
        SCXmlUtil.importElement(response, SCXmlUtil.getChildElement(tempInputEl,VSIConstants.ELE_APPEASEMENT_REASON));
        
        Element ueInput = SCXmlUtil.createDocument(VSIConstants.ELE_INVOKE_UE).getDocumentElement();
        Element xmlData = SCXmlUtil.createChild(ueInput,VSIConstants.ELE_XML_DATA);
        Element appeasementOffers = SCXmlUtil.createChild(xmlData,VSIConstants.ELE_APPEASMENT_OFFERS);
        SCXmlUtil.setAttribute(ueInput, VSIConstants.ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(response, VSIConstants.ATTR_ENTERPRISE_CODE));
        SCXmlUtil.importElement(appeasementOffers, response);
        if (cat.isDebugEnabled()) {
          cat.debug("input outof SCCSCustomerAppeasementOffersUEMashup is:" + SCXmlUtil.getString(ueInput));
        }
        
        // if true, it's alpine user
        if(SCUIAuthorizationHelper.hasPermission(uiContext,VSIConstants.STR_VSI_ALPINE_RESOURCE))
        {
        	appeasementOffers.setAttribute(VSIConstants.ATTR_IS_ALPINE_USER,VSIConstants.FLAG_Y);
        }
        
        return ueInput;
      }
    }
    return inputEl;
  }
  
  public Element massageOutput(Element outEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext)
  {
    if (!SCUtil.isVoid(outEl))
    {
      if (cat.isDebugEnabled()) {
        cat.debug("output to SCCSCustomerAppeasementOffersUEMashup is:" + SCXmlUtil.getString(outEl));
      }
      ArrayList<String> validOfferTypes = new ArrayList(Arrays.asList(new String[] { "FLAT_AMOUNT_ORDER", "PERCENT_ORDER", "PERCENT_FUTURE_ORDER", "VARIABLE_AMOUNT_ORDER", "VARIABLE_FUTURE_AMOUNT_ORDER", "VARIABLE_PERCENT_AMOUNT_ORDER", "VARIABLE_PERCENT_FUTURE_AMOUNT_ORDER", "APPEASE_SHIPPING_CHARGES" }));
      ArrayList<String> validAdminOfferTypes = new ArrayList(Arrays.asList(new String[] { "VARIABLE_AMOUNT_ORDER", "VARIABLE_FUTURE_AMOUNT_ORDER", "VARIABLE_PERCENT_AMOUNT_ORDER", "VARIABLE_PERCENT_FUTURE_AMOUNT_ORDER", "APPEASE_SHIPPING_CHARGES" }));
      YFCElement outTempElem = YFCDocument.getDocumentFor(outEl.getOwnerDocument()).getDocumentElement();
      YFCNodeList<YFCElement> appeasementOfferList = outTempElem.getElementsByTagName("AppeasementOffer");
      int count = 0;
      int preferredCount = 0;
      for (int i = 0; i < appeasementOfferList.getLength(); i++)
      {
        YFCElement appeasementOffer = (YFCElement)appeasementOfferList.item(i);
        String offerType = appeasementOffer.getAttribute(VSIConstants.ATTR_OFFER_TYPE);
        if ((SCUtil.isVoid(offerType)) || (!validOfferTypes.contains(offerType)))
        {
          appeasementOffer.getParentElement().removeChild(appeasementOffer);
          i--;
        }
        else
        {
          if (validAdminOfferTypes.contains(offerType))
          {
            if (!SCUIAuthorizationHelper.hasPermission(uiContext, "ISCCSCA002"))
            {
              appeasementOffer.getParentElement().removeChild(appeasementOffer);
              i--;
              continue;
            }
            if (SCUtil.equals(offerType, "VARIABLE_AMOUNT_ORDER")) {
              setAdditionalAttributes(appeasementOffer, "Y", "Y", "N", "N");
            } else if (SCUtil.equals(offerType, "VARIABLE_FUTURE_AMOUNT_ORDER")) {
              setAdditionalAttributes(appeasementOffer, "Y", "Y", "N", "Y");
            } else if (SCUtil.equals(offerType, "VARIABLE_PERCENT_AMOUNT_ORDER")) {
              setAdditionalAttributes(appeasementOffer, "Y", "N", "Y", "N");
            } else if (SCUtil.equals(offerType, "VARIABLE_PERCENT_FUTURE_AMOUNT_ORDER")) {
              setAdditionalAttributes(appeasementOffer, "Y", "N", "Y", "Y");
            }
          }
          else if (SCUtil.equals(offerType, "FLAT_AMOUNT_ORDER"))
          {
            setAdditionalAttributes(appeasementOffer, "N", "Y", "N", "N");
          }
          else if (SCUtil.equals(offerType, "PERCENT_ORDER"))
          {
            setAdditionalAttributes(appeasementOffer, "N", "N", "Y", "N");
          }
          else if (SCUtil.equals(offerType, "PERCENT_FUTURE_ORDER"))
          {
            setAdditionalAttributes(appeasementOffer, "N", "N", "Y", "Y");
          }
          else
          {
            setAdditionalAttributes(appeasementOffer, "Y", "Y", "N", "N");
          }
          if (YFCObject.equals("Y", appeasementOffer.getAttribute("Preferred"))) {
            preferredCount = count;
          }
          appeasementOffer.setAttribute("OfferCount", count);
          count++;
        }
      }
      YFCElement appeasementOffers = (YFCElement)outTempElem.getElementsByTagName("AppeasementOffers").item(0);
      if (!YFCObject.isVoid(appeasementOffers)) {
        appeasementOffers.setAttribute("SelectedCount", preferredCount);
      }
      if (cat.isDebugEnabled()) {
        cat.debug("output outof SCCSCustomerAppeasementOffersUEMashup is:" + SCXmlUtil.getString(outEl));
      }
    }
    return outEl;
  }
  
  private void setAdditionalAttributes(YFCElement appeasementOffer, String IsVariable, String IsAmount, String IsPercent, String IsFuture)
  {
    appeasementOffer.setAttribute("IsVariable", IsVariable);
    appeasementOffer.setAttribute("IsAmount", IsAmount);
    appeasementOffer.setAttribute("IsPercent", IsPercent);
    appeasementOffer.setAttribute("IsFuture", IsFuture);
  }
}
