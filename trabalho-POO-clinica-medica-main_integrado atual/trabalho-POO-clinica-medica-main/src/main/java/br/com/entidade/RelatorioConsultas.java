package br.com.entidade;

import br.com.controle.LinhaRelatorioConsulta;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Executa as consultas SQL do relatório de consultas médicas.
 * A classe usa JOIN para transformar IDs em informações compreensíveis ao usuário.
 */
public class RelatorioConsultas extends DAO {

    private static final DateTimeFormatter FORMATO_DATA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORARIO
            = DateTimeFormatter.ofPattern("HH:mm");

    /** Busca consultas cujo nome do paciente corresponde ao filtro informado. */
    public ArrayList<LinhaRelatorioConsulta> buscarPorPaciente(String nomePaciente)
            throws Exception {
        return buscar(nomePaciente, true);
    }

    /** Busca consultas cujo nome do profissional corresponde ao filtro informado. */
    public ArrayList<LinhaRelatorioConsulta> buscarPorProfissional(String nomeProfissional)
            throws Exception {
        return buscar(nomeProfissional, false);
    }

    /**
     * Monta e executa o JOIN usado pelo relatório. O boolean escolhe de forma segura
     * se o filtro será aplicado ao paciente ou ao profissional.
     */
    private ArrayList<LinhaRelatorioConsulta> buscar(String nome, boolean filtrarPorPaciente)
            throws Exception {
        ArrayList<LinhaRelatorioConsulta> linhas = new ArrayList<>();
        // Define qual coluna receberá o filtro sem receber texto SQL do usuário.
        String campoFiltro = filtrarPorPaciente ? "pa.nome" : "pr.nome";

        // INNER JOIN recupera os dados obrigatórios da consulta; LEFT JOIN mantém
        // também as consultas particulares, que não possuem convênio.
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
            // LIKE permite procurar mesmo que o usuário digite somente parte do nome.
            pst.setString(1, "%" + nome + "%");
            rs = pst.executeQuery();

            while (rs.next()) {
                // Converte uma linha do JOIN em um objeto próprio para a tabela do relatório.
                linhas.add(new LinhaRelatorioConsulta(
                        rs.getInt("id"),
                        rs.getString("paciente"),
                        rs.getString("profissional"),
                        rs.getString("especialidade"),
                        rs.getString("convenio"),
                        // O banco devolve DATE/TIME; aqui eles são formatados para o usuário.
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
