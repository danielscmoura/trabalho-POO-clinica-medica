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


/**
 * Classe base de acesso ao banco (DAO = Data Access Object).
 *
 * Concentra a conexão JDBC com o MySQL (schema clinica_medica). As classes
 * Manter* e RelatorioConsultas herdam dela (extends DAO) e reaproveitam os
 * atributos con, pst e rs e os métodos abrirBanco() e fecharBanco().
 */
public class DAO {
    // Objetos JDBC compartilhados com as subclasses (sem modificador de acesso,
    // ficam visíveis para as classes do mesmo pacote br.com.entidade):
    // con = conexão aberta, pst = comando SQL preparado, rs = resultado de um SELECT.
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    /**
     * Abre a conexão com o MySQL e a guarda no atributo con.
     * É chamado no início de todo método dos DAOs, antes de montar o SQL.
     * Se o driver não for encontrado, apenas mostra a mensagem no console
     * (con continua null). Se a conexão falhar (MySQL desligado, senha errada,
     * banco inexistente), lança RuntimeException para a tela tratar.
     */
    public void abrirBanco() throws SQLException{
        try{
            // Carrega o driver JDBC do MySQL (vem da dependência mysql-connector-j do pom.xml).
            // No Connector/J 8 o nome atual é com.mysql.cj.jdbc.Driver; este nome antigo
            // ainda funciona, só gera um aviso de "deprecated" no console.
            Class.forName("com.mysql.jdbc.Driver");
            // Endereço do banco: servidor local, porta padrão 3306, schema clinica_medica.
            String url = "jdbc:mysql://localhost:3306/clinica_medica";
            // Usuário e senha do MySQL local, fixos no código.
            String user = "root";
            String senha = "12345678";
            
            // O DriverManager usa o driver carregado para abrir a conexão.
            con = (Connection) DriverManager.getConnection(url,user,senha);
            System.out.println("Conectado ao Banco!");
            
        } catch(ClassNotFoundException ex){
            // O .jar do driver do MySQL não está no classpath do projeto.
            System.out.println("Classe não encontrada, adicione o driver nas bibliotecas");
            
        } catch(SQLException e){
            // Falha ao conectar: mostra o erro no console e relança como
            // RuntimeException, interrompendo a operação que pediu a conexão.
            System.out.println(e);
            throw new RuntimeException(e);
            
        }
    }
    
    /**
     * Libera os recursos da última operação. É chamado no bloco finally de
     * todos os métodos dos DAOs, para rodar mesmo quando ocorre erro.
     * Esta versão fecha apenas o PreparedStatement (pst); a conexão (con) e o
     * ResultSet (rs) não são fechados aqui. A classe ManterEspecialidade
     * sobrescreve este método para fechar a conexão.
     */
    public void fecharBanco() throws Exception{
        // Só fecha se algum comando chegou a ser preparado (evita NullPointerException).
        if(pst != null){
            pst.close();
            System.out.println("Encerrando conexão com o Banco");
        }
    }
}
