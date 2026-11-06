package br.com.visao.convenios;

import br.com.controle.Convenio;
import br.com.entidade.ManterConvenio;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ListarConvenios extends JFrame {

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoTelefone = new JTextField(15);
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Telefone"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public ListarConvenios() {
        setTitle("Listagem de Convênios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        montarTela();
        pack();
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel filtros = new JPanel(new GridBagLayout());
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(6, 6, 6, 6);
        restricoes.anchor = GridBagConstraints.WEST;

        restricoes.gridx = 0;
        restricoes.gridy = 0;
        filtros.add(new JLabel("Nome:"), restricoes);
        restricoes.gridx = 1;
        filtros.add(campoNome, restricoes);

        restricoes.gridx = 2;
        filtros.add(new JLabel("Telefone:"), restricoes);
        restricoes.gridx = 3;
        filtros.add(campoTelefone, restricoes);

        JButton botaoPesquisar = new JButton("Pesquisar");
        botaoPesquisar.addActionListener(e -> pesquisar());
        restricoes.gridx = 4;
        filtros.add(botaoPesquisar, restricoes);

        JTable tabela = new JTable(modeloTabela);
        tabela.setAutoCreateRowSorter(true);

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoFechar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void pesquisar() {
        modeloTabela.setRowCount(0);

        try {
            ArrayList<Convenio> convenios = new ManterConvenio().pesquisar(
                    campoNome.getText().trim(), campoTelefone.getText().trim());

            for (Convenio convenio : convenios) {
                modeloTabela.addRow(new Object[]{
                    convenio.getId(), convenio.getNome(), convenio.getTelefone()
                });
            }

            if (convenios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum convênio encontrado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao pesquisar convênios: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
