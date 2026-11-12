package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditarConsulta extends JFrame {
    // Encapsulamento rigoroso
    private JTextField txtIdConsulta, txtIdPaciente, txtIdProfissional, txtIdConvenio, txtData, txtHorario, txtSituacao;
    private JButton btnSalvar;

    public EditarConsulta() {
        setTitle("Editar Consulta");
        setSize(400, 350);
        setLayout(new GridLayout(8, 2, 10, 10));
        setLocationRelativeTo(null);

        add(new JLabel(" ID da Consulta (Para alterar):"));
        txtIdConsulta = new JTextField();
        add(txtIdConsulta);

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
        
        add(new JLabel(" Nova Situação:"));
        txtSituacao = new JTextField("Agendada");
        add(txtSituacao);

        add(new JLabel(""));
        btnSalvar = new JButton("Atualizar");
        add(btnSalvar);

        // Arrow function (Expressão Lambda) para simplificar a chamada do método de clique
        btnSalvar.addActionListener(e -> atualizar());
    }

    private void atualizar() {
        try {
            // Cria um objeto para transportar (Data Transfer Object) os novos dados digitados para o DAO
            Consulta c = new Consulta();
            c.setId(Integer.parseInt(txtIdConsulta.getText().trim()));
            c.setIdPaciente(Integer.parseInt(txtIdPaciente.getText().trim()));
            c.setIdProfissional(Integer.parseInt(txtIdProfissional.getText().trim()));
            
            String conv = txtIdConvenio.getText().trim();
            c.setIdConvenio(conv.isEmpty() ? null : Integer.parseInt(conv));
            
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            c.setDataConsulta(LocalDate.parse(txtData.getText().trim(), formatador));
            c.setHorario(LocalTime.parse(txtHorario.getText().trim()));
            c.setSituacao(txtSituacao.getText().trim());

            ManterConsulta dao = new ManterConsulta();
            dao.editarConsulta(c);
            
            JOptionPane.showMessageDialog(this, "Consulta atualizada!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
        }
    }
}