package com.wonkglorg.database;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseTest {
    @Test
    public void test() {
        //todo:jmd write out some of the cases how one would write them and compare which would be an easier execution process
        Database database = new SqliteDatabase(Path.of("heads.db"), Path.of("heads.db"));


        try (PreparedStatement preparedStatement = database.prepareStatement("SELECT * FROM heads WHERE name = ?")) {
            preparedStatement.setString(1, "");
            var result = preparedStatement.executeQuery();
            System.out.println("Success: " + result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
