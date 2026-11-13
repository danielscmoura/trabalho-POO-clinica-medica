package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import java.awt.*;

/**
 * Tela de EXCLUSÃO de consultas (o "D" do CRUD de Consulta).
 * Mostra um campo para o ID da consulta e o botão Deletar. Depois de uma
 * confirmação Sim/Não, chama a classe de lógica ManterConsulta (método
 * deletarConsulta), que executa o DELETE pelo ID.
 */
public class DeletarConsulta extends JFrame {
    // Campo onde se digita o ID da consulta e botão que dispara a exclusão.
    private JTextField txtIdConsulta;
    private JButton btnDeletar;

    /** Construtor: monta uma janela pequena com o campo do ID e o botão Deletar. */
    public DeletarConsulta() {
        // Título e tamanho fixo (300 x 150); sem setDefaultCloseOperation, o X só esconde a janela (padrão do JFrame).
        setTitle("Deletar Consulta");
        setSize(300, 150);
        // GridLayout(2, 2, 10, 10): grade de 2 linhas x 2 colunas, células iguais e 10 px de espaço entre elas.
        setLayout(new GridLayout(2, 2, 10, 10));
        setLocationRelativeTo(null);

        add(new JLabel(" ID da Consulta a deletar:"));
        txtIdConsulta = new JTextField();
        add(txtIdConsulta);

        // Segunda linha: rótulo vazio à esquerda para o botão ficar embaixo do campo.
        add(new JLabel(""));
        btnDeletar = new JButton("Deletar");
        add(btnDeletar);

        // O clique em Deletar chama o método deletar().
        btnDeletar.addActionListener(e -> deletar());
    }

    /**
     * Executado ao clicar em Deletar: converte o ID digitado, pede confirmação e,
     * se a resposta for Sim, manda o ManterConsulta excluir a consulta.
     */
    private void deletar() {
        try {
            // Integer.parseInt lança NumberFormatException se o campo estiver vazio ou tiver letras (vai para o catch).
            int id = Integer.parseInt(txtIdConsulta.getText().trim());
            
            // Confirmação visual para prevenir exclusões acidentais na interface
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem a certeza que deseja deletar a consulta " + id + "?", "Aviso", JOptionPane.YES_NO_OPTION);
            
            if (confirmacao == JOptionPane.YES_OPTION) {
                // Instancia o objeto fornecendo apenas o ID, que é a única Primary Key necessária para o comando DELETE no banco
                Consulta c = new Consulta();
                c.setId(id);
                
                ManterConsulta dao = new ManterConsulta();
                dao.deletarConsulta(c);
                
                // Sem exceção: confirma e fecha a janela.
                JOptionPane.showMessageDialog(this, "Consulta apagada!");
                dispose();
            }
            // Se a resposta for Não, nada acontece e a janela continua aberta.
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar: verifique o ID.\n" + ex.getMessage());
        }
    }
}