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

public class EditarConvenios extends JFrame {

    private final JTextField campoId = new JTextField(8);
    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(20);
    private int idConvenio;

    public EditarConvenios() {
        setTitle("Editar Convênio");
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
        campos.add(new JLabel("ID do convênio:"), restricoes);
        restricoes.gridx = 1;
        campos.add(campoId, restricoes);

        JButton botaoBuscar = new JButton("Buscar");
        botaoBuscar.addActionListener(e -> buscar());
        restricoes.gridx = 2;
        campos.add(botaoBuscar, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 1;
        campos.add(new JLabel("Nome:*"), restricoes);
        restricoes.gridx = 1;
        restricoes.gridwidth = 2;
        campos.add(campoNome, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 2;
        restricoes.gridwidth = 1;
        campos.add(new JLabel("Telefone:"), restricoes);
        restricoes.gridx = 1;
        restricoes.gridwidth = 2;
        campos.add(campoTelefone, restricoes);

        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoSalvar);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);

        add(campos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void buscar() {
        Integer idInformado = lerId();
        if (idInformado == null) {
            return;
        }

        Convenio convenio = new Convenio();
        convenio.setId(idInformado);

        try {
            if (new ManterConvenio().pesquisarConvenio(convenio)) {
                idConvenio = convenio.getId();
                campoNome.setText(convenio.getNome());
                campoTelefone.setText(convenio.getTelefone());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar convênio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        if (idConvenio == 0) {
            JOptionPane.showMessageDialog(this, "Busque um convênio antes de salvar.");
            return;
        }

        Convenio convenio = new Convenio(idConvenio,
                campoNome.getText().trim(), campoTelefone.getText().trim());

        try {
            new ManterConvenio().editarConvenio(convenio);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao editar convênio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer lerId() {
        try {
            return Integer.valueOf(campoId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um ID numérico válido.");
            return null;
        }
    }

    private void limpar() {
        idConvenio = 0;
        campoId.setText("");
        campoNome.setText("");
        campoTelefone.setText("");
        campoId.requestFocus();
    }
}
