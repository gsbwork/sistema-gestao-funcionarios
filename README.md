# Sistema de Gestão de Funcionários

Teste prático para a vaga de **Programador Java** — SISAUDCON / SAAM.

Aplicação desktop em **Java Swing**, desenvolvida no **NetBeans**, com persistência em **PostgreSQL** via **JDBC nativo** (SQL puro, sem ORM).

[![Java](https://img.shields.io/badge/Java-17%2B-orange)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791)]()
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36)]()
[![Testes](https://img.shields.io/badge/Testes-48%20passing-brightgreen)]()

---

## 📄 Documentos

- **[Edital do teste (PDF)](Teste%20Pr%C3%A1tico%20Programador%20-%20Java%20Desktop.pdf)**
- **[Documentação técnica completa](DOCUMENTACAO_PROJETO.md)** — arquitetura, banco de dados, segurança, testes, como executar

---

## ✅ Requisitos atendidos

- Tela de Cadastro e Login (nome, e-mail, senha) com validação de campos
- Senha criptografada com **SHA-256** antes de ser armazenada
- Login funcional com dados previamente cadastrados
- Tela de Cadastro de Funcionários (nome, data de admissão, salário, status) em `JTable`
- Conexão via driver JDBC oficial do PostgreSQL, com **SQL nativo** para criação das tabelas (sem Hibernate/JPA)
- Telas construídas no **GUI Builder do NetBeans (Matisse)**
- Tratamento de erros de conexão e consultas, com mensagens amigáveis ao usuário

Detalhamento completo de cada item na [documentação técnica](DOCUMENTACAO_PROJETO.md#1-requisitos-do-teste).

---

## ✨ Recursos adicionais

- **Busca em tempo real** na listagem de funcionários (por nome, data, salário ou status)
- **Edição via ícone de lápis** na linha da tabela, evitando exclusões acidentais por seleção
- **Valores de salário formatados em moeda** (`R$ 3.500,00`) na tabela e no formulário

---

## 🏗️ Arquitetura

Padrão **MVC + DAO**, em `com.saam.gestao`:

```
config/    → conexão JDBC e inicialização do schema
model/     → entidades (Usuario, Funcionario)
dao/       → acesso a dados via SQL nativo (PreparedStatement)
service/   → validações e regras de negócio
exception/ → exceções de domínio e de banco
util/      → hash de senha, política de senha, componentes visuais
view/      → telas Swing (LoginView, CadastroUsuarioView, FuncionarioView)
```

## 🧪 Testes

**48 testes automatizados** (JUnit 5), cobrindo validações, autenticação, hash de senha e o CRUD de funcionários — sem depender do PostgreSQL estar rodando (DAOs falsos em memória).

```bash
mvn test
```

## ▶️ Como executar

1. Ter um PostgreSQL em execução e criar o banco: `CREATE DATABASE saam_gestao;`
2. Ajustar credenciais em `sistema-gestao-funcionarios/src/main/resources/db.properties`, se necessário
3. Abrir `sistema-gestao-funcionarios/` no NetBeans (reconhecido como projeto Maven) e rodar `Main.java`

O schema do banco é criado automaticamente na primeira execução.

---

**Guilherme Sousa Barbosa**
