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
// Mostra o campo ID com o botão Buscar e, abaixo, Nome e Telefone para alterar; botões Salvar, Limpar e Fechar.
// Usa ManterConvenio: pesquisarConvenio() para carregar os dados e editarConvenio() para o UPDATE.
public class EditarConvenios extends JFrame {

    // Campos: ID a buscar e os dados (nome e telefone) que podem ser alterados.
    private final JTextField campoId = new JTextField(8);
    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(20);
    // ID do convênio carregado pela última busca bem-sucedida (0 = nenhum); é ele que vai no UPDATE.
    private int idConvenio;

    /**
     * Construtor: define o título, faz o X fechar só esta janela (DISPOSE_ON_CLOSE),
     * monta os componentes, ajusta o tamanho ao conteúdo (pack) e centraliza.
     */
    public EditarConvenios() {
        setTitle("Editar Convênio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        montarTela();
        pack();
        setLocationRelativeTo(null);
    }

    /** Monta os campos de busca, edição e os botões da tela. */
    private void montarTela() {
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
        // Linhas 1 e 2: Nome e Telefone; gridwidth = 2 faz o campo ocupar as colunas 1 e 2
        // (a largura do ID mais o botão Buscar); no rótulo Telefone o gridwidth volta a 1.
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

        // Salvar chama salvar(), Limpar chama limpar() e Fechar chama dispose().
        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());
        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        // FlowLayout(RIGHT): botões em fila, alinhados à direita.
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoSalvar);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);

        // O JFrame usa BorderLayout por padrão: campos no CENTRO e botões no SUL (embaixo).
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

        // Objeto só com o ID; se encontrar o registro, o DAO preenche nome e telefone nele.
        Convenio convenio = new Convenio();
        convenio.setId(idInformado);

        try {
            // pesquisarConvenio devolve true quando acha; quando não acha, ele mesmo avisa "Convênio não encontrado!".
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
        // idConvenio = 0 significa que nenhuma busca deu certo ainda.
        if (idConvenio == 0) {
            JOptionPane.showMessageDialog(this, "Busque um convênio antes de salvar.");
            return;
        }

        // Cria um objeto com o ID original e os valores que o usuário alterou na tela.
        Convenio convenio = new Convenio(idConvenio,
                campoNome.getText().trim(), campoTelefone.getText().trim());

        try {
            // O DAO valida o nome, impede nome igual ao de outro convênio, faz o UPDATE e mostra a mensagem.
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
