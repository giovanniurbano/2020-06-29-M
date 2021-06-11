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
	
	private List<Director> migliore;
	private int totACBest;
	
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
	
	public List<Director> getRegistiAffini(Director d, int c) {
		this.migliore = new ArrayList<Director>();
		this.totACBest = 0;
		int totAttoriCondivisi = 0;
		
		List<Director> parziale = new ArrayList<Director>();
		parziale.add(d);
		
		this.cerca(parziale, c, 1,totAttoriCondivisi);
		
		return migliore;
	}

	private void cerca(List<Director> parziale, int c, int L, int totAttoriCondivisi) {
		//casi terminali
		if(totAttoriCondivisi > c) {
			//soglia superata
			return;
		}
		else {
			//sequenza accettabile
			if(parziale.size() > this.migliore.size()) {
				this.migliore = new ArrayList<Director>(parziale);
				this.totACBest = totAttoriCondivisi;
			}
		}
		
		if(L == this.grafo.vertexSet().size())
			return;
		
		for(Adiacenza a : this.getAdiacenti(parziale.get(parziale.size()-1))) {
			if(a.getPeso() <= c && !parziale.contains(a.getD2())) {
				parziale.add(a.getD2());
				totAttoriCondivisi += a.getPeso();
				
				this.cerca(parziale, c, L+1, totAttoriCondivisi);
				
				//backtracking
				parziale.remove(parziale.size()-1);
				totAttoriCondivisi -= a.getPeso();
			}
		}
		
	}

	public int getTotAttoriCondivisi() {
		return totACBest;
	}
	
}
