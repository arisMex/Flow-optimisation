package maxflow;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.LinkedList;
import java.util.Queue;

public class MaxFlow implements Algorithm {
	private Graph network;
	private String capacityAttribute = "cap";
	private Node source, sink;
	private double totalFlow;
	private Queue<Node> queue = new LinkedList<>();

	public void init(Graph graph)
	{
		this.network = new SingleGraph("network");
		for (Node n : graph) network.addNode(n.getId());
		graph.edges().forEach(e->
		{
			Edge e1 = network.addEdge(e.getId(), e.getSourceNode().getId(), e.getTargetNode().getId(), true);
			e1.setAttribute("cap", e.hasNumber(capacityAttribute) ? e.getNumber(capacityAttribute) : Double.POSITIVE_INFINITY);
			e1.setAttribute("flow", 0);
			Edge e2 = network.addEdge(e.getId() + "_rev", e.getTargetNode().getId(), e.getSourceNode().getId(), true);
			e2.setAttribute("cap", 0);
			e2.setAttribute("flow", 0);
		});
	}

	public Graph getNetwork(){
		return network;
	}

	public void compute() {
		totalFlow = 0;
		bfs();
		while (sink.hasAttribute("arc")) {
			augmentFlow();
			bfs();
		}
	}

	public void setSource(Node n) {
		source = network.getNode(n.getId());
	}

	public void setSink(Node n) {
		sink = network.getNode(n.getId());
	}

	public void setCapacityAttribute(String capacityAttribute) {
		this.capacityAttribute = capacityAttribute;
	}

	public double getFlow() {
		return totalFlow;
	}

	public double getFlow(Edge e) {
		return network.getEdge(e.getId()).getNumber("flow");
	}

	public double getCapacity(Edge e) {
		return network.getEdge(e.getId()).getNumber("cap");
	}

	private void bfs()
	{
		queue.clear();
		queue.add(source);
		for (Node n : network)
			n.removeAttribute("arc");

		while (!queue.isEmpty())
		{
			Node n = queue.remove();
			n.leavingEdges().forEach(e ->
			{
				double cap = e.getNumber("cap");
				double flow = e.getNumber("flow");
				Node neighbor = e.getTargetNode();
				if (neighbor != source && !neighbor.hasAttribute("arc") && cap > flow)
				{
					neighbor.setAttribute("arc", e);
					queue.add(neighbor);
				}
			});
		}
	}

	private void augmentFlow() {
		double df = Double.POSITIVE_INFINITY;
		for (Edge e = (Edge) sink.getAttribute("arc"); e != null; e = (Edge) e.getSourceNode().getAttribute("arc"))
		{
			df = Math.min(df, e.getNumber("cap") - e.getNumber("flow"));
		}
		totalFlow += df;
		for (Edge e = (Edge) sink.getAttribute("arc"); e != null; e = (Edge) e.getSourceNode().getAttribute("arc"))
		{
			e.setAttribute("flow", e.getNumber("flow") + df);
			Edge er = e.getTargetNode().getEdgeToward(e.getSourceNode());
			er.setAttribute("flow", er.getNumber("flow") - df);
		}
	}

}
