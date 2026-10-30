/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.entidade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DAO {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public void abrirBanco() throws SQLException{
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/clinica_medica";
            String user = "root";
            String senha = "12345678";
            
            con = (Connection) DriverManager.getConnection(url,user,senha);
            System.out.println("Conectado ao Banco!");
            
        } catch(ClassNotFoundException ex){
            System.out.println("Classe não encontrada, adicione o driver nas bibliotecas");
            
        } catch(SQLException e){
            System.out.println(e);
            throw new RuntimeException(e);
            
        }
    }
    
    public void fecharBanco() throws Exception{
        if(pst != null){
            pst.close();
            System.out.println("Encerrando conexão com o Banco");
        }
    }
}
