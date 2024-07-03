package it.polito.tdp.gosales.model;

import java.time.LocalDate;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.gosales.dao.GOsalesDAO;
import it.polito.tdp.gosales.model.Event.EventType;

public class Simulator {
	private GOsalesDAO dao;
	private int N;
	private int Q;
	private Retailers r;
	private int avgD;
	private int avgQ;
	private Products p;
	private int anno;
	private int insoddisfatti;
	private double costoTot;
	private double ricavo;
	private double costoP;
	private double costoAcquisto;
	private int dim;
	
	private List<DailySale> vendite;
	
	private Graph<Retailers, DefaultWeightedEdge> grafo;
	
	private double soddifatti;
	private PriorityQueue<Event> queue;

	public Simulator(Graph<Retailers, DefaultWeightedEdge> grafo) {
		super();
		this.grafo = grafo;
		this.dao = new GOsalesDAO();
	}
	
	public void initialize(int N, int Q, Retailers r, Products p, int anno, Connessione c) {
		this.N =N;
		this.Q = Q;
		this.r = r;
		this.p = p;
		this.ricavo =0;
		this.costoTot =0;
		this.costoAcquisto = dao.getPrezzo(p);
		this.dim =c.getDimensione();
		this.costoP = dao.getCosto(p, r, anno);
		this.anno = anno;
		this.vendite = dao.getD(p, r, anno);
		this.avgD = (int)(12*30/vendite.size());
		this.avgQ = dao.getQ(p, r, anno);
		this.queue = new PriorityQueue<Event>();
		
		for (int i =1; i<13; i++) {
			LocalDate date = LocalDate.of(anno, i, 1);
			this.queue.add(new Event(date ,  EventType.rifornimento));
		}
		LocalDate primo = LocalDate.of(anno, 1, 15);
		while(primo.isBefore(LocalDate.of(anno, 12, 31))) {
			this.queue.add(new Event(primo, EventType.vendita));
			primo.plusDays(avgD);
		}
		
	}
	public void run() {
		while(!queue.isEmpty()) {
			Event e = queue.poll();
			processa(e);
		}
	}

	private void processa(Event e) {
		EventType type = e.getType();
		LocalDate date = e.getDate();
		switch (type) {
		case rifornimento:
			double random =Math.random();
			if (random < (0.20+0.1*( this.dim-1)) && (0.20+0.1*( this.dim-1)<0.5) ) {
				double cost =0.8*N;
				this.Q+= cost;
				costoTot += cost*costoAcquisto;
				
			}else {
				this.Q+= N;
				costoAcquisto+= N*costoAcquisto;
			}
			
			
			break;
		case vendita:
			//quantita richiesta non disponibile 
			if (Q<avgQ) {
				if (Q<0.9*avgQ) {
					insoddisfatti++;
				}
				
				ricavo += costoP *Q;
				Q=0;
			}else {
				Q-=avgQ;
				ricavo += costoP* avgQ;
			}
			
			break;
		}
		
	}

}
