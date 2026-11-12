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

/** Tela Swing usada para buscar um convênio pelo ID e alterar seus dados. */
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

    /** Monta os campos de busca, edição e os botões da tela. */
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

    /** Busca o convênio pelo ID e carrega os dados encontrados nos campos de edição. */
    private void buscar() {
        // Primeiro valida e converte o ID digitado antes de pesquisar no banco.
        Integer idInformado = lerId();
        if (idInformado == null) {
            return;
        }

        Convenio convenio = new Convenio();
        convenio.setId(idInformado);

        try {
            if (new ManterConvenio().pesquisarConvenio(convenio)) {
                // Guarda o ID para garantir que o UPDATE altere o mesmo registro pesquisado.
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

    /** Envia as alterações do convênio selecionado para o banco de dados. */
    private void salvar() {
        if (idConvenio == 0) {
            JOptionPane.showMessageDialog(this, "Busque um convênio antes de salvar.");
            return;
        }

        // Cria um objeto com o ID original e os valores que o usuário alterou na tela.
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

    /** Converte o texto digitado em ID numérico e trata entradas inválidas. */
    private Integer lerId() {
        try {
            // Integer.valueOf pode gerar erro se o texto não for numérico.
            return Integer.valueOf(campoId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um ID numérico válido.");
            return null;
        }
    }

    /** Limpa os campos e remove o ID do convênio que estava selecionado. */
    private void limpar() {
        idConvenio = 0;
        campoId.setText("");
        campoNome.setText("");
        campoTelefone.setText("");
        campoId.requestFocus();
    }
}
