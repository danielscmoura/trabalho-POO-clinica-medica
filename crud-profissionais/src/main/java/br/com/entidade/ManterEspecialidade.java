/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.entidade;

import br.com.controle.Especialidade;
import java.util.ArrayList;

/**
 * Acesso à tabela "especialidades".
 * Por enquanto só tem a listagem, usada para preencher o JComboBox
 * das telas de Profissional. O CRUD completo de Especialidade pode
 * ser acrescentado aqui seguindo o mesmo padrão do ManterPaciente.
 */
public class ManterEspecialidade extends DAO {

    public ArrayList<Especialidade> listar() throws Exception {
        ArrayList<Especialidade> especialidades = new ArrayList<>();
        try {
            abrirBanco();
            pst = con.prepareStatement("SELECT id, nome FROM especialidades ORDER BY nome");
            rs = pst.executeQuery();
            while (rs.next()) {
                especialidades.add(new Especialidade(rs.getInt("id"), rs.getString("nome")));
            }
        } finally {
            fecharBanco();
        }
        return especialidades;
    }
}
