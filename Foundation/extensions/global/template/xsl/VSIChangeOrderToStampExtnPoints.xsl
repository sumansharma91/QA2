<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Order>
			<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="/Order/@OrderHeaderKey" /></xsl:attribute>
			<xsl:attribute name="DocumentType">0001</xsl:attribute>
			<xsl:attribute name="Override">Y</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">VSI.com</xsl:attribute>
			<xsl:attribute name="SelectMethod">WAIT</xsl:attribute>
			<Extn>
			<xsl:attribute name="ExtnTotalPointsEarned"><xsl:value-of select="/Order/Extn/@ExtnTotalPointsEarned" /></xsl:attribute>
			<xsl:attribute name="ExtnPointsEarned"><xsl:value-of select="/Order/Extn/@ExtnPointsEarned" /></xsl:attribute>
			</Extn>
			
		</Order>
		
	</xsl:template>
</xsl:stylesheet>