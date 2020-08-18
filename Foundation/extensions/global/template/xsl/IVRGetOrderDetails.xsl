<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Order>
			<xsl:copy-of select = "/OrderList/Order/@*"/>
		</Order>
	</xsl:template>
</xsl:stylesheet>