package maxflow;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphParseException;

import java.io.IOException;

public class TestMaxFlow {

	public static void main(String[] args) throws IOException, GraphParseException {

        System.setProperty("org.graphstream.ui", "swing");


        Graph g = new SingleGraph("test");
        g.addNode("s");
        g.addNode("1");
        g.addNode("2");
        g.addNode("t");
        g.addEdge("e1", "s", "1", true).setAttribute("cap", 10);
        g.addEdge("e2", "s", "2", true).setAttribute("cap", 10);
        g.addEdge("e3", "1", "2", true).setAttribute("cap", 5);
        g.addEdge("e4", "1", "t", true).setAttribute("cap", 8);
        g.addEdge("e5", "2", "t", true).setAttribute("cap", 7);
        //g.display();

        MaxFlow mf = new MaxFlow();
        mf.setCapacityAttribute("cap");
        mf.init(g);
        mf.setSource(g.getNode("s"));
        mf.setSink(g.getNode("t"));
        mf.compute();

        System.out.println(mf.getFlow());
        g.edges().forEach((Edge e) -> {
            double flow = mf.getFlow(e);
            double cap = mf.getCapacity(e);
            if (flow > 0) e.setAttribute("ui.label", "" + flow);
            if (cap == flow) e.setAttribute("ui.style", "fill-color: red;");
        });

        g.display(false);


    }

}
