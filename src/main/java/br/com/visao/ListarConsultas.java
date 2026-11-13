package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Tela de LISTAGEM de consultas (o "R" do CRUD de Consulta).
 * Ao abrir, busca todas as consultas no banco e mostra numa tabela com ID,
 * nome do paciente, nome do profissional, convênio ("Particular" quando não há),
 * data, horário e situação. Os nomes vêm dos JOINs feitos em
 * ManterConsulta.pesquisar(); a tela não tem filtro nem botões.
 */
// Herança: A tela também herda as propriedades visuais da classe JFrame
public class ListarConsultas extends JFrame {
    
    // Encapsulamento dos componentes visuais da tabela
    // tabela = componente visual (JTable); modelo = DefaultTableModel, que guarda as colunas e linhas exibidas.
    private JTable tabela;
    private DefaultTableModel modelo;

    /** Construtor: configura a janela, cria a tabela com suas colunas e já carrega os dados do banco. */
    public ListarConsultas() {
        // Título, tamanho (750 x 400) e centralização (null = centro da tela).
        // Sem setDefaultCloseOperation, o X usa o padrão do JFrame e só esconde a janela.
        setTitle("Listagem de Consultas");
        setSize(750, 400);
        setLocationRelativeTo(null);
        
        // Nomes das colunas; o 0 no DefaultTableModel indica que a tabela começa sem nenhuma linha.
        String[] colunas = {"ID", "Paciente", "Profissional", "Convênio", "Data", "Horário", "Situação"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        
        // O JFrame usa BorderLayout por padrão (regiões NORTE, SUL, LESTE, OESTE e CENTRO).
        // A tabela vai dentro de um JScrollPane (barra de rolagem) no CENTRO, ocupando a janela toda.
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        carregarTabela(); // Invocação do método local logo que a tela abre
    }

    /**
     * Pede ao ManterConsulta todas as consultas (o filtro "" no LIKE traz todas)
     * e acrescenta uma linha na tabela para cada uma. Se houver erro de banco,
     * mostra uma mensagem em vez de travar a tela.
     */
    private void carregarTabela() {
        try {
            ManterConsulta dao = new ManterConsulta();
            
            // Coleções (Collections): Recebe um ArrayList dinâmico contendo vários objetos Consulta vindos do Banco de Dados
            ArrayList<Consulta> lista = dao.pesquisar(""); 
            
            // Laço de Repetição For-Each: Itera sobre cada objeto da lista para desenhar uma linha na tabela
            for (Consulta c : lista) {
                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNomePaciente(),     // Exibe o Nome resultante do INNER JOIN cruzado no DAO
                    c.getNomeProfissional(), // Exibe o Nome resultante do INNER JOIN cruzado no DAO
                    c.getNomeConvenio(),    // Exibe o Nome resultante do LEFT JOIN
                    // Data e horário aparecem no formato padrão do Java (AAAA-MM-DD e HH:MM).
                    c.getDataConsulta(),
                    c.getHorario(),
                    c.getSituacao()
                });
            }
        } catch (Exception ex) {
            // Falha de conexão ou de SQL: avisa o usuário e a tabela fica vazia.
            JOptionPane.showMessageDialog(this, "Erro ao listar as consultas: " + ex.getMessage());
        }
    }
}