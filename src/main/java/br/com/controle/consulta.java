package br.com.controle;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consulta {
    private int id;
    private int idPaciente;
    private int idProfissional;
    private Integer idConvenio; 
    private LocalDate dataConsulta;
    private LocalTime horario;
    private String situacao;

    // Atributos para guardar os nomes vindos do INNER JOIN
    private String nomePaciente;
    private String nomeProfissional;
    private String nomeConvenio;

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