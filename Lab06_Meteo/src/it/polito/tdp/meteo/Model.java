package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Citta> citta = new LinkedList<Citta>();
	public Model() {

	}

	public String getUmiditaMedia(int mese) {
		MeteoDAO dao = new MeteoDAO();
		this.citta.clear();
		this.citta = dao.getCitta();
		String s = "";
		for(Citta c : this.citta)
			s+=c.getNome() + " " + dao.getAvgRilevamentiLocalitaMese(mese, c.getNome()) + "\n";
		return s ;
	}

	public String trovaSequenza(int mese) {
		MeteoDAO dao = new MeteoDAO();
		this.citta.clear();
		this.citta=dao.getCitta();
		
		for(Citta c : citta){
			c.setCounter(0);
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		
		List<SimpleCity> parziale = new ArrayList<SimpleCity>();
		List<SimpleCity> best = new ArrayList<SimpleCity>();
		int livello = 1 ;
		double score = Double.MAX_VALUE;
		recursive(parziale,livello,best,score);
		return best.toString() ;
	}

	private void recursive(List<SimpleCity> parziale, int livello, List<SimpleCity> best, double score) {
		if(parziale.size()==15)
			if(this.punteggioSoluzione(parziale)<score){
				best.clear();
				best.addAll(parziale);
				score= this.punteggioSoluzione(best);
				return;
			}
		
		for(Citta c : this.citta)
			for(Rilevamento r : c.getRilevamenti())
				if(r.getData().getDay()==livello){
					parziale.add(new SimpleCity(r.getLocalita(),r.getUmidita()));
					if(this.controllaParziale(parziale)){
						recursive(parziale,livello+1,best,score);
						c.setCounter(c.getCounter() +1);
					}
					parziale.remove(parziale.size()-1);
				}
				
		
	}
	

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		score+=soluzioneCandidata.get(0).getCosto();
		
		for(int i=1 ; i<soluzioneCandidata.size() ; i++){
			score+=soluzioneCandidata.get(i).getCosto();
			if(soluzioneCandidata.get(i).getNome().compareTo(soluzioneCandidata.get(i-1).getNome())!=0)
				score+=100;
		}
		
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		for(Citta c : citta)
			if(c.getCounter()>6)
				return false;
		
		if(parziale.size()<=1)
			return true;
		
		if(!parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2))){
			if(parziale.size()==2 || parziale.size()==3)
				return false ;
			
			if(parziale.size()>3){
				if(!parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
					return false ;
				if(!parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-4)))
					return false ;
			}
		}
		
		return true;
	}

}
