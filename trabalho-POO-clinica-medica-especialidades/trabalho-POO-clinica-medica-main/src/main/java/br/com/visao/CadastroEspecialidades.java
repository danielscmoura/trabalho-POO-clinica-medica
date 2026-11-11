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
 * View: tela de CADASTRO de Especialidade (Create).
 * Só interface; o SQL fica em ManterEspecialidade.
 */
public class CadastroEspecialidades extends JFrame {

    private final ManterEspecialidade manter = new ManterEspecialidade();
    private JTextField jTnome;

    public CadastroEspecialidades() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Cadastrar Especialidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // fecha só esta tela

        jTnome = new JTextField(25);
        jTnome.addActionListener(e -> cadastrar()); // Enter cadastra

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Nome da especialidade"), g);
        g.gridx = 1;
        form.add(jTnome, g);

        JButton jBcadastrar = new JButton("Cadastrar");
        JButton jBlimpar = new JButton("Limpar");
        JButton jBfechar = new JButton("Fechar");
        jBcadastrar.addActionListener(e -> cadastrar());
        jBlimpar.addActionListener(e -> limpar());
        jBfechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(jBcadastrar);
        botoes.add(jBlimpar);
        botoes.add(jBfechar);

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }

    private void cadastrar() {
        Especialidade esp = new Especialidade();
        esp.setNome(jTnome.getText().trim());
        try {
            if (manter.inserirEspecialidade(esp)) {
                limpar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage());
        }
    }

    private void limpar() {
        jTnome.setText("");
        jTnome.requestFocus();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new CadastroEspecialidades().setVisible(true));
    }
}
