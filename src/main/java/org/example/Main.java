package org.example;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.model.Projections;
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
import java.util.Scanner;

import java.security.SecureRandom;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    static class User {
        public User(String ModelName, String ModelPassword){
            name = ModelName;
            password = ModelPassword;
        }
        String name;
        String password;
        List<Site> sites = Arrays.asList();
    }
    class Site{
        String url;
        String name;
        String password;
    }
    public static void mainMenu(MongoCollection<Document> users){
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
            registerUser(users);
        }
        else if(option==2){
            System.out.println("======================================");
            System.out.println("Wybrano opcje Logowanie");
            logInUser(users);
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
    public static void registerUser(MongoCollection<Document> users){
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
                mainMenu(users);
            }
            else{
                System.out.println("Taki użytkownik już istnieje.");
                mainMenu(users);
            }
        }
        catch(Exception e){
            System.out.println("Rejestracja nie powiodla się.");
            mainMenu(users);
        }
    }
    public static void logInUser(MongoCollection<Document> users){
        System.out.println("Wprowadź dane użytkownika: ");
        System.out.println("Nazwa użytkownika: ");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.println("Hasło użytkownika: ");
        String paswd = scanner.nextLine();


        Document user = users.find(eq("name", name))
                .first();



        System.out.println(user.toBsonDocument());



    }
    public static void main(String[] args) {




        MongoClient client = MongoClients.create("mongodb+srv://admin:admin@project.ksjanfd.mongodb.net/?retryWrites=true&w=majority");

        MongoDatabase db = client.getDatabase("java_project");

        MongoCollection<Document> users = db.getCollection("users");
        /*
        Document newUser = new Document()
                .append("name", "Test")
                .append("password", "Password")
                .append("sites", Arrays.asList()); //rejestracja

        users.insertOne(newUser);
        */
        mainMenu(users);

    }
}