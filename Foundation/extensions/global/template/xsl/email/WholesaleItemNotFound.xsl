<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lxslt="http://xml.apache.org/xslt"
                version="1.0">
<xsl:template match="/">
<HTML>

<BODY topmargin="0" leftmargin="0">
	<BR/><BR/><font>Team,</font><BR/><BR/>
	<font>Please look into below mentioned order and take required action. The list contains the LineNo and UPCCode data for lines without ItemId.</font>
	<BR/><BR/><BR/><P><font><B>Order Number : <xsl:value-of select="@OrderNo"/></B></font></P>
	<BR/><BR/><BR/><P><font><B><xsl:value-of select="@DetailDescription"/></B></font></P>
</BODY>
</HTML>
</xsl:template>
</xsl:stylesheet>
