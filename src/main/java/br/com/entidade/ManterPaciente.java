/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.entidade;

import br.com.controle.Paciente;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * DAO da tabela "pacientes": faz o CRUD (inserir, editar, excluir e pesquisar).
 *
 * Herda de DAO a conexão (con, pst, rs) e os métodos abrirBanco()/fecharBanco().
 * A coluna cpf é UNIQUE, e a tabela é referenciada pela FK consultas.id_paciente,
 * por isso um paciente que já tem consultas não pode ser excluído.
 * As mensagens para o usuário são exibidas com JOptionPane.
 */
public class ManterPaciente extends DAO {

    /**
     * Cadastra um novo paciente.
     * Regras: nome e CPF são obrigatórios e o CPF não pode se repetir.
     * Se a validação falhar ou o CPF já existir, mostra a mensagem e não grava nada.
     * Erros de banco são repassados (throws) para a tela tratar.
     */
    public void inserirPaciente(Paciente p) throws Exception {
        // Valida os campos antes mesmo de abrir a conexão.
        if (!dadosValidos(p)) {
            return;
        }
        try {
            abrirBanco();
            // Verifica duplicidade de CPF. A coluna já é UNIQUE no banco, mas checar
            // antes permite mostrar uma mensagem amigável em vez de um erro de SQL.
            pst = con.prepareStatement("SELECT id FROM pacientes WHERE cpf = ?");
            pst.setString(1, p.getCpf());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "CPF já cadastrado anteriormente!");
                return;
            }

