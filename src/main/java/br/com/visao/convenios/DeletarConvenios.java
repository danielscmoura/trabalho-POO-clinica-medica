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
// Mostra o campo ID com o botão Buscar, os dados do convênio (só leitura) para conferir e os botões Excluir, Limpar e Fechar.
// Usa ManterConvenio: pesquisarConvenio() para localizar o registro e deletarConvenio() para o DELETE.
public class DeletarConvenios extends JFrame {

    // Campos: ID a buscar e nome/telefone exibidos só para conferência.
    private final JTextField campoId = new JTextField(8);
    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(20);
    // Convênio encontrado pela última busca bem-sucedida (null = nenhum); é ele que será excluído.
    private Convenio convenioSelecionado;

    /**
     * Construtor: define o título, faz o X fechar só esta janela (DISPOSE_ON_CLOSE),
     * monta os componentes, ajusta o tamanho ao conteúdo (pack) e centraliza.
     */
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

        // Painel de campos com GridBagLayout: cada componente numa célula (gridx = coluna, gridy = linha),
        // com 8 px de margem (insets) e alinhado à esquerda (WEST).
        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(8, 8, 8, 8);
        restricoes.anchor = GridBagConstraints.WEST;

        restricoes.gridx = 0;
        restricoes.gridy = 0;
        // Linha 0: ID do convênio e botão Buscar, que chama buscar().
        campos.add(new JLabel("ID do convênio:"), restricoes);
        restricoes.gridx = 1;
        campos.add(campoId, restricoes);

        JButton botaoBuscar = new JButton("Buscar");
        botaoBuscar.addActionListener(e -> buscar());
        restricoes.gridx = 2;
        campos.add(botaoBuscar, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 1;
        // Linhas 1 e 2: Nome e Telefone ocupando as colunas 1 e 2 (gridwidth = 2).
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

        // Excluir chama excluir(), Limpar chama limpar() e Fechar chama dispose().
        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(e -> excluir());
        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        // FlowLayout(RIGHT): botões em fila, alinhados à direita.
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoExcluir);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);

        // O JFrame usa BorderLayout por padrão: campos no CENTRO e botões no SUL (embaixo).
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

        // Objeto só com o ID; se encontrar o registro, o DAO preenche nome e telefone nele
        // (se não encontrar, ele mesmo avisa "Convênio não encontrado!").
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
        // Sem uma busca bem-sucedida antes, não há o que excluir.
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
