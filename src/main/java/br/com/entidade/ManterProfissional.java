/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.entidade;

import br.com.controle.Especialidade;
import br.com.controle.Profissional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * CRUD da tabela "profissionais" (Create, Read, Update, Delete).
 *
 * A tabela tem a chave estrangeira id_especialidade -> especialidades(id).
 * Nas consultas é feito INNER JOIN com "especialidades" para trazer o NOME
 * da especialidade, e não apenas o código.
 * A tabela também é referenciada pela FK consultas.id_profissional, por isso
 * um profissional que já tem consultas não pode ser excluído.
 *
 * Os métodos que alteram o banco devolvem true quando a operação deu certo,
 * para a tela saber se deve limpar os campos / atualizar a tabela.
 */
public class ManterProfissional extends DAO {

    // SELECT base com o JOIN, reaproveitado nas pesquisas
    // Os apelidos (AS id_especialidade, AS nome_especialidade) diferenciam e.id e
    // e.nome de p.id e p.nome, que têm o mesmo nome de coluna nas duas tabelas.
    private static final String SELECT_COM_JOIN
            = "SELECT p.id, p.nome, p.email, "
            + "       e.id AS id_especialidade, e.nome AS nome_especialidade "
            + "FROM profissionais p "
            + "INNER JOIN especialidades e ON p.id_especialidade = e.id ";

