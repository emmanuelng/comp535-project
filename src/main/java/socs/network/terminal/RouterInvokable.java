package socs.network.terminal;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import socs.network.graph.Path;
import socs.network.message.LinkDescription;
import socs.network.node.Router;
import socs.network.node.RouterDescription;

public class RouterInvokable {

	private Router aRouter;

	public RouterInvokable(Router pRouter) {
		aRouter = pRouter;
	}

	public void start() {
		if (!aRouter.isStarted()) {
			if (aRouter.start() >= 0) {
				System.out.println("Success!\n");
				info();
			} else {
				System.out.println("Failure");
			}
		} else {
			System.out.println("Failure. The router is already started.");
		}
	}

	public void attach(String pProcessIP, int pProcessPort, String pSimulatedIP, int pWeight) {
		if (!aRouter.isStarted()) {
			int port = aRouter.attach(pProcessIP, pProcessPort, pSimulatedIP, pWeight);
			System.out.println(port < 0 ? "Failure" : "Success!");
		} else {
			System.out.println("Failure. Use 'connect' instead.");
		}
	}

	public void connect(String pProcessIP, int pProcessPort, String pSimulatedIP, int pWeight) {
		if (aRouter.isStarted()) {
			int port = aRouter.connect(pProcessIP, pProcessPort, pSimulatedIP, pWeight);
			System.out.println(port < 0 ? "Failure" : "Success!");
		} else {
			System.out.println("Failure. Use 'attach' instead.");
		}
	}

	public void disconnect(int pPort) {
		if (aRouter.isStarted()) {
			boolean success = aRouter.disconnect(pPort);
			System.out.println(success ? "Success!" : "Failure");
		} else {
			System.out.println("Failure. Please start the router first.");
		}
	}

	public void neighbors() {
		RouterDescription[] neighbors = aRouter.neighbors();
		System.out.println("\n  Port\tIP address\tStatus");
		System.out.println("  -----------------------------");
		for (int i = 0; i < neighbors.length; i++) {
			String ip = neighbors[i] != null ? neighbors[i].getSimulatedIp() : "Free      ";
			String status = neighbors[i] != null ? "" + neighbors[i].getStatus() : "N/A";
			System.out.println("  " + i + "\t" + ip + "\t" + status);
		}
	}

	public void detect(String pDestinationIP) {
		Path<String, LinkDescription> path = aRouter.detect(pDestinationIP);
		System.out.println(path != null ? path : "Cannot reach " + pDestinationIP);
	}

	public void quit() {
		System.out.println("Bye!");
		aRouter.quit();
	}

	public void log() {
		System.out.println(aRouter.log());
	}

	public void lsd() {
		System.out.println(aRouter.lsd().toString().replaceAll("\t", "   "));
	}

	public void info() {
		RouterDescription rd = aRouter.description();
		String pip = rd.getProcessIp();
		String ppt = rd.getProcessPort() + "";
		String sip = rd.getSimulatedIp();

		System.out.println("Process IP  : " + pip);
		System.out.println("Process port: " + ppt);
		System.out.println("Simulated IP: " + sip);
	}

	public void attachcmd() {
		RouterDescription rd = aRouter.description();
		String pip = rd.getProcessIp();
		String ppt = rd.getProcessPort() + "";
		String sip = rd.getSimulatedIp();

		String cmd = "attach " + pip + " " + ppt + " " + sip + " [weight]";
		System.out.println(cmd);
		cmdToClipboard(cmd);
	}

	public void connectcmd() {
		RouterDescription rd = aRouter.description();
		String pip = rd.getProcessIp();
		String ppt = rd.getProcessPort() + "";
		String sip = rd.getSimulatedIp();

		String cmd = "connect " + pip + " " + ppt + " " + sip + " [weight]";
		System.out.println(cmd);
		cmdToClipboard(cmd);
	}

	private void cmdToClipboard(String pCmd) {
		StringSelection selection = new StringSelection(pCmd);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		System.out.println("Copied to clipboard!");
	}
}
