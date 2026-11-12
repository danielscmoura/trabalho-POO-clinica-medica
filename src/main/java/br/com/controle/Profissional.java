/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.controle;

/**
 * Classe de domínio que representa uma linha da tabela "profissionais".
 *
 * No banco, a chave estrangeira é a coluna id_especialidade.
 * No Java, em vez de guardar só o número, o profissional guarda o OBJETO
 * Especialidade (associação entre classes). Assim temos acesso ao id
 * (para gravar a FK) e ao nome (para mostrar na tela).
 */
public class Profissional {

    private int id;
    private String nome;
    private String email;
    private Especialidade especialidade;

    public Profissional() {

    }

    public Profissional(int id, String nome, String email, Especialidade especialidade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.especialidade = especialidade;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }
}
