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

/** Tela Swing que localiza um convênio pelo ID e confirma sua exclusão. */
public class DeletarConvenios extends JFrame {

    private final JTextField campoId = new JTextField(8);
    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(20);
    private Convenio convenioSelecionado;

    public DeletarConvenios() {
        setTitle("Excluir Convênio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        montarTela();
        pack();
        setLocationRelativeTo(null);
    }

    /** Monta a tela e deixa os dados do convênio somente para leitura antes da exclusão. */
    private void montarTela() {
        campoNome.setEditable(false);
        campoTelefone.setEditable(false);

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
        campos.add(new JLabel("Nome:"), restricoes);
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

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(e -> excluir());
        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoExcluir);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);

        add(campos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    /** Busca o convênio pelo ID e o mantém selecionado para uma possível exclusão. */
    private void buscar() {
        // Só é possível excluir depois de localizar um registro válido pelo ID.
        Integer idInformado = lerId();
        if (idInformado == null) {
            return;
        }

        Convenio convenio = new Convenio();
        convenio.setId(idInformado);

        try {
            if (new ManterConvenio().pesquisarConvenio(convenio)) {
                // Mantém o objeto selecionado para excluir exatamente o registro exibido.
                convenioSelecionado = convenio;
                campoNome.setText(convenio.getNome());
                campoTelefone.setText(convenio.getTelefone());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar convênio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pede confirmação ao usuário e solicita a exclusão do convênio selecionado. */
    private void excluir() {
        if (convenioSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Busque um convênio antes de excluir.");
            return;
        }

        // A confirmação evita exclusões acidentais.
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o convênio " + convenioSelecionado.getNome() + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // A classe de negócio também trata o caso de convênio ligado a consultas.
            new ManterConvenio().deletarConvenio(convenioSelecionado);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir convênio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Converte o texto digitado em ID numérico e trata entradas inválidas. */
    private Integer lerId() {
        try {
            return Integer.valueOf(campoId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um ID numérico válido.");
            return null;
        }
    }

    /** Limpa os campos e desfaz a seleção atual. */
    private void limpar() {
        convenioSelecionado = null;
        campoId.setText("");
        campoNome.setText("");
        campoTelefone.setText("");
        campoId.requestFocus();
    }
}
