package socs.network.message;

import java.io.Serializable;
import java.util.Vector;

public class SOSPFPacket implements Serializable {

	private static final long serialVersionUID = 7260418816846728775L;

	public short aSospfType;
	public String aRouterID;

	public String aSrcProcessIP;
	public int aSrcProcessPort;

	public String aSrcSimulatedIP;
	public String aDstSimulatedIP;

	public int aWeight;

	public Vector<LSA> aLsaArray;
}
