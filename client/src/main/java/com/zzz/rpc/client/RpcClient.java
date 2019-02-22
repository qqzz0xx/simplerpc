package com.zzz.rpc.client;

import com.zzz.rpc.comm.RpcDecoder;
import com.zzz.rpc.comm.RpcEncoder;
import com.zzz.rpc.comm.RpcRequest;
import com.zzz.rpc.comm.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private String address;
    private int port;
    private RpcResponse response;

    private  final Lock lock = new ReentrantLock();
    private  final Condition condition = lock.newCondition();

    public RpcClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public RpcResponse send(RpcRequest request) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                        .addLast(new RpcDecoder(RpcResponse.class))
                        .addLast(this);
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.SO_TIMEOUT, 1000);

        try {
            ChannelFuture future = bootstrap.connect(address, port).sync();
            future.channel().writeAndFlush(request).sync();

            lock.lock();
            condition.await();

            return response;


        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;

        lock.lock();
        condition.signalAll();
        lock.unlock();

    }
}
