package br.com.entidade;

import br.com.controle.Consulta;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * DAO da tabela "consultas": agenda, altera, exclui e lista consultas.
 *
 * A tabela consultas liga as demais por chaves estrangeiras:
 * id_paciente -> pacientes(id), id_profissional -> profissionais(id) e
 * id_convenio -> convenios(id). O convênio é opcional (NULL = consulta particular).
 * A restrição UNIQUE uk_profissional_horario (id_profissional, data_consulta, horario)
 * impede, no próprio banco, dois agendamentos do mesmo profissional no mesmo horário.
 * Herda de DAO a conexão (con, pst, rs) e os métodos abrirBanco()/fecharBanco().
 */
public class ManterConsulta extends DAO {

    /**
     * Agenda uma nova consulta.
     * Regras: paciente, profissional, data e horário são obrigatórios, e o
     * profissional não pode ter outra consulta na mesma data e horário (choque
     * de horário). Se alguma regra falhar, mostra a mensagem e não grava nada.
     * Erros de banco (ex.: id de paciente inexistente, que viola a FK) são
     * repassados para a tela.
     */
    public void inserirConsulta(Consulta c) throws Exception {
        // Valida os campos antes de abrir a conexão.
        if (!dadosValidos(c)) {
            return;
        }
        try {
            abrirBanco();
            
            // Regra de negócio: O médico não pode ter duas consultas no mesmo dia e horário
            // (o banco também garante isso com a UNIQUE uk_profissional_horario; checar
            // antes permite mostrar uma mensagem amigável em vez de um erro de SQL).
            pst = con.prepareStatement("SELECT id FROM consultas WHERE id_profissional = ? AND data_consulta = ? AND horario = ?");
            pst.setInt(1, c.getIdProfissional());
            // Date.valueOf/Time.valueOf convertem LocalDate/LocalTime do modelo para
            // os tipos java.sql.Date/Time que o JDBC usa nas colunas DATE e TIME.
            pst.setDate(2, Date.valueOf(c.getDataConsulta()));
            pst.setTime(3, Time.valueOf(c.getHorario()));
            rs = pst.executeQuery();
            
            if (rs.next()) {
                // Achou uma linha: o horário já está ocupado, então cancela o agendamento.
                JOptionPane.showMessageDialog(null, "Este profissional já possui uma consulta agendada para esta data e horário!");
                return;
            }

            // Monta o INSERT com ? (PreparedStatement): os valores vão separados do SQL,
            // o que evita SQL injection. O id é gerado pelo AUTO_INCREMENT.
            pst = con.prepareStatement(
                    "INSERT INTO consultas (id_paciente, id_profissional, id_convenio, data_consulta, horario, situacao) VALUES (?, ?, ?, ?, ?, ?)");
            pst.setInt(1, c.getIdPaciente());
            pst.setInt(2, c.getIdProfissional());
            
            // Tratamento do LEFT JOIN (Convênio nulo para consultas particulares)
            // Sem convênio (null ou 0), grava NULL em id_convenio com setNull: setInt não
            // aceita null, e o valor 0 violaria a FK, pois não existe convênio com id 0.
            if (c.getIdConvenio() == null || c.getIdConvenio() == 0) {
                pst.setNull(3, Types.INTEGER);
            } else {
                pst.setInt(3, c.getIdConvenio());
            }
            
            pst.setDate(4, Date.valueOf(c.getDataConsulta()));
            pst.setTime(5, Time.valueOf(c.getHorario()));
            // Se a situação não vier preenchida, usa "Agendada" (o mesmo DEFAULT da coluna).
            pst.setString(6, c.getSituacao() != null ? c.getSituacao() : "Agendada");
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Consulta agendada com sucesso!");
        } finally {
            // O finally roda sempre: no sucesso, no return antecipado ou em erro.
            fecharBanco();
        }
    }

