package maxflow;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphParseException;

import java.io.IOException;
import java.net.URL;

public class Autoroute {


    //une fonction qui génére un graphe d'ecart :)
    public static Graph grapheDEcart(Graph go, Graph g){
        Graph ge = new SingleGraph("graphedecart");

        //insertion des sommets
        for (Node node : g) {
            ge.addNode(node.getId());
            ge.getNode(node.getId()).setAttribute("x", node.getAttribute("x"));
            ge.getNode(node.getId()).setAttribute("y", node.getAttribute("y"));
        }
        // insertions des arêtes
        go.edges().forEach((Edge e) -> {
            String no1 = e.getNode0().getId();
            String no2 = e.getNode1().getId();
            Node n1 = ge.getNode(no1);
            Node n2 = ge.getNode(no2);

            String eName = n1.getId() + "" + n2.getId();
            String eNameR = n2.getId() + "" + n1.getId();

            if (e.getId().equals(eName) ) {
                Number flow = (Number) e.getAttribute("flow");
                Number cap = (Number) e.getAttribute("cap");

                // ajouter les arêtes existantes dans le graphe qui ont un flot non nul
                if (flow.doubleValue() > 0 ) {
                    ge.addEdge(eNameR, n2, n1, true);
                    ge.getEdge(eNameR).setAttribute("flow", flow.intValue());
                    ge.getEdge(eNameR).setAttribute("cap", cap.intValue());

                    //colorier si saturé
                    if (flow.equals(cap)) {
                        ge.getEdge(eNameR).setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                    }
                }

                // ajouter toutes les arêtes oû on peut augmenter le flot
                if (flow.doubleValue() < cap.doubleValue()) {
                            ge.addEdge(eName, n1, n2, true);
                            ge.getEdge(eName).setAttribute("flow", cap.intValue()-flow.intValue());
                            //ge.getEdge(eName).setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                }

            }

        });
        return ge;
    }

    public static void main(String[] args) throws IOException, GraphParseException {

        System.setProperty("org.graphstream.ui", "swing");

        //Lecture du fichier DGS
        Graph graph = new SingleGraph("Graph");
        URL resourceUrl = TestMaxFlow.class.getResource("/data.dgs");
        String path = null;
        if (resourceUrl != null) {
            path = resourceUrl.getFile();
        } else {
            System.out.println("Fichier non trouvé : data.dgs");
        }
        graph.read(path);

        // Ajout des Labels (Noms) sur les Sommets
        for (int i = 0; i < graph.getNodeCount(); i++) {
            Node node = graph.getNode(i);
            node.setAttribute("label", node.getId());
        }



        graph.setAttribute("ui.stylesheet", "node { text-size: 12; text-mode: normal; text-offset: -5, -20; }");

        // Trouver le flot Max
        MaxFlow mf2 = new MaxFlow();
        mf2.setCapacityAttribute("cap");
        mf2.init(graph);
        mf2.setSource(graph.getNode("A"));
        mf2.setSink(graph.getNode("I"));
        mf2.compute();

        System.out.println("Flot max : " + mf2.getFlow());

        // Ajout des labels (flot) sur chaque arête
        graph.edges().forEach((Edge e) -> {
            double flow = mf2.getFlow(e);
            double cap = mf2.getCapacity(e);
            if (flow > 0) e.setAttribute("ui.label", "" + flow);
            // si saturée colorier
            if (cap == flow) e.setAttribute("ui.style", "fill-color: red;");

        });
        //Affichage
        graph.display(false);

        Graph gmf = mf2.getNetwork();

        // Générer le graphe d'écart
        Graph ge = grapheDEcart(gmf, graph);

        //set labels (Noms) sur les Sommets du graphe d'ecart
        for (int i = 0; i < ge.getNodeCount(); i++) {
            Node node = ge.getNode(i);
            node.setAttribute("label", node.getId());
        }
        ge.setAttribute("ui.stylesheet", "node { text-size: 12; text-mode: normal; text-offset: -5, -20; }");

        //pour afficher le graphe d'ecart :
        //ge.display(false);

    }
}