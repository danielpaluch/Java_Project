package org.example;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.model.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import javax.print.Doc;
import javax.swing.event.DocumentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import java.security.SecureRandom;

class App{
    static MongoCollection<Document> users;
    static Scanner scanner = new Scanner(System.in);

    public App(MongoDatabase db){
        MongoCollection<Document> usersList = db.getCollection("users");
        users = usersList;
    }
    public static void mainMenu(){
        System.out.println("======================================");
        System.out.println("1. Rejestracja");
        System.out.println("2. Logowanie");
        System.out.println("3. Wyjście");
        System.out.println();
        System.out.print("Wybierz opcję, którą chcesz wykonać: ");
        int option = scanner.nextInt();

        if(option==1){
            System.out.println("======================================");
            System.out.println("Wybrano opcje Rejestracja");
            registerUser();
        }
        else if(option==2){
            System.out.println("======================================");
            System.out.println("Wybrano opcje Logowanie");
            logInUser();
        }
        else if(option==3){
            System.out.println("======================================");
            System.out.println("Wybrano opcje Wyjscie");
        }
        else{
            System.out.println();
            System.out.println("Brak takiej opcji.");
        }

    }
    public static void registerUser(){
        System.out.println("Wprowadź dane użytkownika: ");
        System.out.println("Nazwa użytkownika: ");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.println("Hasło użytkownika: ");
        String paswd = scanner.nextLine();
        try {
            Document userExists = users.find(eq("name", name))
                    .first();

            if (userExists == null) {
                Document newUser = new Document()
                        .append("name", name)
                        .append("password", paswd)
                        .append("sites", Arrays.asList()); //rejestracja

                users.insertOne(newUser);

                System.out.println("Użytkownik zarejestrowany. ");
                mainMenu();
            }
            else{
                System.out.println("Taki użytkownik już istnieje.");
                mainMenu();
            }
        }
        catch(Exception e){
            System.out.println("Rejestracja nie powiodla się.");
            mainMenu();
        }
    }
    public static void logInUser(){
        System.out.println("Wprowadź dane użytkownika: ");
        System.out.println("Nazwa użytkownika: ");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.println("Hasło użytkownika: ");
        String paswd = scanner.nextLine();

        Document user = users.find(eq("name", name))
                .first();

        if(user == null){
            System.out.println("Nieprawidłowe dane.");
            mainMenu();
        }

        if(Objects.equals(user.get("password").toString(), paswd.toString())){
            System.out.println("Poprawnie zalogowano.");
            Object userId = user.get("_id");
            loggedMenu(userId);
        }
        else{
            System.out.println("Nieprawidłowe dane.");
            mainMenu();
        }
    }
    public static void loggedMenu(Object userId){
        System.out.println("======================================");
        System.out.println("1. Dodaj hasło.");
        System.out.println("2. Wyświetl hasła.");
        System.out.println("3. Wyloguj.");
        System.out.println();



        System.out.println("Wybierz opcje, która chcesz wykonać: ");
        int option = scanner.nextInt();

        if(option == 1){
            System.out.println("Wybrano dodawanie hasla.");

            System.out.println();
            System.out.println("Nazwa strony: ");
            scanner.nextLine();
            String name = scanner.nextLine();
            System.out.println("Login: ");
            String login = scanner.nextLine();
            System.out.println("Hasło: ");
            String paswd = scanner.nextLine();

            Bson filter = Filters.eq("_id", userId);
            Bson update = Updates.push("sites", new Document()
                    .append("name", name)
                    .append("login", login)
                    .append("password", paswd));
            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
                    .returnDocument(ReturnDocument.AFTER);
            Document result = users.findOneAndUpdate(filter, update, options);
            if(result != null){
                System.out.println("Pomyślnie dodano do bazy.");
                loggedMenu(userId);
            }
            else {
                System.out.println("Nie dodano do bazy.");
                loggedMenu(userId);
            }
        }
        else if(option==2){
            Bson projection = include("sites");
            Document result = users.find(eq("_id", userId))
                    .projection(projection)
                    .first();
            List<Document> list = result.getList("sites", Document.class);
            for (Document obj : list) {System.out.println(
                    "Strona: " + obj.get("name").toString() + " " +
                            "Login: " + obj.get("login").toString() + " " +
                            "Hasło: " + obj.get("password").toString());}
            loggedMenu(userId);
        }
        else if(option==3){
            System.out.println("Wybrano wylogowanie.");
            mainMenu();
        }
        else{
            System.out.println("Nie ma takiej opcji.");
            loggedMenu(userId);
        }
    }
}


public class Main {
    public static void main(String[] args) {
        MongoClient client = MongoClients.create("mongodb+srv://admin:admin@project.ksjanfd.mongodb.net/?retryWrites=true&w=majority");

        MongoDatabase db = client.getDatabase("java_project");

        App application = new App(db);

        application.mainMenu();

    }
}