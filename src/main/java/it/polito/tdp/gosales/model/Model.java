package it.polito.tdp.gosales.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.gosales.dao.GOsalesDAO;

public class Model {
	private GOsalesDAO dao;
	private List<String> nazioni;
	private Map<Integer, Retailers> mappa;
	private Graph<Retailers, DefaultWeightedEdge> grafo;
	private List<Arco> archi;
	
	public Model() {
		super();
		this.dao = new GOsalesDAO();
		this.nazioni = dao.nazioni();
		this.mappa = new HashMap<>();
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.archi = new ArrayList<>();
	}
	
	public List<String> getNazioni(){
		return nazioni;
	}
	
	public void creaGrafo(int anno, String c, int min) {
		List<Retailers> vertici = dao.getRetailers(c);
		Graphs.addAllVertices(this.grafo, vertici);
		for (Retailers r: dao.getAllRetailers()) {
			mappa.put(r.getCode(), r);
		}
		 archi = dao.getArchi(anno, c, min, mappa);
		for (Arco a : archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getR1(), a.getR2(), a.getPeso());
		}
	}
	public int getV() {
		return this.grafo.vertexSet().size();
	}
	public int getA() {
		return this.grafo.edgeSet().size();
	}
	public Set<Retailers> getVertici(){
		return this.grafo.vertexSet();
	}
	public List<Arco> getArchi(){
		Collections.sort(archi);
		return this.archi;
	}
	
	
	public Connessione connessione(Retailers r) {
		ConnectivityInspector<Retailers, DefaultWeightedEdge> ci = new ConnectivityInspector<>(grafo);
		Set<Retailers> connessi =ci.connectedSetOf(r);
		int peso=0;
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if (connessi.contains(this.grafo.getEdgeSource(e)) && connessi.contains(this.grafo.getEdgeTarget(e))){
				peso += this.grafo.getEdgeWeight(e);
			}
		}
		Connessione c = new Connessione(connessi.size(), peso);
		return c;
	}
	
	
	
	
	
}
