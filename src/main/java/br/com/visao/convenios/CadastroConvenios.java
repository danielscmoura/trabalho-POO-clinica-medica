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
// Mostra os campos Nome (obrigatório, marcado com *) e Telefone e os botões Cadastrar, Limpar e Fechar.
// A validação, a checagem de nome repetido e o INSERT ficam em ManterConvenio.inserirConvenio().
public class CadastroConvenios extends JFrame {

    // Campos de texto do formulário: nome do convênio e telefone (opcional).
    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoTelefone = new JTextField(20);

    /**
     * Construtor: define o título, faz o X fechar só esta janela (DISPOSE_ON_CLOSE),
     * monta os componentes, ajusta o tamanho ao conteúdo (pack) e centraliza.
     */
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
        // gridx = coluna e gridy = linha de cada componente; insets dá 8 px de margem e anchor WEST alinha à esquerda.
        restricoes.insets = new Insets(8, 8, 8, 8);
        restricoes.anchor = GridBagConstraints.WEST;

        restricoes.gridx = 0;
        restricoes.gridy = 0;
        // Linha 0: rótulo Nome (coluna 0) e campo (coluna 1); o * indica campo obrigatório.
        campos.add(new JLabel("Nome:*"), restricoes);

        restricoes.gridx = 1;
        campos.add(campoNome, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 1;
        // Linha 1: rótulo e campo do telefone.
        campos.add(new JLabel("Telefone:"), restricoes);

        restricoes.gridx = 1;
        campos.add(campoTelefone, restricoes);

        // Botões: Cadastrar chama cadastrar(), Limpar chama limpar() e Fechar chama dispose(), que fecha a janela.
        JButton botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.addActionListener(e -> cadastrar());

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limpar());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        // FlowLayout(RIGHT): botões em fila, na ordem do add(), alinhados à direita.
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoCadastrar);
        botoes.add(botaoLimpar);
        botoes.add(botaoFechar);

        // O JFrame usa BorderLayout por padrão: campos no CENTRO e botões no SUL (embaixo).
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
            // O próprio ManterConvenio exibe as mensagens de sucesso, nome vazio ou nome repetido.
            new ManterConvenio().inserirConvenio(convenio);
        } catch (Exception e) {
            // Falha de conexão ou de SQL vira uma caixa de erro.
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
