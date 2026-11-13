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
 * View: tela de CADASTRO de Especialidade (Create).
 * Só interface; o SQL fica em ManterEspecialidade.
 * Mostra um único campo (nome da especialidade) e os botões Cadastrar, Limpar
 * e Fechar. A validação (nome vazio, mais de 50 caracteres, nome já cadastrado)
 * e o INSERT são feitos por ManterEspecialidade.inserirEspecialidade().
 */
public class CadastroEspecialidades extends JFrame {

    // Classe de lógica/banco usada pela tela; criada uma vez e reaproveitada a cada clique.
    private final ManterEspecialidade manter = new ManterEspecialidade();
    // Campo de texto onde o usuário digita o nome da especialidade.
    private JTextField jTnome;

    /** Construtor: só chama initComponents(), que monta a tela inteira. */
    public CadastroEspecialidades() {
        initComponents();
    }

    /**
     * Monta a tela: configura a janela, cria o formulário com o campo de nome,
     * a barra de botões e junta tudo num painel raiz.
     */
    private void initComponents() {
        setTitle("Cadastrar Especialidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // fecha só esta tela

        // Campo do nome, com largura para cerca de 25 caracteres.
        jTnome = new JTextField(25);
        jTnome.addActionListener(e -> cadastrar()); // Enter cadastra

        // Formulário com GridBagLayout: cada componente vai numa célula (gridx = coluna, gridy = linha);
        // insets dá 6 px de margem em volta. Aqui: rótulo na coluna 0 e campo na coluna 1, na mesma linha.
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Nome da especialidade"), g);
        g.gridx = 1;
        form.add(jTnome, g);

        // Botões: Cadastrar chama cadastrar(), Limpar chama limpar() e Fechar chama dispose(), que fecha a janela.
        JButton jBcadastrar = new JButton("Cadastrar");
        JButton jBlimpar = new JButton("Limpar");
        JButton jBfechar = new JButton("Fechar");
        jBcadastrar.addActionListener(e -> cadastrar());
        jBlimpar.addActionListener(e -> limpar());
        jBfechar.addActionListener(e -> dispose());

        // FlowLayout(RIGHT): coloca os botões lado a lado, na ordem do add(), alinhados à direita.
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(jBcadastrar);
        botoes.add(jBlimpar);
        botoes.add(jBfechar);

        // Painel raiz com BorderLayout (regiões NORTE, SUL, LESTE, OESTE e CENTRO): formulário no CENTRO,
        // botões no SUL; a borda vazia deixa 12 px de margem. setContentPane o torna o conteúdo da janela.
        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);

        // pack() ajusta o tamanho da janela ao conteúdo; setLocationRelativeTo(null) centraliza na tela.
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Executado pelo botão Cadastrar ou pelo Enter no campo: monta o objeto
     * Especialidade com o nome digitado e pede ao ManterEspecialidade que o grave.
     * O DAO valida, mostra as mensagens e devolve true só quando gravou.
     */
    private void cadastrar() {
        // Monta o objeto do Model com o nome sem espaços nas pontas (trim).
        Especialidade esp = new Especialidade();
        esp.setNome(jTnome.getText().trim());
        try {
            // Só limpa o campo se gravou; com nome inválido ou repetido o texto fica para correção.
            if (manter.inserirEspecialidade(esp)) {
                limpar();
            }
        } catch (Exception ex) {
            // Falha de conexão ou de SQL vira mensagem para o usuário.
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage());
        }
    }

    /** Esvazia o campo e devolve o cursor para ele, pronto para o próximo cadastro. */
    private void limpar() {
        jTnome.setText("");
        jTnome.requestFocus();
    }

}
