package com.baisha.modulespringcacheredis.config;

import java.io.IOException;
import java.nio.charset.Charset;

import com.alibaba.fastjson.parser.Feature;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

/**
 * @Author 小智
 * @Date 13/7/22 11:02 AM
 * @Version 1.0
 */
public class FastjsonCodec extends BaseCodec {
    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try {
            ByteBufOutputStream os = new ByteBufOutputStream(out);
            JSON.writeJSONString(os, in,SerializerFeature.WriteClassName);
            return os.buffer();
        } catch (IOException e) {
            out.release();
            throw e;
        } catch (Exception e) {
            out.release();
            throw new IOException(e);
        }
    };

    private final Decoder<Object> decoder = (buf, state) ->
            JSON.parseObject(new ByteBufInputStream(buf).readLine(), Object.class);

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}
