package socs.network.message;

import java.io.Serializable;

public class LinkDescription implements Serializable {

	private static final long serialVersionUID = -1062246419657521313L;

	public String aLinkId;
	public int aPortNum;
	public int aTosMetrics;

	public LinkDescription(String pLinkId, int pPortNum, int pTosMetrics) {
		aLinkId = pLinkId;
		aPortNum = pPortNum;
		aTosMetrics = pTosMetrics;
	}

	@Override
	public String toString() {
		String result = "";

		result += "LinkDescription {\n";
		result += "\taLinkId     : " + aLinkId + "\n";
		result += "\taPortNum    : " + aPortNum + "\n";
		result += "\taTosMetrics : " + aTosMetrics + "\n";
		result += "}";

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LinkDescription) {
			LinkDescription other = (LinkDescription) obj;
			boolean equals = true;

			equals &= other.aLinkId.equals(aLinkId);
			equals &= other.aPortNum == aPortNum;
			equals &= other.aTosMetrics == aTosMetrics;

			return equals;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int result = 8;

		result = result * 37 + (aLinkId == null ? 0 : aLinkId.hashCode());
		result = result * 37 + aPortNum;
		result = result * 37 + aTosMetrics;

		return result;
	}
}
