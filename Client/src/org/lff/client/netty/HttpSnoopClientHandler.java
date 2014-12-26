package org.lff.client.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

public class HttpSnoopClientHandler extends SimpleChannelUpstreamHandler {

    private boolean readingChunks;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!readingChunks) {
            HttpResponse response = (HttpResponse) e.getMessage();


            if (!response.headers().names().isEmpty()) {
                for (String name: response.headers().names()) {
                }
            }

            if (response.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = response.getContent();
                if (content.readable()) {
                    String serial = response.headers().get("Serial");
                    if (serial != null) {
                        ResultNotifier.tell(serial, content.array());
                    }
                }
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
            } else {
//                System.out.print(chunk.getContent().toString(CharsetUtil.UTF_8));
//                System.out.flush();
            }
        }
    }
}