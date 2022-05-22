package com.ryland.netty.example11.protocol;

import com.ryland.netty.example11.message.LoginRequestMessage;
import com.ryland.netty.example11.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author Ryland
 */
@Slf4j
// ByteToMessageCodec annotates that sub-classes of ByteToMessageCodec MUST NOT annotated with @Sharable.
// @ChannelHandler.Sharable
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * msg to ByteBuf when outbound
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. magic
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. version
        out.writeByte(1);
        // 3. algorithm for serialization: 0-jdk, 1-json
        out.writeByte(Serializer.Algorithm.JDK.getCode());
        // 4. instruction type
        out.writeByte(msg.getMessageType());
        // 5. sequence id
        out.writeInt(msg.getSequenceId());
        // padding
        out.writeByte(0xff);

        //FIXME add json version
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 6. content length
        out.writeInt(bytes.length);
        // 7. content
        out.writeBytes(bytes);
    }

    /**
     * ByteBuf to msg when inbound
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Message message = null;
        if (Serializer.Algorithm.JDK.getCode() == serializerAlgorithm) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            message = (Message) ois.readObject();
        }
        log.debug("magic:[{}]\nversion:[{}]\nserializerType:[{}]\nmessageType:[{}]\nsequenceId:[{}]\nlength:[{}]",
                magic, version, serializerAlgorithm, messageType, sequenceId, length);
        log.debug("message:[{}]", message);
        out.add(message);
    }

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                // NOTE: thread-unsafe handlers shouldn't be shared between EventLoop
                // @Sharable can help us judge
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec());
        LoginRequestMessage message = new LoginRequestMessage("ryland", "123");
        channel.writeOutbound(message);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);

        // channel.writeInbound(buf);

        // simulate half bag
        ByteBuf buf1 = buf.slice(0, 100);
        ByteBuf buf2 = buf.slice(100, buf.readableBytes() - 100);

        // release is invoked after writeInbound
        buf2.retain();
        channel.writeInbound(buf1);
        channel.writeInbound(buf2);

    }
}