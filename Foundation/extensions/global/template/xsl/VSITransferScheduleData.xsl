<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/TransferSchedules/TransferSchedule">
	<NodeTransferSchedule >
		
		<xsl:attribute name="EffectiveFromDate">
			<xsl:value-of select="@EffectiveFrom" />
		</xsl:attribute>
		
		<xsl:attribute name="EffectiveToDate">
			<xsl:value-of select="@EffectiveTo" />
		</xsl:attribute>
		
		
		<xsl:copy-of select="@SundayShip" />
		<xsl:copy-of select="@MondayShip" />
		<xsl:copy-of select="@TuesdayShip" />
		<xsl:copy-of select="@WednesdayShip" />
		<xsl:copy-of select="@ThursdayShip" />
		<xsl:copy-of select="@FridayShip" />
		<xsl:copy-of select="@SaturdayShip" />
		
		<xsl:attribute name="SundayTransitTime">
			<xsl:value-of select="@SundayTransitTime" />
		</xsl:attribute>
		<xsl:attribute name="MondayTransitTime">
			<xsl:value-of select="@MondayTransitTime" />
		</xsl:attribute>
		<xsl:attribute name="TuesdayTransitTime">
			<xsl:value-of select="@TuesdayTransitTime" />
		</xsl:attribute>
		<xsl:attribute name="WednesdayTransitTime">
			<xsl:value-of select="@WednesdayTransitTime" />
		</xsl:attribute>
		<xsl:attribute name="ThursdayTransitTime">
			<xsl:value-of select="@ThursdayTransitTime" />
		</xsl:attribute>
		<xsl:attribute name="FridayTransitTime">
			<xsl:value-of select="@FridayTransitTime" />
		</xsl:attribute>
		<xsl:attribute name="SaturdayTransitTime">
			<xsl:value-of select="@SaturdayTransitTime" />
		</xsl:attribute>
		
		<xsl:copy-of select="@FromNode" />
		<xsl:copy-of select="@ToNode" />
	</NodeTransferSchedule>
	</xsl:template>
</xsl:stylesheet>