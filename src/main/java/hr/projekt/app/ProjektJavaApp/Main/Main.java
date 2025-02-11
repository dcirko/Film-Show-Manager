package hr.projekt.app.ProjektJavaApp.Main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import hr.projekt.app.ProjektJavaApp.Model.Actor;
import hr.projekt.app.ProjektJavaApp.Model.Genre;
import hr.projekt.app.ProjektJavaApp.Model.Movie;
import hr.projekt.app.ProjektJavaApp.Model.Revenue;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;

/*
    Aplikacija za upravljanje filmovima i serijama.
        Razvijte sustav za praćenje informacija o filmovima i serijama, uključujući ocjene, recenzije i preporuke.
        Podaci o filmovima i serijama mogu se pohraniti u bazu podataka.
        Koristite JavaFX za sučelje i threadove za asinkronu obradu pretraga i prikaza informacija o filmovima i serijama.*/



public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String ADMIN_FILE = "loginFiles/admin.txt";

    public static void main(String[] args){
        logger.info("Program je pokrenut");
        Scanner input = new Scanner(System.in);
        List<Movie> movieList = new ArrayList<>();
        List<Genre> genreList = genresMethod(input);

        System.out.println("Unesite 3 filma: ");
        Boolean error = false;

        for(int i = 0; i < 3; i++){
            System.out.println((i+1) + ". filma: ");

            String movieName = null;
            do{
                error = false;
                System.out.println("Unesite ime filma: ");
                try{
                    movieName = input.nextLine();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos imena filma. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
            }while (error);

            Genre chosenGenre = getGenre(genreList, input);

            Integer movieLength = null;
            do{
                error = false;
                System.out.println("Unesite duljinu filma u minutama: ");
                try{
                    movieLength = input.nextInt();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos duljine. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
                input.nextLine();
            }while (error);

            BigDecimal movieRating = null;
            do{
                error = false;
                System.out.println("Unesite ocjenu filma od 1 do 10: ");
                try{
                    movieRating = input.nextBigDecimal();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos ocjene filma. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
                input.nextLine();
            }while (error);

            Integer movieReleaseYear = null;
            do{
                error = false;
                System.out.println("Unesite godinu izlaska filma: ");
                try{
                    movieReleaseYear = input.nextInt();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos godine izlaska filma. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
                input.nextLine();
            }while (error);

            BigDecimal movieRevenue = null;
            do{
                error = false;
                System.out.println("Unesite zaradu filma(u dolarima): ");
                try{
                    movieRevenue = input.nextBigDecimal();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos zarade filma. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
                input.nextLine();
            }while (error);

            Revenue revenue = new Revenue(movieRevenue);

            String actorName = null;
            do{
                error = false;
                System.out.println("Unesite ime glumca/glumice: ");
                try{
                    actorName = input.nextLine();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos imena glumca. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
            }while (error);

            String actorSurname = null;
            do{
                error = false;
                System.out.println("Unesite prezime glumca/glumice: ");
                try{
                    actorSurname = input.nextLine();
                }catch (InputMismatchException e){
                    String msg = "Neispravan unos prezimena glumca. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }
            }while (error);

            Actor actor = new Actor.Builder()
                    .name(actorName)
                    .surname(actorSurname)
                    .build();

            Movie movie = new Movie((i+1), movieName, movieLength, chosenGenre, movieRating, movieReleaseYear, revenue, actor);

            movieList.add(movie);
        }

        System.out.println(movieList);

        saveAdminPassword();
    }

    private static void saveAdminPassword() {
        String hashedPassword = BCrypt.hashpw("47LFKBAp", BCrypt.gensalt());

        try (PrintWriter writer = new PrintWriter(new FileWriter(ADMIN_FILE, true))) {
            writer.println("dcirko" + "," + hashedPassword);
            System.out.println("Admin registration successful!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error registering admin.");
        }
    }

    private static Genre getGenre(List<Genre> genreList, Scanner input) {
        Boolean error;
        System.out.println("Odaberite žanr: ");
        for(int j = 0; j < genreList.size(); j++) {
            System.out.println((j + 1) + ". žanr: " + genreList.get(j));
        }

        Integer chosenGenreIndex = null;
        do {
            do {
                error = false;
                try {
                    chosenGenreIndex = input.nextInt();
                } catch (InputMismatchException e) {
                    String msg = "Neispravan unos za odabir žanra. Ponovite odabir:";
                    logger.error(msg);
                    System.out.println(msg);
                    error = true;
                }

                input.nextLine();
            }
            while (error);
        } while (chosenGenreIndex < 1 || chosenGenreIndex > 3);

        return genreList.get(chosenGenreIndex-1);
    }

    private static List<Genre> genresMethod(Scanner input) {
        System.out.println("Unesite 3 žanra: ");
        List<Genre> genreList = new ArrayList<>();

        for(int j = 0; j < 3; j++){
            System.out.println((j +1) + ". žanr: ");
            System.out.println("Unesite ime žanra: ");
            String genreName = input.nextLine();
            System.out.println("Unesi opis žanra: ");
            String genreDescription = input.nextLine();
            Genre genre = new Genre((j+1), genreName, genreDescription);

            genreList.add(genre);
        }

        return genreList;
    }

}
