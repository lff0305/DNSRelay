package org.lff.server.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;
import org.lff.common.messages.RSAEncryptedRequestMessage;
import org.lff.common.messages.RequestMessage;
import org.lff.common.messages.ResponseMessage;
import org.lff.rsa.RSACipher;
import org.lff.server.worker.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.util.Arrays;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:07
 */
public class DNSServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(DNSServerHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        HttpRequest request = (HttpRequest) e.getMessage();
        String uri = request.getUri();

        byte[] data = request.getContent().array();
        data = RSACipher.decrypt(data);


        String serial = request.headers().get("Serial");
        if (serial == null) {

        }

        RequestMessage requestMessage = new RequestMessage(data);

        logger.debug("Request Message Hash = " + Arrays.hashCode(requestMessage.toByteArray()));

        byte[] dnsServer = requestMessage.getDnsServer();
        byte[] dnsRequest = requestMessage.getRequest();

        byte[] result = Resolver.doWork(dnsRequest, dnsServer);

        if (result != null) {

            logger.debug("Real DNS Response is {} {}", result.length, Arrays.toString(result));

            ResponseMessage m = new ResponseMessage();
            m.setData(result);
            m.setPort(requestMessage.getPort());
            m.setInetaddr(requestMessage.getInetaddr());

            logger.debug("Response Message Hash = " + Arrays.hashCode(m.toByteArray()));

            byte[] r = RSACipher.encrypt(m.toByteArray());
            logger.debug("Encrypted Response is {} {}", r.length, Arrays.toString(r));
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

            ChannelBuffer buffer = new DynamicChannelBuffer(2048);
            buffer.writeBytes(r);
            response.setContent(buffer);
            response.headers().add("Content-Type", "text/html; charset=UTF-8");
            response.headers().add("Content-Length", response.getContent().writerIndex());
            response.headers().add("Serial", serial);
            Channel ch = e.getChannel();
            // Write the initial line and the header.
            ch.write(response);
            ch.disconnect();
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        if (cause instanceof TooLongFrameException) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        cause.printStackTrace();
        if (ch.isConnected()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.headers().add(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer("Failure: "
                + status.toString() + "\r\n", CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
