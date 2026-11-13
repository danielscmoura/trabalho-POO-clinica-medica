package br.com.controle;

/**
 * Model de leitura: representa uma linha já pronta para ser exibida no relatório.
 * Não corresponde a uma nova tabela; reúne campos vindos de várias tabelas do JOIN.
 */
public class LinhaRelatorioConsulta {

    // Campos já prontos para exibição na JTable da TelaRelatorioConsultas.
    // São final: o objeto é imutável (preenchido uma vez no construtor).
    // Data e horário chegam como texto formatado (dd/MM/yyyy e HH:mm), e o
    // convênio chega como "Particular" quando a consulta não tem convênio.
    private final int id;
    private final String paciente;
    private final String profissional;
    private final String especialidade;
    private final String convenio;
    private final String data;
    private final String horario;
    private final String situacao;

    /**
     * Recebe os dados de uma consulta retornada pelo JOIN e monta a linha do relatório.
     */
    public LinhaRelatorioConsulta(int id, String paciente, String profissional,
            String especialidade, String convenio, String data, String horario,
            String situacao) {
        this.id = id;
        this.paciente = paciente;
        this.profissional = profissional;
        this.especialidade = especialidade;
        this.convenio = convenio;
        this.data = data;
        this.horario = horario;
        this.situacao = situacao;
    }

    // Só getters, sem setters: os campos são final e servem apenas para leitura.
    public int getId() {
        return id;
    }

    public String getPaciente() {
        return paciente;
    }

    public String getProfissional() {
        return profissional;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String getConvenio() {
        return convenio;
    }

    public String getData() {
        return data;
    }

    public String getHorario() {
        return horario;
    }

    public String getSituacao() {
        return situacao;
    }
}
