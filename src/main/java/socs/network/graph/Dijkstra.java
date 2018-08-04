package socs.network.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Dijkstra<N, E> {

	private Graph<N, E> aGraph;
	private N aSrc;
	private Map<N, N> aParent;
	private Map<N, Integer> aDistance;

	public Dijkstra(Graph<N, E> pGraph, N pSrc) {
		aGraph = pGraph;
		aSrc = pSrc;
		aParent = new HashMap<>();
		aDistance = new HashMap<>();

		init();
	}

	private void init() {
		Set<N> nodes = new HashSet<>(aGraph.getNodes());
		aDistance.put(aSrc, 0);

		N current = aSrc;
		while (!nodes.isEmpty()) {
			if (!nodes.remove(current))
				break;

			for (E edge : aGraph.getEdges(current)) {
				N child = aGraph.getChild(current, edge);

				if (nodes.contains(child)) {
					int dist = aDistance.get(current) + aGraph.getWeight(edge);
					if (!aDistance.containsKey(child) || aDistance.get(child) > dist) {
						aParent.put(child, current);
						aDistance.put(child, dist);
					}
				}
			}

			int minDistance = Integer.MAX_VALUE;
			for (Entry<N, Integer> entry : aDistance.entrySet()) {
				if (entry.getValue() < minDistance && nodes.contains(entry.getKey())) {
					minDistance = entry.getValue();
					current = entry.getKey();
				}
			}
		}
	}

	public Path<N, E> shortestPath(N pDst) {
		if (aDistance.containsKey(pDst)) {
			List<N> path = new ArrayList<>();
			N current = pDst;

			do {
				path.add(current);
			} while ((current = aParent.get(current)) != null);

			Collections.reverse(path);
			return new Path<>(aGraph, path);
		}

		return null;
	}

	public int shortestDistance(N pDst) {
		return aDistance.get(pDst);
	}

	public Set<N> reachableNodes() {
		return new HashSet<>(aDistance.keySet());
	}
}
