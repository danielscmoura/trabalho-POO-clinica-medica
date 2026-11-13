/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.visao;

import br.com.controle.Especialidade;
import br.com.controle.Profissional;
import br.com.entidade.ManterEspecialidade;
import br.com.entidade.ManterProfissional;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de alteração (U - Update) de profissionais.
 * O usuário pesquisa, clica no profissional da tabela, altera e salva.
 *
 * Parte de cima: campo "Pesquisar por nome", botão "Pesquisar" e uma tabela
 * com as colunas ID, Nome, E-mail e Especialidade. Parte de baixo: os campos
 * de edição ID (só leitura), Nome, E-mail e Especialidade (lista suspensa),
 * e os botões "Salvar", "Limpar" e "Fechar".
 *
 * Classes de lógica usadas:
 * - {@link ManterProfissional}: pesquisar(...) para a tabela,
 *   pesquisarProfissional(...) para carregar um profissional pelo id e
 *   editarProfissional(...) para o UPDATE;
 * - {@link ManterEspecialidade}: carrega as opções da lista de especialidades.
 * A tela é aberta pelo menu Editar → Profissional da TelaPrincipal.
 */
public class EditarProfissionais extends javax.swing.JFrame {

    // ------------------------------------------------------------------
    // Estado da tela (atributos escritos à mão)
    // ------------------------------------------------------------------

    // id do profissional que está sendo editado (0 = nenhum selecionado)
    private int idProfissional = 0;
    // profissionais mostrados na tabela, na mesma ordem das linhas
    private ArrayList<Profissional> lista = new ArrayList<>();

    /**
     * Creates new form EditarProfissionais
     *
     * Construtor: monta os componentes (initComponents), centraliza a janela,
     * deixa a tabela selecionar só uma linha por vez, estreita a coluna ID,
     * carrega a lista de especialidades e já preenche a tabela com todos os
     * profissionais (sem aviso se ela vier vazia).
     */
    public EditarProfissionais() {
        initComponents();
        setLocationRelativeTo(null);
        jTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable.getColumnModel().getColumn(0).setMaxWidth(50);
        carregarEspecialidades();
        pesquisar(false); // já abre listando todos
    }

    // ------------------------------------------------------------------
    // Métodos auxiliares (escritos à mão)
    // ------------------------------------------------------------------

