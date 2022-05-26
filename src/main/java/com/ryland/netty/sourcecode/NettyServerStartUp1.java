package com.ryland.netty.sourcecode;

import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * not for running
 * source code analysis
 *
 * @author Ryland
 */
public class NettyServerStartUp1 {

    public static void main(String[] args) throws IOException {

        // Netty uses NioEventLoopGroup to encapsulate threads and Selector
        Selector selector = Selector.open();

        // NioServerSocketChannel will initialize its handlers and store config for ServerSocketChannel
        NioServerSocketChannel attachment = new NioServerSocketChannel();

        // create original ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        // bind Selector and ServerSocketChannel but interest in nothing
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, attachment);

        // bind port
        serverSocketChannel.bind(new InetSocketAddress(8080));

        // trigger event of Channel active and interest in OP_ACCEPT
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

    }

}
