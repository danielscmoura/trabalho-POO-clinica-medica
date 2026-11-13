package br.com.controle;

/**
 * Model: representa uma linha da tabela "especialidades".
 *
 * Também é usada como item dos JComboBox de especialidade nas telas de
 * profissionais; por isso sobrescreve toString() (texto exibido no combo)
 * e equals()/hashCode() (comparação pelo id).
 */
public class Especialidade {

    // Campos que espelham as colunas da tabela especialidades (nome é UNIQUE).
    private int id;
    private String nome;

    // Construtores: vazio (preenchido pelos setters) e completo (usado pelo DAO
    // ao ler do banco e pelas telas ao montar o combo).
    public Especialidade() {
    }

    public Especialidade(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Getters e setters.
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

    // O JComboBox chama toString() para saber o que mostrar: exibimos só o nome.
    @Override
    public String toString() {
        return nome;
    }

    // Duas especialidades são iguais quando têm o mesmo id, mesmo sendo objetos
    // diferentes. A tela de edição de profissional usa isso para selecionar no
    // combo a especialidade que veio do banco.
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

    // Quem sobrescreve equals() deve sobrescrever hashCode() com o mesmo critério (o id).
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
