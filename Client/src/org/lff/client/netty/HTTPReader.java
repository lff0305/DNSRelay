package org.lff.client.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;


public class HTTPReader {

    private final URI uri;
    private ClientBootstrap bootstrap;

    public HTTPReader(String host, int port) throws URISyntaxException {
        this.uri = new URI("http://" + host + ":" + port + "/");
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpSnoopClientPipelineFactory());
    }

    public byte[] run(byte[] msg, DatagramSocket socket) {


        String host = this.uri.getHost();
        int port = this.uri.getPort();

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            return null;
        }

        // Prepare the HTTP request.
        HttpRequest request = new DefaultHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.headers().set(HttpHeaders.Names.HOST, host);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
        request.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded");
        ChannelBuffer cb = ChannelBuffers.copiedBuffer(msg);
        long serial = System.nanoTime();
        request.headers().add("Serial", String.valueOf(serial));
        request.headers().add(HttpHeaders.Names.CONTENT_LENGTH,cb.readableBytes());
        request.setContent(cb);

        // Send the HTTP request.
        channel.write(request);

        // Wait for the server to close the connection.
        channel.getCloseFuture().awaitUninterruptibly();

        try {
            byte[] result = ResultNotifier.waitFor(String.valueOf(serial));
            return result;
        } catch (InterruptedException e) {
            return null;
        }

        // shutdown(bootstrap);

    }

    private void shutdown(ClientBootstrap bootstrap) {
        // Shut down executor threads to exit.
        bootstrap.releaseExternalResources();
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        HTTPReader r = new HTTPReader(host, port);
        for (int i=0; i<10; i++) {
            r.run("AAAAAAAAAAAAAAAAAA".getBytes(), null);
        }
    }
}