package com.ryland.netty.example11.protocol;

import com.ryland.netty.example11.config.Config;
import com.ryland.netty.example11.message.LoginRequestMessage;
import com.ryland.netty.example11.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * must use this with LengthFieldBasedFrameDecoder, ensure that
 * ByteBuf received is entire
 *
 * @author Ryland
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        Serializer.Algorithm alg = Config.getSerializerAlgorithm();
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(alg.getCode());
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = alg.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }

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
        Serializer.Algorithm algorithm = Serializer.Algorithm.getByCode(serializerAlgorithm).orElseThrow(IllegalArgumentException::new);
        log.debug("magic:[{}]\nversion:[{}]\nserializerType:[{}]\nmessageType:[{}]\nsequenceId:[{}]\nlength:[{}]",
                magic, version, serializerAlgorithm, messageType, sequenceId, length);

        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);

        log.debug("message:[{}]", message);
        out.add(message);
    }

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodecSharable()
        );

        LoginRequestMessage message = new LoginRequestMessage("ryland", "123");
        channel.writeOutbound(message);
    }

}
