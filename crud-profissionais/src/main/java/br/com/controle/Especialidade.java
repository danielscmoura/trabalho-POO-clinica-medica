/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.controle;

/**
 * Classe de domínio que representa uma linha da tabela "especialidades".
 * É usada pelo Profissional (cada profissional TEM UMA especialidade).
 */
public class Especialidade {

    private int id;
    private String nome;

    public Especialidade() {

    }

    public Especialidade(int id, String nome) {
        this.id = id;
        this.nome = nome;
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

    // O JComboBox usa o toString() para mostrar o item na tela,
    // por isso ele devolve o nome e não o id.
    @Override
    public String toString() {
        return nome;
    }

    // Duas especialidades são iguais quando têm o mesmo id.
    // Isso permite selecionar no JComboBox a especialidade de um profissional.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Especialidade)) {
            return false;
        }
        return this.id == ((Especialidade) obj).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
