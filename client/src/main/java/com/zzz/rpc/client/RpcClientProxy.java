package com.zzz.rpc.client;

import com.zzz.rpc.comm.RpcRequest;
import com.zzz.rpc.comm.RpcResponse;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

public class RpcClientProxy {
    private String serverAddress;
    private static AtomicLong requestId = new AtomicLong();

    public RpcClientProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public <T> T createProxy(Class<?> aClass) {

        return (T)Proxy.newProxyInstance(aClass.getClassLoader(),new Class<?>[]{aClass}, (proxy, method, args) -> {

            RpcRequest request = new RpcRequest();
            request.setId( requestId.incrementAndGet());
            request.setClassName(aClass.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParamTypes(method.getParameterTypes());
            request.setParams(args);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            RpcClient client = new RpcClient(host, port);
            RpcResponse response =   client.send(request);

            if (response.getError() != null)
            {
                throw response.getError();
            }

            return  response.getResult();

        });

    }
}
