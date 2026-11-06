package br.com.entidade;

import br.com.controle.Convenio;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ManterConvenio extends DAO {

    public void inserirConvenio(Convenio convenio) throws Exception {
        if (!dadosValidos(convenio)) {
            return;
        }

        try {
            abrirBanco();
            pst = con.prepareStatement("SELECT id FROM convenios WHERE nome = ?");
            pst.setString(1, convenio.getNome());
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Convênio já cadastrado anteriormente!");
                return;
            }

            pst = con.prepareStatement(
                    "INSERT INTO convenios (nome, telefone) VALUES (?, ?)");
            pst.setString(1, convenio.getNome());
            pst.setString(2, convenio.getTelefone());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Convênio inserido com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    public void editarConvenio(Convenio convenio) throws Exception {
        if (convenio.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um convênio para alterar.");
            return;
        }
        if (!dadosValidos(convenio)) {
            return;
        }

        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id FROM convenios WHERE nome = ? AND id <> ?");
            pst.setString(1, convenio.getNome());
            pst.setInt(2, convenio.getId());
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Esse nome já pertence a outro convênio!");
                return;
            }

            pst = con.prepareStatement(
                    "UPDATE convenios SET nome = ?, telefone = ? WHERE id = ?");
            pst.setString(1, convenio.getNome());
            pst.setString(2, convenio.getTelefone());
            pst.setInt(3, convenio.getId());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Convênio alterado com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    public void deletarConvenio(Convenio convenio) throws Exception {
        if (convenio.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um convênio para excluir.");
            return;
        }

        try {
            abrirBanco();
            pst = con.prepareStatement("DELETE FROM convenios WHERE id = ?");
            pst.setInt(1, convenio.getId());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Convênio excluído com sucesso!");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Não é possível excluir: este convênio possui consultas cadastradas.");
            } else {
                throw e;
            }
        } finally {
            fecharBanco();
        }
    }

    public boolean pesquisarConvenio(Convenio convenio) throws Exception {
        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id, nome, telefone FROM convenios WHERE id = ?");
            pst.setInt(1, convenio.getId());
            rs = pst.executeQuery();

            if (rs.next()) {
                convenio.setNome(rs.getString("nome"));
                convenio.setTelefone(rs.getString("telefone"));
                return true;
            }

            JOptionPane.showMessageDialog(null, "Convênio não encontrado!");
            return false;
        } finally {
            fecharBanco();
        }
    }

    public ArrayList<Convenio> pesquisar(String nome, String telefone) throws Exception {
        ArrayList<Convenio> convenios = new ArrayList<>();

        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id, nome, telefone FROM convenios "
                    + "WHERE nome LIKE ? AND COALESCE(telefone, '') LIKE ? "
                    + "ORDER BY nome");
            pst.setString(1, "%" + nome + "%");
            pst.setString(2, "%" + telefone + "%");
            rs = pst.executeQuery();

            while (rs.next()) {
                Convenio convenio = new Convenio();
                convenio.setId(rs.getInt("id"));
                convenio.setNome(rs.getString("nome"));
                convenio.setTelefone(rs.getString("telefone"));
                convenios.add(convenio);
            }
        } finally {
            fecharBanco();
        }

        return convenios;
    }

    private boolean dadosValidos(Convenio convenio) {
        if (convenio.getNome() == null || convenio.getNome().isBlank()) {
            JOptionPane.showMessageDialog(null, "Informe o nome do convênio.");
            return false;
        }
        return true;
    }
}
