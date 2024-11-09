package com.wonkglorg.database.values;

public record DbPort(String port) {
    @Override
    public String toString() {
        return port;
    }
}
