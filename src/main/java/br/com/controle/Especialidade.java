package br.com.controle;

/**
 * Model: representa uma linha da tabela "especialidades".
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

    @Override
    public String toString() {
        return nome;
    }

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
