package br.com.controle;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modelo (entidade) que representa uma linha da tabela "consultas".
 *
 * Guarda os IDs das chaves estrangeiras (paciente, profissional e convênio),
 * que são o que de fato é gravado no banco, e também os nomes correspondentes,
 * preenchidos pelo ManterConsulta.pesquisar() com JOIN apenas para exibir na tela.
 */
public class Consulta {
    // Campos que espelham as colunas da tabela consultas.
    // id_paciente, id_profissional e id_convenio são chaves estrangeiras
    // para pacientes, profissionais e convenios.
    private int id;
    private int idPaciente;
    private int idProfissional;
    // Integer (e não int) para aceitar null: consulta particular não tem convênio,
    // e id_convenio é a única FK da tabela que aceita NULL.
    private Integer idConvenio; 
    // LocalDate e LocalTime correspondem às colunas DATE e TIME; o DAO converte
    // para java.sql.Date/Time ao gravar e ler.
    private LocalDate dataConsulta;
    private LocalTime horario;
    // Texto como "Agendada" (valor padrão da coluna no banco).
    private String situacao;

    // Atributos para guardar os nomes vindos do INNER JOIN
    private String nomePaciente;
    private String nomeProfissional;
    // O nome do convênio vem de um LEFT JOIN; quando não há convênio, o COALESCE
    // do SQL devolve "Particular".
    private String nomeConvenio;

    // Construtores: o vazio é usado pelo DAO ao montar objetos a partir do ResultSet;
    // o completo não recebe o id porque ele é gerado pelo AUTO_INCREMENT do banco.
    public Consulta() {
    }

    public Consulta(int idPaciente, int idProfissional, Integer idConvenio, LocalDate dataConsulta, LocalTime horario, String situacao) {
        this.idPaciente = idPaciente;
        this.idProfissional = idProfissional;
        this.idConvenio = idConvenio;
        this.dataConsulta = dataConsulta;
        this.horario = horario;
        this.situacao = situacao;
    }

    // Getters e setters dos campos da tabela.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }

    public Integer getIdConvenio() { return idConvenio; }
    public void setIdConvenio(Integer idConvenio) { this.idConvenio = idConvenio; }

    public LocalDate getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(LocalDate dataConsulta) { this.dataConsulta = dataConsulta; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    // Getters e Setters do INNER JOIN
    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }

    public String getNomeProfissional() { return nomeProfissional; }
    public void setNomeProfissional(String nomeProfissional) { this.nomeProfissional = nomeProfissional; }

    public String getNomeConvenio() { return nomeConvenio; }
    public void setNomeConvenio(String nomeConvenio) { this.nomeConvenio = nomeConvenio; }
}