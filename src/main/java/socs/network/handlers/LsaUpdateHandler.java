package socs.network.handlers;

import socs.network.message.LSA;
import socs.network.message.SOSPFPacket;
import socs.network.node.LinkStateDatabase;
import socs.network.node.Router;
import socs.network.packets.PacketHandler;
import socs.network.util.Log;

public class LsaUpdateHandler extends PacketHandler {

	private Router aRouter;
	private LinkStateDatabase aLsd;
	private Log aLog;

	public LsaUpdateHandler(Router pRouter, LinkStateDatabase pLsd, Log pLog) {
		aRouter = pRouter;
		aLsd = pLsd;
		aLog = pLog;
	}

	@Override
	public void handle(SOSPFPacket pPacket) {
		String senderIp = pPacket.aDstSimulatedIP;
		aLog.write("received LSAUPDATE from " + senderIp);

		boolean synchronize = false;
		for (LSA lsa : pPacket.aLsaArray) {
			if (aLsd.put(lsa) != null) {
				aLog.write("updated LSA (id=" + lsa.aLinkStateId + ", seq. number=" + lsa.aLsaSeqNumber + ")");
				synchronize = true;
			}
		}

		if (synchronize) {
			aRouter.synchronize();
			aLsd.clean();
		}
	}
}
