package com.wonkglorg.database.values;

public record DbUser(String user) {
    @Override
    public String toString() {
        return user;
    }
}
