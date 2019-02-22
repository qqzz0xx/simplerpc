package com.zzz.rpc.comm;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializeHelper {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static <T> Schema<T> getSchema(Class<T> aClass) {

        Schema<T> schema = (Schema<T>) cachedSchema.get(aClass);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(aClass);
            if (schema != null) {
                cachedSchema.put(aClass, schema);
            }
        }

        return schema;
    }

    public static <T> byte[] serialize(T obj) {

        Class<T> cls = (Class<T>) obj.getClass();

        Schema<T> schema = getSchema(cls);

        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        // ser
        final byte[] protostuff;
        try
        {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        }
        finally
        {
            buffer.clear();
        }

        return protostuff;
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {

        Schema<T> schema = getSchema(cls);
        T message = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data, message, schema);

        return message;
    }
}
