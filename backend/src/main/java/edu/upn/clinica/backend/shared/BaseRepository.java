package edu.upn.clinica.backend.shared;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseRepository {

    @Autowired
    private DataSource dataSource;

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}