package com.zzz.rpc.server;

import com.zzz.rpc.comm.RpcRequest;
import com.zzz.rpc.comm.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> serviceMap;

    public RpcHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getId());

        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
        }

        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest rpcRequest) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        String className = rpcRequest.getClassName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] paramTypes = rpcRequest.getParamTypes();
        Object params = rpcRequest.getParams();

        Object service = serviceMap.get(className);

        Class cls = service.getClass();

        Method method = cls.getDeclaredMethod(methodName, paramTypes);
        return method.invoke(service, params);
    }
}
