package org.lff.server.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:04
 */
public class NettyServer {
    public static void start() {
        // 配置服务器-使用java线程池作为解释线程
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        // 设置 pipeline factory.
        bootstrap.setPipelineFactory(new DNSPipelineFactory());
        // 绑定端口
        bootstrap.bind(new InetSocketAddress(8080));
    }
}
