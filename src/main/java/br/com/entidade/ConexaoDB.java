/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.entidade;

import java.sql.*;

/**
 *
 * @author danielscmoura
 */
public class ConexaoDB {
    private static final String url = "jdbc:mysql://localhost:3306/clinica_medica";
    private static final String user = "root";
    private static final String senha = "12345678";
            
    public static Connection getConexao() throws SQLException{
        return DriverManager.getConnection(url,user,senha);
    }
}
