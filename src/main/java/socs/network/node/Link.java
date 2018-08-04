package socs.network.node;

public class Link {

	private RouterDescription aRouter1;
	private RouterDescription aRouter2;

	public Link(RouterDescription pRouter1, RouterDescription pRouter2) {
		aRouter1 = pRouter1;
		aRouter2 = pRouter2;
	}

	public RouterDescription getRouter1() {
		return aRouter1;
	}

	public RouterDescription getRouter2() {
		return aRouter2;
	}
}
