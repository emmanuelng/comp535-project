package socs.network.packets;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

import socs.network.message.SOSPFPacket;
import socs.network.node.RouterDescription;

public abstract class PacketSender {

	public synchronized boolean sendPacket(SOSPFPacket pPacket, RouterDescription pDst) {
		try {
			String host = pDst.getProcessIp();
			int port = pDst.getProcessPort();
			Socket socket = new Socket(host, port);

			try {
				OutputStream os = socket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(pPacket);
				oos.close();
			} finally {
				socket.close();
			}

			return true;
		} catch (ConnectException e) {
			// Ignore.
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
