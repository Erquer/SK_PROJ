package app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Connection {

    // ==== Private Fields ====
    private String ip;
    private int port;
    private SocketChannel socketChannel;

    // ==== Constructors ====
    public Connection(String ip, int port) throws IOException{
        this.port = port;
        this.ip = ip;
        this.connect();
    }
    // ==== Private Methods ====
    private void connect() throws IOException{
        var address = new InetSocketAddress(ip, port);
        this.socketChannel = SocketChannel.open(address);
        System.out.println("Connected to server with ip: " + this.ip + " on port: " + this.port);
    }
    // ==== Public Methods ====
    public void sendMessage(String message) throws IOException {
        var buffer = ByteBuffer.wrap(message.getBytes());

        socketChannel.write(buffer);
        buffer.clear();
    }

    public String read() throws IOException{

        var buffer = ByteBuffer.allocate(255);
        int len = socketChannel.read(buffer);
        byte[] bytes = new byte[len];

        for(int i = 0; i < len ;i++){
            byte m = buffer.get(i);
            bytes[i] = m;
        }

        return new String(bytes, Charset.defaultCharset());
    }



    // ==== Getters & Setters ====
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
