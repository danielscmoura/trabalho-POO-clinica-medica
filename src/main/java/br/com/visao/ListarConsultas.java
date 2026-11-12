package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// Herança: A tela também herda as propriedades visuais da classe JFrame
public class ListarConsultas extends JFrame {
    
    // Encapsulamento dos componentes visuais da tabela
    private JTable tabela;
    private DefaultTableModel modelo;

    public ListarConsultas() {
        setTitle("Listagem de Consultas");
        setSize(750, 400);
        setLocationRelativeTo(null);
        
        String[] colunas = {"ID", "Paciente", "Profissional", "Convênio", "Data", "Horário", "Situação"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        carregarTabela(); // Invocação do método local logo que a tela abre
    }

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
                    c.getDataConsulta(),
                    c.getHorario(),
                    c.getSituacao()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar as consultas: " + ex.getMessage());
        }
    }
}