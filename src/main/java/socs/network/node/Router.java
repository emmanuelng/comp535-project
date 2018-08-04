package socs.network.node;

import java.util.Arrays;

import socs.network.graph.Path;
import socs.network.handlers.HelloHandler;
import socs.network.handlers.LsaUpdateHandler;
import socs.network.message.LinkDescription;
import socs.network.packets.PacketFactory;
import socs.network.packets.PacketListener;
import socs.network.packets.PacketSender;
import socs.network.util.Configuration;
import socs.network.util.Log;

public class Router extends PacketSender {

	private RouterDescription aRd;
	private LinkStateDatabase aLsd;
	private Link[] aPorts;
	private RouterDescription[] aNeighbors;

	private PacketListener aPacketListener;
	private Log aLog;
	private HeartBeatManager aHeartBeatManager;

	public Router(Configuration pConfig) {
		String simulatedIp = pConfig.getString("socs.network.router.ip");

		aRd = new RouterDescription("localhost", -1, simulatedIp);
		aLsd = new LinkStateDatabase(this);
		aPorts = new Link[4];
		aNeighbors = new RouterDescription[4];
		aPacketListener = null;
		aLog = new Log();
		aHeartBeatManager = new HeartBeatManager(this, aLsd);
	}

	public int start() {
		if (aPacketListener == null) {
			// Initialize the packet listener
			aPacketListener = new PacketListener();
			aPacketListener.register(PacketFactory.HELLO, new HelloHandler(this, aLsd, aLog));
			aPacketListener.register(PacketFactory.LSUPDATE, new LsaUpdateHandler(this, aLsd, aLog));

			// Start the packet listener
			int processPort = aPacketListener.start();
			aRd.setProcessPort(processPort);

			// Broadcast a HELLO message
			for (RouterDescription dst : neighbors()) {
				if (dst != null)
					sendPacket(PacketFactory.hello(this, dst), dst);
			}

			return processPort;
		}

		return -1;
	}

	public synchronized int attach(String pProcessIP, int pProcessPort, String pSimulatedIp, int pWeight) {
		if (!pSimulatedIp.equals(aRd.getSimulatedIp())) {
			RouterDescription rd = new RouterDescription(pProcessIP, pProcessPort, pSimulatedIp);

			for (int i = 0; i < aPorts.length; i++) {
				if (aNeighbors[i] == null) {
					aPorts[i] = new Link(aRd, rd);
					aNeighbors[i] = rd;
					if (aHeartBeatManager.watch(i)) {
						aLsd.link(pSimulatedIp, i, pWeight);
						return i;
					} else {
						aPorts[i] = null;
						aNeighbors[i] = null;
						break;
					}
				} else if (aNeighbors[i].equals(rd)) {
					return i;
				}
			}
		}

		return -1;
	}

	public int connect(String pProcessIP, int pProcessPort, String pSimulatedIp, int pWeight) {
		if (isStarted()) {
			int port = attach(pProcessIP, pProcessPort, pSimulatedIp, pWeight);
			if (port >= 0) {
				RouterDescription rd = aNeighbors[port];
				sendPacket(PacketFactory.hello(this, rd), rd);
			}
			return port;
		}

		return -1;
	}

	public boolean detach(int pPort) {
		if (pPort >= 0 && pPort < aPorts.length) {
			aHeartBeatManager.unwatch(pPort);
			aLsd.unlink(pPort);
			aPorts[pPort] = null;
			aNeighbors[pPort] = null;
			aLsd.clean();
			return true;
		}

		return false;
	}

	public boolean disconnect(int pPort) {
		if (pPort >= 0 && pPort < aPorts.length) {
			RouterDescription rd = aNeighbors[pPort];
			if (rd != null && detach(pPort)) {
				sendPacket(PacketFactory.lsaupdate(this, rd, aLsd.values()), rd);
				synchronize();
				return true;
			}
		}

		return false;
	}

	public RouterDescription[] neighbors() {
		return Arrays.copyOf(aNeighbors, aNeighbors.length);
	}

	public Path<String, LinkDescription> detect(String pDestinationIP) {
		return aLsd.getShortestPath(pDestinationIP);
	}

	public void quit() {
		aHeartBeatManager.stop();
		aPacketListener.stop();
		System.exit(0);
	}

	public String log() {
		return aLog.toString();
	}

	public RouterDescription description() {
		return new RouterDescription(aRd);
	}

	public LinkStateDatabase lsd() {
		return new LinkStateDatabase(aLsd);
	}

	public boolean isStarted() {
		return aPacketListener != null;
	}

	public synchronized void synchronize() {
		for (RouterDescription dst : aNeighbors) {
			if (dst != null)
				sendPacket(PacketFactory.lsaupdate(this, dst, aLsd.values()), dst);
		}
	}
}
