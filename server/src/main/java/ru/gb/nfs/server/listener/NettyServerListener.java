package ru.gb.nfs.server.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gb.nfs.server.config.NettyConfig;
import ru.gb.nfs.server.handler.ServerHandler;

import javax.annotation.Resource;

@Component
public class NettyServerListener {

    public static final int MAX_OBJECT_SIZE = 20 * 1_000_000;

    private static Logger logger = LoggerFactory.getLogger(NettyServerListener.class);

    ServerBootstrap serverBootstrap = new ServerBootstrap();

    EventLoopGroup boss = new NioEventLoopGroup();

    EventLoopGroup work = new NioEventLoopGroup();

    @Autowired
    private ServerHandler serverHandler;

    @Resource
    private NettyConfig nettyConfig;

    public void start() throws InterruptedException {
        int port = nettyConfig.getPort();
        try {
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(
                                    new ObjectDecoder(MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    serverHandler
                            );
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
