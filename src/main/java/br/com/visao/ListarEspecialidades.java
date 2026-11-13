package br.com.visao;

import br.com.controle.Especialidade;
import br.com.entidade.ManterEspecialidade;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * View: tela de LISTAGEM de Especialidades (Read), com filtro por nome.
 * Ao abrir mostra todas as especialidades numa tabela (Código e Nome); o filtro
 * procura parte do nome (LIKE) e o rodapé mostra quantas foram encontradas.
 * Os dados vêm de ManterEspecialidade.pesquisar().
 */
public class ListarEspecialidades extends JFrame {

    // Classe de lógica/banco usada pela tela.
    private final ManterEspecialidade manter = new ManterEspecialidade();

    // Campo do filtro, tabela, modelo da tabela (guarda as linhas exibidas na JTable) e rótulo com o total.
    private JTextField jTnome;
    private JTable jTabela;
    private DefaultTableModel modelo;
    private JLabel jLtotal;

    /** Construtor: monta a tela e já carrega a lista completa. */
    public ListarEspecialidades() {
        initComponents();
        carregar(""); // ao abrir, mostra todas
    }

    /** Monta a tela: filtro no topo, tabela no centro e rodapé com o total e o botão Fechar. */
    private void initComponents() {
        // Título e DISPOSE_ON_CLOSE: o X fecha e libera só esta janela.
        setTitle("Listar Especialidades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---- filtro ----
        // Pesquisar (botão ou Enter no campo) chama pesquisar(); Mostrar todas apaga o filtro e recarrega a lista inteira.
        jTnome = new JTextField(16);
        JButton jBpesquisa = new JButton("Pesquisar");
        JButton jBtodas = new JButton("Mostrar todas");
        jTnome.addActionListener(e -> pesquisar());
        jBpesquisa.addActionListener(e -> pesquisar());
        jBtodas.addActionListener(e -> {
            jTnome.setText("");
            carregar("");
        });

        // FlowLayout(LEFT): rótulo, campo e botões em fila à esquerda, dentro de uma moldura "Filtro".
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.setBorder(BorderFactory.createTitledBorder("Filtro"));
        filtro.add(new JLabel("Nome contém:"));
        filtro.add(jTnome);
        filtro.add(jBpesquisa);
        filtro.add(jBtodas);

        // ---- tabela ----
        // Modelo com as colunas Código e Nome, começando sem linhas; a classe anônima sobrescreve
        // isCellEditable para impedir que o usuário edite as células.
        modelo = new DefaultTableModel(new Object[]{"Código", "Nome"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTabela = new JTable(modelo);
        // Limita a largura da coluna Código a 80 px, deixando o resto para o nome.
        jTabela.getColumnModel().getColumn(0).setMaxWidth(80);

        // ---- rodapé ----
        // Rodapé em BorderLayout: total à esquerda (WEST) e botão Fechar, que chama dispose(), à direita (EAST).
        jLtotal = new JLabel(" ");
        JButton jBfechar = new JButton("Fechar");
        jBfechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(jLtotal, BorderLayout.WEST);
        rodape.add(jBfechar, BorderLayout.EAST);

        // Painel raiz (BorderLayout): filtro no NORTE, tabela com barra de rolagem (JScrollPane) no CENTRO
        // e rodapé no SUL, com 12 px de margem.
        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(filtro, BorderLayout.NORTH);
        raiz.add(new JScrollPane(jTabela), BorderLayout.CENTER);
        raiz.add(rodape, BorderLayout.SOUTH);
        setContentPane(raiz);

        // Tamanho fixo de 680 x 440 e janela centralizada na tela.
        setSize(680, 440);
        setLocationRelativeTo(null);
    }

    /**
     * Executado pelo botão Pesquisar (ou Enter): recarrega a tabela com o filtro digitado
     * e avisa quando nenhuma especialidade foi encontrada.
     */
    private void pesquisar() {
        String filtro = jTnome.getText().trim();
        int total = carregar(filtro);
        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Nenhuma especialidade encontrada.");
        }
    }

    // preenche a tabela; devolve quantas linhas encontrou
    private int carregar(String filtro) {
        modelo.setRowCount(0); // limpa a tabela
        try {
            // Busca as especialidades cujo nome contém o filtro (filtro vazio = todas) e cria uma linha para cada uma.
            ArrayList<Especialidade> lista = manter.pesquisar(filtro);
            for (Especialidade e : lista) {
                modelo.addRow(new Object[]{e.getId(), e.getNome()});
            }
            // Atualiza o contador do rodapé.
            jLtotal.setText(lista.size() + " especialidade(s) encontrada(s)");
            return lista.size();
        } catch (Exception ex) {
            // Em erro de conexão ou SQL, avisa e informa 0 encontradas.
            JOptionPane.showMessageDialog(this, "Erro ao listar: " + ex.getMessage());
            return 0;
        }
    }

}
