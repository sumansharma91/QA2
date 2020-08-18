<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     <Items>  
     <Item> 
        <xsl:attribute  name = "AdjustmentType">ADJUSTMENT</xsl:attribute>
        <xsl:attribute  name = "ItemID">
	     <xsl:value-of select="Item/@ItemID"/>
	     </xsl:attribute>
	     <xsl:attribute  name = "OrganizationCode">VSIINV</xsl:attribute>
	     <xsl:attribute  name = "ProductClass">GOOD</xsl:attribute>
	     <xsl:attribute  name = "Quantity">0</xsl:attribute>
	     <xsl:attribute  name = "ReasonCode">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ReasonText">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ShipNode">9004</xsl:attribute>
	     <xsl:attribute  name = "SupplyType">ONHAND</xsl:attribute>
	     <xsl:attribute  name = "UnitOfMeasure">EACH</xsl:attribute>
     </Item>
	  <Item> 
        <xsl:attribute  name = "AdjustmentType">ADJUSTMENT</xsl:attribute>
        <xsl:attribute  name = "ItemID">
	     <xsl:value-of select="Item/@ItemID"/>
	     </xsl:attribute>
	     <xsl:attribute  name = "OrganizationCode">ADPINV</xsl:attribute>
	     <xsl:attribute  name = "ProductClass">GOOD</xsl:attribute>
	     <xsl:attribute  name = "Quantity">0</xsl:attribute>
	     <xsl:attribute  name = "ReasonCode">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ReasonText">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ShipNode">9004</xsl:attribute>
	     <xsl:attribute  name = "SupplyType">ONHAND</xsl:attribute>
	     <xsl:attribute  name = "UnitOfMeasure">EACH</xsl:attribute>
     </Item>
	  <Item> 
        <xsl:attribute  name = "AdjustmentType">ADJUSTMENT</xsl:attribute>
        <xsl:attribute  name = "ItemID">
	     <xsl:value-of select="Item/@ItemID"/>
	     </xsl:attribute>
	     <xsl:attribute  name = "OrganizationCode">DTCINV</xsl:attribute>
	     <xsl:attribute  name = "ProductClass">GOOD</xsl:attribute>
	     <xsl:attribute  name = "Quantity">0</xsl:attribute>
	     <xsl:attribute  name = "ReasonCode">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ReasonText">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ShipNode">9004</xsl:attribute>
	     <xsl:attribute  name = "SupplyType">ONHAND</xsl:attribute>
	     <xsl:attribute  name = "UnitOfMeasure">EACH</xsl:attribute>
     </Item>
	  <Item> 
        <xsl:attribute  name = "AdjustmentType">ADJUSTMENT</xsl:attribute>
        <xsl:attribute  name = "ItemID">
	     <xsl:value-of select="Item/@ItemID"/>
	     </xsl:attribute>
	     <xsl:attribute  name = "OrganizationCode">MCLINV</xsl:attribute>
	     <xsl:attribute  name = "ProductClass">GOOD</xsl:attribute>
	     <xsl:attribute  name = "Quantity">0</xsl:attribute>
	     <xsl:attribute  name = "ReasonCode">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ReasonText">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ShipNode">9004</xsl:attribute>
	     <xsl:attribute  name = "SupplyType">ONHAND</xsl:attribute>
	     <xsl:attribute  name = "UnitOfMeasure">EACH</xsl:attribute>
     </Item>
	 <Item> 
        <xsl:attribute  name = "AdjustmentType">ADJUSTMENT</xsl:attribute>
        <xsl:attribute  name = "ItemID">
	     <xsl:value-of select="Item/@ItemID"/>
	     </xsl:attribute>
	     <xsl:attribute  name = "OrganizationCode">VSI.com</xsl:attribute>
	     <xsl:attribute  name = "ProductClass">GOOD</xsl:attribute>
	     <xsl:attribute  name = "Quantity">0</xsl:attribute>
	     <xsl:attribute  name = "ReasonCode">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ReasonText">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ShipNode">9004</xsl:attribute>
	     <xsl:attribute  name = "SupplyType">ONHAND</xsl:attribute>
	     <xsl:attribute  name = "UnitOfMeasure">EACH</xsl:attribute>
     </Item>
	 <Item> 
        <xsl:attribute  name = "AdjustmentType">ADJUSTMENT</xsl:attribute>
        <xsl:attribute  name = "ItemID">
	     <xsl:value-of select="Item/@ItemID"/>
	     </xsl:attribute>
	     <xsl:attribute  name = "OrganizationCode">ADP</xsl:attribute>
	     <xsl:attribute  name = "ProductClass">GOOD</xsl:attribute>
	     <xsl:attribute  name = "Quantity">0</xsl:attribute>
	     <xsl:attribute  name = "ReasonCode">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ReasonText">Initial Item Load</xsl:attribute>
	     <xsl:attribute  name = "ShipNode">9004</xsl:attribute>
	     <xsl:attribute  name = "SupplyType">ONHAND</xsl:attribute>
	     <xsl:attribute  name = "UnitOfMeasure">EACH</xsl:attribute>
     </Item>
	     </Items>
     </xsl:template>
</xsl:stylesheet>