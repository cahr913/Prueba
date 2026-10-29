package com.labcontrol.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class ConexionBD {
    private static final String URL =
            "jdbc:mysql://localhost:3306/gestion_laboratorio"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Guatemala";

    private static final String USER = "root";
    private static final String PASS = "Neferere1!.";

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}