<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/">
		<Shipment>
			<Containers>
				<Container>
					<xsl:attribute name="ContainerNo">
						<xsl:value-of select="Order/@ContainerNo" />
					</xsl:attribute>
				</Container>				
			</Containers>
		</Shipment>		
	</xsl:template>
</xsl:stylesheet>