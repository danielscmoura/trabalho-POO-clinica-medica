package br.com.visao;

import br.com.controle.Especialidade;
import br.com.entidade.ManterEspecialidade;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * View: tela de EDIÇÃO de Especialidade (Update).
 * 1) pesquisa pelo nome; 2) altera o nome; 3) salva.
 */
public class EditarEspecialidades extends JFrame {

    private final ManterEspecialidade manter = new ManterEspecialidade();
    private Especialidade encontrada; // objeto carregado pela pesquisa (guarda o id)

    private JTextField jTnomePesquisa;
    private JTextField jTid;
    private JTextField jTnome;
    private JButton jBsalvar;

    public EditarEspecialidades() {
        initComponents();
        habilitarEdicao(false);
    }

    private void initComponents() {
        setTitle("Editar Especialidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---- pesquisa ----
        jTnomePesquisa = new JTextField(22);
        JButton jBpesquisar = new JButton("Pesquisar");
        jTnomePesquisa.addActionListener(e -> pesquisar());
        jBpesquisar.addActionListener(e -> pesquisar());

        JPanel painelPesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelPesquisa.setBorder(BorderFactory.createTitledBorder("Pesquisar pelo nome"));
        painelPesquisa.add(new JLabel("Nome:"));
        painelPesquisa.add(jTnomePesquisa);
        painelPesquisa.add(jBpesquisar);

        // ---- dados ----
        jTid = new JTextField(6);
        jTid.setEditable(false);
        jTnome = new JTextField(25);
        jTnome.addActionListener(e -> salvar());

        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder("Dados da especialidade"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0;
        painelDados.add(new JLabel("Código"), g);
        g.gridx = 1;
        painelDados.add(jTid, g);
        g.gridx = 0; g.gridy = 1;
        painelDados.add(new JLabel("Novo nome"), g);
        g.gridx = 1;
        painelDados.add(jTnome, g);

        // ---- botões ----
        jBsalvar = new JButton("Salvar");
        JButton jBlimpar = new JButton("Limpar");
        JButton jBfechar = new JButton("Fechar");
        jBsalvar.addActionListener(e -> salvar());
        jBlimpar.addActionListener(e -> limpar());
        jBfechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(jBsalvar);
        botoes.add(jBlimpar);
        botoes.add(jBfechar);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(painelPesquisa, BorderLayout.NORTH);
        centro.add(painelDados, BorderLayout.CENTER);

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }

    private void pesquisar() {
        String nome = jTnomePesquisa.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da especialidade.");
            return;
        }
        try {
            encontrada = manter.buscarPorNome(nome);
            if (encontrada == null) {
                habilitarEdicao(false);
                jTid.setText("");
                jTnome.setText("");
                JOptionPane.showMessageDialog(this,
                        "Nenhuma especialidade encontrada com esse nome.\nUse Listar para consultar os nomes cadastrados.");
                return;
            }
            jTid.setText(String.valueOf(encontrada.getId()));
            jTnome.setText(encontrada.getNome());
            habilitarEdicao(true);
            jTnome.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar: " + ex.getMessage());
        }
    }

    private void salvar() {
        if (encontrada == null) {
            JOptionPane.showMessageDialog(this, "Pesquise uma especialidade antes de salvar.");
            return;
        }
        // usa o id que veio da pesquisa; só o nome muda
        Especialidade esp = new Especialidade(encontrada.getId(), jTnome.getText().trim());
        try {
            if (manter.editarEspecialidade(esp)) {
                limpar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + ex.getMessage());
        }
    }

    private void limpar() {
        encontrada = null;
        jTnomePesquisa.setText("");
        jTid.setText("");
        jTnome.setText("");
        habilitarEdicao(false);
        jTnomePesquisa.requestFocus();
    }

    private void habilitarEdicao(boolean habilitar) {
        jTnome.setEnabled(habilitar);
        jBsalvar.setEnabled(habilitar);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new EditarEspecialidades().setVisible(true));
    }
}
