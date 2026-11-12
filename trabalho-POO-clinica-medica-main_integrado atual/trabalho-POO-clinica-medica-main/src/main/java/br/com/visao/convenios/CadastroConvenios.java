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

/** Tela Swing responsável pelo cadastro de novos convênios. */
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

    /** Monta os campos e associa os botões aos métodos da tela. */
    private void montarTela() {
        // GridBagLayout organiza rótulos e campos em linhas e colunas.
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

    /** Lê os campos da tela, cria o Model Convenio e solicita a inclusão no banco. */
    private void cadastrar() {
        // A View transforma o texto dos campos em um objeto do Model.
        Convenio convenio = new Convenio();
        convenio.setNome(campoNome.getText().trim());
        convenio.setTelefone(campoTelefone.getText().trim());

        try {
            // A regra e o SQL ficam fora da tela, na classe ManterConvenio.
            new ManterConvenio().inserirConvenio(convenio);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar convênio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Limpa os campos para permitir um novo cadastro. */
    private void limpar() {
        campoNome.setText("");
        campoTelefone.setText("");
        campoNome.requestFocus();
    }
}
