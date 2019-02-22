package com.zzz.rpc.comm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;



import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> aClass;

    public RpcDecoder(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if (length < 0) {
            channelHandlerContext.close();
        }
        else if (length < byteBuf.readableBytes())
        {
            byteBuf.resetReaderIndex();
        }
        else
        {
            byte[] body = new byte[length];
            byteBuf.readBytes(body);

            Object obj = SerializeHelper.deserialize(body, aClass);

            list.add(obj);

        }

    }
}
