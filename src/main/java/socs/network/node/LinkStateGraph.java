package socs.network.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import socs.network.graph.Graph;
import socs.network.message.LSA;
import socs.network.message.LinkDescription;

public class LinkStateGraph implements Graph<String, LinkDescription> {

	private Map<String, LSA> aLsas;

	public LinkStateGraph(Map<String, LSA> pLsas) {
		aLsas = pLsas;
	}

	public LinkStateGraph(LinkStateGraph other) {
		aLsas = new HashMap<>(other.aLsas);
	}

	@Override
	public Set<String> getNodes() {
		return aLsas.keySet();
	}

	@Override
	public Set<LinkDescription> getEdges(String pNode) {
		return new HashSet<LinkDescription>(aLsas.get(pNode).aLinks);
	}

	@Override
	public String getChild(String parent, LinkDescription pEdge) {
		return pEdge.aLinkId;
	}

	@Override
	public int getWeight(LinkDescription pEdge) {
		return pEdge.aTosMetrics;
	}

	@Override
	public LinkDescription getEdge(String pParent, String pChild) {
		if (!pParent.equals(pChild)) {
			for (LinkDescription ld : aLsas.get(pParent).aLinks)
				if (ld.aLinkId.equals(pChild))
					return ld;
		}

		return null;
	}

}
