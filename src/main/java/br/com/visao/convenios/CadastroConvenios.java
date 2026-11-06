package br.com.visao.convenios;

import br.com.controle.Convenio;
import br.com.entidade.ManterConvenio;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CadastroConvenios extends JFrame {

    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(20);

    public CadastroConvenios() {
        setTitle("Cadastro de Convênio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        montarTela();
        pack();
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(8, 8, 8, 8);
        restricoes.anchor = GridBagConstraints.WEST;

        restricoes.gridx = 0;
        restricoes.gridy = 0;
        campos.add(new JLabel("Nome:*"), restricoes);

        restricoes.gridx = 1;
        campos.add(campoNome, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 1;
        campos.add(new JLabel("Telefone:"), restricoes);

        restricoes.gridx = 1;
        campos.add(campoTelefone, restricoes);

        JButton botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.addActionListener(e -> cadastrar());

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoCadastrar);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);

        add(campos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void cadastrar() {
        Convenio convenio = new Convenio();
        convenio.setNome(campoNome.getText().trim());
        convenio.setTelefone(campoTelefone.getText().trim());

        try {
            new ManterConvenio().inserirConvenio(convenio);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar convênio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        campoNome.setText("");
        campoTelefone.setText("");
        campoNome.requestFocus();
    }
}
