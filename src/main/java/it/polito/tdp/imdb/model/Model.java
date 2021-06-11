package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	private ImdbDAO dao;
	private List<Director> vertici;
	private Graph<Director, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	public String creaGrafo(Integer anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Director, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//vertici
		this.vertici = new ArrayList<Director>(this.dao.getDirectorsByYear(anno));
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
}
