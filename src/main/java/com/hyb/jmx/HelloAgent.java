package com.hyb.jmx;


import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

public class HelloAgent {
    public static void exec(Properties properties) throws MalformedObjectNameException,
            NotCompliantMBeanException, InstanceAlreadyExistsException,
            MBeanRegistrationException, IOException {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String domainName = HelloAgent.class.getPackage().getName();
        ObjectName helloName = new ObjectName(domainName+":name=Hello");
        mbs.registerMBean(new Hello(),helloName);
        String jmxServerUrl;
        int rmiPort=-1;
        if(properties.containsKey("-Dcom.hyb.manager.jmx.url")){
            jmxServerUrl=properties.getProperty("-Dcom.hyb.manager.jmx.url");
            String[] split = jmxServerUrl.split("[:,/]");
            rmiPort=Integer.parseInt(split[split.length-2]);
        }
        else{
            String hostname=properties.getProperty("-Dcom.hyb.manager.hostname");
            String jmxServerPort=properties.getProperty("-Dcom.hyb.manager.jmx.port");
            String rmiRegisterPort=properties.getProperty("-Dcom.hyb.manager.rmi.port");
            String serviceName=properties.getProperty("-Dcom.hyb.manager.jmx.service");

            jmxServerUrl=String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/%s",
                    hostname,jmxServerPort,hostname,rmiRegisterPort,serviceName);
            rmiPort=Integer.parseInt(rmiRegisterPort);
        }
        System.out.println(jmxServerUrl);
        System.out.println(rmiPort);

        if(rmiPort<0)
        {
            System.out.println("can not createRegistry on port:"+rmiPort);
        }else{
            //注册rmi register
            LocateRegistry.createRegistry(rmiPort);

            JMXServiceURL url = new JMXServiceURL(jmxServerUrl);
            JMXConnectorServer jmxConnector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
            jmxConnector.start();
        }

    }
}
