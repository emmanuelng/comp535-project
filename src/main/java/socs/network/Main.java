package socs.network;

import socs.network.node.Router;
import socs.network.terminal.RouterInvokable;
import socs.network.terminal.Terminal;
import socs.network.util.Configuration;

public class Main {

	public static void main(String[] args) {
		if (args.length == 1) {
			Configuration config = new Configuration(args[0]);
			Router router = new Router(config);
			RouterInvokable routerInvokable = new RouterInvokable(router);
			new Terminal(routerInvokable, ">>").start();
		} else {
			System.out.println("usage: program conf_path");
			System.exit(1);
		}
	}
}
