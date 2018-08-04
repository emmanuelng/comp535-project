package socs.network.graph;

import java.util.ArrayList;
import java.util.List;

public class Path<N, E> {

	private Graph<N, E> aGraph;
	private List<N> aNodes;
	private List<E> aEdges;

	public Path(Graph<N, E> pGraph, List<N> pNodes) {
		aGraph = pGraph;
		aNodes = new ArrayList<>(pNodes);
		aEdges = new ArrayList<>();

		build();
	}

	@SafeVarargs
	public Path(Graph<N, E> pGraph, N... pNodes) {
		aGraph = pGraph;
		aNodes = new ArrayList<>();
		aEdges = new ArrayList<>();

		for (N node : pNodes)
			aNodes.add(node);

		build();
	}

	private void build() {
		for (int i = 0; i < aNodes.size() - 1; i++) {
			N parent = aNodes.get(i);
			N child = aNodes.get(i + 1);
			E edge = aGraph.getEdge(parent, child);

			if (edge != null)
				aEdges.add(edge);
		}
	}

	@Override
	public String toString() {
		String str = "";

		for (int i = 0; i < aNodes.size(); i++) {
			str += aNodes.get(i).toString();
			if (i < aEdges.size())
				str += "->(" + aGraph.getWeight(aEdges.get(i)) + ") ";
		}

		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Path<?, ?>) {
			Path<?, ?> other = (Path<?, ?>) obj;
			boolean result = true;

			result &= other.aNodes.equals(aNodes);
			result &= other.aEdges.equals(aEdges);

			return result;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int result = 8;

		result = result * 37 + (aNodes == null ? 0 : aNodes.hashCode());
		result = result * 37 + (aEdges == null ? 0 : aEdges.hashCode());

		return result;
	}
}
