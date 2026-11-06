package br.com.controle;

public class LinhaRelatorioConsulta {

    private final int id;
    private final String paciente;
    private final String profissional;
    private final String especialidade;
    private final String convenio;
    private final String data;
    private final String horario;
    private final String situacao;

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
