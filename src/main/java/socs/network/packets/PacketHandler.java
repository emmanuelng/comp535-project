package socs.network.packets;

import socs.network.message.SOSPFPacket;

public abstract class PacketHandler extends PacketSender {

	public abstract void handle(SOSPFPacket pPacket);
}
