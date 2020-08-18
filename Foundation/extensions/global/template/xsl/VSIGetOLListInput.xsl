<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<OrderLine>
			<xsl:attribute name="CustomerPONo"><xsl:value-of select="/Order/@OrderNo" /></xsl:attribute>
		</OrderLine>
	</xsl:template>
</xsl:stylesheet>