#nohup /opt/sterling/build/startIntegrationServer.sh VSISendVGCActivation "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSIOGCInvoice "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/agentserver_mod.sh VSIReturnOrderCreateInvoice "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/agentserver_mod.sh VSIReturnOrderPaymentCollection "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/agentserver_mod.sh VSIReturnOrderPaymentExecution "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIAckFromJDA "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIARCreateUpdate "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIReleaseAckWMSWholesaleIntegServer "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSIShipCnWholesaleFromWMSIntegServer "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh ManageItemServer   "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSICRMCustomerSync "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSISyncInventory  "-Xms512m -Xmx1024m -XX:PermSize=512m -XX:MaxPermSize=1024m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSISyncInventory  "-Xms512m -Xmx1024m -XX:PermSize=512m -XX:MaxPermSize=1024m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh adjustInventory "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh adjustInventory "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh adjustInventory "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

nohup /opt/sterling/build/startIntegrationServer.sh VSISTSUpdate "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSICreateCustomer  "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&2 &



 


