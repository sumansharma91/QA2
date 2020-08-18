package com.vsi.isccs.providers.image;

import com.sterlingcommerce.ui.web.framework.utils.SCUIUtils;
import com.sterlingcommerce.ui.web.platform.helpers.SCUIAdditionalDataProviderHelper;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class SCCSImageProviderInitServlet extends HttpServlet
{
  private static final long serialVersionUID = 8808616055836017630L;

  public synchronized void init(ServletConfig config)
    throws ServletException
  {
    loadImageProvider(config.getServletContext());
  }

  private void loadImageProvider(ServletContext servletContext) {
    String imageProvider = "/extn/dataproviders/SCCSDataProviderExtn.xml";
    if (SCUIUtils.resourceExists(imageProvider, servletContext))
      SCUIAdditionalDataProviderHelper.loadAdditionalDataProvidersXML(imageProvider, servletContext);
  }
}