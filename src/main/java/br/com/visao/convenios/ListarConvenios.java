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

/** Tela Swing que pesquisa e exibe os convênios em uma tabela não editável. */
// Mostra dois filtros (parte do nome e parte do telefone), o botão Pesquisar, a tabela ID/Nome/Telefone e o botão Fechar.
// A tabela começa vazia; os dados vêm de ManterConvenio.pesquisar(nome, telefone) ao clicar em Pesquisar.
public class ListarConvenios extends JFrame {

    // Campos de filtro: parte do nome e parte do telefone.
    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoTelefone = new JTextField(15);
    // Modelo da tabela com as colunas ID, Nome e Telefone, começando sem linhas; a classe anônima
    // sobrescreve isCellEditable para bloquear a edição das células.
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Telefone"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    /**
     * Construtor: define o título, faz o X fechar só esta janela (DISPOSE_ON_CLOSE),
     * monta os componentes, ajusta o tamanho ao conteúdo (pack) e centraliza.
     */
    public ListarConvenios() {
        setTitle("Listagem de Convênios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        montarTela();
        pack();
        setLocationRelativeTo(null);
    }

    /** Monta os filtros de pesquisa, a tabela de resultados e o botão de fechar. */
    private void montarTela() {
        // Painel de filtros com GridBagLayout: todos os componentes na linha 0, um ao lado do outro
        // (gridx de 0 a 4), com 6 px de margem (insets) e alinhados à esquerda (WEST).
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

        // O botão Pesquisar chama pesquisar().
        JButton botaoPesquisar = new JButton("Pesquisar");
        botaoPesquisar.addActionListener(e -> pesquisar());
        restricoes.gridx = 4;
        filtros.add(botaoPesquisar, restricoes);

        // A JTable exibe o modelo; setAutoCreateRowSorter permite ordenar clicando no cabeçalho da coluna.
        JTable tabela = new JTable(modeloTabela);
        tabela.setAutoCreateRowSorter(true);

        // Fechar chama dispose(); o botão fica à direita (FlowLayout RIGHT).
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoFechar);

        // O JFrame usa BorderLayout por padrão: filtros no NORTE, tabela com barra de rolagem
        // (JScrollPane) no CENTRO e botão no SUL.
        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    /**
     * Envia os filtros digitados ao ManterConvenio e preenche a JTable com a lista retornada.
     */
    private void pesquisar() {
        // Evita manter resultados da pesquisa anterior na tabela.
        modeloTabela.setRowCount(0);

        try {
            // Filtros vazios trazem todos os convênios, pois o DAO usa LIKE com % antes e depois do texto.
            ArrayList<Convenio> convenios = new ManterConvenio().pesquisar(
                    campoNome.getText().trim(), campoTelefone.getText().trim());

            // Cada objeto retornado pelo banco é incluído como uma linha da JTable.
            for (Convenio convenio : convenios) {
                modeloTabela.addRow(new Object[]{
                    convenio.getId(), convenio.getNome(), convenio.getTelefone()
                });
            }

            if (convenios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum convênio encontrado.");
            }
        } catch (Exception e) {
            // Falha de conexão ou de SQL vira uma caixa de erro.
            JOptionPane.showMessageDialog(this,
                    "Erro ao pesquisar convênios: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
