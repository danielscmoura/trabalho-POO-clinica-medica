package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Herança: A classe CadastroConsulta herda (extends) as propriedades de um JFrame (janela padrão do Java)
public class CadastroConsulta extends JFrame {
    
    // Encapsulamento: atributos privados, acessíveis apenas por dentro desta classe
    private JTextField txtIdPaciente, txtIdProfissional, txtIdConvenio, txtData, txtHorario;
    private JButton btnSalvar;

    // Construtor: Define a aparência e carrega os componentes assim que a tela é instanciada
    public CadastroConsulta() {
        setTitle("Agendar Consulta");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10)); // Organiza os itens numa grelha (matriz)
        setLocationRelativeTo(null); // Centraliza a janela no ecrã

        // Instanciação (uso do "new") de objetos visuais
        add(new JLabel(" ID do Paciente:"));
        txtIdPaciente = new JTextField();
        add(txtIdPaciente);

        add(new JLabel(" ID do Profissional:"));
        txtIdProfissional = new JTextField();
        add(txtIdProfissional);

        add(new JLabel(" ID do Convênio (vazio = Particular):"));
        txtIdConvenio = new JTextField();
        add(txtIdConvenio);

        add(new JLabel(" Data (DD/MM/AAAA):"));
        txtData = new JTextField();
        add(txtData);

        add(new JLabel(" Horário (HH:MM):"));
        txtHorario = new JTextField();
        add(txtHorario);

        add(new JLabel("")); 
        btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        // Tratamento de Eventos (Event Listener): Define a ação disparada pelo clique do botão
        btnSalvar.addActionListener(e -> salvar());
    }

    private void salvar() {
        // Tratamento de Exceções (Try-Catch): Impede que o sistema quebre se o utilizador digitar uma letra onde deveria estar um número
        try {
            // Instanciação: Cria um novo objeto da classe Modelo (Consulta) na memória
            Consulta c = new Consulta();
            
            // Preenchimento dos atributos do objeto usando os métodos Setters
            c.setIdPaciente(Integer.parseInt(txtIdPaciente.getText().trim()));
            c.setIdProfissional(Integer.parseInt(txtIdProfissional.getText().trim()));
            
            String conv = txtIdConvenio.getText().trim();
            c.setIdConvenio(conv.isEmpty() ? null : Integer.parseInt(conv));
            
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            c.setDataConsulta(LocalDate.parse(txtData.getText().trim(), formatador));
            c.setHorario(LocalTime.parse(txtHorario.getText().trim()));
            c.setSituacao("Agendada");

            // Separação em Camadas: Instancia a classe DAO para tratar do banco de dados (não mistura SQL com interface)
            ManterConsulta dao = new ManterConsulta();
            dao.inserirConsulta(c); 
            
            JOptionPane.showMessageDialog(this, "Consulta cadastrada com sucesso!");
            dispose(); // Fecha apenas esta janela após o sucesso
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: verifique os dados digitados.\n" + ex.getMessage());
        }
    }
}