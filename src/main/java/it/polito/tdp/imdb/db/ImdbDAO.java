package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Adiacenza> getAdiacenze(Integer anno, Map<Integer, Director> idMap) {
		String sql = "SELECT d1.id AS id1, d2.id AS id2, COUNT(*) AS peso "
				+ "FROM directors d1, directors d2, movies m1, movies m2, movies_directors md1, movies_directors md2, roles r1, roles r2 "
				+ "WHERE d1.id = md1.director_id "
				+ "AND d2.id = md2.director_id "
				+ "AND d1.id > d2.id "
				+ "AND r1.actor_id = r2.actor_id "
				+ "AND ( (md1.movie_id = m1.id AND md2.movie_id = m2.id AND md1.movie_id = md2.movie_id AND r1.movie_id = r2.movie_id AND r1.movie_id = m1.id AND r2.movie_id = m2.id) "
				+ "OR (md1.movie_id <> md2.movie_id AND md1.movie_id = m1.id AND md2.movie_id = m2.id AND md1.movie_id = r1.movie_id AND md2.movie_id = r2.movie_id) ) "
				+ "AND m1.year = m2.year AND m1.year = ? "
				+ "GROUP BY d1.id, d2.id "
				+ "HAVING peso > 0";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Director d1 = idMap.get(res.getInt("id1"));
				Director d2 = idMap.get(res.getInt("id2"));
				
				if(d1 != null && d2 != null && !d1.equals(d2)) {
					Adiacenza a = new Adiacenza(d1, d2, res.getDouble("peso"));
					result.add(a);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Director> getDirectorsByYear(Integer year) {
		String sql = "SELECT d.id, d.first_name, d.last_name "
				+ "FROM directors d, movies m, movies_directors md "
				+ "WHERE d.id = md.director_id "
				+ "AND md.movie_id = m.id "
				+ "AND m.year = ? "
				+ "GROUP BY d.id, d.first_name, d.last_name "
				+ "ORDER BY d.last_name";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
}
