package com.saam.gestao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Funcionario: representa um registro cadastrado na tela de Funcionarios.
 */
public class Funcionario {

    private Long id;
    private String nome;
    private LocalDate dataAdmissao;
    private BigDecimal salario;
    private boolean status;
    private LocalDateTime dataCadastro;

    public Funcionario() {
    }

    public Funcionario(String nome, LocalDate dataAdmissao, BigDecimal salario, boolean status) {
        this.nome = nome;
        this.dataAdmissao = dataAdmissao;
        this.salario = salario;
        this.status = status;
    }

    public Funcionario(Long id, String nome, LocalDate dataAdmissao, BigDecimal salario,
                        boolean status, LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.dataAdmissao = dataAdmissao;
        this.salario = salario;
        this.status = status;
        this.dataCadastro = dataCadastro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
