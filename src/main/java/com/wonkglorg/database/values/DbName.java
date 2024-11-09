package com.wonkglorg.database.values;

public record DbName(String name) {
    @Override
    public String toString() {
        return name;
    }
}