            // Monta o INSERT com ? (PreparedStatement): os valores são enviados
            // separados do SQL, o que evita SQL injection. O id é gerado pelo AUTO_INCREMENT.
            pst = con.prepareStatement(
                    "INSERT INTO pacientes (nome, cpf, email, telefone) VALUES (?, ?, ?, ?)");
            // Preenche os ? na ordem em que aparecem no comando (1, 2, 3, 4).
            pst.setString(1, p.getNome());
            pst.setString(2, p.getCpf());
            pst.setString(3, p.getEmail());
            pst.setString(4, p.getTelefone());
            // executeUpdate é usado para INSERT, UPDATE e DELETE.
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Paciente inserido com sucesso!");
        } finally {
            // O finally roda sempre: no sucesso, no return antecipado ou em erro.
            fecharBanco();
        }
    }

    /**
     * Altera os dados do paciente identificado pelo id.
     * Regras: é preciso ter um paciente selecionado (id diferente de 0), os campos
     * obrigatórios devem estar preenchidos e o novo CPF não pode pertencer a outro
     * paciente. Em qualquer desses casos mostra a mensagem e não altera nada.
     */
    public void editarPaciente(Paciente p) throws Exception {
        // id 0 significa que nenhum paciente foi carregado na tela.
        if (p.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um paciente para alterar.");
            return;
        }
        if (!dadosValidos(p)) {
            return;
        }
        try {
            abrirBanco();
            // CPF não pode pertencer a OUTRO paciente
            // (o "id <> ?" deixa o paciente manter o próprio CPF ao editar).
            pst = con.prepareStatement("SELECT id FROM pacientes WHERE cpf = ? AND id <> ?");
            pst.setString(1, p.getCpf());
            pst.setInt(2, p.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Esse CPF já pertence a outro paciente!");
                return;
            }

            // Atualiza todos os campos; o WHERE id = ? garante que só essa linha muda.
            pst = con.prepareStatement(
                    "UPDATE pacientes SET nome = ?, cpf = ?, email = ?, telefone = ? WHERE id = ?");
            pst.setString(1, p.getNome());
            pst.setString(2, p.getCpf());
            pst.setString(3, p.getEmail());
            pst.setString(4, p.getTelefone());
            pst.setInt(5, p.getId());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Paciente alterado com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    /**
     * Exclui o paciente pelo id.
     * Se ele tiver consultas, o MySQL bloqueia a exclusão por causa da FK
     * consultas.id_paciente (erro 1451); nesse caso mostra uma mensagem explicando.
     * Outros erros de banco são repassados para a tela.
     */
    public void deletarPaciente(Paciente p) throws Exception {
        if (p.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione um paciente para excluir.");
            return;
        }
        try {
            abrirBanco();
            pst = con.prepareStatement("DELETE FROM pacientes WHERE id = ?");
            pst.setInt(1, p.getId());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Paciente excluído com sucesso!");
        } catch (SQLException e) {
            // 1451 = existe consulta apontando para este paciente (violação de FK).
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Não é possível excluir: este paciente possui consultas cadastradas.");
            } else {
                // Qualquer outro erro é relançado para a tela tratar.
                throw e;
            }
        } finally {
            fecharBanco();
        }
    }

    // Preenche o objeto p pelo id. Retorna false se não encontrar.
    // Nesse caso também avisa o usuário com uma mensagem.
    public boolean pesquisarPaciente(Paciente p) throws Exception {
        try {
            abrirBanco();
            // Busca pela chave primária: retorna no máximo uma linha.
            pst = con.prepareStatement(
                    "SELECT id, nome, cpf, email, telefone FROM pacientes WHERE id = ?");
            pst.setInt(1, p.getId());
            rs = pst.executeQuery();
            // rs.next() avança para a primeira linha; é false se o SELECT veio vazio.
            if (rs.next()) {
                // Copia as colunas do ResultSet para o próprio objeto recebido da tela.
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                p.setEmail(rs.getString("email"));
                p.setTelefone(rs.getString("telefone"));
                return true;
            }
            JOptionPane.showMessageDialog(null, "Paciente não encontrado!");
            return false;
        } finally {
            fecharBanco();
        }
    }

    /**
     * Lista os pacientes filtrando por parte do nome, do CPF e do e-mail.
     * Filtros vazios não restringem nada, então pesquisar("", "", "") traz todos.
     * Retorna a lista ordenada por nome (vazia se nada for encontrado), usada
     * para preencher a JTable da tela de listagem.
     */
    public ArrayList<Paciente> pesquisar(String nome, String cpf, String email) throws Exception {
        ArrayList<Paciente> pacientes = new ArrayList<>();
        try {
            abrirBanco();
            // COALESCE(email, '') troca NULL por texto vazio: sem isso, pacientes sem
            // e-mail sumiriam do resultado, porque NULL LIKE '%%' não é verdadeiro.
            pst = con.prepareStatement(
                    "SELECT id, nome, cpf, email, telefone FROM pacientes "
                    + "WHERE nome LIKE ? AND cpf LIKE ? AND COALESCE(email, '') LIKE ? "
                    + "ORDER BY nome");
            // Os % antes e depois fazem o LIKE achar o texto em qualquer posição.
            pst.setString(1, "%" + nome + "%");
            pst.setString(2, "%" + cpf + "%");
            pst.setString(3, "%" + email + "%");
            rs = pst.executeQuery();
            // Percorre o ResultSet: cada linha retornada vira um objeto Paciente na lista.
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                p.setEmail(rs.getString("email"));
                p.setTelefone(rs.getString("telefone"));
                pacientes.add(p);
            }
        } finally {
            fecharBanco();
        }
        return pacientes;
    }

    // Validação dos campos obrigatórios
    // (nome e cpf são NOT NULL na tabela; isBlank() também recusa texto só com espaços).
    // Mostra a mensagem do primeiro campo inválido e retorna false.
    private boolean dadosValidos(Paciente p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            JOptionPane.showMessageDialog(null, "Informe o nome do paciente.");
            return false;
        }
        if (p.getCpf() == null || p.getCpf().isBlank()) {
            JOptionPane.showMessageDialog(null, "Informe o CPF do paciente.");
            return false;
        }
        return true;
    }
}
