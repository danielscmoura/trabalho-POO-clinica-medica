package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Tela de EDIÇÃO de consultas (o "U" do CRUD de Consulta).
 * O usuário informa o ID da consulta que quer alterar e digita todos os dados
 * novos: paciente, profissional, convênio, data, horário e situação. A tela não
 * carrega os valores atuais, então o que for digitado substitui o registro inteiro.
 * Usa a classe de lógica ManterConsulta (método editarConsulta), que valida os
 * dados, checa choque de horário do profissional (ignorando a própria consulta)
 * e executa o UPDATE.
 */
public class EditarConsulta extends JFrame {
    // Encapsulamento rigoroso
    // Um campo de texto para cada coluna da tabela consultas (o primeiro é o ID que identifica
    // o registro a alterar) e o botão Atualizar.
    private JTextField txtIdConsulta, txtIdPaciente, txtIdProfissional, txtIdConvenio, txtData, txtHorario, txtSituacao;
    private JButton btnSalvar;

    /**
     * Construtor: configura a janela, monta a grade com os rótulos e campos e liga
     * o botão Atualizar ao método atualizar().
     */
    public EditarConsulta() {
        // Título e tamanho fixo (400 x 350); sem setDefaultCloseOperation, o X usa o padrão do JFrame e só esconde a janela.
        setTitle("Editar Consulta");
        setSize(400, 350);
        // GridLayout(8, 2, 10, 10): grade de 8 linhas x 2 colunas com células do mesmo tamanho e 10 px de espaço;
        // cada add() preenche a próxima célula, da esquerda para a direita.
        setLayout(new GridLayout(8, 2, 10, 10));
        setLocationRelativeTo(null);

        // Linha 1: ID da consulta que será alterada (é a chave usada no WHERE do UPDATE).
        add(new JLabel(" ID da Consulta (Para alterar):"));
        txtIdConsulta = new JTextField();
        add(txtIdConsulta);

        // Linhas 2 a 7: os novos valores; todos eles são regravados no banco.
        add(new JLabel(" Novo ID do Paciente:"));
        txtIdPaciente = new JTextField();
        add(txtIdPaciente);

        add(new JLabel(" Novo ID do Profissional:"));
        txtIdProfissional = new JTextField();
        add(txtIdProfissional);

        add(new JLabel(" Novo ID do Convênio:"));
        txtIdConvenio = new JTextField();
        add(txtIdConvenio);

        add(new JLabel(" Nova Data (DD/MM/AAAA):"));
        txtData = new JTextField();
        add(txtData);

        add(new JLabel(" Novo Horário (HH:MM):"));
        txtHorario = new JTextField();
        add(txtHorario);
        
        // A situação já vem preenchida com "Agendada", mas aceita qualquer texto (ex.: Realizada, Cancelada).
        add(new JLabel(" Nova Situação:"));
        txtSituacao = new JTextField("Agendada");
        add(txtSituacao);

        // Última linha: rótulo vazio à esquerda para o botão Atualizar ficar na coluna da direita.
        add(new JLabel(""));
        btnSalvar = new JButton("Atualizar");
        add(btnSalvar);

        // Arrow function (Expressão Lambda) para simplificar a chamada do método de clique
        btnSalvar.addActionListener(e -> atualizar());
    }

    /**
     * Executado ao clicar em Atualizar: converte os textos digitados, monta um objeto
     * Consulta com o ID e os novos dados e pede ao ManterConsulta que grave a alteração.
     * ID não numérico, data ou horário fora do formato e erros de banco viram mensagem.
     */
    private void atualizar() {
        try {
            // Cria um objeto para transportar (Data Transfer Object) os novos dados digitados para o DAO
            Consulta c = new Consulta();
            // O ID diz QUAL consulta será alterada; os setters seguintes trazem os novos valores.
            c.setId(Integer.parseInt(txtIdConsulta.getText().trim()));
            c.setIdPaciente(Integer.parseInt(txtIdPaciente.getText().trim()));
            c.setIdProfissional(Integer.parseInt(txtIdProfissional.getText().trim()));
            
            // Convênio vazio = consulta particular (null, gravado como NULL no banco).
            String conv = txtIdConvenio.getText().trim();
            c.setIdConvenio(conv.isEmpty() ? null : Integer.parseInt(conv));
            
            // Data no formato dd/MM/yyyy e horário no formato HH:mm (ex.: 14:30).
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            c.setDataConsulta(LocalDate.parse(txtData.getText().trim(), formatador));
            c.setHorario(LocalTime.parse(txtHorario.getText().trim()));
            c.setSituacao(txtSituacao.getText().trim());

            // O DAO valida, confere se o profissional já tem OUTRA consulta nesse dia e horário e executa o UPDATE.
            ManterConsulta dao = new ManterConsulta();
            dao.editarConsulta(c);
            
            // Se não houve exceção, confirma e fecha a janela.
            JOptionPane.showMessageDialog(this, "Consulta atualizada!");
            dispose();
        } catch (Exception ex) {
            // Conversões inválidas (NumberFormatException, DateTimeParseException) e erros de SQL chegam aqui.
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
        }
    }
}