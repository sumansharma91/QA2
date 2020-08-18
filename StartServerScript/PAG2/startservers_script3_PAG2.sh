nohup /opt/sterling/build/startIntegrationServer.sh VSICancelFromJDA "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 & 

nohup /opt/sterling/build/startIntegrationServer.sh VSIWavedFromWMSIntegServer "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSICancelFromES "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 & 

nohup /opt/sterling/build/startIntegrationServer.sh VSIIssueGiftCard "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIPublishInvoice "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 & 

nohup /opt/sterling/build/startIntegrationServer.sh VSISendVGCActivation "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIOGCInvoice "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/agentserver_mod.sh VSIReturnOrderCreateInvoice "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/agentserver_mod.sh VSIShipmentInvoiceServer "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSICRMCustomerSync "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSISyncInventory  "-Xms512m -Xmx1024m -XX:PermSize=512m -XX:MaxPermSize=1024m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSISyncInventory  "-Xms512m -Xmx1024m -XX:PermSize=512m -XX:MaxPermSize=1024m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh adjustInventory "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh adjustInventory "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh adjustInventory "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSISTSUpdate "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/agentserver_mod.sh VSIItemBasedAllocation "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIAckFromJDA "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &