package com.zzz.rpc.server;

import com.zzz.rpc.comm.RpcService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServerMain {
    public static void main(String[] args) {

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-server.xml");
        Map<String, Object> map = ctx.getBeansWithAnnotation(RpcService.class);

        Map<String, Object> serviceMap = new ConcurrentHashMap<>();
        for (Object obj : map.values()) {
            String serviceClassName = obj.getClass().getAnnotation(RpcService.class).value().getName();

            serviceMap.put(serviceClassName, obj);
        }

        RpcServer server = (RpcServer) ctx.getBean("RpcServer");

        server.Init(serviceMap);
        server.Run();


    }
}