    // ------------------------------------------------------------------
    // C - CREATE
    // ------------------------------------------------------------------
    /**
     * Cadastra um novo profissional.
     * Regras (dadosValidos): nome obrigatório com até 100 caracteres, e-mail
     * opcional mas em formato válido, e especialidade selecionada.
     * Se a especialidade tiver sido excluída nesse meio-tempo, o MySQL recusa
     * pela FK (erro 1452) e o método avisa o usuário.
     * Retorna true se gravou e false se alguma regra falhou; outros erros são repassados.
     */
    public boolean inserirProfissional(Profissional p) throws Exception {
        if (!dadosValidos(p)) {
            return false;
        }
        try {
            abrirBanco();
            // INSERT com ? (PreparedStatement evita SQL injection); o id vem do AUTO_INCREMENT.
            pst = con.prepareStatement(
                    "INSERT INTO profissionais (nome, email, id_especialidade) VALUES (?, ?, ?)");
            pst.setString(1, p.getNome());
            pst.setString(2, emailOuNulo(p.getEmail()));
            // Na FK id_especialidade vai só o id do objeto Especialidade associado.
            pst.setInt(3, p.getEspecialidade().getId());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Profissional inserido com sucesso!");
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) { // FK: especialidade não existe
                JOptionPane.showMessageDialog(null, "A especialidade selecionada não existe mais. Atualize a tela.");
                return false;
            }
            throw e;
        } finally {
            fecharBanco();
        }
    }

    // ------------------------------------------------------------------
    // U - UPDATE
    // ------------------------------------------------------------------
    /**
     * Altera nome, e-mail e especialidade do profissional identificado pelo id.
     * Aplica as mesmas validações do cadastro. Retorna false (com mensagem) se
     * nenhum profissional foi selecionado, se a validação falhar, se o id não
     * existir mais ou se a especialidade não existir (FK, erro 1452).
     * Retorna true se alterou; outros erros são repassados.
     */
    public boolean editarProfissional(Profissional p) throws Exception {
        if (p.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um profissional para alterar.");
            return false;
        }
        if (!dadosValidos(p)) {
            return false;
        }
        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "UPDATE profissionais SET nome = ?, email = ?, id_especialidade = ? WHERE id = ?");
            pst.setString(1, p.getNome());
            pst.setString(2, emailOuNulo(p.getEmail()));
            pst.setInt(3, p.getEspecialidade().getId());
            pst.setInt(4, p.getId());
            // executeUpdate devolve quantas linhas foram afetadas; 0 = o id não existe mais.
            if (pst.executeUpdate() == 0) {
                JOptionPane.showMessageDialog(null, "Profissional não encontrado!");
                return false;
            }
            JOptionPane.showMessageDialog(null, "Profissional alterado com sucesso!");
            return true;
        } catch (SQLException e) {
            // 1452 = a especialidade escolhida não existe mais (violação da FK).
            if (e.getErrorCode() == 1452) {
                JOptionPane.showMessageDialog(null, "A especialidade selecionada não existe mais. Atualize a tela.");
                return false;
            }
            throw e;
        } finally {
            fecharBanco();
        }
    }

    // ------------------------------------------------------------------
    // D - DELETE
    // ------------------------------------------------------------------
    /**
     * Exclui o profissional pelo id.
     * Retorna false (com mensagem) se nenhum foi selecionado, se o id não existe
     * mais ou se ele tem consultas, caso em que a FK consultas.id_profissional
     * bloqueia a exclusão (erro 1451). Retorna true se excluiu; outros erros são repassados.
     */
    public boolean deletarProfissional(Profissional p) throws Exception {
        if (p.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um profissional para excluir.");
            return false;
        }
        try {
            abrirBanco();
            pst = con.prepareStatement("DELETE FROM profissionais WHERE id = ?");
            pst.setInt(1, p.getId());
            if (pst.executeUpdate() == 0) {
                JOptionPane.showMessageDialog(null, "Profissional não encontrado!");
                return false;
            }
            JOptionPane.showMessageDialog(null, "Profissional excluído com sucesso!");
            return true;
        } catch (SQLException e) {
            // 1451 = existe linha em outra tabela (consultas) apontando para este profissional
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Não é possível excluir: este profissional possui consultas cadastradas.");
                return false;
            }
            throw e;
        } finally {
            fecharBanco();
        }
    }

    // ------------------------------------------------------------------
    // R - READ
    // ------------------------------------------------------------------

    // Preenche o objeto p pelo id. Retorna false se não encontrar.
    public boolean pesquisarProfissional(Profissional p) throws Exception {
        try {
            abrirBanco();
            // Reaproveita o SELECT com JOIN e acrescenta o filtro pela chave primária.
            pst = con.prepareStatement(SELECT_COM_JOIN + "WHERE p.id = ?");
            pst.setInt(1, p.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                p.setNome(rs.getString("nome"));
                p.setEmail(rs.getString("email"));
                p.setEspecialidade(new Especialidade(
                        rs.getInt("id_especialidade"), rs.getString("nome_especialidade")));
                return true;
            }
            JOptionPane.showMessageDialog(null, "Profissional não encontrado!");
            return false;
        } finally {
            fecharBanco();
        }
    }

    // Pesquisa com filtros. Campos vazios trazem tudo.
    // idEspecialidade = 0 significa "todas as especialidades".
    public ArrayList<Profissional> pesquisar(String nome, String email, int idEspecialidade) throws Exception {
        ArrayList<Profissional> profissionais = new ArrayList<>();
        try {
            abrirBanco();
            // (? = 0 OR p.id_especialidade = ?): se o id passado for 0, a primeira
            // condição é verdadeira e o filtro de especialidade é ignorado; por isso
            // o mesmo valor preenche dois ?. COALESCE(p.email, '') mantém no
            // resultado os profissionais sem e-mail (NULL LIKE '%%' não é verdadeiro).
            pst = con.prepareStatement(SELECT_COM_JOIN
                    + "WHERE p.nome LIKE ? AND COALESCE(p.email, '') LIKE ? "
                    + "AND (? = 0 OR p.id_especialidade = ?) "
                    + "ORDER BY p.nome");
            pst.setString(1, "%" + nome + "%");
            pst.setString(2, "%" + email + "%");
            pst.setInt(3, idEspecialidade);
            pst.setInt(4, idEspecialidade);
            rs = pst.executeQuery();
            // Cada linha do resultado vira um objeto Profissional na lista.
            while (rs.next()) {
                profissionais.add(montarProfissional(rs));
            }
        } finally {
            fecharBanco();
        }
        return profissionais;
    }

    // Lista todos os profissionais (com o nome da especialidade)
    public ArrayList<Profissional> listar() throws Exception {
        return pesquisar("", "", 0);
    }

    // ------------------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------------------

    // Transforma a linha atual do ResultSet em um objeto Profissional
    private Profissional montarProfissional(ResultSet rs) throws SQLException {
        Especialidade e = new Especialidade(rs.getInt("id_especialidade"), rs.getString("nome_especialidade"));
        return new Profissional(rs.getInt("id"), rs.getString("nome"), rs.getString("email"), e);
    }

    // E-mail é opcional: se vier vazio grava NULL no banco
    private String emailOuNulo(String email) {
        return (email == null || email.isBlank()) ? null : email;
    }

    // Validação dos campos obrigatórios
    // Os limites de 100 caracteres são o tamanho das colunas VARCHAR(100); o e-mail
    // só é conferido se foi preenchido. Mostra a mensagem do primeiro problema e retorna false.
    private boolean dadosValidos(Profissional p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            JOptionPane.showMessageDialog(null, "Informe o nome do profissional.");
            return false;
        }
        if (p.getNome().length() > 100) {
            JOptionPane.showMessageDialog(null, "O nome pode ter no máximo 100 caracteres.");
            return false;
        }
        String email = p.getEmail();
        if (email != null && !email.isBlank()) {
            if (email.length() > 100) {
                JOptionPane.showMessageDialog(null, "O e-mail pode ter no máximo 100 caracteres.");
                return false;
            }
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(null, "Informe um e-mail válido.");
                return false;
            }
        }
        if (p.getEspecialidade() == null || p.getEspecialidade().getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione a especialidade do profissional.");
            return false;
        }
        return true;
    }
}
