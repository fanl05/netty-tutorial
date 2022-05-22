package com.ryland.netty.example02;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * EventLoop is an executor of single thread(maintaining a Selector) which continuously handle the io in Channel
 * EventLoop extends from ScheduledExecutorService and OrderedEventExecutor
 *
 * 2 important method:
 * boolean inEventLoop(Thread thread): judge if the thread belongs to the EventLoop
 * EventExecutorGroup parent(): find the EventLoopGroup it belongs to
 * <p>
 * EventLoopGroup is a group of EventLoops
 * Channel will bind the EventLoop by EventLoopGroup#register and io in the Channel will be handled
 * by this EventLoop to ensure the thread safety
 *
 * @author Ryland
 */
@Slf4j
public class EventLoopBase {

    public static void main(String[] args) {
        // NioEventLoopGroup is a comprehensive EventLoopGroup
        // NioEventLoopGroup can handle io, common task and timing task
        // DefaultEventLoopGroup can just handle common task and timing task
        // the default thread count is from system property 'io.netty.eventLoopThreads'
        // or the max of 1 and cpu core * 2
        EventLoopGroup group = new NioEventLoopGroup(2);
        log.info("[{}]", NettyRuntime.availableProcessors());
        log.info("EventLoop1: [{}]", group.next());
        log.info("EventLoop2: [{}]", group.next());
        // same as EventLoop1
        log.info("EventLoop3: [{}]", group.next());

        // execute common task
        group.next().submit(() -> log.debug("OK"));

        // execute timing task
        group.next().scheduleAtFixedRate(() -> log.debug("OK"), 0, 1, TimeUnit.SECONDS);
    }

}
