<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
      <Promise>
      
      <xsl:copy-of select = "/Promise/@*"/>
      <PromiseLines>
      <xsl:copy-of select = "/Promise/PromiseLines/@*"/>
      <xsl:for-each select = "/Promise/PromiseLines/PromiseLine">
      <PromiseLine>
      <xsl:copy-of select = "./@*"/>
      <Availability>
      <xsl:copy-of select = "./Availability/@*"/>
      <AvailableInventory>
      <xsl:copy-of select = "./Availability/AvailableInventory/@*"/>
      <ShipNodeAvailableInventory>
      <xsl:copy-of select = "./Availability/AvailableInventory/ShipNodeAvailableInventory/@*"/>
      <Inventory>
      <xsl:if test = "./Availability/AvailableInventory/ShipNodeAvailableInventory/Inventory">
      <xsl:copy-of select = "./Availability/AvailableInventory/ShipNodeAvailableInventory/Inventory/@*"/>
      	  <xsl:attribute  name = "Status">1</xsl:attribute>  
      </xsl:if>
      <xsl:if test = "not(./Availability/AvailableInventory/ShipNodeAvailableInventory/Inventory)">
      <xsl:attribute  name = "Node">9001</xsl:attribute>
	  <xsl:attribute  name = "Status">1</xsl:attribute>       
	   <xsl:attribute  name = "AvailableQuantity">0.00</xsl:attribute>
	   <xsl:attribute  name = "AvailableOnhandQuantity">0.00</xsl:attribute>
     <xsl:attribute  name = "AvailableFutureQuantity">0.00</xsl:attribute>
	   <xsl:attribute  name = "AvailableFromUnplannedInventory">N</xsl:attribute>         
      </xsl:if>
      </Inventory>
      </ShipNodeAvailableInventory>
      </AvailableInventory>
      </Availability>
      </PromiseLine>
      </xsl:for-each>
      </PromiseLines>
      </Promise>
            
   </xsl:template>
</xsl:stylesheet>
