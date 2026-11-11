package br.com.entidade;

import br.com.controle.Consulta;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ManterConsulta extends DAO {

    public void inserirConsulta(Consulta c) throws Exception {
        if (!dadosValidos(c)) {
            return;
        }
        try {
            abrirBanco();
            
            // Regra de negócio: O médico não pode ter duas consultas no mesmo dia e horário
            pst = con.prepareStatement("SELECT id FROM consultas WHERE id_profissional = ? AND data_consulta = ? AND horario = ?");
            pst.setInt(1, c.getIdProfissional());
            pst.setDate(2, Date.valueOf(c.getDataConsulta()));
            pst.setTime(3, Time.valueOf(c.getHorario()));
            rs = pst.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Este profissional já possui uma consulta agendada para esta data e horário!");
                return;
            }

            pst = con.prepareStatement(
                    "INSERT INTO consultas (id_paciente, id_profissional, id_convenio, data_consulta, horario, situacao) VALUES (?, ?, ?, ?, ?, ?)");
            pst.setInt(1, c.getIdPaciente());
            pst.setInt(2, c.getIdProfissional());
            
            // Tratamento do LEFT JOIN (Convênio nulo para consultas particulares)
            if (c.getIdConvenio() == null || c.getIdConvenio() == 0) {
                pst.setNull(3, Types.INTEGER);
            } else {
                pst.setInt(3, c.getIdConvenio());
            }
            
            pst.setDate(4, Date.valueOf(c.getDataConsulta()));
            pst.setTime(5, Time.valueOf(c.getHorario()));
            pst.setString(6, c.getSituacao() != null ? c.getSituacao() : "Agendada");
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Consulta agendada com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    public void editarConsulta(Consulta c) throws Exception {
        if (c.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma consulta para alterar.");
            return;
        }
        if (!dadosValidos(c)) {
            return;
        }
        try {
            abrirBanco();
            
            // Verifica choque de horário, mas ignora a própria consulta que está sendo editada
            pst = con.prepareStatement("SELECT id FROM consultas WHERE id_profissional = ? AND data_consulta = ? AND horario = ? AND id <> ?");
            pst.setInt(1, c.getIdProfissional());
            pst.setDate(2, Date.valueOf(c.getDataConsulta()));
            pst.setTime(3, Time.valueOf(c.getHorario()));
            pst.setInt(4, c.getId());
            rs = pst.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Este profissional já possui outra consulta neste horário!");
                return;
            }

            pst = con.prepareStatement(
                    "UPDATE consultas SET id_paciente = ?, id_profissional = ?, id_convenio = ?, data_consulta = ?, horario = ?, situacao = ? WHERE id = ?");
            pst.setInt(1, c.getIdPaciente());
            pst.setInt(2, c.getIdProfissional());
            
            if (c.getIdConvenio() == null || c.getIdConvenio() == 0) {
                pst.setNull(3, Types.INTEGER);
            } else {
                pst.setInt(3, c.getIdConvenio());
            }
            
            pst.setDate(4, Date.valueOf(c.getDataConsulta()));
            pst.setTime(5, Time.valueOf(c.getHorario()));
            pst.setString(6, c.getSituacao());
            pst.setInt(7, c.getId());
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Consulta alterada com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    public void deletarConsulta(Consulta c) throws Exception {
        if (c.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma consulta para excluir.");
            return;
        }
        try {
            abrirBanco();
            pst = con.prepareStatement("DELETE FROM consultas WHERE id = ?");
            pst.setInt(1, c.getId());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Consulta excluída com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    // LISTAGEM COM INNER JOIN E LEFT JOIN EXIGIDA NA TAREFA
    public ArrayList<Consulta> pesquisar(String nomePacienteBusca) throws Exception {
        ArrayList<Consulta> consultas = new ArrayList<>();
        try {
            abrirBanco();
            // A query traz os IDs originais, mas também busca os nomes das outras tabelas
            String sql = "SELECT c.id, c.id_paciente, c.id_profissional, c.id_convenio, "
                       + "c.data_consulta, c.horario, c.situacao, "
                       + "p.nome AS nome_paciente, prof.nome AS nome_profissional, "
                       + "COALESCE(conv.nome, 'Particular') AS nome_convenio "
                       + "FROM consultas c "
                       + "INNER JOIN pacientes p ON c.id_paciente = p.id "
                       + "INNER JOIN profissionais prof ON c.id_profissional = prof.id "
                       + "LEFT JOIN convenios conv ON c.id_convenio = conv.id "
                       + "WHERE p.nome LIKE ? "
                       + "ORDER BY c.data_consulta, c.horario";
                       
            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + nomePacienteBusca + "%");
            rs = pst.executeQuery();
            
            while (rs.next()) {
                Consulta c = new Consulta();
                c.setId(rs.getInt("id"));
                c.setIdPaciente(rs.getInt("id_paciente"));
                c.setIdProfissional(rs.getInt("id_profissional"));
                
                int idConv = rs.getInt("id_convenio");
                if (!rs.wasNull()) {
                    c.setIdConvenio(idConv);
                } else {
                    c.setIdConvenio(null);
                }
                
                c.setDataConsulta(rs.getDate("data_consulta").toLocalDate());
                c.setHorario(rs.getTime("horario").toLocalTime());
                c.setSituacao(rs.getString("situacao"));
                
                // Guardando os nomes resgatados pelo JOIN nos novos atributos
                c.setNomePaciente(rs.getString("nome_paciente"));
                c.setNomeProfissional(rs.getString("nome_profissional"));
                c.setNomeConvenio(rs.getString("nome_convenio"));
                
                consultas.add(c);
            }
        } finally {
            fecharBanco();
        }
        return consultas;
    }

    // Validação dos campos obrigatórios
    private boolean dadosValidos(Consulta c) {
        if (c.getIdPaciente() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione o paciente.");
            return false;
        }
        if (c.getIdProfissional() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione o profissional.");
            return false;
        }
        if (c.getDataConsulta() == null) {
            JOptionPane.showMessageDialog(null, "Informe a data da consulta.");
            return false;
        }
        if (c.getHorario() == null) {
            JOptionPane.showMessageDialog(null, "Informe o horário da consulta.");
            return false;
        }
        return true;
    }
}