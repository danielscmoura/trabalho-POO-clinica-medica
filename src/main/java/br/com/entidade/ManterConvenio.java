package br.com.entidade;

import br.com.controle.Convenio;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Centraliza as operações CRUD de Convênio e usa a conexão herdada de DAO.
 */
public class ManterConvenio extends DAO {

    /**
     * Valida os dados, evita nomes duplicados e insere um novo convênio no banco.
     */
    public void inserirConvenio(Convenio convenio) throws Exception {
        // Interrompe o cadastro antes de acessar o banco se o nome estiver vazio.
        if (!dadosValidos(convenio)) {
            return;
        }

        try {
            // A conexão e os objetos JDBC são herdados da classe DAO.
            abrirBanco();
            // A tabela possui nome UNIQUE, mas a verificação permite uma mensagem amigável.
            pst = con.prepareStatement("SELECT id FROM convenios WHERE nome = ?");
            pst.setString(1, convenio.getNome());
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Convênio já cadastrado anteriormente!");
                return;
            }

            // PreparedStatement separa o SQL dos valores, evitando concatenação no comando.
            pst = con.prepareStatement(
                    "INSERT INTO convenios (nome, telefone) VALUES (?, ?)");
            pst.setString(1, convenio.getNome());
            pst.setString(2, convenio.getTelefone());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Convênio inserido com sucesso!");
        } finally {
            // Executa mesmo se ocorrer erro ou retorno antecipado dentro do try.
            fecharBanco();
        }
    }

    /**
     * Atualiza nome e telefone do convênio selecionado, sem permitir duplicidade de nome.
     */
    public void editarConvenio(Convenio convenio) throws Exception {
        // O ID identifica qual registro existente receberá a alteração.
        if (convenio.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um convênio para alterar.");
            return;
        }
        if (!dadosValidos(convenio)) {
            return;
        }

        try {
            abrirBanco();
            // O "id <> ?" permite manter o próprio nome ao editar o mesmo convênio.
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

    /**
     * Exclui o convênio pelo ID. Se ele estiver ligado a uma consulta, trata o erro
     * de chave estrangeira e informa que a exclusão não é permitida.
     */
    public void deletarConvenio(Convenio convenio) throws Exception {
        if (convenio.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um convênio para excluir.");
            return;
        }

        try {
            abrirBanco();
            // A exclusão usa o ID, pois ele é a chave primária do registro.
            pst = con.prepareStatement("DELETE FROM convenios WHERE id = ?");
            pst.setInt(1, convenio.getId());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Convênio excluído com sucesso!");
        } catch (SQLException e) {
            // Código 1451 ocorre quando uma chave estrangeira ainda referencia o convênio.
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

    /**
     * Busca um convênio pelo ID e preenche o mesmo objeto recebido como parâmetro.
     * Retorna true quando encontra o registro e false caso contrário.
     */
    public boolean pesquisarConvenio(Convenio convenio) throws Exception {
        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id, nome, telefone FROM convenios WHERE id = ?");
            pst.setInt(1, convenio.getId());
            rs = pst.executeQuery();

            if (rs.next()) {
                // Os dados retornados pelo ResultSet preenchem o objeto recebido pela tela.
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

    /**
     * Lista convênios aplicando filtros parciais de nome e telefone para a tela de listagem.
     */
    public ArrayList<Convenio> pesquisar(String nome, String telefone) throws Exception {
        // A lista será usada pela JTable da tela de listagem.
        ArrayList<Convenio> convenios = new ArrayList<>();

        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id, nome, telefone FROM convenios "
                    + "WHERE nome LIKE ? AND COALESCE(telefone, '') LIKE ? "
                    + "ORDER BY nome");
            // Os símbolos % permitem pesquisar apenas parte do texto digitado.
            pst.setString(1, "%" + nome + "%");
            pst.setString(2, "%" + telefone + "%");
            rs = pst.executeQuery();

            while (rs.next()) {
                // Cada linha retornada pelo banco se transforma em um objeto Convenio.
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

    /** Valida o único campo obrigatório antes de gravar ou alterar um convênio. */
    private boolean dadosValidos(Convenio convenio) {
        if (convenio.getNome() == null || convenio.getNome().isBlank()) {
            JOptionPane.showMessageDialog(null, "Informe o nome do convênio.");
            return false;
        }
        return true;
    }
}
