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

public class ManterPaciente extends DAO {

    public void inserirPaciente(Paciente p) throws Exception {
        if (!dadosValidos(p)) {
            return;
        }
        try {
            abrirBanco();
            pst = con.prepareStatement("SELECT id FROM pacientes WHERE cpf = ?");
            pst.setString(1, p.getCpf());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "CPF já cadastrado anteriormente!");
                return;
            }

            pst = con.prepareStatement(
                    "INSERT INTO pacientes (nome, cpf, email, telefone) VALUES (?, ?, ?, ?)");
            pst.setString(1, p.getNome());
            pst.setString(2, p.getCpf());
            pst.setString(3, p.getEmail());
            pst.setString(4, p.getTelefone());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Paciente inserido com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    public void editarPaciente(Paciente p) throws Exception {
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
            pst = con.prepareStatement("SELECT id FROM pacientes WHERE cpf = ? AND id <> ?");
            pst.setString(1, p.getCpf());
            pst.setInt(2, p.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Esse CPF já pertence a outro paciente!");
                return;
            }

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
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Não é possível excluir: este paciente possui consultas cadastradas.");
            } else {
                throw e;
            }
        } finally {
            fecharBanco();
        }
    }

    // Preenche o objeto p pelo id. Retorna false se não encontrar.
    public boolean pesquisarPaciente(Paciente p) throws Exception {
        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id, nome, cpf, email, telefone FROM pacientes WHERE id = ?");
            pst.setInt(1, p.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
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

    public ArrayList<Paciente> pesquisar(String nome, String cpf, String email) throws Exception {
        ArrayList<Paciente> pacientes = new ArrayList<>();
        try {
            abrirBanco();
            pst = con.prepareStatement(
                    "SELECT id, nome, cpf, email, telefone FROM pacientes "
                    + "WHERE nome LIKE ? AND cpf LIKE ? AND COALESCE(email, '') LIKE ? "
                    + "ORDER BY nome");
            pst.setString(1, "%" + nome + "%");
            pst.setString(2, "%" + cpf + "%");
            pst.setString(3, "%" + email + "%");
            rs = pst.executeQuery();
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
