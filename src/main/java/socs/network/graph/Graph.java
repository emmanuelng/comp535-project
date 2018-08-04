package socs.network.graph;

import java.util.Set;

public interface Graph<N, E> {

	public Set<N> getNodes();

	public Set<E> getEdges(N pNode);

	public N getChild(N pParent, E pEdge);

	public E getEdge(N pParent, N pChild);

	public int getWeight(E edge);

}
