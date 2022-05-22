package com.ryland.netty.example07;

import com.ryland.netty.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

/**
 * @author Ryland
 */
@Slf4j
public class ByteBufBase {

    public static void main(String[] args) {
        // base();
        // allocate();
        // pooled();
        // commonMethods();
        // slice1();
        // slice2();
        // composite();
        testUnpooled();
    }

    private static void testUnpooled() {
        // no copy
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{1, 2, 3}, new byte[]{4, 5, 6});
        log.debug("[{}]", buf.getClass());
        ByteBufUtils.log(buf);
    }

    private static void composite() {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(64);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(64);
        buf1.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // has data copy
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(buf1).writeBytes(buf2);

        buf1.resetReaderIndex();
        buf2.resetReaderIndex();

        // zero
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();

        // log read index:0 write index:0 capacity:0
        // compositeByteBuf.addComponents(buf1, buf2);

        // increase write index
        compositeByteBuf.addComponents(true, buf1, buf2);
        ByteBufUtils.log(compositeByteBuf);

        buf1.retain();
        compositeByteBuf.release();
        ByteBufUtils.log(buf1);
        // buf2 is recycled
        ByteBufUtils.log(buf2);
    }

    /**
     * deep copy
     */
    private static void copy() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        ByteBuf copy = buffer.copy();
        buffer.release();
        copy.release();
    }

    /**
     * Like slice, it duplicates the entire ByteBuf and has no limit on max capacity
     * it uses the same memory and has independent read and write pointers
     */
    private static void duplicate() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        ByteBuf buf = buffer.duplicate();
        buffer.release();
    }

    private static void slice2() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        ByteBufUtils.log(buffer);

        // appending is not allowed after slicing
        ByteBuf f1 = buffer.slice(0, 5);
        f1.retain();
        ByteBufUtils.log(f1);

        buffer.release();
        // throw IllegalReferenceCountException if retain not invoked
        ByteBufUtils.log(f1);

        // Remember to release f1 unless you want space leak
        f1.release();
    }

    /**
     * method slice is the reflection of zero-copy
     * if we need to slice a ByteBuf into 2 ByteBuf, we do not need to copy memory and each
     * ByteBuf has its own read and write pointer
     */
    private static void slice1() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        ByteBufUtils.log(buffer);

        // appending is not allowed after slicing
        ByteBuf f1 = buffer.slice(0, 5);
        ByteBuf f2 = buffer.slice(5, 5);
        ByteBufUtils.log(f1);
        ByteBufUtils.log(f2);

        f1.setByte(0, 'z');
        ByteBufUtils.log(buffer);
    }

    /**
     * 1. UnpooledHeapByteBuf: use JVM memory, recycled by GC
     * 2. UnpooledDirectByteBuf: use direct memory, recycled by GC but not timely, so we use special method to recycle
     * 3. PooledByteBuf: complicated
     * <p>
     * Fortunately, Netty provides us interface ReferenceCounted to recycle
     * it use Reference-Count method to control recycling
     * each ByteBuf's initial count is 1
     * when release invoked, count--
     * when retain invoked, count++
     * recycled when 0, even the ByteBuf Object is existing, the methods can not be used
     * <p>
     * the last one who uses the ByteBuf will release it!!!
     * inbound: tail can release the ByteBuf created by head
     * outbound: head can release the ByteBuf created by tail
     */
    private static void impl() {
        // tail: TailContext
        // io.netty.channel.DefaultChannelPipeline.TailContext.channelRead
        // io.netty.channel.DefaultChannelPipeline.onUnhandledInboundMessage(java.lang.Object)

        // head: HeadContext
        // io.netty.channel.DefaultChannelPipeline.HeadContext.write
        // io.netty.channel.AbstractChannel.AbstractUnsafe.write
    }

    /**
     * if the size after expanding is not bigger than 512, it expands to integer multiple of 16
     * if the size after expanding is bigger than 512, it expands to 2^n (not bigger than max capacity)
     */
    private static void expandRule() {

    }

    private static void commonMethods() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(128);
        // 01-true, 00-false
        buffer.writeBoolean(true);
        ByteBufUtils.log(buffer);

        // Big Endian, 0x250->00 00 02 50
        buffer.writeInt(1);
        // Little Endian, 0x250->50 02 00 00
        buffer.writeIntLE(1);

        // NIO ByteBuffer -> ByteBuf
        buffer.writeBytes(ByteBuffer.allocate(10));

        buffer.writeCharSequence(new StringBuilder(), Charset.defaultCharset());

        // read one byte and drop the byte
        buffer.readByte();

        // mark before read, the bytes will not be dropped
        buffer.markReaderIndex();
        buffer.readInt();

        // read again
        buffer.resetReaderIndex();

        // getXxx do not change read index
    }

    /**
     * ByteBuf max capacity: Integer.MAX_VALUE
     * <p>
     * ByteBuf has a writing pointer and a reading pointer, initial values are both 0
     * <p>
     * Netty ByteBuf vs NIO ByteBuffer
     * 1. NIO ByteBuffer share the writing and reading pointer
     * 2. NIO ByteBuffer can not expand dynamically
     */
    private static void composition() {

    }

    /**
     * Pooled: reuse ByteBuf
     * we can also config the system property or VM option '-Dio.netty.allocator.type={unpooled|pooled}'
     * <p>
     * from 4.1 use pooled by default except Android
     * before 4.1 use unpooled by default
     */
    private static void pooled() {
        // PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 10)
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        log.debug("[{}]", buffer);
    }

    /**
     * Direct Memory vs Heap Memory
     * direct memory: high speed of read and write, low speed of allocation
     * heap memory: to the opposite
     * <p>
     * default: direct memory
     */
    private static void allocate() {
        ByteBufAllocator.DEFAULT.buffer(10);
        ByteBufAllocator.DEFAULT.heapBuffer(10);
        ByteBufAllocator.DEFAULT.directBuffer(10);
    }

    private static void base() {
        // the capacity of ByteBuf is 256 if we don't assign it
        // ByteBuf can expand dynamically
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        log.debug("[{}]", byteBuf);

        StringBuilder builder = new StringBuilder();
        IntStream.range(0, 300).forEach(i -> builder.append("a"));
        byteBuf.writeBytes(builder.toString().getBytes(StandardCharsets.UTF_8));
        log.debug("[{}]", byteBuf);
        ByteBufUtils.log(byteBuf);
    }

}
