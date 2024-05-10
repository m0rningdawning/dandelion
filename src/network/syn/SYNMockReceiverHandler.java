package network.syn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SYNMockReceiverHandler extends SimpleChannelInboundHandler<Object> {
    // Invoked on read (Skip message handling isn't necessary for an attack)
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String sourceIP = ctx.channel().remoteAddress().toString();
        int sourcePort = ((java.net.InetSocketAddress) ctx.channel().remoteAddress()).getPort();
        System.out.println("Received SYN packet from " + sourceIP + ":" + sourcePort);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Exception handling the SYN package, SYNHandler.java: " + cause.getMessage());
        ctx.close();
    }
}
