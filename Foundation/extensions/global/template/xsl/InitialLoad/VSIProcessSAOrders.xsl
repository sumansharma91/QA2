<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
<xsl:for-each select="OrderList/Order">
<Order>
<xsl:attribute name="OrderNo">
<xsl:value-of select="@OrderNo"/>
</xsl:attribute>
<xsl:attribute name="ExtnTransactionNo">
<xsl:value-of select="@ExtnTransactionNo"/>
</xsl:attribute>
<xsl:attribute name="Amount">
<xsl:value-of select="@Amount"/>
</xsl:attribute>
</Order>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>