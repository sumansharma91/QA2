<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

   <xsl:template match="/">
		<Order>
			<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="Order/@OrderHeaderKey" /></xsl:attribute>
			<xsl:attribute name="Override">Y</xsl:attribute>
			<OrderHoldTypes>
				<OrderHoldType>
					<xsl:attribute name="HoldType">VSI_SCHEDULE_HOLD</xsl:attribute>
					<xsl:attribute name="Status">1300</xsl:attribute>
				</OrderHoldType>
			</OrderHoldTypes>
		</Order>	
   </xsl:template>
</xsl:stylesheet>                       