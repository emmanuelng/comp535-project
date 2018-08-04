package socs.network.packets;

import java.util.Vector;

import socs.network.message.LSA;
import socs.network.message.SOSPFPacket;
import socs.network.node.Router;
import socs.network.node.RouterDescription;

public class PacketFactory {

	public static final short HELLO = 0;
	public static final short LSUPDATE = 1;

	public static SOSPFPacket hello(Router pSrc, RouterDescription pDst) {
		SOSPFPacket packet = new SOSPFPacket();
		RouterDescription srcDescription = pSrc.description();

		packet.aSospfType = HELLO;
		packet.aRouterID = srcDescription.getProcessIp();

		packet.aSrcProcessIP = srcDescription.getProcessIp();
		packet.aSrcProcessPort = srcDescription.getProcessPort();

		packet.aSrcSimulatedIP = srcDescription.getSimulatedIp();
		packet.aDstSimulatedIP = pDst.getSimulatedIp();

		packet.aWeight = pSrc.lsd().tosMetric(pDst.getSimulatedIp());

		return packet;
	}

	public static SOSPFPacket lsaupdate(Router pSrc, RouterDescription pDst, Vector<LSA> pLsaArray) {
		SOSPFPacket packet = new SOSPFPacket();
		RouterDescription srcDescription = pSrc.description();

		packet.aSospfType = LSUPDATE;
		packet.aRouterID = srcDescription.getProcessIp();

		packet.aSrcProcessIP = srcDescription.getProcessIp();
		packet.aSrcProcessPort = srcDescription.getProcessPort();

		packet.aSrcSimulatedIP = srcDescription.getSimulatedIp();
		packet.aDstSimulatedIP = pDst.getSimulatedIp();

		packet.aLsaArray = pLsaArray;
		return packet;
	}
}
