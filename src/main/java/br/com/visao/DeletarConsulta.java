package br.com.visao;

import br.com.controle.Consulta;
import br.com.entidade.ManterConsulta;
import javax.swing.*;
import java.awt.*;

public class DeletarConsulta extends JFrame {
    private JTextField txtIdConsulta;
    private JButton btnDeletar;

    public DeletarConsulta() {
        setTitle("Deletar Consulta");
        setSize(300, 150);
        setLayout(new GridLayout(2, 2, 10, 10));
        setLocationRelativeTo(null);

        add(new JLabel(" ID da Consulta a deletar:"));
        txtIdConsulta = new JTextField();
        add(txtIdConsulta);

        add(new JLabel(""));
        btnDeletar = new JButton("Deletar");
        add(btnDeletar);

        btnDeletar.addActionListener(e -> deletar());
    }

    private void deletar() {
        try {
            int id = Integer.parseInt(txtIdConsulta.getText().trim());
            
            // Confirmação visual para prevenir exclusões acidentais na interface
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem a certeza que deseja deletar a consulta " + id + "?", "Aviso", JOptionPane.YES_NO_OPTION);
            
            if (confirmacao == JOptionPane.YES_OPTION) {
                // Instancia o objeto fornecendo apenas o ID, que é a única Primary Key necessária para o comando DELETE no banco
                Consulta c = new Consulta();
                c.setId(id);
                
                ManterConsulta dao = new ManterConsulta();
                dao.deletarConsulta(c);
                
                JOptionPane.showMessageDialog(this, "Consulta apagada!");
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar: verifique o ID.\n" + ex.getMessage());
        }
    }
}