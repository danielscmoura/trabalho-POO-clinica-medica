package br.com.entidade;

import br.com.controle.Especialidade;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * CRUD de Especialidade (acesso ao MySQL via JDBC).
 * Herda de DAO para reaproveitar a conexão (abrirBanco).
 *
 * Tabela: especialidades (nome obrigatório, UNIQUE, até 50 caracteres). Ela é
 * referenciada pela FK profissionais.id_especialidade, por isso uma
 * especialidade que tem profissionais não pode ser excluída.
 * Diferente das outras classes Manter*, aqui o PreparedStatement e o ResultSet
 * são variáveis locais em try-with-resources, fechadas automaticamente.
 */
public class ManterEspecialidade extends DAO {

    private static final int TAMANHO_MAXIMO_NOME = 50; // VARCHAR(50) na tabela

    // C - Create
    /**
     * Cadastra uma nova especialidade.
     * Regras: nome obrigatório, com no máximo 50 caracteres, e que não repita
     * o de outra especialidade.
     * Retorna true se gravou; false se alguma regra falhou (a mensagem é exibida).
     * Erros de banco são repassados para a tela.
     */
    public boolean inserirEspecialidade(Especialidade e) throws Exception {
        // Valida os campos antes de abrir a conexão.
        if (!dadosValidos(e)) {
            return false;
        }
        try {
            abrirBanco();
            // Verifica duplicidade de nome. A coluna já é UNIQUE, mas checar antes
            // permite mostrar uma mensagem amigável em vez de um erro de SQL.
            // try-with-resources: o que é aberto entre os parênteses do try é
            // fechado sozinho ao fim do bloco, mesmo com return ou erro.
            try (PreparedStatement busca = con.prepareStatement(
                    "SELECT id FROM especialidades WHERE nome = ?")) {
                busca.setString(1, e.getNome());
                try (ResultSet r = busca.executeQuery()) {
                    if (r.next()) {
                        JOptionPane.showMessageDialog(null, "Essa especialidade já está cadastrada!");
                        return false;
                    }
                }
            }
            // INSERT com ? (PreparedStatement evita SQL injection); o id vem do AUTO_INCREMENT.
            try (PreparedStatement ins = con.prepareStatement(
                    "INSERT INTO especialidades (nome) VALUES (?)")) {
                ins.setString(1, e.getNome());
                ins.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Especialidade cadastrada com sucesso!");
            return true;
        } finally {
            fecharBanco();
        }
    }

    // U - Update
    /**
     * Altera o nome da especialidade identificada pelo id.
     * Regras: precisa haver uma especialidade selecionada (id diferente de 0), o
     * nome deve ser válido e não pode pertencer a outra especialidade.
     * Retorna true se alterou; false se alguma regra falhou (a mensagem é exibida).
     */
    public boolean editarEspecialidade(Especialidade e) throws Exception {
        if (e.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma especialidade para alterar.");
            return false;
        }
        if (!dadosValidos(e)) {
            return false;
        }
        try {
            abrirBanco();
            // o nome não pode pertencer a OUTRA especialidade
            // (o "id <> ?" tira a própria especialidade da busca, para ela poder manter o nome).
            try (PreparedStatement busca = con.prepareStatement(
                    "SELECT id FROM especialidades WHERE nome = ? AND id <> ?")) {
                busca.setString(1, e.getNome());
                busca.setInt(2, e.getId());
                try (ResultSet r = busca.executeQuery()) {
                    if (r.next()) {
                        JOptionPane.showMessageDialog(null, "Já existe outra especialidade com esse nome!");
                        return false;
                    }
                }
            }
            // Grava o novo nome somente na linha com esse id.
            try (PreparedStatement upd = con.prepareStatement(
                    "UPDATE especialidades SET nome = ? WHERE id = ?")) {
                upd.setString(1, e.getNome());
                upd.setInt(2, e.getId());
                upd.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Especialidade alterada com sucesso!");
            return true;
        } finally {
            fecharBanco();
        }
    }

    // D - Delete
    /**
     * Exclui a especialidade pelo id.
     * Se houver profissionais ligados a ela, a FK profissionais.id_especialidade
     * bloqueia a exclusão (erro 1451 do MySQL): o método mostra uma mensagem e
     * retorna false. Retorna true se excluiu. Outros erros são repassados.
     */
    public boolean deletarEspecialidade(Especialidade e) throws Exception {
        if (e.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma especialidade para excluir.");
            return false;
        }
        try {
            abrirBanco();
            try (PreparedStatement del = con.prepareStatement(
                    "DELETE FROM especialidades WHERE id = ?")) {
                del.setInt(1, e.getId());
                del.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Especialidade excluída com sucesso!");
            return true;
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) { // violação de chave estrangeira
                JOptionPane.showMessageDialog(null,
                        "Não é possível excluir: há profissionais cadastrados nesta especialidade.");
                return false;
            }
            // Qualquer outro erro de banco sobe para a tela tratar.
            throw ex;
        } finally {
            fecharBanco();
        }
    }

    // R - Read (um registro pelo id). Preenche o objeto; retorna false se não achar.
    // Quando não acha, também mostra uma mensagem ao usuário.
    public boolean pesquisarEspecialidade(Especialidade e) throws Exception {
        try {
            abrirBanco();
            try (PreparedStatement sel = con.prepareStatement(
                    "SELECT id, nome FROM especialidades WHERE id = ?")) {
                sel.setInt(1, e.getId());
                try (ResultSet r = sel.executeQuery()) {
                    if (r.next()) {
                        // Achou: copia o nome para o próprio objeto recebido da tela.
                        e.setNome(r.getString("nome"));
                        return true;
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Especialidade não encontrada!");
            return false;
        } finally {
            fecharBanco();
        }
    }

    // R - Read (lista, com filtro opcional por nome)
    // pesquisar("") traz todas, ordenadas por nome: é assim que as telas de
    // profissionais preenchem o combo de especialidades.
    public ArrayList<Especialidade> pesquisar(String nome) throws Exception {
        ArrayList<Especialidade> lista = new ArrayList<>();
        try {
            abrirBanco();
            try (PreparedStatement sel = con.prepareStatement(
                    "SELECT id, nome FROM especialidades WHERE nome LIKE ? ORDER BY nome")) {
                // Os % fazem o LIKE achar o texto em qualquer parte do nome.
                sel.setString(1, "%" + nome + "%");
                try (ResultSet r = sel.executeQuery()) {
                    // Cada linha do resultado vira um objeto Especialidade na lista.
                    while (r.next()) {
                        lista.add(new Especialidade(r.getInt("id"), r.getString("nome")));
                    }
                }
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    // R - Read (um registro pelo nome exato). Retorna null se não achar.
    // Usado pelas telas de editar e excluir especialidade, que localizam o registro
    // pelo nome digitado. Aqui não há mensagem: quem chama decide o que fazer com null.
    public Especialidade buscarPorNome(String nome) throws Exception {
        try {
            abrirBanco();
            try (PreparedStatement sel = con.prepareStatement(
                    "SELECT id, nome FROM especialidades WHERE nome = ?")) {
                sel.setString(1, nome);
                try (ResultSet r = sel.executeQuery()) {
                    if (r.next()) {
                        return new Especialidade(r.getInt("id"), r.getString("nome"));
                    }
                }
            }
            return null;
        } finally {
            fecharBanco();
        }
    }

    // Fecha a conexão de verdade (o DAO original só fecha o PreparedStatement).
    // O @Override indica que este método substitui o fecharBanco() herdado de DAO
    // (polimorfismo): os finally desta classe chamam esta versão.
    @Override
    public void fecharBanco() throws Exception {
        // Só fecha se a conexão chegou a ser aberta e ainda não foi fechada.
        if (con != null && !con.isClosed()) {
            con.close();
            System.out.println("Encerrando conexão com o Banco");
        }
    }

    // Validação dos campos
    // Nome obrigatório (isBlank() recusa texto só com espaços) e com até 50
    // caracteres, o tamanho da coluna. Mostra a mensagem do primeiro problema e retorna false.
    private boolean dadosValidos(Especialidade e) {
        if (e.getNome() == null || e.getNome().isBlank()) {
            JOptionPane.showMessageDialog(null, "Informe o nome da especialidade.");
            return false;
        }
        if (e.getNome().length() > TAMANHO_MAXIMO_NOME) {
            JOptionPane.showMessageDialog(null,
                    "O nome pode ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
            return false;
        }
        return true;
    }
}
