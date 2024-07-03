package it.polito.tdp.gosales.model;

import java.util.Objects;

public class Connessione {
	private int dimensione;
	private int peso;
	public Connessione(int dimensione, int peso) {
		super();
		this.dimensione = dimensione;
		this.peso = peso;
	}
	public int getDimensione() {
		return dimensione;
	}
	public void setDimensione(int dimensione) {
		this.dimensione = dimensione;
	}
	public int getPeso() {
		return peso;
	}
	public void setPeso(int peso) {
		this.peso = peso;
	}
	@Override
	public int hashCode() {
		return Objects.hash(dimensione, peso);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Connessione other = (Connessione) obj;
		return dimensione == other.dimensione && peso == other.peso;
	}
	
	
}
