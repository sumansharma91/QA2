<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<MultiApi>

			<xsl:for-each select="Order/OrderLines/OrderLine">
				<API Name="getPricingRuleList">
					<Input>
						<PricingRule RuleType="COMBINATION" RuleCategory="PRICING"
							PricingStatus="ACTIVE" OrganizationCode="VSI-Cat" IsCouponRule="Y"
							CallingOrganizationCode="VSI-Cat">

							<PricingRuleItemList>
								<PricingRuleItem Quantity="1">
									<xsl:attribute name="UnitOfMeasure">
							<xsl:value-of select="Item/@UnitOfMeasure" />
						</xsl:attribute>
									<xsl:attribute name="ItemID">
							<xsl:value-of select="Item/@ItemID" />
						</xsl:attribute>
								</PricingRuleItem>
							</PricingRuleItemList>
							<PricingRuleTargetItemList>
								<PricingRuleTargetItem>
									<xsl:attribute name="TargetUnitOfMeasure">
							<xsl:value-of select="Item/@UnitOfMeasure" />
						</xsl:attribute>
									<xsl:attribute name="TargetItemID">
							<xsl:value-of select="Item/@ItemID" />
						</xsl:attribute>
								</PricingRuleTargetItem>
							</PricingRuleTargetItemList>
						</PricingRule>
					</Input>
					<Template>
						<PricingRuleList>
							<PricingRule Currency="" Description=""
								DistributeAdjustment="" EndDateActive="" IgnoreRuleOnPriceLock=""
								IsCouponRule="" IsCustomRule="" IsItemAttributeValueRule=""
								IsItemRule="" ItemOperatorCode="" OrganizationCode=""
								PricingRuleKey="" PricingRuleName="" PricingStatus=""
								RuleCategory="" RuleType="" StartDateActive="">
								<PricingRuleItemList>
									<PricingRuleItem Description=""
										IncludeOrExclude="" ItemID="" PricingRuleItemKey=""
										PricingRuleKey="" Quantity="" ShortDescription=""
										UnitOfMeasure="" />
								</PricingRuleItemList>
								<Coupon CouponID="" CouponKey="" OrganizationCode="" />
							</PricingRule>
						</PricingRuleList>
					</Template>
				</API>
			</xsl:for-each>
		</MultiApi>
	</xsl:template>
</xsl:stylesheet>