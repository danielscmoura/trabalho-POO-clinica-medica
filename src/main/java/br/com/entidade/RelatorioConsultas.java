package br.com.entidade;

import br.com.controle.LinhaRelatorioConsulta;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RelatorioConsultas extends DAO {

    private static final DateTimeFormatter FORMATO_DATA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORARIO
            = DateTimeFormatter.ofPattern("HH:mm");

    public ArrayList<LinhaRelatorioConsulta> buscarPorPaciente(String nomePaciente)
            throws Exception {
        return buscar(nomePaciente, true);
    }

    public ArrayList<LinhaRelatorioConsulta> buscarPorProfissional(String nomeProfissional)
            throws Exception {
        return buscar(nomeProfissional, false);
    }

    private ArrayList<LinhaRelatorioConsulta> buscar(String nome, boolean filtrarPorPaciente)
            throws Exception {
        ArrayList<LinhaRelatorioConsulta> linhas = new ArrayList<>();
        String campoFiltro = filtrarPorPaciente ? "pa.nome" : "pr.nome";

        String sql = "SELECT c.id, "
                + "pa.nome AS paciente, "
                + "pr.nome AS profissional, "
                + "e.nome AS especialidade, "
                + "COALESCE(cv.nome, 'Particular') AS convenio, "
                + "c.data_consulta, c.horario, c.situacao "
                + "FROM consultas c "
                + "INNER JOIN pacientes pa ON c.id_paciente = pa.id "
                + "INNER JOIN profissionais pr ON c.id_profissional = pr.id "
                + "INNER JOIN especialidades e ON pr.id_especialidade = e.id "
                + "LEFT JOIN convenios cv ON c.id_convenio = cv.id "
                + "WHERE " + campoFiltro + " LIKE ? "
                + "ORDER BY c.data_consulta, c.horario";

        try {
            abrirBanco();
            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + nome + "%");
            rs = pst.executeQuery();

            while (rs.next()) {
                linhas.add(new LinhaRelatorioConsulta(
                        rs.getInt("id"),
                        rs.getString("paciente"),
                        rs.getString("profissional"),
                        rs.getString("especialidade"),
                        rs.getString("convenio"),
                        rs.getDate("data_consulta").toLocalDate().format(FORMATO_DATA),
                        rs.getTime("horario").toLocalTime().format(FORMATO_HORARIO),
                        rs.getString("situacao")
                ));
            }
        } finally {
            fecharBanco();
        }

        return linhas;
    }
}
