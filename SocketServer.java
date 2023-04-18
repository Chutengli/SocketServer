import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
	private static final int TCP_PORT = 17;
	private static final int UDP_PORT = 17;
	private static ExecutorService executorService = null;
	private static ExecutorService udpExecutorService = null;

	private static final String[] princessBrideQuotes = new String[]{
		"Life is pain, Highness. Anyone who says differently is selling something.",
		"As you wish.",
		"Death cannot stop true love. All it can do is delay it for a while.",
		"There’s a shortage of perfect breasts in this world. It would be a pity to damage yours.",
		"You mean you'll put down your rock and I'll put down my sword, and we'll try and kill each other like civilized people?",
		"Why lose your venom on me?",
		"Rest well and dream of large women.",
		"I'll explain and I'll use small words so that you'll be sure to understand, you warthog faced buffoon.",
		"This is true love. You think this happens every day?",
		"It's not that bad! Well, I'm not saying I'd like to build a summer home here, but the trees are actually quite lovely.",
		"I always think that everything could be a trap, which is why I'm still alive.",
		"My name is Inigo Montoya. You killed my father. Prepare to die."
	};

	private static final String[] myFavoriateQuotes = new String[]{
		"Be yourself; everyone else is already taken.",
		"Two things are infinite: the universe and human stupidity; and I'm not sure about the universe."
	};

	public static void main(String[] args) {
		try {
			ServerSocket tcpServer = new ServerSocket(TCP_PORT);
			DatagramSocket udpServer = new DatagramSocket(UDP_PORT);

			executorService = Executors.newFixedThreadPool(10);

			// udp thread
			new Thread(() -> {
				try {
					System.out.println("UDP Server listening at port 17");

					byte[] buffer = new byte[512];
					DatagramPacket updPacket = new DatagramPacket(buffer, buffer.length);
					while (true) {
						udpServer.receive(updPacket);
						System.out.println("UDP packet received from " + updPacket.getAddress().getHostAddress());
						executorService.submit(() -> handleUdpRequest(udpServer, updPacket));
					}

				} catch (IOException err) {
					err.printStackTrace();
				}
			}).start();

			// tcp thread
			new Thread(() -> {
				try {
					System.out.println("TCP Server listening at port 17");

					while(true) {
						Socket threadSocket = tcpServer.accept();
						System.out.println("TCP client connected: " + threadSocket.getInetAddress().getHostAddress());

						executorService.submit(() -> handleTcpRequest(threadSocket));
					}
				} catch (IOException err) {
					err.printStackTrace();;
				}

			}).start();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void handleTcpRequest(Socket socket) {
		Random isFavoriate = new Random();
		Random quoteIdx = new Random();

		try {
			OutputStream outputStream = socket.getOutputStream();
			System.out.println("Incoming TCP Client: " + socket.getInetAddress().getHostAddress());
			PrintWriter write = new PrintWriter(outputStream, true);

			String response;
			if(isFavoriate.nextBoolean()) {
				response = myFavoriateQuotes[quoteIdx.nextInt(myFavoriateQuotes.length)];
			} else {
				response = princessBrideQuotes[quoteIdx.nextInt(princessBrideQuotes.length)];
			}

			write.println(response);
			socket.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public static void handleUdpRequest(DatagramSocket socket, DatagramPacket packet) {
		Random isFavoriate = new Random();
		Random quoteIdx = new Random();

		try {
			System.out.println("Incoming UDP Client: " + packet.getAddress().getHostAddress());

			String response;
			if(isFavoriate.nextBoolean()) {
				response = myFavoriateQuotes[quoteIdx.nextInt(myFavoriateQuotes.length)];
			} else {
				response = princessBrideQuotes[quoteIdx.nextInt(princessBrideQuotes.length)];
			}

			DatagramPacket responsePacket = new DatagramPacket(response.getBytes(StandardCharsets.UTF_8), response.getBytes(StandardCharsets.UTF_8).length, packet.getAddress(), packet.getPort());
			socket.send(responsePacket);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
