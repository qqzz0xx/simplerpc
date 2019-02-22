package com.zzz.rpc.server;

import com.zzz.rpc.comm.RpcDecoder;
import com.zzz.rpc.comm.RpcEncoder;
import com.zzz.rpc.comm.RpcRequest;
import com.zzz.rpc.comm.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

public class RpcServer {

    private Map<String, Object> serviceMap;
    private String serverAddr;

    public RpcServer(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public void Init(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public void Run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new RpcDecoder(RpcRequest.class))
                        .addLast(new RpcEncoder(RpcResponse.class))
                        .addLast(new RpcHandler(serviceMap));
            }
        });

        bootstrap.option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            String[] addrs = serverAddr.split(":");
            int port = Integer.parseInt(addrs[1]);
            ChannelFuture future = bootstrap.bind(addrs[0], port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
}
