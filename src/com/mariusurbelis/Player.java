package com.mariusurbelis;

public class Player {
    public Player (String username) {
        this.username = username;
    }

    public String username;
    public int actionPoints = 25;
    public int infantry = 0;
    public int archers = 0;
    public int cavalry = 0;
    public int actionPointsPerTime = 1;
}
