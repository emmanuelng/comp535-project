package socs.network.packets;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import socs.network.message.SOSPFPacket;

public class PacketListener {

	private class Channel implements Runnable {

		private Socket aSocket;
		private ObjectInputStream aOis;

		public Channel(Socket pSocket) throws IOException {
			aSocket = pSocket;
			aOis = new ObjectInputStream(pSocket.getInputStream());
		}

		@Override
		public void run() {
			try {
				while (true) {
					Object obj = aOis.readObject();
					if (obj instanceof SOSPFPacket) {
						SOSPFPacket packet = (SOSPFPacket) obj;
						if (aHandlers.containsKey(packet.aSospfType))
							aHandlers.get(packet.aSospfType).handle(packet);
					}
				}
			} catch (EOFException | SocketException e) {
				// Ignore.
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				aChannelThreads.remove(aSocket);
			}
		}
	}

	private ServerSocket aServerSocket;
	private Map<Short, PacketHandler> aHandlers;

	private Thread aServerThread;
	private Map<Socket, Thread> aChannelThreads;

	public PacketListener() {
		aServerSocket = null;
		aHandlers = new HashMap<>();
		aServerThread = null;
		aChannelThreads = new HashMap<>();
	}

	public void register(short pSospfType, PacketHandler pHandler) {
		aHandlers.put(pSospfType, pHandler);
	}

	public int start() {
		if (aServerSocket == null) {
			try {
				aServerSocket = new ServerSocket(0);
				aServerThread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							while (true) {
								Socket socket = aServerSocket.accept();
								Thread thread = new Thread(new Channel(socket));
								aChannelThreads.put(socket, thread);
								thread.start();
							}
						} catch (SocketException e) {
							// Socket was closed. Ignore.
						} catch (NullPointerException e) {
							// The stop() method was called. Ignore.
						} catch (EOFException e) {
							// The connection was closed quickly (happens often
							// during the heart beats). Ignore.
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							aServerThread = null;
						}
					}
				});

				aServerThread.start();
				return aServerSocket.getLocalPort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	public void stop() {
		try {
			aServerSocket.close();
			aServerSocket = null;

			if (aServerThread != null)
				aServerThread.interrupt();

			for (Thread channelThread : aChannelThreads.values())
				channelThread.interrupt();

			aServerThread = null;
			aChannelThreads = new HashMap<>();
		} catch (Exception e) {
			// Ignore
		}
	}
}
