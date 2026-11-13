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
 * View: tela de EXCLUSÃO de Especialidade (Delete).
 * Pesquisa pelo nome, pede confirmação e exclui.
 * Mostra o campo do nome e os botões Deletar e Fechar. A busca é pelo nome exato
 * (ManterEspecialidade.buscarPorNome) e a exclusão, pelo id encontrado
 * (ManterEspecialidade.deletarEspecialidade). Se houver profissionais nessa
 * especialidade, o banco bloqueia a exclusão e o DAO avisa o usuário.
 */
public class DeletarEspecialidades extends JFrame {

    // Classe de lógica/banco usada pela tela.
    private final ManterEspecialidade manter = new ManterEspecialidade();
    // Campo onde o usuário digita o nome da especialidade a excluir.
    private JTextField jTnome;

    /** Construtor: só chama initComponents(), que monta a tela. */
    public DeletarEspecialidades() {
        initComponents();
    }

    /** Monta a tela: formulário com o nome no centro e botões Deletar e Fechar embaixo. */
    private void initComponents() {
        // Título e DISPOSE_ON_CLOSE: o X fecha e libera só esta janela.
        setTitle("Deletar Especialidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Campo do nome; Enter nele também chama deletar().
        jTnome = new JTextField(25);
        jTnome.addActionListener(e -> deletar());

        // GridBagLayout: rótulo na coluna 0 e campo na coluna 1 da mesma linha, com 6 px de margem (insets).
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Nome da especialidade"), g);
        g.gridx = 1;
        form.add(jTnome, g);

        // Deletar chama deletar(); Fechar chama dispose(), que fecha a janela.
        JButton jBdeletar = new JButton("Deletar");
        JButton jBfechar = new JButton("Fechar");
        jBdeletar.addActionListener(e -> deletar());
        jBfechar.addActionListener(e -> dispose());

        // FlowLayout(RIGHT): botões lado a lado, alinhados à direita.
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(jBdeletar);
        botoes.add(jBfechar);

        // Painel raiz (BorderLayout): formulário no CENTRO e botões no SUL, com 12 px de margem.
        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);
        setContentPane(raiz);

        // pack() ajusta o tamanho ao conteúdo e setLocationRelativeTo(null) centraliza a janela.
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Executado pelo botão Deletar (ou Enter): valida o nome, procura a especialidade,
     * pede confirmação e só então manda excluir.
     */
    private void deletar() {
        // Validação: o nome é obrigatório.
        String nome = jTnome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da especialidade.");
            return;
        }
        try {
            // Procura pelo nome exato; null significa que não existe.
            Especialidade encontrada = manter.buscarPorNome(nome);
            if (encontrada == null) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma especialidade encontrada com esse nome.\nUse Listar para consultar os nomes cadastrados.");
                return;
            }
            // Caixa Sim/Não para evitar exclusão acidental; qualquer resposta diferente de Sim cancela.
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente excluir a especialidade " + encontrada.getNome() + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
            // O DAO executa o DELETE e mostra a mensagem; se excluiu (true), o campo é esvaziado.
            if (manter.deletarEspecialidade(encontrada)) { // exclui pelo id vindo da pesquisa
                jTnome.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
        }
    }

}
