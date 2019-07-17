package com.hyb;


import com.hyb.jmx.Hello;
import com.hyb.jmx.HelloAgent;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App
{
    static Properties env=new Properties();
    static {
        env.put("-Dcom.hyb.manager.hostname","localhost");
        env.put("-Dcom.hyb.manager.rmi.port","11099");
        env.put("-Dcom.hyb.manager.jmx.port","10099");
        env.put("-Dcom.hyb.manager.jmx.service","manager");
        //env.put("-Dcom.hyb.manager.jmx.url","service:jmx:rmi:///jndi/rmi://localhost:11099/manager");
    }
    public static void main( String[] args )
    {
        explainArgs(args);
        try{
            for (String arg : args) {
                System.out.println(arg);
            }
            HelloAgent.exec(env);
            //jmx(args);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private static void explainArgs(String[] args){
        for (String arg : args) {
            String[] split = arg.split("=");
            if(split.length!=2)
            {
                System.out.println(String.format("parameter context %s\t is wrong,a equal(=) is needed here.",arg));
            }else if(!split[0].startsWith("-D")){
                System.out.println(String.format("parameter context %s\t is wrong.parameter must start with -D",arg));
            }else{
                env.put(split[0],split[1]);
            }
        }
    }
    public static void jmx(String[] args) throws JMException, NullPointerException
    {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName helloName = new ObjectName("jmxBean:name=hello");
        //create mbean and register mbean
        server.registerMBean(new Hello(), helloName);
        try
        {
            //这个步骤很重要，注册一个端口，绑定url后用于客户端通过rmi方式连接JMXConnectorServer
            LocateRegistry.createRegistry(9999);
            //URL路径的结尾可以随意指定，但如果需要用Jconsole来进行连接，则必须使用jmxrmi
            JMXServiceURL url = new JMXServiceURL
                    ("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
            JMXConnectorServer jcs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            System.out.println("begin rmi start");
            jcs.start();
            System.out.println("rmi start");
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}