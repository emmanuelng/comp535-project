package socs.network.handlers;

import java.util.HashSet;
import java.util.Set;

import socs.network.message.SOSPFPacket;
import socs.network.node.LinkStateDatabase;
import socs.network.node.Router;
import socs.network.node.RouterDescription;
import socs.network.node.RouterStatus;
import socs.network.packets.PacketFactory;
import socs.network.packets.PacketHandler;
import socs.network.util.Log;

public class HelloHandler extends PacketHandler {

	private Router aRouter;
	private LinkStateDatabase aLsd;
	private Log aLog;
	private Set<String> aAddedNeighbors;

	public HelloHandler(Router pRouter, LinkStateDatabase pLsd, Log pLog) {
		aRouter = pRouter;
		aLsd = pLsd;
		aLog = pLog;
		aAddedNeighbors = new HashSet<>();
	}

	@Override
	public void handle(SOSPFPacket pPacket) {
		String senderIp = pPacket.aSrcSimulatedIP;
		RouterDescription neighbor = findNeighbor(senderIp);
		aLog.write("received HELLO from " + senderIp);

		if (neighbor != null) {
			// Set two way
			neighbor.setStatus(RouterStatus.TWO_WAY);
			aLog.write("set " + senderIp + " state to TWO_WAY");

			// Broadcast the Link State Database
			for (RouterDescription dst : aRouter.neighbors()) {
				if (dst != null)
					sendPacket(PacketFactory.lsaupdate(aRouter, dst, aLsd.values()), dst);
			}

			// Send hello back to the sender
			if (!aAddedNeighbors.contains(senderIp))
				sendPacket(PacketFactory.hello(aRouter, neighbor), neighbor);

		} else {
			// Attach the neighbor
			String processIP = pPacket.aSrcProcessIP;
			int processPort = pPacket.aSrcProcessPort;
			int portNum = aRouter.attach(processIP, processPort, senderIp, pPacket.aWeight);
			neighbor = aRouter.neighbors()[portNum];

			aAddedNeighbors.add(senderIp);
			aLog.write("set " + senderIp + " state to INIT");

			if (neighbor != null)
				sendPacket(PacketFactory.hello(aRouter, neighbor), neighbor);
		}
	}

	private RouterDescription findNeighbor(String pSimulatedIp) {
		for (RouterDescription rd : aRouter.neighbors())
			if (rd != null && rd.getSimulatedIp().equals(pSimulatedIp))
				return rd;

		return null;
	}
}
