package hr.projekt.app.ProjektJavaApp.Utils;

import hr.projekt.app.ProjektJavaApp.Enum.MediaType;
import hr.projekt.app.ProjektJavaApp.Genericsi.Review;
import hr.projekt.app.ProjektJavaApp.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "config/databaseProperties";

    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties svojstva = new Properties();
        svojstva.load(new FileReader(DATABASE_FILE));
        String urlBazePodataka = svojstva.getProperty("databaseUrl");
        String korisnickoIme = svojstva.getProperty("username");
        String lozinka = svojstva.getProperty("password");
        Connection veza = DriverManager.getConnection(urlBazePodataka,
                korisnickoIme, lozinka);
        return veza;
    }

    public static List<Genre> getGenres() {

        List<Genre> genreList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM GENRE";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String name = rs.getString("NAME");
                String description = rs.getString("DESCRIPTION");

                Genre newGenre = new Genre(id, name, description);

                genreList.add(newGenre);
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return genreList;
    }

    public static List<Genre> getGenreFiltered(String name, String description) {
        List<Genre> genreList = new ArrayList<>();

        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM GENRE WHERE 1=1 ";

            if (!name.isEmpty()) {
                baseSqlQuery += " AND NAME = ?";
                queryParams.put(paramOrdinalNumber, name);
                paramOrdinalNumber++;
            }

            if (!description.isEmpty()) {
                baseSqlQuery += " AND DESCRIPTION = ?";
                queryParams.put(paramOrdinalNumber, description);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String genreName = rs.getString("NAME");
                String genreDescription = rs.getString("DESCRIPTION");

                Genre newGenre = new Genre(id, genreName, genreDescription);

                genreList.add(newGenre);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return genreList;
    }

    public static void saveGenre(List<Genre> genreList) {
        try (Connection connection = connectToDatabase()) {
            for (Genre newGenre : genreList) {
                String sql = "INSERT INTO GENRE(NAME, DESCRIPTION) VALUES(?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, newGenre.getName());
                pstmt.setString(2, newGenre.getDescription());
                pstmt.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja podataka u bazu!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void deleteGenres(Genre genre) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        deleteMoviesWithWantedGenre(genre, queryParams, paramOrdinalNumber);

        deleteSeriesWithWantedGenre(genre, queryParams, paramOrdinalNumber);

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM GENRE WHERE ID = ?";

            if (genre != null) {
                Integer id = genre.getId();
                queryParams.put(paramOrdinalNumber, id);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

            Map<Integer, Object> queryParams2 = new HashMap<>();
            Integer paramOrdinalNumber2 = 1;

            String sqlQuery = "ALTER TABLE GENRE\n" +
                    "ALTER COLUMN ID RESTART WITH ?;";
            List<Genre> genres = DatabaseUtils.getGenres();

            if (genres.isEmpty()) {
                queryParams2.put(paramOrdinalNumber2, 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            } else {
                Genre genreTemp = genres.get(genres.size() - 1);
                queryParams2.put(paramOrdinalNumber2, genreTemp.getId() + 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    private static void deleteSeriesWithWantedGenre(Genre genre, Map<Integer, Object> queryParams, Integer paramOrdinalNumber) {
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM SERIES WHERE GENRE_ID = ?";

            if (genre != null) {
                Integer id = genre.getId();
                queryParams.put(paramOrdinalNumber, id);

            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

            Map<Integer, Object> queryParams2 = new HashMap<>();
            Integer paramOrdinalNumber2 = 1;

            String sqlQuery = "ALTER TABLE SERIES\n" +
                    "ALTER COLUMN SERIES_ID RESTART WITH ?;";
            List<Series> seriesList = getSeries();
            if (seriesList.isEmpty()) {
                queryParams2.put(paramOrdinalNumber2, 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            } else {
                Series seriesTemp = seriesList.get(seriesList.size() - 1);
                queryParams2.put(paramOrdinalNumber2, seriesTemp.getId() + 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    private static void deleteMoviesWithWantedGenre(Genre genre, Map<Integer, Object> queryParams, Integer paramOrdinalNumber) {
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM MOVIE WHERE GENRE_ID = ?";

            if (genre != null) {
                Integer id = genre.getId();
                queryParams.put(paramOrdinalNumber, id);
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

            Map<Integer, Object> queryParams2 = new HashMap<>();
            Integer paramOrdinalNumber2 = 1;

            String sqlQuery = "ALTER TABLE MOVIE\n" +
                    "ALTER COLUMN MOVIE_ID RESTART WITH ?;";
            List<Movie> movies = getMovies();

            if (movies.isEmpty()) {
                queryParams2.put(paramOrdinalNumber2, 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            } else {
                Movie movieTemp = movies.get(movies.size() - 1);
                queryParams2.put(paramOrdinalNumber2, movieTemp.getId() + 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void editGenre(Genre genre, String chosenInfo, String newInfo) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "UPDATE GENRE SET " + chosenInfo + " = ? WHERE ID = ?";

            if (!newInfo.isEmpty()) {
                queryParams.put(paramOrdinalNumber, newInfo);
                paramOrdinalNumber++;
            }

            if (genre != null) {
                Integer id = genre.getId();
                queryParams.put(paramOrdinalNumber, id);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<Movie> getMovies() {
        List<Genre> genreList = DatabaseUtils.getGenres();
        List<Movie> movieList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MOVIE";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt("MOVIE_ID");
                Integer genreId = rs.getInt("GENRE_ID");
                String name = rs.getString("NAME");
                Integer length = rs.getInt("LENGTH");
                BigDecimal rating = rs.getBigDecimal("RATING");
                Integer releaseYear = rs.getInt("RELEASE_YEAR");
                BigDecimal tempRevenue = rs.getBigDecimal("MOVIE_REVENUE");
                String tempActor = rs.getString("ACTOR");

                String[] names = tempActor.split(" ");
                String actorName = names[0];
                String actorSurname = names[1];

                Genre genre = genreList.stream().filter(genre1 -> genre1.getId().equals(genreId)).findFirst().get();
                //Genre genre = genreList.get(genreId-1);
                Revenue revenue = new Revenue(tempRevenue);
                Actor actor = new Actor.Builder().name(actorName).surname(actorSurname).build();

                Movie newMovie = new Movie(id, name, length, genre, rating, releaseYear, revenue, actor);

                movieList.add(newMovie);
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return movieList;
    }

    public static List<Movie> getMovieFiltered(String name, Integer genreId) {
        List<Movie> movieList = new ArrayList<>();
        List<Genre> genreList = DatabaseUtils.getGenres();

        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM MOVIE WHERE 1=1 ";

            if (!name.isEmpty()) {
                baseSqlQuery += " AND NAME = ?";
                queryParams.put(paramOrdinalNumber, name);
                paramOrdinalNumber++;
            }

            if (genreId > 0) {
                baseSqlQuery += " AND GENRE_ID = ?";
                queryParams.put(paramOrdinalNumber, genreId);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            while (rs.next()) {
                Integer id = rs.getInt("MOVIE_ID");
                Integer genre_Id = rs.getInt("GENRE_ID");
                String movieName = rs.getString("NAME");
                Integer length = rs.getInt("LENGTH");
                BigDecimal rating = rs.getBigDecimal("RATING");
                Integer releaseYear = rs.getInt("RELEASE_YEAR");
                BigDecimal tempRevenue = rs.getBigDecimal("MOVIE_REVENUE");
                String tempActor = rs.getString("ACTOR");

                String[] names = tempActor.split(" ");
                String actorName = names[0];
                String actorSurname = names[1];

                Genre genre = genreList.stream().filter(genre1 -> genre1.getId().equals(genre_Id)).findFirst().get();
                Revenue revenue = new Revenue(tempRevenue);
                Actor actor = new Actor.Builder().name(actorName).surname(actorSurname).build();

                Movie newMovie = new Movie(id, movieName, length, genre, rating, releaseYear, revenue, actor);

                movieList.add(newMovie);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return movieList;
    }

    public static void saveMovie(List<Movie> movieList) {
        List<Genre> genreList = getGenres();
        try (Connection connection = connectToDatabase()) {
            for (Movie movie : movieList) {
                String sql = "INSERT INTO MOVIE(GENRE_ID, NAME, LENGTH, RATING, RELEASE_YEAR, MOVIE_REVENUE, ACTOR) VALUES(?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                //Integer genreId = genreList.indexOf(movie.getMovieGenre());
                Genre genre = movie.getMovieGenre();
                pstmt.setInt(1, genre.getId());
                pstmt.setString(2, movie.getName());
                pstmt.setInt(3, movie.returnMovieLength());
                pstmt.setBigDecimal(4, movie.getRating());
                pstmt.setInt(5, movie.releaseYear());
                BigDecimal movieRevenue = movie.getMovieRevenue().moneyRevenue();
                pstmt.setBigDecimal(6, movieRevenue);
                Actor actor = movie.getActor();
                String name = actor.getName();
                String surname = actor.getSurname();
                String fullName = name + " " + surname;

                pstmt.setString(7, fullName);
                pstmt.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja podataka u bazu!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void deleteMovies(Movie movie) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        deleteReviewOfADeletedMovie(movie, queryParams, paramOrdinalNumber);

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM MOVIE WHERE MOVIE_ID = ?";

            if (movie != null) {
                Integer id = movie.getId();
                queryParams.put(paramOrdinalNumber, id);
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

            Map<Integer, Object> queryParams2 = new HashMap<>();
            Integer paramOrdinalNumber2 = 1;

            String sqlQuery = "ALTER TABLE MOVIE\n" +
                    "ALTER COLUMN MOVIE_ID RESTART WITH ?;";
            List<Movie> movies = getMovies();

            if (movies.isEmpty()) {
                queryParams2.put(paramOrdinalNumber2, 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            } else {
                Movie movieTemp = movies.get(movies.size() - 1);
                queryParams2.put(paramOrdinalNumber2, movieTemp.getId() + 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    private static void deleteReviewOfADeletedMovie(Movie movie, Map<Integer, Object> queryParams, Integer paramOrdinalNumber) {
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM REVIEW WHERE REVIEWED_MEDIA = ?";

            if (movie != null) {
                String movieString = movie.getTitle();
                queryParams.put(paramOrdinalNumber, movieString);

            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void editMovie(Movie movie, String chosenInfo, String newInfo) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "UPDATE MOVIE SET " + chosenInfo + " = ? WHERE MOVIE_ID = ?";

            if (!newInfo.isEmpty()) {
                queryParams.put(paramOrdinalNumber, newInfo);
                paramOrdinalNumber++;
            }

            if (movie != null) {
                Integer id = movie.getId();
                queryParams.put(paramOrdinalNumber, id);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                } else if (queryParams.get(paramNumber) instanceof BigDecimal bqp) {
                    pstmt.setBigDecimal(paramNumber, bqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<Series> getSeries() {
        List<Genre> genreList = DatabaseUtils.getGenres();
        List<Series> seriesList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM SERIES";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt("SERIES_ID");
                Integer genreId = rs.getInt("GENRE_ID");
                String name = rs.getString("NAME");
                BigDecimal rating = rs.getBigDecimal("RATING");
                Integer releaseYear = rs.getInt("RELEASE_YEAR");
                Integer numOfSeasons = rs.getInt("NUMBER_OF_SEASONS");
                String tempActor = rs.getString("ACTOR");

                String[] names = tempActor.split(" ");
                String actorName = names[0];
                String actorSurname = names[1];

                Genre genre = genreList.stream().filter(genre1 -> genre1.getId().equals(genreId)).findFirst().get();
                Actor actor = new Actor.Builder().name(actorName).surname(actorSurname).build();

                Series newSeries = new Series(id, name, genre, rating, releaseYear, numOfSeasons, actor);

                seriesList.add(newSeries);
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return seriesList;
    }

    public static List<Series> getSeriesFiltered(String seriesName, Integer genreId) {
        List<Series> seriesList = new ArrayList<>();
        List<Genre> genreList = DatabaseUtils.getGenres();

        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM SERIES WHERE 1=1 ";

            if (!seriesName.isEmpty()) {
                baseSqlQuery += " AND NAME = ?";
                queryParams.put(paramOrdinalNumber, seriesName);
                paramOrdinalNumber++;
            }

            if (genreId > 0) {
                baseSqlQuery += " AND GENRE_ID = ?";
                queryParams.put(paramOrdinalNumber, genreId);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            while (rs.next()) {
                Integer id = rs.getInt("SERIES_ID");
                Integer genre_Id = rs.getInt("GENRE_ID");
                String name = rs.getString("NAME");
                BigDecimal rating = rs.getBigDecimal("RATING");
                Integer releaseYear = rs.getInt("RELEASE_YEAR");
                Integer numOfSeasons = rs.getInt("NUMBER_OF_SEASONS");
                String tempActor = rs.getString("ACTOR");

                String[] names = tempActor.split(" ");
                String actorName = names[0];
                String actorSurname = names[1];

                Genre genre = genreList
                        .stream()
                        .filter(genre1 -> genre1.getId().equals(genre_Id))
                        .findFirst()
                        .get();
                Actor actor = new Actor.Builder().name(actorName).surname(actorSurname).build();

                Series newSeries = new Series(id, name, genre, rating, releaseYear, numOfSeasons, actor);

                seriesList.add(newSeries);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return seriesList;
    }

    public static void saveSeries(List<Series> seriesList) {
        List<Genre> genreList = getGenres();
        try (Connection connection = connectToDatabase()) {
            for (Series serie : seriesList) {
                String sql = "INSERT INTO SERIES(GENRE_ID, NAME, RATING, RELEASE_YEAR, NUMBER_OF_SEASONS, ACTOR) VALUES(?, ?, ?, ?, ?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                //Integer genreId = genreList.indexOf(serie.getSeriesGenre());
                Genre genre = serie.getSeriesGenre();
                pstmt.setInt(1, genre.getId());
                pstmt.setString(2, serie.getTitle());
                pstmt.setBigDecimal(3, serie.getRating());
                pstmt.setInt(4, serie.releaseYear());
                pstmt.setInt(5, serie.getNumberOfSeasons());
                Actor actor = serie.getActor();
                String name = actor.getName();
                String surname = actor.getSurname();
                String fullName = name + " " + surname;

                pstmt.setString(6, fullName);
                pstmt.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja podataka u bazu!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void deleteSeries(Series series) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        deleteReviewOfADeletedSeries(series, queryParams, paramOrdinalNumber);

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM SERIES WHERE SERIES_ID = ?";

            if (series != null) {
                Integer id = series.getId();
                queryParams.put(paramOrdinalNumber, id);
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

            Map<Integer, Object> queryParams2 = new HashMap<>();
            Integer paramOrdinalNumber2 = 1;

            String sqlQuery = "ALTER TABLE SERIES\n" +
                    "ALTER COLUMN SERIES_ID RESTART WITH ?;";
            List<Series> seriesList = getSeries();
            if (seriesList.isEmpty()) {
                queryParams2.put(paramOrdinalNumber2, 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            } else {
                Series seriesTemp = seriesList.get(seriesList.size() - 1);
                queryParams2.put(paramOrdinalNumber2, seriesTemp.getId() + 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    private static void deleteReviewOfADeletedSeries(Series series, Map<Integer, Object> queryParams, Integer paramOrdinalNumber) {
        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM REVIEW WHERE REVIEWED_MEDIA = ?";

            if (series != null) {
                String movieString = series.getTitle();
                queryParams.put(paramOrdinalNumber, movieString);

            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void editSeries(Series series, String chosenInfo, String newInfo) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "UPDATE SERIES SET " + chosenInfo + " = ? WHERE SERIES_ID = ?";

            if (!newInfo.isEmpty()) {
                queryParams.put(paramOrdinalNumber, newInfo);
                paramOrdinalNumber++;
            }

            if (series != null) {
                Integer id = series.getId();
                queryParams.put(paramOrdinalNumber, id);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                } else if (queryParams.get(paramNumber) instanceof BigDecimal bqp) {
                    pstmt.setBigDecimal(paramNumber, bqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<Review<Reviewable>> getReviews() {
        List<Review<Reviewable>> reviewList = new ArrayList<>();
        List<Movie> movieList = getMovies();
        List<Series> seriesList = getSeries();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM REVIEW";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String mediaType1 = rs.getString("MEDIA_TYPE");
                String username = rs.getString("USERNAME");
                String reviewedMedia = rs.getString("REVIEWED_MEDIA");
                String comment = rs.getString("COMMENT");

                MediaType mediaType = null;
                Reviewable chosenMedia = null;
                if (mediaType1.equals("MOVIE")) {
                    mediaType = MediaType.MOVIE;
                    chosenMedia = movieList
                            .stream()
                            .filter(movie -> movie.getTitle().equals(reviewedMedia))
                            .findFirst()
                            .orElse(null);
                } else if (mediaType1.equals("SERIES")) {
                    mediaType = MediaType.SERIES;
                    chosenMedia = seriesList
                            .stream()
                            .filter(series -> series.getTitle().equals(reviewedMedia))
                            .findFirst()
                            .orElse(null);
                }

                Review<Reviewable> review = new Review<>(id, mediaType, username, chosenMedia, comment);
                reviewList.add(review);
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return reviewList;
    }

    public static List<Review<Reviewable>> getReviewsFiltered(String name) {
        List<Review<Reviewable>> reviewList = new ArrayList<>();
        List<Movie> movieList = getMovies();
        List<Series> seriesList = getSeries();

        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM REVIEW WHERE 1=1 ";

            if (!name.isEmpty()) {
                baseSqlQuery += " AND USERNAME = ?";
                queryParams.put(paramOrdinalNumber, name);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String mediaType1 = rs.getString("MEDIA_TYPE");
                String username = rs.getString("USERNAME");
                String reviewedMedia = rs.getString("REVIEWED_MEDIA");
                String comment = rs.getString("COMMENT");

                MediaType mediaType = MediaType.valueOf(mediaType1);

                Reviewable chosenMedia = null;
                if (mediaType == MediaType.MOVIE) {
                    chosenMedia = movieList.stream()
                            .filter(movie -> movie.getTitle().equals(reviewedMedia))
                            .findFirst()
                            .orElse(null);
                } else if (mediaType == MediaType.SERIES) {
                    chosenMedia = seriesList.stream()
                            .filter(series -> series.getTitle().equals(reviewedMedia))
                            .findFirst()
                            .orElse(null);
                }

                Review<Reviewable> review = new Review<>(id, mediaType, username, chosenMedia, comment);
                reviewList.add(review);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return reviewList;
    }

    public static void saveReviews(List<Review<Reviewable>> reviewList) {
        try (Connection connection = connectToDatabase()) {
            for (Review review : reviewList) {
                String sql = "INSERT INTO REVIEW(MEDIA_TYPE, USERNAME, REVIEWED_MEDIA, COMMENT) VALUES(?, ?, ?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, review.getMediaType().getName());
                pstmt.setString(2, review.getUsername());
                pstmt.setString(3, review.getMedia().getTitle());
                pstmt.setString(4, review.getComment());
                pstmt.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja podataka u bazu!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void deleteReview(Review<Reviewable> review) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM REVIEW WHERE ID = ?";

            if (review != null) {
                Integer id = review.getId();
                queryParams.put(paramOrdinalNumber, id);
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

            Map<Integer, Object> queryParams2 = new HashMap<>();
            Integer paramOrdinalNumber2 = 1;

            String sqlQuery = "ALTER TABLE REVIEW\n" +
                    "ALTER COLUMN ID RESTART WITH ?;";
            List<Review<Reviewable>> reviewList = DatabaseUtils.getReviews();
            if (reviewList.isEmpty()) {
                queryParams2.put(paramOrdinalNumber2, 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            } else {
                Review<Reviewable> review1 = reviewList.get(reviewList.size() - 1);
                queryParams2.put(paramOrdinalNumber2, review1.getId() + 1);
                PreparedStatement pstmt2 = connection.prepareStatement(sqlQuery);

                for (Integer paramNumber : queryParams2.keySet()) {
                    if (queryParams2.get(paramNumber) instanceof String sqp) {
                        pstmt2.setString(paramNumber, sqp);
                    } else if (queryParams2.get(paramNumber) instanceof Integer iqp) {
                        pstmt2.setInt(paramNumber, iqp);
                    }
                }

                pstmt2.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void editReview(Review<Reviewable> review, String newInfo) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "UPDATE REVIEW SET COMMENT = ? WHERE ID = ?";

            if (!newInfo.isEmpty()) {
                queryParams.put(paramOrdinalNumber, newInfo);
                paramOrdinalNumber++;
            }

            if (review != null) {
                Integer id = review.getId();
                queryParams.put(paramOrdinalNumber, id);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                } else if (queryParams.get(paramNumber) instanceof BigDecimal bqp) {
                    pstmt.setBigDecimal(paramNumber, bqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<WatchlistItem> getWatchlist() {
        List<WatchlistItem> watchlistItems = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM WATCHLIST";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String mediaType1 = rs.getString("MEDIA_TYPE");
                String name = rs.getString("NAME");

                WatchlistItem watchlistItem = new WatchlistItem(id, mediaType1, name);
                watchlistItems.add(watchlistItem);
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return watchlistItems;
    }

    public static List<WatchlistItem> getWatchlistFiltered(String type) {
        List<WatchlistItem> watchlistItems = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM WATCHLIST WHERE 1=1 ";

            if (!type.isEmpty()) {
                baseSqlQuery += " AND MEDIA_TYPE = ?";
                queryParams.put(paramOrdinalNumber, type);
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String mediaType1 = rs.getString("MEDIA_TYPE");
                String name = rs.getString("NAME");

                WatchlistItem watchlistItem = new WatchlistItem(id, mediaType1, name);
                watchlistItems.add(watchlistItem);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return watchlistItems;
    }

    public static void saveWatchlistitem(List<WatchlistItem> watchlistItems) {
        try (Connection connection = connectToDatabase()) {
            for (WatchlistItem watchlistItem : watchlistItems) {
                String sql = "INSERT INTO WATCHLIST (MEDIA_TYPE, NAME) VALUES(?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, watchlistItem.getType());
                pstmt.setString(2, watchlistItem.getName());
                pstmt.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja podataka u bazu!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void removeMediaFromWatchlist(WatchlistItem watchlistItem) {
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "DELETE FROM WATCHLIST WHERE ID = ?";

            if (watchlistItem != null) {
                Integer id = watchlistItem.getId();
                queryParams.put(paramOrdinalNumber, id);
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Integer iqp) {
                    pstmt.setInt(paramNumber, iqp);
                }
            }

            pstmt.execute();

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod dohvaćanja podataka iz baze!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static Integer getNextGenreId() {
        List<Genre> genreList = getGenres();
        if (genreList.isEmpty()) {
            return 1;
        } else {
            Integer lastGenreId = genreList
                    .stream()
                    .map(NamedEntity::getId)
                    .max(Integer::compareTo)
                    .get();

            return lastGenreId + 1;
        }
    }

    public static Integer getNextMovieId() {
        List<Movie> movieList = getMovies();
        if (movieList.isEmpty()) {
            return 1;
        } else {
            Integer lastMovieId = movieList
                    .stream()
                    .map(NamedEntity::getId)
                    .max(Integer::compareTo)
                    .get();

            return lastMovieId + 1;
        }
    }

    public static Integer getNextSeriesId() {
        List<Series> seriesList = getSeries();
        if (seriesList.isEmpty()) {
            return 1;
        } else {
            Integer lastSeriesId = seriesList
                    .stream()
                    .map(NamedEntity::getId)
                    .max(Integer::compareTo)
                    .get();

            return lastSeriesId + 1;
        }
    }

    public static Integer getNextReviewId() {
        List<Review<Reviewable>> reviewList = getReviews();
        if (reviewList.isEmpty()) {
            return 1;
        } else {
            Integer lastReviewId = reviewList
                    .stream()
                    .map(Review::getId)
                    .max(Integer::compareTo)
                    .get();

            return lastReviewId + 1;
        }
    }

    public static Integer getNextWatchlistitemId() {
        List<WatchlistItem> watchlistItems = getWatchlist();
        if (watchlistItems.isEmpty()) {
            return 1;
        } else {
            Integer lastWatchlistId = watchlistItems
                    .stream()
                    .map(WatchlistItem::getId)
                    .max(Integer::compareTo)
                    .get();

            return lastWatchlistId + 1;
        }

    }

    public static Optional<Movie> findBestRatedMovie() {
        List<Genre> genreList = getGenres();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MOVIE ORDER BY RATING DESC LIMIT 1";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            if (rs.next()) {
                Integer id = rs.getInt("MOVIE_ID");
                Integer genreId = rs.getInt("GENRE_ID");
                String name = rs.getString("NAME");
                Integer length = rs.getInt("LENGTH");
                BigDecimal rating = rs.getBigDecimal("RATING");
                Integer releaseYear = rs.getInt("RELEASE_YEAR");
                BigDecimal tempRevenue = rs.getBigDecimal("MOVIE_REVENUE");
                String tempActor = rs.getString("ACTOR");

                String[] names = tempActor.split(" ");
                String actorName = names[0];
                String actorSurname = names[1];

                Genre genre = genreList.stream().filter(genre1 -> genre1.getId().equals(genreId)).findFirst().get();
                Revenue revenue = new Revenue(tempRevenue);
                Actor actor = new Actor.Builder().name(actorName).surname(actorSurname).build();

                Movie newMovie = new Movie(id, name, length, genre, rating, releaseYear, revenue, actor);

                return Optional.of(newMovie);
            } else {
                return Optional.empty();
            }
        }catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<Series> findBestRatedSeries() {
        List<Genre> genreList = getGenres();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM SERIES ORDER BY RATING DESC LIMIT 1";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            if (rs.next()) {
                Integer id = rs.getInt("SERIES_ID");
                Integer genreId = rs.getInt("GENRE_ID");
                String name = rs.getString("NAME");
                BigDecimal rating = rs.getBigDecimal("RATING");
                Integer releaseYear = rs.getInt("RELEASE_YEAR");
                Integer numOfSeasons = rs.getInt("NUMBER_OF_SEASONS");
                String tempActor = rs.getString("ACTOR");

                String[] names = tempActor.split(" ");
                String actorName = names[0];
                String actorSurname = names[1];

                Genre genre = genreList.stream().filter(genre1 -> genre1.getId().equals(genreId)).findFirst().get();
                Actor actor = new Actor.Builder().name(actorName).surname(actorSurname).build();

                Series newSeries = new Series(id, name, genre, rating, releaseYear, numOfSeasons, actor);

                return Optional.of(newSeries);
            } else {
                return Optional.empty();
            }
        }catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }
}