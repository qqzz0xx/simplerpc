package com.zzz.rpc.service;


import com.zzz.rpc.comm.RpcService;

@RpcService(IHelloService.class)
public class HelloService implements IHelloService {
    @Override
    public String hello(String name) {
        return name + ",hello!";
    }
}
