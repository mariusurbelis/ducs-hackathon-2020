package com.mariusurbelis;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Server {

    Date lastPointsAddedDate = null;


    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void print(String text) {
        System.out.println(text);
    }

    public static void removePlayer(String name) {
        for (Player p : players) {
            if (p.username.equalsIgnoreCase(name)) {
                players.remove(p);
                return;
            }
        }
    }

    public static void refreshPoints() {


        for(Player p : players) {
            p.actionPoints += p.actionPointsPerTime;
        }
    }

    public static boolean battle(String attacker, String defender) {

        Player a = null, d = null, b = null;

        for(Player p : players) {
            if (p.username.equalsIgnoreCase(attacker)) {
                a = p;
            } else if (p.username.equalsIgnoreCase(defender)) {
                d = p;
            }
        }

        for(Player p : players) {
            if (p.username.equalsIgnoreCase(attacker)) {
                p.infantry -= Math.floor(d.archers * 1.2);
                p.archers -= Math.floor(d.cavalry * 1.2);
                p.cavalry -= Math.floor(d.infantry * 1.2);
                if (p.infantry < 0)
                    p.infantry = 0;
                if (p.archers < 0)
                    p.archers = 0;
                if (p.cavalry < 0)
                    p.cavalry = 0;
                b = p;
            } else if (p.username.equalsIgnoreCase(defender)) {
                p.infantry -= Math.floor(a.archers * 1.2);
                p.archers -= Math.floor(a.cavalry * 1.2);
                p.cavalry -= Math.floor(a.infantry * 1.2);
                if (p.infantry < 0)
                    p.infantry = 0;
                if (p.archers < 0)
                    p.archers = 0;
                if (p.cavalry < 0)
                    p.cavalry = 0;
                d = p;
            }
        }

        return ((d.infantry + d.archers + d.cavalry) < (b.infantry + b.archers + b.cavalry));
    }

    public static void addPlayer(String name) {
        for (Player p : players) {
            if (p.username.equalsIgnoreCase(name)) {
                return;
            }
        }
        players.add(new Player(name));
    }

    public static void givePoints(String username, int amount) {
        for(Player p : players) {
            if (p.username.equalsIgnoreCase(username)) {
                p.actionPoints += amount;
                return;
            }
        }
    }

    public static String playerStats(String username) {
        String stats = "";
        for (Player p : players) {
            if (p.username.equalsIgnoreCase(username)) {
                stats += p.actionPoints + " " + p.infantry + " " + p.archers + " " + p.cavalry;
            }
        }
        return stats;
    }

    public static String spy(String username) {
        String stats = "";
        for (Player p : players) {
            if (p.username.equalsIgnoreCase(username)) {
                stats += p.infantry + " " + p.archers + " " + p.cavalry;
            }
        }
        return stats;
    }

    public static void makeArmy(String army, int n, String user) {
        for (Player p : players) {
            if (p.username.equalsIgnoreCase(user)) {

                if (army.equalsIgnoreCase("INF"))
                    p.infantry += n;
                else if (army.equalsIgnoreCase("ARC"))
                    p.archers += n;
                else if (army.equalsIgnoreCase("CAV"))
                    p.cavalry += n;

                p.actionPoints -= n;
                return;
            }
        }
    }

    public static ArrayList<Player> players = new ArrayList<Player>();

    public static String allPlayers() {
        String playerString = "";
        for (Player p : players) {
            playerString += p.username + " ";
        }
        return playerString;
    }

    public static void main(String[] args) throws IOException {

        try (var listener = new ServerSocket(59090)) {
            System.out.println("The game server is running...");
            while (true) {
                try (var socket = listener.accept()) {
                    var userInput = new Scanner(socket.getInputStream()).nextLine();
                    refreshPoints();

                    if (userInput.equalsIgnoreCase("PLAYERS")) {
                        var out = new PrintWriter(socket.getOutputStream(), true);
                        if (players.isEmpty()) {
                            out.println("No players currently");
                        } else {
                            out.println(allPlayers());
                        }
                    } else if (userInput.split(" ")[0].equalsIgnoreCase("REGISTER")) {
                        addPlayer(userInput.split(" ")[1]);
                        clearScreen();
                        var out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("SUCCESS");
                        print("Registered user " + userInput.split(" ")[1]);
                    } else if (userInput.split(" ")[0].equalsIgnoreCase("QUIT")) {
//                        removePlayer(userInput.split(" ")[1]);
                        clearScreen();
                        var out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("BYE!");
                        print("Removed player " + userInput.split(" ")[1]);
                    } else if (userInput.split(" ")[0].equalsIgnoreCase("INFO")) {
                        clearScreen();
                        var out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(playerStats(userInput.split(" ")[1]));
                        print("Stats of user " + userInput.split(" ")[1] + " " + playerStats(userInput.split(" ")[1]));
                    } else if (userInput.split(" ")[0].equalsIgnoreCase("MAKE")) {
                        clearScreen();

                        int amount = Integer.parseInt(userInput.split(" ")[2]);
                        String user = userInput.split(" ")[3];

                        makeArmy(userInput.split(" ")[1], amount, user);

                        var out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(playerStats(userInput.split(" ")[1]));
                        print("Stats of user " + userInput.split(" ")[1] + " " + playerStats(userInput.split(" ")[1]));
                    } else if (userInput.split(" ")[0].equalsIgnoreCase("SPY")) {
                        clearScreen();

                        var out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(spy(userInput.split(" ")[1]));
                        print("Army of user " + userInput.split(" ")[1] + " " + spy(userInput.split(" ")[1]));
                    } else if (userInput.split(" ")[0].equalsIgnoreCase("BATTLE")) {
                        clearScreen();

                        String attacker = userInput.split(" ")[1];
                        String defender = userInput.split(" ")[2];

                        print("Battle happening between " + attacker + " and " + defender + "\n");
                        print(attacker + " is attacking with " + playerStats(attacker));
                        print(defender + " is defending with " + playerStats(attacker) + "\n");

                        if (battle(attacker, defender)) {
                            var out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("YOU WIN!");

                        } else {
                            var out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("YOU LOSE!");
                        }

                        print("Stats of user " + userInput.split(" ")[1] + " " + playerStats(userInput.split(" ")[1]));
                        print("Stats of user " + userInput.split(" ")[2] + " " + playerStats(userInput.split(" ")[2]));
                    }
                }
            }
        }
    }
}