package socs.network.node;

public class RouterDescription {

	private String aProcessIp;
	private int aProcessPort;
	private String aSimulatedIp;
	private RouterStatus aStatus;

	public RouterDescription(String pProcessIP, int pProcessPort, String pSimulatedIP) {
		aProcessIp = pProcessIP;
		aProcessPort = pProcessPort;
		aSimulatedIp = pSimulatedIP;
		aStatus = RouterStatus.INIT;
	}

	public RouterDescription(RouterDescription other) {
		aProcessIp = other.aProcessIp;
		aProcessPort = other.aProcessPort;
		aSimulatedIp = other.aSimulatedIp;
		aStatus = other.aStatus;
	}

	public String getProcessIp() {
		return aProcessIp;
	}

	public int getProcessPort() {
		return aProcessPort;
	}

	public String getSimulatedIp() {
		return aSimulatedIp;
	}

	public RouterStatus getStatus() {
		return aStatus;
	}

	public void setProcessPort(int pProcessPort) {
		aProcessPort = pProcessPort;
	}

	public void setStatus(RouterStatus pStatus) {
		aStatus = pStatus;
	}

	@Override
	public String toString() {
		String result = "";

		result += "RouterDescription{\n";
		result += "\taProcessIp  : " + aProcessIp + "\n";
		result += "\taProcessPort: " + aProcessPort + "\n";
		result += "\taSimulatedIp: " + aSimulatedIp + "\n";
		result += "\taStatus     : " + aStatus + "\n";
		result += "}";

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RouterDescription) {
			RouterDescription other = (RouterDescription) obj;
			boolean result = true;

			result &= other.aProcessIp.equals(aProcessIp);
			result &= other.aProcessPort == aProcessPort;
			result &= other.aSimulatedIp.equals(aSimulatedIp);
			result &= other.aStatus.equals(aStatus);

			return result;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int result = 6;

		result = result * 37 + (aProcessIp == null ? 0 : aProcessIp.hashCode());
		result = result * 37 + aProcessPort;
		result = result * 37 + (aSimulatedIp == null ? 0 : aSimulatedIp.hashCode());
		result = result * 37 + (aStatus == null ? 0 : aStatus.hashCode());

		return result;
	}
}
