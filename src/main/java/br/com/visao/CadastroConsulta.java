package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Tela de AGENDAMENTO de consultas (o "C" do CRUD de Consulta).
 * Mostra cinco campos de texto: ID do paciente, ID do profissional, ID do
 * convênio (vazio = consulta particular), data e horário, mais o botão Salvar.
 * Toda consulta nova é gravada com a situação "Agendada".
 * Usa a classe de lógica ManterConsulta, que valida os dados, impede que o
 * profissional tenha duas consultas no mesmo dia e horário e faz o INSERT.
 */
// Herança: A classe CadastroConsulta herda (extends) as propriedades de um JFrame (janela padrão do Java)
public class CadastroConsulta extends JFrame {
    
    // Encapsulamento: atributos privados, acessíveis apenas por dentro desta classe
    // Os cinco campos de texto do formulário e o botão que dispara a gravação.
    private JTextField txtIdPaciente, txtIdProfissional, txtIdConvenio, txtData, txtHorario;
    private JButton btnSalvar;

    // Construtor: Define a aparência e carrega os componentes assim que a tela é instanciada
    public CadastroConsulta() {
        // Configuração da janela: título e tamanho fixo de 400 x 300 pixels.
        // Como não há setDefaultCloseOperation, vale o padrão do JFrame (HIDE_ON_CLOSE): o X apenas esconde a janela.
        setTitle("Agendar Consulta");
        setSize(400, 300);
        // GridLayout(6, 2, 10, 10): 6 linhas x 2 colunas de células do mesmo tamanho, com 10 px de espaço entre elas.
        // Cada add() abaixo ocupa a próxima célula, da esquerda para a direita e de cima para baixo.
        setLayout(new GridLayout(6, 2, 10, 10)); // Organiza os itens numa grelha (matriz)
        setLocationRelativeTo(null); // Centraliza a janela no ecrã

        // Instanciação (uso do "new") de objetos visuais
        // Cada par abaixo forma uma linha da grade: rótulo (JLabel) à esquerda e campo (JTextField) à direita.
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

        // Última linha: um rótulo vazio ocupa a célula da esquerda para o botão Salvar ficar na coluna da direita.
        add(new JLabel("")); 
        btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        // Tratamento de Eventos (Event Listener): Define a ação disparada pelo clique do botão
        // O clique em Salvar chama o método salvar(), logo abaixo.
        btnSalvar.addActionListener(e -> salvar());
    }

    /**
     * Executado ao clicar em Salvar: lê os campos, converte os textos para os tipos
     * do Model (int, Integer, LocalDate e LocalTime), monta o objeto Consulta e pede
     * ao ManterConsulta que o grave no banco. Erros de digitação ou de banco caem
     * no catch e aparecem numa mensagem.
     */
    private void salvar() {
        // Tratamento de Exceções (Try-Catch): Impede que o sistema quebre se o utilizador digitar uma letra onde deveria estar um número
        try {
            // Instanciação: Cria um novo objeto da classe Modelo (Consulta) na memória
            Consulta c = new Consulta();
            
            // Preenchimento dos atributos do objeto usando os métodos Setters
            c.setIdPaciente(Integer.parseInt(txtIdPaciente.getText().trim()));
            c.setIdProfissional(Integer.parseInt(txtIdProfissional.getText().trim()));
            
            // Convênio é opcional: campo vazio vira null (consulta particular) e o DAO grava NULL na coluna.
            String conv = txtIdConvenio.getText().trim();
            c.setIdConvenio(conv.isEmpty() ? null : Integer.parseInt(conv));
            
            // Converte a data do formato brasileiro (dd/MM/yyyy) para LocalDate e o horário (HH:mm, ex.: 08:30) para LocalTime.
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            c.setDataConsulta(LocalDate.parse(txtData.getText().trim(), formatador));
            c.setHorario(LocalTime.parse(txtHorario.getText().trim()));
            // Toda consulta nova começa com a situação "Agendada".
            c.setSituacao("Agendada");

            // Separação em Camadas: Instancia a classe DAO para tratar do banco de dados (não mistura SQL com interface)
            // O ManterConsulta valida os campos, verifica se o profissional já tem consulta nesse dia e horário
            // e só então executa o INSERT; ele mesmo mostra as mensagens dessas verificações.
            ManterConsulta dao = new ManterConsulta();
            dao.inserirConsulta(c); 
            
            // Se o DAO não lançou exceção, a tela mostra a confirmação e se fecha.
            JOptionPane.showMessageDialog(this, "Consulta cadastrada com sucesso!");
            dispose(); // Fecha apenas esta janela após o sucesso
        } catch (Exception ex) {
            // Chegam aqui: NumberFormatException (ID vazio ou com letras), DateTimeParseException
            // (data ou horário fora do formato) e erros de banco, como um ID de paciente que não existe.
            JOptionPane.showMessageDialog(this, "Erro ao salvar: verifique os dados digitados.\n" + ex.getMessage());
        }
    }
}