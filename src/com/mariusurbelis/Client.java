package com.mariusurbelis;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;

public class Client {

    public static String ip;

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void print(String text) {
        System.out.println(text);
    }

    public static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
    }

    public static String getData(String command) throws IOException {
        var socket = new Socket(ip, 59090);
        var out = new PrintWriter(socket.getOutputStream(), true);
        out.println(command);
        return new Scanner(socket.getInputStream()).nextLine();
    }

    public static void main(String[] args) throws IOException {
        int points = 0;
        int infantry = 0;
        int archers = 0;
        int cavalry = 0;

        if (args.length != 1) {
            ip = "localhost";
        } else {
            ip = args[0];
        }

        var playerIn = new Scanner(System.in);

        clearScreen();

        System.out.print("Choose your username: ");
        String username = new Scanner(System.in).nextLine();

        username = username.replace(' ', '_');

        clearScreen();

        print(getData("REGISTER " + username));
        print("Your chosen username is " + username);

        pause();

        while (true) {
            clearScreen();

            var stats = getData("INFO " + username);
            points = Integer.parseInt(stats.split(" ")[0]);
            infantry = Integer.parseInt(stats.split(" ")[1]);
            archers = Integer.parseInt(stats.split(" ")[2]);
            cavalry = Integer.parseInt(stats.split(" ")[3]);

            print("You have " + points + " action points.");
            print("Infantry: " + infantry + ", archers: " + archers + ", cavalry: " + cavalry + "\n");
            print("You can choose from a range of commands:");
            print("1. ATTACK");
            print("2. MAKE <CAV, ARC, INF> NUMBER\n");

            var command = playerIn.nextLine();

            if (command.split(" ")[0].equalsIgnoreCase("ATTACK")) {
                String players = getData("PLAYERS");
                clearScreen();
                print("Choose a player from the list:");
                print(players);
                print("Your choice: ");

                String chosenPlayer = playerIn.nextLine();

                boolean valid = false;

                for(String s : players.split(" ")) {
                    if (s.equalsIgnoreCase(chosenPlayer)) {
                        valid = true;
                    }
                }

                if (valid) {
                    // Attack the player
                } else {
                    print("Player does not exist");
                }

            } else if (command.split(" ")[0].equalsIgnoreCase("MAKE")) {
                String unit = command.split(" ")[1];

                boolean valid = false;

                if (unit.equalsIgnoreCase("INF") || unit.equalsIgnoreCase("ARC") || unit.equalsIgnoreCase("CAV"))
                    valid = true;

                if (valid) {
                    var amount = Integer.parseInt(command.split(" ")[2]);

                    print("SENT: " + command + " " + username);

                    if (amount > points)
                        print("You do not have enough action points");
                    else
                        print(getData(command + " " + username));
                }
            } else if (command.split(" ")[0].equalsIgnoreCase("SPY")) {
                String players = getData("PLAYERS");
                clearScreen();
                print("Armies currently in the game:");
                print(players);
                print("Your choice: ");

                String chosenPlayer = playerIn.nextLine();

                boolean valid = false;

                for(String s : players.split(" ")) {
                    if (s.equalsIgnoreCase(chosenPlayer)) {
                        valid = true;
                    }
                }

                if (valid) {
                    // Spy the player
                    print(getData(command + " " + username));
                } else {
                    print("Player does not exist");
                }
            }


            pause();
        }
    }
}