    /**
     * Altera uma consulta existente (paciente, profissional, convênio, data,
     * horário e situação), identificada pelo id.
     * Regras: precisa haver uma consulta selecionada (id diferente de 0), os campos
     * obrigatórios devem estar preenchidos e o novo horário não pode chocar com
     * outra consulta do mesmo profissional. Se alguma regra falhar, mostra a
     * mensagem e não altera nada. Erros de banco são repassados para a tela.
     */
    public void editarConsulta(Consulta c) throws Exception {
        // id 0 significa que nenhuma consulta foi carregada na tela.
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
            // (sem o "id <> ?", salvar sem mudar o horário acusaria choque com ela mesma).
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

            // Atualiza todas as colunas; o WHERE id = ? (último ?) garante que só essa consulta muda.
            pst = con.prepareStatement(
                    "UPDATE consultas SET id_paciente = ?, id_profissional = ?, id_convenio = ?, data_consulta = ?, horario = ?, situacao = ? WHERE id = ?");
            pst.setInt(1, c.getIdPaciente());
            pst.setInt(2, c.getIdProfissional());
            
            // Mesmo tratamento do cadastro: sem convênio, grava NULL em id_convenio.
            if (c.getIdConvenio() == null || c.getIdConvenio() == 0) {
                pst.setNull(3, Types.INTEGER);
            } else {
                pst.setInt(3, c.getIdConvenio());
            }
            
            pst.setDate(4, Date.valueOf(c.getDataConsulta()));
            pst.setTime(5, Time.valueOf(c.getHorario()));
            // Na edição, a situação é gravada como veio da tela (campo de texto livre).
            pst.setString(6, c.getSituacao());
            pst.setInt(7, c.getId());
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Consulta alterada com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    /**
     * Exclui a consulta pelo id.
     * Como nenhuma outra tabela aponta para consultas, não há bloqueio de chave
     * estrangeira aqui (ao contrário de pacientes, profissionais e convênios).
     * Erros de banco são repassados para a tela.
     */
    public void deletarConsulta(Consulta c) throws Exception {
        if (c.getId() == 0) {
            JOptionPane.showMessageDialog(null, "Selecione uma consulta para excluir.");
            return;
        }
        try {
            abrirBanco();
            // Exclui pela chave primária: apaga somente a consulta selecionada.
            pst = con.prepareStatement("DELETE FROM consultas WHERE id = ?");
            pst.setInt(1, c.getId());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Consulta excluída com sucesso!");
        } finally {
            fecharBanco();
        }
    }

    // LISTAGEM COM INNER JOIN E LEFT JOIN EXIGIDA NA TAREFA
    /**
     * Lista as consultas cujo nome do paciente contém o texto informado
     * (texto vazio traz todas), ordenadas por data e horário.
     * INNER JOIN com pacientes e profissionais: toda consulta tem os dois
     * (colunas NOT NULL). LEFT JOIN com convenios: a consulta particular tem
     * id_convenio NULL e seria descartada por um INNER JOIN.
     * Retorna a lista (vazia se nada for encontrado) para a tela de listagem.
     */
    public ArrayList<Consulta> pesquisar(String nomePacienteBusca) throws Exception {
        ArrayList<Consulta> consultas = new ArrayList<>();
        try {
            abrirBanco();
            // A query traz os IDs originais, mas também busca os nomes das outras tabelas
            // (c, p, prof e conv são apelidos das tabelas; COALESCE troca o nome nulo
            // do convênio, nas consultas particulares, pelo texto 'Particular').
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
            // Os % nos dois lados fazem o LIKE achar o texto em qualquer parte do nome.
            pst.setString(1, "%" + nomePacienteBusca + "%");
            rs = pst.executeQuery();
            
            while (rs.next()) {
                // Cada linha do resultado vira um objeto Consulta na lista.
                Consulta c = new Consulta();
                c.setId(rs.getInt("id"));
                c.setIdPaciente(rs.getInt("id_paciente"));
                c.setIdProfissional(rs.getInt("id_profissional"));
                
                // getInt devolve 0 quando a coluna é NULL; wasNull() informa se o
                // valor lido era NULL, e então o modelo recebe null (consulta particular).
                int idConv = rs.getInt("id_convenio");
                if (!rs.wasNull()) {
                    c.setIdConvenio(idConv);
                } else {
                    c.setIdConvenio(null);
                }
                
                // Converte DATE/TIME do banco (java.sql) para LocalDate/LocalTime do modelo.
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
    // (colunas NOT NULL da tabela; id 0 significa paciente/profissional não informado).
    // Mostra a mensagem do primeiro campo inválido e retorna false.
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