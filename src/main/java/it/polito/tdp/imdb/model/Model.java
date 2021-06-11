package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	private ImdbDAO dao;
	private List<Director> vertici;
	private Map<Integer, Director> idMap;
	private Graph<Director, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	public String creaGrafo(Integer anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Director, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//vertici
		this.vertici = new ArrayList<Director>(this.dao.getDirectorsByYear(anno));
		Graphs.addAllVertices(this.grafo, this.vertici);
		this.idMap = new HashMap<Integer, Director>();
		for(Director d : this.vertici)
			this.idMap.put(d.getId(), d);
		
		//archi
		List<Adiacenza> archi = this.dao.getAdiacenze(anno, idMap);
		for(Adiacenza arco : archi) {
			Graphs.addEdge(this.grafo, arco.getD1(), arco.getD2(), arco.getPeso());
		}
		
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}

	public List<Director> getVertici() {
		return vertici;
	}

	public Graph<Director, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public List<Adiacenza> getAdiacenti(Director d) {
		Set<DefaultWeightedEdge> archiVicini = this.grafo.edgesOf(d);
		List<Adiacenza> vicini = new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge edge : archiVicini) {
			vicini.add(new Adiacenza(d, Graphs.getOppositeVertex(this.grafo, edge, d), this.grafo.getEdgeWeight(edge)));
		}
		Collections.sort(vicini);
		return vicini;
	}
}
