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
 * View: tela de EDIÇÃO de Especialidade (Update).
 * 1) pesquisa pelo nome; 2) altera o nome; 3) salva.
 * A pesquisa usa o nome exato (ManterEspecialidade.buscarPorNome). O código (id)
 * aparece só para leitura, e o campo "Novo nome" e o botão Salvar ficam
 * desabilitados até a pesquisa encontrar um registro. A gravação é feita por
 * ManterEspecialidade.editarEspecialidade().
 */
public class EditarEspecialidades extends JFrame {

    // Classe de lógica/banco usada pela tela.
    private final ManterEspecialidade manter = new ManterEspecialidade();
    private Especialidade encontrada; // objeto carregado pela pesquisa (guarda o id)

    // Campos: nome a pesquisar, código (somente leitura) e novo nome. O botão Salvar é atributo
    // porque habilitarEdicao() precisa ligá-lo e desligá-lo.
    private JTextField jTnomePesquisa;
    private JTextField jTid;
    private JTextField jTnome;
    private JButton jBsalvar;

    /** Construtor: monta a tela e começa com a edição bloqueada, pois nada foi pesquisado ainda. */
    public EditarEspecialidades() {
        initComponents();
        habilitarEdicao(false);
    }

    /**
     * Monta a tela em três partes: painel de pesquisa (topo), painel com os dados
     * da especialidade (meio) e botões (rodapé).
     */
    private void initComponents() {
        // Título e DISPOSE_ON_CLOSE: o X fecha e libera só esta janela, sem encerrar o sistema.
        setTitle("Editar Especialidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---- pesquisa ----
        // Enter no campo ou clique em Pesquisar chamam pesquisar().
        jTnomePesquisa = new JTextField(22);
        JButton jBpesquisar = new JButton("Pesquisar");
        jTnomePesquisa.addActionListener(e -> pesquisar());
        jBpesquisar.addActionListener(e -> pesquisar());

        // FlowLayout(LEFT): rótulo, campo e botão em fila, alinhados à esquerda, dentro de uma moldura com título.
        JPanel painelPesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelPesquisa.setBorder(BorderFactory.createTitledBorder("Pesquisar pelo nome"));
        painelPesquisa.add(new JLabel("Nome:"));
        painelPesquisa.add(jTnomePesquisa);
        painelPesquisa.add(jBpesquisar);

        // ---- dados ----
        // Código não editável (vem do banco); Enter no novo nome chama salvar().
        jTid = new JTextField(6);
        jTid.setEditable(false);
        jTnome = new JTextField(25);
        jTnome.addActionListener(e -> salvar());

        // GridBagLayout em grade: coluna 0 com os rótulos e coluna 1 com os campos (linha 0 = código,
        // linha 1 = novo nome); anchor WEST alinha tudo à esquerda e insets dá a margem.
        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder("Dados da especialidade"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0;
        painelDados.add(new JLabel("Código"), g);
        g.gridx = 1;
        painelDados.add(jTid, g);
        g.gridx = 0; g.gridy = 1;
        painelDados.add(new JLabel("Novo nome"), g);
        g.gridx = 1;
        painelDados.add(jTnome, g);

        // ---- botões ----
        // Salvar chama salvar(), Limpar chama limpar() e Fechar chama dispose(); ficam à direita (FlowLayout RIGHT).
        jBsalvar = new JButton("Salvar");
        JButton jBlimpar = new JButton("Limpar");
        JButton jBfechar = new JButton("Fechar");
        jBsalvar.addActionListener(e -> salvar());
        jBlimpar.addActionListener(e -> limpar());
        jBfechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(jBsalvar);
        botoes.add(jBlimpar);
        botoes.add(jBfechar);

        // Painel do meio com BorderLayout: pesquisa no NORTE (em cima) e dados no CENTRO.
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(painelPesquisa, BorderLayout.NORTH);
        centro.add(painelDados, BorderLayout.CENTER);

        // Painel raiz (BorderLayout): conteúdo no CENTRO e botões no SUL, com 12 px de margem; vira o conteúdo da janela.
        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);

        // pack() ajusta o tamanho ao conteúdo e setLocationRelativeTo(null) centraliza a janela.
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Executado pelo botão Pesquisar (ou Enter): procura a especialidade pelo nome exato.
     * Se achar, guarda o objeto em "encontrada", preenche código e nome e libera a edição;
     * se não achar, limpa os campos e mantém a edição bloqueada.
     */
    private void pesquisar() {
        // Validação: sem nome não há o que pesquisar.
        String nome = jTnomePesquisa.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da especialidade.");
            return;
        }
        try {
            // Consulta o banco; o retorno é null quando não existe especialidade com esse nome.
            encontrada = manter.buscarPorNome(nome);
            if (encontrada == null) {
                habilitarEdicao(false);
                jTid.setText("");
                jTnome.setText("");
                JOptionPane.showMessageDialog(this,
                        "Nenhuma especialidade encontrada com esse nome.\nUse Listar para consultar os nomes cadastrados.");
                return;
            }
            // Encontrou: mostra os dados, libera a edição e põe o cursor no campo do novo nome.
            jTid.setText(String.valueOf(encontrada.getId()));
            jTnome.setText(encontrada.getNome());
            habilitarEdicao(true);
            jTnome.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar: " + ex.getMessage());
        }
    }

    /**
     * Executado pelo botão Salvar (ou Enter no novo nome): exige uma pesquisa feita antes,
     * monta a Especialidade com o id encontrado e o nome digitado e pede ao
     * ManterEspecialidade que grave. Se deu certo, a tela volta ao estado inicial.
     */
    private void salvar() {
        if (encontrada == null) {
            JOptionPane.showMessageDialog(this, "Pesquise uma especialidade antes de salvar.");
            return;
        }
        // usa o id que veio da pesquisa; só o nome muda
        Especialidade esp = new Especialidade(encontrada.getId(), jTnome.getText().trim());
        try {
            // O DAO valida (vazio, tamanho, nome já usado por outra especialidade), faz o UPDATE
            // e mostra a mensagem; true significa que gravou.
            if (manter.editarEspecialidade(esp)) {
                limpar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + ex.getMessage());
        }
    }

    /** Volta a tela ao estado inicial: esquece o registro pesquisado, esvazia os campos e bloqueia a edição. */
    private void limpar() {
        encontrada = null;
        jTnomePesquisa.setText("");
        jTid.setText("");
        jTnome.setText("");
        habilitarEdicao(false);
        jTnomePesquisa.requestFocus();
    }

    /** Liga (true) ou desliga (false) o campo Novo nome e o botão Salvar. */
    private void habilitarEdicao(boolean habilitar) {
        jTnome.setEnabled(habilitar);
        jBsalvar.setEnabled(habilitar);
    }

}
