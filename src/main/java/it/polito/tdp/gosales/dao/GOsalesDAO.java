package it.polito.tdp.gosales.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.gosales.model.Arco;
import it.polito.tdp.gosales.model.DailySale;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;

public class GOsalesDAO {
	
	/**
	 * Metodo per leggere la lista di tutti i rivenditori dal database
	 * @return
	 */

	public List<Retailers> getAllRetailers(){
		String query = "SELECT * from go_retailers";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	
	/**
	 * Metodo per leggere la lista di tutti i prodotti dal database
	 * @return
	 */
	public List<Products> getAllProducts(){
		String query = "SELECT * from go_products";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	
	/**
	 * Metodo per leggere la lista di tutte le vendite nel database
	 * @return
	 */
	public List<DailySale> getAllSales(){
		String query = "SELECT * from go_daily_sales";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
		
	public List<String> nazioni(){
		String sql = "SELECT DISTINCT  g.Country as c "
				+ "FROM go_retailers g";
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("c"));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Retailers> getRetailers(String c ){
		String query = "SELECT g.* "
				+ "FROM go_retailers g "
				+ "WHERE g.Country = ?";
		List<Retailers> result = new ArrayList<Retailers>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, c);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Retailers(rs.getInt("Retailer_code"), 
						rs.getString("Retailer_name"),
						rs.getString("Type"), 
						rs.getString("Country")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Arco> getArchi(int anno, String c, int min, Map<Integer, Retailers>mappa){
		String sql = "SELECT g1.Retailer_code as r1, g2.Retailer_code as r2,  COUNT(DISTINCT gs2.Product_number) AS peso "
				+ "FROM go_retailers g1, go_retailers g2, go_daily_sales gs1, go_daily_sales gs2 "
				+ "WHERE g1.Country = ? AND g2.Country = ? AND g1.Retailer_code > g2.Retailer_code "
				+ "		AND year(gs1.Date) = ? AND YEAR(gs2.Date) = ? "
				+ "		AND g1.Retailer_code = gs1.Retailer_code AND gs2.Retailer_code = g2.Retailer_code AND gs1.Product_number = gs2.Product_number "
				+ "GROUP BY g1.Retailer_code, g2.Retailer_code "
				+ "HAVING peso >= ?";
		
		List<Arco> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, c);
			st.setString(2, c);
			st.setInt(3, anno);
			st.setInt(4, anno);
			st.setInt(5, min);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Arco(mappa.get(rs.getInt("r1")), mappa.get(rs.getInt("r2")), rs.getInt("peso")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Products> getProducts(Retailers r ){
		String query = "SELECT distinct g2.* "
				+ "FROM go_daily_sales g1, go_products g2 "
				+ "WHERE g1.Product_number = g2.Product_number AND g1.Retailer_code = ? ";
		List<Products> result = new ArrayList<Products>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Products(rs.getInt("Product_number"), 
						rs.getString("Product_line"), 
						rs.getString("Product_type"), 
						rs.getString("Product"), 
						rs.getString("Product_brand"), 
						rs.getString("Product_color"),
						rs.getDouble("Unit_cost"), 
						rs.getDouble("Unit_price")));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<DailySale> getD(Products p, Retailers r, int anno){
		String query = "SELECT DISTINCT g1.* "
				+ "FROM go_daily_sales g1 "
				+ "WHERE  g1.Retailer_code = ? AND YEAR(g1.Date) = ? AND g1.Product_number= ? ";
		List<DailySale> result = new ArrayList<DailySale>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			st.setInt(2, anno);
			st.setInt(3, p.getNumber());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new DailySale(rs.getInt("retailer_code"),
				rs.getInt("product_number"),
				rs.getInt("order_method_code"),
				rs.getTimestamp("date").toLocalDateTime().toLocalDate(),
				rs.getInt("quantity"),
				rs.getDouble("unit_price"),
				rs.getDouble("unit_sale_price")  ));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	public int  getQ(Products p, Retailers r, int anno){
		String query = "SELECT SUM(g1.Quantity) AS somma "
				+ "FROM go_daily_sales g1 "
				+ "WHERE  g1.Retailer_code = ? AND YEAR(g1.Date) = ? AND g1.Product_number= ? ";
		int  result = 0;

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			st.setInt(2, anno);
			st.setInt(3, p.getNumber());
			ResultSet rs = st.executeQuery();
			
			rs.next();
			result = rs.getInt("somma");

			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	public int  getCosto(Products p, Retailers r, int anno){
		String query = "SELECT  distinct g1.Unit_price as costo "
				+ "FROM go_daily_sales g1 "
				+ "WHERE  g1.Retailer_code = ? AND YEAR(g1.Date) = ? AND g1.Product_number= ? ";
		int  result = 0;

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, r.getCode());
			st.setInt(2, anno);
			st.setInt(3, p.getNumber());
			ResultSet rs = st.executeQuery();
			
			rs.next();
			result = rs.getInt("costo");

			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public int  getPrezzo(Products p){
		String query = "SELECT  distinct g1.Unit_cost as costo "
				+ "FROM go_products g1 "
				+ "WHERE  g1.Product_number= ? ";
		int  result = 0;

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			
			st.setInt(1, p.getNumber());
			ResultSet rs = st.executeQuery();
			
			rs.next();
			result = rs.getInt("costo");

			
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	
	
	
}
