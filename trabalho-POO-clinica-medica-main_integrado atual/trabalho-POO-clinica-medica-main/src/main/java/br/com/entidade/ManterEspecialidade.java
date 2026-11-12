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
 */
public class ManterEspecialidade extends DAO {

    private static final int TAMANHO_MAXIMO_NOME = 50; // VARCHAR(50) na tabela

    // C - Create
    public boolean inserirEspecialidade(Especialidade e) throws Exception {
        if (!dadosValidos(e)) {
            return false;
        }
        try {
            abrirBanco();
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
            throw ex;
        } finally {
            fecharBanco();
        }
    }

    // R - Read (um registro pelo id). Preenche o objeto; retorna false se não achar.
    public boolean pesquisarEspecialidade(Especialidade e) throws Exception {
        try {
            abrirBanco();
            try (PreparedStatement sel = con.prepareStatement(
                    "SELECT id, nome FROM especialidades WHERE id = ?")) {
                sel.setInt(1, e.getId());
                try (ResultSet r = sel.executeQuery()) {
                    if (r.next()) {
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
    public ArrayList<Especialidade> pesquisar(String nome) throws Exception {
        ArrayList<Especialidade> lista = new ArrayList<>();
        try {
            abrirBanco();
            try (PreparedStatement sel = con.prepareStatement(
                    "SELECT id, nome FROM especialidades WHERE nome LIKE ? ORDER BY nome")) {
                sel.setString(1, "%" + nome + "%");
                try (ResultSet r = sel.executeQuery()) {
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
    @Override
    public void fecharBanco() throws Exception {
        if (con != null && !con.isClosed()) {
            con.close();
            System.out.println("Encerrando conexão com o Banco");
        }
    }

    // Validação dos campos
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
