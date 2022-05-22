package com.ryland.netty.example04;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * JDK Future(get the result sync)
 * <=
 * Netty Future(get the result async)
 * <=
 * Promise(independent from the task and just be the container passing the result between 2 threads)
 *
 * @author Ryland
 */
@Slf4j
public class FutureBase {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // testJdkFuture();
        testNettyFuture1();
        // testNettyFuture2();
        // testNettyPromise();
    }

    private static void testNettyPromise() throws InterruptedException, ExecutionException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoopGroup.next());

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                throw new Exception("ex");
                // promise.setSuccess(100);
            } catch (Exception ex) {
                log.error("[{}]", ex);
                promise.setFailure(ex);
            }

        }).start();

        log.debug("waiting...");
        log.debug("result: [{}]", promise.get());
        eventLoopGroup.shutdownGracefully();
    }

    private static void testNettyFuture2() {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        io.netty.util.concurrent.Future<Integer> nettyFuture = eventLoop.submit(() -> {
            TimeUnit.SECONDS.sleep(3);
            return 90;
        });
        log.debug("waiting...");
        nettyFuture.addListener(future -> {
            // getNow doesn't block
            log.debug("result: [{}]", future.getNow());
            group.shutdownGracefully();
        });
    }

    private static void testNettyFuture1() throws InterruptedException, ExecutionException {
        EventLoopGroup group = new NioEventLoopGroup();
        // easy way to get EventLoop
        EventLoop eventLoop = group.next();
        io.netty.util.concurrent.Future<Integer> nettyFuture = eventLoop.submit(() -> {
            TimeUnit.SECONDS.sleep(3);
            return 80;
        });
        log.debug("waiting...");
        log.debug("result: [{}]", nettyFuture.get());
        group.shutdownGracefully();
    }

    private static void testJdkFuture() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(() -> {
            TimeUnit.SECONDS.sleep(3);
            return 50;
        });
        log.debug("waiting...");
        log.debug("result: [{}]", future.get());
    }

}
