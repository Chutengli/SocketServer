import java.io.IOException;
import java.net.*;

public class EchoClient {
	private DatagramSocket socket;
	private InetAddress address;

	private byte[] buf;

	public EchoClient() throws SocketException, UnknownHostException {
		socket = new DatagramSocket();
		address = InetAddress.getByName("localhost");
	}

	public String sendEcho(String msg) throws IOException {
		buf = msg.getBytes();
		DatagramPacket packet
				= new DatagramPacket(buf, buf.length, address, 17);
		socket.send(packet);
		buf = new byte[1024];
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String received = new String(
				packet.getData(), 0, packet.getLength());
		return received;
	}

	public void close() {
		socket.close();
	}

	public static void main(String[] args) throws IOException {
		EchoClient client = new EchoClient();
		System.out.println(client.sendEcho("message!!!"));
		client.close();
	}
}