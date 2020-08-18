<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8"  />
	<xsl:strip-space elements="*" />
	<xsl:template match="/">
		<!-- TODO: Auto-generated template -->
		<xsl:element name="Order">
			<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="/Order/@OrderHeaderKey"/>
			</xsl:attribute>
			<xsl:attribute name="TransactionId">CREATE_RETURN_INVOICE.0003.ex</xsl:attribute>
			<xsl:attribute name="IgnoreStatusCheck">Y</xsl:attribute>
			<xsl:element name="OrderLines">
				<xsl:for-each select="/Order/OrderLines/OrderLine">
					<xsl:element name="OrderLine">
						<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"/>
						</xsl:attribute>
						<xsl:attribute name="Quantity">
							<xsl:value-of select="@OrderedQty"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>