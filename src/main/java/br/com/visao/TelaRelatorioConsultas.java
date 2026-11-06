package br.com.visao;

import br.com.controle.LinhaRelatorioConsulta;
import br.com.entidade.RelatorioConsultas;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

public class TelaRelatorioConsultas extends JFrame {

    private final JRadioButton opcaoPaciente = new JRadioButton("Paciente", true);
    private final JRadioButton opcaoProfissional = new JRadioButton("Profissional");
    private final JTextField campoNome = new JTextField(25);
    private final JLabel labelTotal = new JLabel("Total de consultas: 0");
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Paciente", "Profissional", "Especialidade",
                "Convênio", "Data", "Horário", "Situação"}, 0) {
        @Override
        public Class<?> getColumnClass(int coluna) {
            return coluna == 0 ? Integer.class : String.class;
        }

        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public TelaRelatorioConsultas() {
        setTitle("Relatório de Consultas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        montarTela();
        pack();
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        ButtonGroup grupoFiltro = new ButtonGroup();
        grupoFiltro.add(opcaoPaciente);
        grupoFiltro.add(opcaoProfissional);

        JPanel filtros = new JPanel(new GridBagLayout());
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(6, 6, 6, 6);
        restricoes.anchor = GridBagConstraints.WEST;

        restricoes.gridx = 0;
        restricoes.gridy = 0;
        filtros.add(new JLabel("Filtrar por:"), restricoes);
        restricoes.gridx = 1;
        filtros.add(opcaoPaciente, restricoes);
        restricoes.gridx = 2;
        filtros.add(opcaoProfissional, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 1;
        filtros.add(new JLabel("Nome:"), restricoes);
        restricoes.gridx = 1;
        restricoes.gridwidth = 2;
        filtros.add(campoNome, restricoes);

        JButton botaoGerar = new JButton("Gerar relatório");
        botaoGerar.addActionListener(e -> gerarRelatorio());
        restricoes.gridx = 3;
        restricoes.gridwidth = 1;
        filtros.add(botaoGerar, restricoes);

        JTable tabela = new JTable(modeloTabela);
        tabela.setAutoCreateRowSorter(true);
        tabela.setPreferredScrollableViewportSize(new Dimension(1000, 300));

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(new EmptyBorder(0, 12, 0, 12));
        rodape.add(labelTotal, BorderLayout.WEST);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);
        rodape.add(botoes, BorderLayout.EAST);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void gerarRelatorio() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome para o filtro.");
            return;
        }

        modeloTabela.setRowCount(0);

        try {
            RelatorioConsultas relatorio = new RelatorioConsultas();
            ArrayList<LinhaRelatorioConsulta> linhas;

            if (opcaoPaciente.isSelected()) {
                linhas = relatorio.buscarPorPaciente(nome);
            } else {
                linhas = relatorio.buscarPorProfissional(nome);
            }

            for (LinhaRelatorioConsulta linha : linhas) {
                modeloTabela.addRow(new Object[]{
                    linha.getId(), linha.getPaciente(), linha.getProfissional(),
                    linha.getEspecialidade(), linha.getConvenio(), linha.getData(),
                    linha.getHorario(), linha.getSituacao()
                });
            }

            labelTotal.setText("Total de consultas: " + linhas.size());

            if (linhas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma consulta encontrada.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        campoNome.setText("");
        modeloTabela.setRowCount(0);
        labelTotal.setText("Total de consultas: 0");
        campoNome.requestFocus();
    }
}
