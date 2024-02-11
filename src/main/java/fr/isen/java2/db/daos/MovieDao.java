package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> listOfMovies = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(Statement statement = connection.createStatement()){
				try(ResultSet results = statement.executeQuery("select * from movie JOIN genre ON movie.genre_id = genre.idgenre")){
					while(results.next()) {
						Movie movie = new Movie(results.getInt("idmovie"), 
								results.getString("title"),
								results.getDate("release_date").toLocalDate(),
								new Genre(results.getInt("genre_id"),results.getString("name")),
								results.getInt("duration"),
								results.getString("director"),
								results.getString("summary"));
						listOfMovies.add(movie);
					}
				}
			}
			return listOfMovies;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> listOfMoviesByGenre = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")){
				statement.setString(1, genreName);
				try(ResultSet results = statement.executeQuery()){
					while(results.next()) {
						
						Movie movie = new Movie(results.getInt("idmovie"), 
								results.getString("title"),
								results.getDate("release_date").toLocalDate(),
								new Genre(results.getInt("genre_id"),results.getString("name")),
								results.getInt("duration"),
								results.getString("director"),
								results.getString("summary"));
						listOfMoviesByGenre.add(movie);
					}
				}
			}
			return listOfMoviesByGenre;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public Movie addMovie(Movie movie) {
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary)"+"VALUES(?,?,?,?,?,?)";
			try(PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)){
				statement.setString(1, movie.getTitle());
				statement.setDate(2, java.sql.Date.valueOf(movie.getReleaseDate()));
				statement.setInt(3, movie.getGenre().getId());
				statement.setInt(4, movie.getDuration());
				statement.setString(5, movie.getDirector());
				statement.setString(6, movie.getSummary());
				statement.executeUpdate();
	            ResultSet id = statement.getGeneratedKeys();
	            if (id.next()) {
	                return new Movie(id.getInt(1), movie.getTitle(), movie.getReleaseDate(), movie.getGenre(), movie.getDuration(), movie.getDirector(), movie.getSummary());
	            }
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
