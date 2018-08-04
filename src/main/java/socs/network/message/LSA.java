package socs.network.message;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LSA implements Serializable {

	private static final long serialVersionUID = 6249201243412246996L;

	public int aLsaSeqNumber;
	public String aLinkStateId;
	public List<LinkDescription> aLinks;

	public LSA(String pLinkStateId) {
		aLsaSeqNumber = Integer.MIN_VALUE;
		aLinkStateId = pLinkStateId;
		aLinks = new LinkedList<>();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LSA) {
			LSA other = (LSA) obj;
			boolean equals = true;

			equals &= other.aLsaSeqNumber == aLsaSeqNumber;
			equals &= other.aLinkStateId.equals(aLinkStateId);
			equals &= other.aLinks.equals(aLinks);

			return equals;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int result = 9;

		result = result * 37 + aLsaSeqNumber;
		result = result * 37 + (aLinkStateId == null ? 0 : aLinkStateId.hashCode());
		result = result * 37 + (aLinks == null ? 0 : aLinks.hashCode());

		return result;
	}

	@Override
	public String toString() {
		String result = "";

		result += "LSA {\n";
		result += "\taLinkStateId : " + aLinkStateId + "\n";
		result += "\taLsaSeqNumber: " + aLsaSeqNumber + "\n";
		result += "\taLinks       : [\n";

		for (LinkDescription ld : aLinks)
			result += "\t\t" + ld.toString().replaceAll("\n", "\n\t\t") + ",\n";

		result += "\t]\n";
		result += "}";

		return result;
	}
}
