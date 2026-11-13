/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.controle;

/**
 * Modelo (entidade) que representa uma linha da tabela "pacientes".
 * Cada objeto guarda os dados de um paciente: a tela preenche o objeto e o
 * ManterPaciente o grava ou o preenche a partir do banco.
 *
 * @author danielscmoura
 */
public class Paciente {
    
    // Campos que espelham as colunas da tabela pacientes
    // (nome e cpf obrigatórios, cpf UNIQUE; email e telefone opcionais).
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    
    // Construtores: vazio (o DAO e as telas preenchem pelos setters) e completo.
    public Paciente(){
        
    }
    
    public Paciente(int id, String nome, String cpf, String email, String telefone){
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    // Getters e setters: acesso aos atributos privados (encapsulamento).
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    
}
