<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/RegionSchemas/RegionSchema">
	<Region RegionLevelName="Region" RegionSchemaKey="ALL_US" ParentRegionKey="">
		<xsl:variable name="msgID">
				<xsl:value-of select="../@YantraMessageSubID"/>
		</xsl:variable>
		<xsl:if test="$msgID = '1'">
				<xsl:attribute name="FirstMessage">Y</xsl:attribute>
		</xsl:if>
		<xsl:attribute name="RegionName">
			<xsl:if test = "@Region ='Northeast'">
				<xsl:value-of select="'Northeast'"/>
			</xsl:if>
			<xsl:if test = "@Region ='Southwest'">
				<xsl:value-of select="'Southwest'"/>
			</xsl:if>
			<xsl:if test = "@Region ='Region 3'">
				<xsl:value-of select="'Region 3'"/>
			</xsl:if>
			<xsl:if test = "@Region ='Region 4'">
				<xsl:value-of select="'Region 4'"/>
			</xsl:if>
		</xsl:attribute>
		<RegionSchema OrganizationCode="DEFAULT" RegionSchemaName="ALL_US" RegionSchemaKey="ALL_US"/>
		<ZipCodeRanges>
			<ZipCodeRange>
				<xsl:attribute name="FromZip" >
					<xsl:value-of select="format-number(@ZipCode,'00000')" />
				</xsl:attribute>
				<xsl:attribute name="ToZip" >
					<xsl:value-of select="format-number(@ZipCode,'00000')"/>
				</xsl:attribute>
				<!--<xsl:attribute name="RegionDetailKey">
					<xsl:value-of select="@ZipCode" />
				</xsl:attribute>-->
			</ZipCodeRange>
		</ZipCodeRanges>
	</Region>
	</xsl:template>
<xsl:template match="/EOF">
<Region LastMessage="Y"/>
</xsl:template>
</xsl:stylesheet>