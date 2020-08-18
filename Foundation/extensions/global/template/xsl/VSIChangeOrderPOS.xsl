<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">

		<xsl:variable name="NoOfIsDirtyFlag">
			<xsl:value-of
				select="count(Order/Extn/VSIOrderTOBList/VSIOrderTOB/@IsTobDirty)" />
		</xsl:variable>
		<xsl:variable name="NoOfIsDirtyFlagON">
			<xsl:value-of
				select="count(Order/Extn/VSIOrderTOBList/VSIOrderTOB[@IsTobDirty='Y'])" />
		</xsl:variable>
		<Order OrderHeaderKey = "{Order/@OrderHeaderKey}" >
		<xsl:choose>
			<xsl:when test="number($NoOfIsDirtyFlag) != number($NoOfIsDirtyFlagON)">
			<xsl:attribute name="Action">MODIFY</xsl:attribute>
			</xsl:when>
			</xsl:choose>
			<Extn>
			<VSIOrderTOBList>
			<xsl:for-each select="Order/Extn/VSIOrderTOBList/VSIOrderTOB">
					<VSIOrderTOB>
						<xsl:attribute name="ExtnVSIOrderTobKey">
							<xsl:value-of select="@ExtnVSIOrderTobKey"></xsl:value-of>
						</xsl:attribute>
						<xsl:attribute name="IsTobDirty">
							<xsl:text>Y</xsl:text>
						</xsl:attribute>
						
					</VSIOrderTOB>
				</xsl:for-each>
			
			
			</VSIOrderTOBList>
			</Extn>
		
</Order>		

	</xsl:template>


</xsl:stylesheet>