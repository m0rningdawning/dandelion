package network.syn;

//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetSocketAddress;
//import java.net.ServerSocket;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SYNMockReceiver {
    private static final int port = 9999;

//    public SYNReceiver(int port) {
//        this.port = port;
//    }
//
//    public void start() {
//        try {
//            ServerSocket socket = new ServerSocket(port);
//            socket.bind(new InetSocketAddress(port));
//
//            System.out.println("SYN Receiver started on port " + port);
//
//            while (true) {
//                byte[] buffer = new byte[1024];
//                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
////                socket.receive(packet);
//
//                String data = new String(packet.getData(), 0, packet.getLength());
//
//                System.out.println("Received packet from " + packet.getAddress().getHostAddress() +
//                        ":" + packet.getPort() + " - Data: " + data);
//            }
//        } catch (Exception e) {
//            System.out.println("Error in SYNReceiver: " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        int port = 9999;
//        SYNReceiver receiver = new SYNReceiver(port);
//        receiver.start();
//    }
public static void main(String[] args) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new SYNMockReceiverHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture f = b.bind(port).sync();

        System.out.println("TCP SYN Receiver started on port " + port);

        f.channel().closeFuture().sync();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
}
