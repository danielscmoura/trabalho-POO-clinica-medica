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
 */
public class ListarEspecialidades extends JFrame {

    private final ManterEspecialidade manter = new ManterEspecialidade();

    private JTextField jTnome;
    private JTable jTabela;
    private DefaultTableModel modelo;
    private JLabel jLtotal;

    public ListarEspecialidades() {
        initComponents();
        carregar(""); // ao abrir, mostra todas
    }

    private void initComponents() {
        setTitle("Listar Especialidades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---- filtro ----
        jTnome = new JTextField(16);
        JButton jBpesquisa = new JButton("Pesquisar");
        JButton jBtodas = new JButton("Mostrar todas");
        jTnome.addActionListener(e -> pesquisar());
        jBpesquisa.addActionListener(e -> pesquisar());
        jBtodas.addActionListener(e -> {
            jTnome.setText("");
            carregar("");
        });

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.setBorder(BorderFactory.createTitledBorder("Filtro"));
        filtro.add(new JLabel("Nome contém:"));
        filtro.add(jTnome);
        filtro.add(jBpesquisa);
        filtro.add(jBtodas);

        // ---- tabela ----
        modelo = new DefaultTableModel(new Object[]{"Código", "Nome"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTabela = new JTable(modelo);
        jTabela.getColumnModel().getColumn(0).setMaxWidth(80);

        // ---- rodapé ----
        jLtotal = new JLabel(" ");
        JButton jBfechar = new JButton("Fechar");
        jBfechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(jLtotal, BorderLayout.WEST);
        rodape.add(jBfechar, BorderLayout.EAST);

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(filtro, BorderLayout.NORTH);
        raiz.add(new JScrollPane(jTabela), BorderLayout.CENTER);
        raiz.add(rodape, BorderLayout.SOUTH);
        setContentPane(raiz);

        setSize(680, 440);
        setLocationRelativeTo(null);
    }

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
            ArrayList<Especialidade> lista = manter.pesquisar(filtro);
            for (Especialidade e : lista) {
                modelo.addRow(new Object[]{e.getId(), e.getNome()});
            }
            jLtotal.setText(lista.size() + " especialidade(s) encontrada(s)");
            return lista.size();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar: " + ex.getMessage());
            return 0;
        }
    }

}
