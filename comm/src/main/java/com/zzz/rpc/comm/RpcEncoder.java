package com.zzz.rpc.comm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<Object> {
    private Class<?> aClass;

    public RpcEncoder(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

      if (aClass.isInstance(o)) {
          byte[] buf = SerializeHelper.serialize(o);

          byteBuf.writeInt(buf.length);
          byteBuf.writeBytes(buf);
      }
    }
}
