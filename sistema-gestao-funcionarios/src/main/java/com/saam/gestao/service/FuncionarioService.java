package com.saam.gestao.service;

import com.saam.gestao.dao.FuncionarioDAO;
import com.saam.gestao.exception.ValidationException;
import com.saam.gestao.model.Funcionario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negocio da tela de Cadastro de Funcionarios: validacao de
 * campos e delegacao da persistencia ao FuncionarioDAO.
 */
public class FuncionarioService {

    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public FuncionarioService(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    public Funcionario cadastrar(String nome, LocalDate dataAdmissao, BigDecimal salario, boolean status) {
        validarNome(nome);
        validarDataAdmissao(dataAdmissao);
        validarSalario(salario);

        Funcionario funcionario = new Funcionario(nome.trim(), dataAdmissao, salario, status);
        return funcionarioDAO.cadastrar(funcionario);
    }

    public void atualizar(Long id, String nome, LocalDate dataAdmissao, BigDecimal salario, boolean status) {
        if (id == null) {
            throw new ValidationException("Selecione um funcionario na tabela para editar.");
        }
        validarNome(nome);
        validarDataAdmissao(dataAdmissao);
        validarSalario(salario);

        Funcionario funcionario = new Funcionario(nome.trim(), dataAdmissao, salario, status);
        funcionario.setId(id);
        funcionarioDAO.atualizar(funcionario);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new ValidationException("Selecione um funcionario na tabela para excluir.");
        }
        funcionarioDAO.excluir(id);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioDAO.listarTodos();
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidationException("O campo Nome e obrigatorio.");
        }
        if (nome.trim().length() < 3) {
            throw new ValidationException("O nome deve ter ao menos 3 caracteres.");
        }
    }

    private void validarDataAdmissao(LocalDate dataAdmissao) {
        if (dataAdmissao == null) {
            throw new ValidationException("Informe a data de admissao (formato dd/MM/yyyy).");
        }
        if (dataAdmissao.isAfter(LocalDate.now())) {
            throw new ValidationException("A data de admissao nao pode ser no futuro.");
        }
    }

    private void validarSalario(BigDecimal salario) {
        if (salario == null) {
            throw new ValidationException("Informe o valor do salario.");
        }
        if (salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("O salario deve ser maior que zero.");
        }
    }
}
