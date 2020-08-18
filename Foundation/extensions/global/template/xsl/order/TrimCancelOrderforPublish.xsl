    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
      <xsl:output version="1.0" indent="yes" encoding="UTF-8" method="xml" />
      <xsl:template match="/">
      <OrderList EmailType="{/OrderList/@EmailType}" MessageType="{/OrderList/@MessageType}">
   
      <xsl:if test = "/OrderList/@Action ='CANCEL'">
      <Order>
      <xsl:copy-of select="/OrderList/Order/@*"/>
 
      <xsl:copy-of select="/OrderList/Order/PriceInfo"/>
      
      <xsl:copy-of select="/OrderList/Order/Promotions"/>
      
      <Extn>
       <xsl:copy-of select="/OrderList/Order/Extn/@*"/>
      </Extn>
    
     <OrderLines>
     <xsl:copy-of select="/OrderList/Order/OrderLines/OrderLine[@OrderedQty!='0']" />
     </OrderLines>
        <xsl:copy-of select="/OrderList/Order/Instructions"/>
        <xsl:copy-of select="/OrderList/Order/PersonInfoShipTo"/>
        <xsl:copy-of select="/OrderList/Order/PersonInfoBillTo"/>
        <xsl:copy-of select="/OrderList/Order/PaymentMethods"/>
      <xsl:copy-of select="/OrderList/Order/HeaderCharges"/>
           <xsl:copy-of select="/OrderList/Order/HeaderTaxes"/>
       

     </Order>
   </xsl:if>
   <xsl:if test = "/OrderList/@Action !='CANCEL'">   
   
   <Order>
   
   <xsl:copy-of select="/OrderList/Order/@*"/>
     <xsl:copy-of select="/OrderList/Order/*[not(self::OrderLines)]"/>
   <OrderLines>
   <xsl:copy-of select="/OrderList/Order/OrderLines/@*"/>
   
   <xsl:for-each select = "/OrderList/OrderLines/OrderLine">
   
   
   <xsl:variable name = "OrderLineKey" select = "./@OrderLineKey" />
   <xsl:variable name = "PrimeLineNo" select = "./@PrimeLineNo" />
   
   <xsl:for-each select = "/OrderList/Order/OrderLines/OrderLine[@Publish='Y' and (@OrderLineKey=$OrderLineKey or @PrimeLineNo=$PrimeLineNo)]">
   
    <OrderLine>
   <xsl:copy-of select="./@*"/>
      <xsl:copy-of select="./*"/>
   </OrderLine>
    </xsl:for-each>
    </xsl:for-each>
   </OrderLines>
   </Order>
   </xsl:if>
   </OrderList>
      
   </xsl:template>
   </xsl:stylesheet>

   