<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<ReleaseOrder>
			<xsl:attribute name="AllocationRuleID">SYSTEM</xsl:attribute>
			<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="/OrderStatusChange/@OrderHeaderKey" />
			</xsl:attribute>
			<xsl:attribute name="IgnoreOrdering">Y</xsl:attribute>
		    <xsl:attribute name="CheckInventory">N</xsl:attribute>
			<xsl:attribute name="IgnoreReleaseDate">Y</xsl:attribute>
		</ReleaseOrder>
	</xsl:template>
</xsl:stylesheet>
