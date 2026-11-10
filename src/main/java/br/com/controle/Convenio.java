package br.com.controle;

/**
 * Model que representa um registro da tabela convenios no Java.
 * Cada objeto guarda os dados de um convênio para a tela e o banco utilizarem.
 */
public class Convenio {

    private int id;
    private String nome;
    private String telefone;

    /** Cria um objeto vazio, preenchido posteriormente pelos setters ou pelo banco. */
    public Convenio() {
    }

    /** Cria um convênio já com todos os dados necessários para uma alteração. */
    public Convenio(int id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
