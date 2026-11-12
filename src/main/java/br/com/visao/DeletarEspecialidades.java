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
 * View: tela de EXCLUSÃO de Especialidade (Delete).
 * Pesquisa pelo nome, pede confirmação e exclui.
 */
public class DeletarEspecialidades extends JFrame {

    private final ManterEspecialidade manter = new ManterEspecialidade();
    private JTextField jTnome;

    public DeletarEspecialidades() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Deletar Especialidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        jTnome = new JTextField(25);
        jTnome.addActionListener(e -> deletar());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Nome da especialidade"), g);
        g.gridx = 1;
        form.add(jTnome, g);

        JButton jBdeletar = new JButton("Deletar");
        JButton jBfechar = new JButton("Fechar");
        jBdeletar.addActionListener(e -> deletar());
        jBfechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(jBdeletar);
        botoes.add(jBfechar);

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }

    private void deletar() {
        String nome = jTnome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da especialidade.");
            return;
        }
        try {
            Especialidade encontrada = manter.buscarPorNome(nome);
            if (encontrada == null) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma especialidade encontrada com esse nome.\nUse Listar para consultar os nomes cadastrados.");
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente excluir a especialidade " + encontrada.getNome() + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
            if (manter.deletarEspecialidade(encontrada)) { // exclui pelo id vindo da pesquisa
                jTnome.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
        }
    }

}
