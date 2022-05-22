package com.ryland.netty.example09;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author Ryland
 */
@Slf4j
public class FixedLengthFrameDecoderTest {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 5),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 4 bytes length
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        write(buffer, "Hello World");
        write(buffer, "Hello Netty");
        write(buffer, "Learning Netty");

        channel.writeInbound(buffer);

    }

    private static void write(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        buffer.writeInt(length);
        // version
        buffer.writeByte(1);
        buffer.writeBytes(bytes);
    }

}
