package socs.network.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import socs.network.graph.Dijkstra;
import socs.network.graph.Path;
import socs.network.message.LSA;
import socs.network.message.LinkDescription;

public class LinkStateDatabase {

	private Router aRouter;
	private Map<String, LSA> aStore;

	public LinkStateDatabase(Router pRouter) {
		aStore = new HashMap<String, LSA>();
		aRouter = pRouter;

		// Initialize the link state database by adding an entry about the router itself
		RouterDescription rd = pRouter.description();
		LSA lsa = new LSA(rd.getSimulatedIp());
		lsa.aLinks.add(new LinkDescription(rd.getSimulatedIp(), -1, 0));
		aStore.put(rd.getSimulatedIp(), lsa);
	}

	public LinkStateDatabase(LinkStateDatabase other) {
		aStore = new HashMap<>(other.aStore);
		aRouter = other.aRouter;
	}

	public Path<String, LinkDescription> getShortestPath(String pDestinationIp) {
		RouterDescription rd = aRouter.description();
		LinkStateGraph graph = new LinkStateGraph(aStore);
		Dijkstra<String, LinkDescription> dijkstra = new Dijkstra<>(graph, rd.getSimulatedIp());
		return dijkstra.shortestPath(pDestinationIp);
	}

	public synchronized LSA put(LSA pLsa) {
		if (!aStore.containsKey(pLsa.aLinkStateId)
				|| aStore.get(pLsa.aLinkStateId).aLsaSeqNumber < pLsa.aLsaSeqNumber) {
			// Put the LSA
			aStore.put(pLsa.aLinkStateId, pLsa);

			// Update the router's ports if necessary
			String routerIp = aRouter.description().getSimulatedIp();
			RouterDescription[] neighbors = aRouter.neighbors();
			for (int port = 0; port < neighbors.length; port++) {
				if (neighbors[port] != null && neighbors[port].getSimulatedIp().equals(pLsa.aLinkStateId)) {
					// Check if the neighbor has a link to the router
					for (LinkDescription ld : pLsa.aLinks)
						if (ld.aLinkId.equals(routerIp))
							return pLsa;

					// The neighbor does not have a link anymore
					aRouter.detach(port);
					break;
				}
			}

			return pLsa;
		}

		return null;
	}

	public synchronized void link(String pLinkId, int pPortNum, int pTosMetrics) {
		RouterDescription rd = aRouter.description();
		LSA lsa = aStore.get(rd.getSimulatedIp());
		lsa.aLinks.add(new LinkDescription(pLinkId, pPortNum, pTosMetrics));
		lsa.aLsaSeqNumber++;
	}

	public synchronized void unlink(int pPortNum) {
		RouterDescription rd = aRouter.description();
		LSA lsa = aStore.get(rd.getSimulatedIp());

		for (LinkDescription ld : lsa.aLinks) {
			if (ld.aPortNum == pPortNum) {
				lsa.aLinks.remove(ld);
				lsa.aLsaSeqNumber++;

				LSA neighbor = aStore.get(ld.aLinkId);
				neighbor.aLsaSeqNumber++;

				break;
			}
		}
	}

	public synchronized void clean() {
		Set<String> linkIds = new HashSet<>(aStore.keySet());

		RouterDescription rd = aRouter.description();
		LinkStateGraph graph = new LinkStateGraph(aStore);
		Dijkstra<String, LinkDescription> dijkstra = new Dijkstra<>(graph, rd.getSimulatedIp());
		Set<String> reachableRouters = dijkstra.reachableNodes();

		for (String linkId : linkIds) {
			if (!reachableRouters.contains(linkId))
				aStore.remove(linkId);
		}

	}

	public Vector<LSA> values() {
		return new Vector<>(aStore.values());
	}

	public int tosMetric(String pLinkId) {
		RouterDescription rd = aRouter.description();
		LSA lsa = aStore.get(rd.getSimulatedIp());

		for (LinkDescription ld : lsa.aLinks) {
			if (ld.aLinkId.equals(pLinkId))
				return ld.aTosMetrics;
		}

		return -1;
	}

	@Override
	public String toString() {
		String result = "";

		result += "LinkStateDatabase{\n";
		result += "\taStore: [\n";

		for (LSA lsa : aStore.values())
			result += "\t\t" + lsa.toString().replaceAll("\n", "\n\t\t") + ",\n";

		result += "\t]\n";
		result += "}";

		return result;
	}
}
