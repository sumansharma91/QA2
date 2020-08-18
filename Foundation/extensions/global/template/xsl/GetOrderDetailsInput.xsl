<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Order>
			<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="Order/@OrderHeaderKey" /></xsl:attribute>
		</Order>
	</xsl:template>
</xsl:stylesheet>