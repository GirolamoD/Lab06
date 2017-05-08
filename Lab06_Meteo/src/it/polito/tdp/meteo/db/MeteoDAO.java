package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.*;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		final String sql = "SELECT Localita, Data, Umidita FROM situazione WHERE `Data`>= ? AND `Data`<=? AND Localita=? ORDER BY `Data` ASC ";
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>() ;
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			//Inserire il settaggio delle date per completare la stringa SQL 
			st.setDate(1, Date.valueOf("2013-"+mese+"-01"));
			st.setDate(2, Date.valueOf("2013-"+mese+"-15"));
			st.setString(3, localita);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public List<Rilevamento> getRilevamentiMese(int mese){
		final String sql = "SELECT Localita, Data, Umidita FROM situazione WHERE `Data`>= ? AND `Data`<=?";
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>() ;
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			//Inserire il settaggio delle date per completare la stringa SQL 
			st.setDate(1, Date.valueOf("2013-"+mese+"-01"));
			st.setDate(2, Date.valueOf("2013-"+mese+"-31"));
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public List<Citta> getCitta(){
		final String sql = "SELECT DISTINCT Localita FROM situazione";
		List<Citta> citta = new ArrayList<Citta>() ;
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Citta c = new Citta(rs.getString("Localita"));
				citta.add(c);
			}

			conn.close();
			return citta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {
		
		final String sql = "SELECT AVG(Umidita) AS Result FROM situazione "+
							"WHERE Localita=? AND `Data`>=? AND `Data`<=?";
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setDate(2, Date.valueOf("2013-"+mese+"-01"));
			st.setDate(3, Date.valueOf("2013-"+mese+"-31"));

			ResultSet rs = st.executeQuery();

			rs.next();
			return rs.getDouble("Result");

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