    // Preenche o JComboBox com as especialidades cadastradas no banco.
    // O primeiro item (id = 0) é só um texto de orientação.
    private void carregarEspecialidades() {
        // Esvazia a lista antes, para não duplicar itens quando o método é
        // chamado de novo (ver jTableMouseClicked).
        jCespecialidade.removeAllItems();
        jCespecialidade.addItem(new Especialidade(0, "Selecione..."));
        try {
            // pesquisar("") = sem filtro: traz todas, em ordem alfabética.
            // O combo mostra o nome porque Especialidade sobrescreve toString().
            for (Especialidade e : new ManterEspecialidade().pesquisar("")) {
                jCespecialidade.addItem(e);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar especialidades: " + ex.getMessage());
        }
    }

    // Busca os profissionais pelo nome e mostra na tabela.
    // A especialidade aparece pelo NOME (INNER JOIN feito no ManterProfissional).
    // avisarSeVazio = true mostra "Nenhum profissional encontrado" quando a
    // busca não traz nada (usado no botão; na abertura da tela é false).
    private void pesquisar(boolean avisarSeVazio) {
        DefaultTableModel modelo = (DefaultTableModel) jTable.getModel();
        modelo.setRowCount(0); // limpa a tabela
        try {
            // Filtra só pelo nome: e-mail "" e especialidade 0 = sem filtro.
            // O resultado fica guardado em "lista" para o clique na tabela
            // descobrir qual profissional corresponde a cada linha.
            lista = new ManterProfissional().pesquisar(jTpesquisa.getText().trim(), "", 0);
            for (Profissional p : lista) {
                // mesma ordem das colunas: ID, Nome, E-mail, Especialidade
                modelo.addRow(new Object[]{
                    p.getId(), p.getNome(), p.getEmail(), p.getEspecialidade().getNome()
                });
            }
            if (lista.isEmpty() && avisarSeVazio) {
                JOptionPane.showMessageDialog(this, "Nenhum profissional encontrado.");
            }
        } catch (Exception ex) {
            // Em caso de erro, a lista fica vazia, igual à tabela.
            lista = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar: " + ex.getMessage());
        }
    }

    // Esvazia os campos de edição e desfaz a seleção da tabela. Zerar o
    // idProfissional garante que um "Salvar" sem seleção não altere ninguém.
    private void limparCampos() {
        idProfissional = 0;
        jTid.setText("");
        jTnome.setText("");
        jTemail.setText("");
        jCespecialidade.setSelectedIndex(0);
        jTable.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     *
     * Em português: método gerado pelo editor visual do NetBeans a partir do
     * arquivo EditarProfissionais.form. Não deve ser editado à mão.
     *
     * Ele monta:
     * - o título jLtitulo ("Editar Profissional");
     * - a área de pesquisa: rótulo, campo jTpesquisa e botão jBpesquisar;
     * - a tabela jTable (dentro do jScrollPane1), com as colunas ID, Nome,
     *   E-mail e Especialidade, todas somente leitura, e um ouvinte de
     *   clique do mouse (MouseListener) que chama jTableMouseClicked;
     * - o rótulo jLabel5 com a instrução "Clique em um profissional...";
     * - os campos de edição jTid (não editável), jTnome, jTemail e a lista
     *   suspensa jCespecialidade ({@code JComboBox<Especialidade>});
     * - os botões jBsalvar, jBlimpar e jBfechar, ligados aos seus eventos;
     * - o GroupLayout com a posição de cada componente.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLtitulo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTpesquisa = new javax.swing.JTextField();
        jBpesquisar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTid = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTnome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTemail = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jCespecialidade = new javax.swing.JComboBox<>();
        jBsalvar = new javax.swing.JButton();
        jBlimpar = new javax.swing.JButton();
        jBfechar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editar Profissional");

        jLtitulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLtitulo.setText("Editar Profissional");

        jLabel1.setText("Pesquisar por nome:");

        jTpesquisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTpesquisaActionPerformed(evt);
            }
        });

        jBpesquisar.setText("Pesquisar");
        jBpesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBpesquisarActionPerformed(evt);
            }
        });

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "E-mail", "Especialidade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable);

        jLabel5.setText("Clique em um profissional da tabela para carregar os dados.");

        jLabel2.setText("ID");

        jTid.setEditable(false);

        jLabel3.setText("Nome");

        jLabel4.setText("E-mail");

        jLabel6.setText("Especialidade");

        jBsalvar.setText("Salvar");
        jBsalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBsalvarActionPerformed(evt);
            }
        });

        jBlimpar.setText("Limpar");
        jBlimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBlimparActionPerformed(evt);
            }
        });

        jBfechar.setText("Fechar");
        jBfechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBfecharActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLtitulo)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTpesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBpesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTnome, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTemail, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCespecialidade, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jBsalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBlimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBfechar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLtitulo)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTpesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBpesquisar))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTnome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jCespecialidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBsalvar)
                    .addComponent(jBlimpar)
                    .addComponent(jBfechar))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ------------------------------------------------------------------
    // Eventos da tela
    // ------------------------------------------------------------------

    // Enter no campo de pesquisa: doClick() "aperta" o botão Pesquisar.
    private void jTpesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTpesquisaActionPerformed
        jBpesquisar.doClick(); // Enter no campo = clicar em Pesquisar
    }//GEN-LAST:event_jTpesquisaActionPerformed

    // Clique em "Pesquisar": recarrega a tabela filtrando pelo nome e avisa
    // se nada for encontrado.
    private void jBpesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBpesquisarActionPerformed
        pesquisar(true);
    }//GEN-LAST:event_jBpesquisarActionPerformed

    // Clique em uma linha da tabela: carrega aquele profissional nos campos
    // de edição, já com a especialidade dele selecionada na lista.
    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        // 1) Descobre a linha selecionada (-1 = clique fora de qualquer linha).
        int linha = jTable.getSelectedRow();
        if (linha < 0) {
            return;
        }
        // pega o id da linha clicada e busca os dados atualizados no banco
        // (convertRowIndexToModel converte o número da linha na tela para o
        // número no modelo; os dois só diferem se a tabela for reordenada).
        Profissional p = new Profissional();
        p.setId(lista.get(jTable.convertRowIndexToModel(linha)).getId());
        try {
            // 2) pesquisarProfissional() preenche o objeto p a partir do id;
            //    devolve false (e avisa) se o profissional não existir mais.
            if (new ManterProfissional().pesquisarProfissional(p)) {
                // 3) Guarda o id para o Salvar e copia os dados para os campos.
                idProfissional = p.getId();
                jTid.setText(String.valueOf(p.getId()));
                jTnome.setText(p.getNome());
                jTemail.setText(p.getEmail());
                // seleciona no combo a especialidade do profissional (usa o equals por id)
                jCespecialidade.setSelectedItem(p.getEspecialidade());
                if (!p.getEspecialidade().equals(jCespecialidade.getSelectedItem())) {
                    // especialidade cadastrada depois que a tela abriu: recarrega o combo
                    carregarEspecialidades();
                    jCespecialidade.setSelectedItem(p.getEspecialidade());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar profissional: " + ex.getMessage());
        }
    }//GEN-LAST:event_jTableMouseClicked

    // Clique em "Salvar": monta um Profissional com o id guardado e o que
    // está nos campos e pede ao ManterProfissional para fazer o UPDATE.
    // Se der certo, limpa os campos e recarrega a tabela.
    private void jBsalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBsalvarActionPerformed
        // 1) Monta o objeto: o id diz QUAL profissional alterar; o item do
        //    combo já é o objeto Especialidade escolhido.
        Profissional p = new Profissional();
        p.setId(idProfissional);
        p.setNome(jTnome.getText().trim());
        p.setEmail(jTemail.getText().trim());
        p.setEspecialidade((Especialidade) jCespecialidade.getSelectedItem());
        try {
            // 2) editarProfissional() valida (id, nome, e-mail, especialidade),
            //    grava, mostra a mensagem e devolve true só se deu certo.
            if (new ManterProfissional().editarProfissional(p)) {
                limparCampos();
                pesquisar(false); // atualiza a tabela com os dados novos
            }
        } catch (Exception ex) {
            // Erro inesperado (ex.: banco fora do ar).
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + ex.getMessage());
        }
    }//GEN-LAST:event_jBsalvarActionPerformed

    // Clique em "Limpar": esvazia os campos e tira a seleção da tabela.
    private void jBlimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBlimparActionPerformed
        limparCampos();
    }//GEN-LAST:event_jBlimparActionPerformed

    // Clique em "Fechar": fecha só esta janela (dispose).
    private void jBfecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBfecharActionPerformed
        dispose();
    }//GEN-LAST:event_jBfecharActionPerformed

    // ------------------------------------------------------------------
    // Execução isolada da tela
    // ------------------------------------------------------------------

    /**
     * @param args the command line arguments
     *
     * Método main gerado pelo NetBeans: permite abrir só esta tela para
     * testes (Run File), sem passar pela TelaPrincipal. Aplica o visual
     * "Nimbus" e cria a janela na thread de eventos do Swing.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditarProfissionais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditarProfissionais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditarProfissionais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditarProfissionais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditarProfissionais().setVisible(true);
            }
        });
    }

    // ------------------------------------------------------------------
    // Componentes da tela (atributos). Bloco gerado pelo NetBeans a partir
    // do .form: jB = botão, jT = campo de texto, jC = combo (lista
    // suspensa), jTable = tabela, jL/jLabel = rótulo.
    // ------------------------------------------------------------------
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBfechar;
    private javax.swing.JButton jBlimpar;
    private javax.swing.JButton jBpesquisar;
    private javax.swing.JButton jBsalvar;
    private javax.swing.JComboBox<br.com.controle.Especialidade> jCespecialidade;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLtitulo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jTemail;
    private javax.swing.JTextField jTid;
    private javax.swing.JTextField jTnome;
    private javax.swing.JTextField jTpesquisa;
    // End of variables declaration//GEN-END:variables
}
