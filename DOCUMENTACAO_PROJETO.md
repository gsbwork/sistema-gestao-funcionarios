# Sistema de Gestão de Funcionários
## Teste Prático — Programador Java (SISAUDCON / SAAM)

Aplicação desktop em **Java Swing**, desenvolvida no **NetBeans**, com persistência em **PostgreSQL** via **JDBC nativo** (SQL puro, sem ORM).

---

## 1. Requisitos do Teste

| Requisito | Status |
|---|---|
| Tela de Cadastro (nome, e-mail, senha) | ✅ |
| Tela de Login (e-mail, senha) | ✅ |
| Validação dos campos de entrada | ✅ |
| Senha criptografada com SHA-256 antes de salvar | ✅ |
| Login funcional com dados previamente cadastrados | ✅ |
| Tela de Cadastro de Funcionários (nome, data de admissão, salário, status) | ✅ |
| Persistência dos funcionários no PostgreSQL | ✅ |
| Listagem dos funcionários em `JTable` | ✅ |
| Conexão via driver JDBC oficial do PostgreSQL | ✅ |
| Criação das tabelas via SQL nativo (sem Hibernate/JPA) | ✅ |
| Desenvolvimento no NetBeans, com uso do GUI Builder (Matisse) | ✅ |
| Tratamento de erros de conexão e consultas malsucedidas | ✅ |
| Mensagens de erro adequadas ao usuário | ✅ |

---

## 2. Arquitetura

O projeto segue o padrão **MVC + DAO**, separando claramente interface, regras de negócio e acesso a dados:

```
com.saam.gestao
 ├── config/    → ConnectionFactory (conexão JDBC) e DatabaseInitializer (executa o DDL)
 ├── model/     → Usuario, Funcionario (entidades de domínio)
 ├── dao/       → UsuarioDAO, FuncionarioDAO (acesso a dados via SQL nativo)
 ├── service/   → AuthService, FuncionarioService (validações e regras de negócio)
 ├── exception/ → DatabaseException, ValidationException
 ├── util/      → HashUtil, PasswordPolicy, BordaArredondada, BotaoArredondado, PlaceholderTextField
 └── view/      → LoginView, CadastroUsuarioView, FuncionarioView (telas Swing)
```

**Por que essa separação:** as camadas `model`, `dao`, `service`, `exception` e `util` são Java puro, sem qualquer dependência de interface gráfica — por isso puderam ser testadas de forma automatizada (DAOs falsos em memória), sem precisar do PostgreSQL nem do NetBeans rodando. Só a camada `view` depende do editor visual.

---

## 3. Banco de Dados

### Script SQL nativo (`src/main/resources/schema.sql`)

```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(64) NOT NULL,
    data_cadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);

CREATE TABLE IF NOT EXISTS funcionarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    data_admissao DATE NOT NULL,
    salario NUMERIC(12, 2) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_funcionarios_nome ON funcionarios(nome);
```

### Inicialização automática

A classe `DatabaseInitializer` lê esse script e executa cada comando via `Statement.execute()` sempre que a aplicação inicia (chamada em `Main.java`, antes de abrir a tela de Login). Como o script usa `CREATE TABLE IF NOT EXISTS`, é seguro rodar em toda inicialização: na primeira vez cria as tabelas, nas seguintes apenas confirma que já existem.

### Conexão

`ConnectionFactory` lê host, porta, nome do banco, usuário e senha de `src/main/resources/db.properties` e abre conexões via `DriverManager` (driver oficial `org.postgresql:postgresql`). Falhas de conexão são convertidas em `DatabaseException` com mensagem amigável, nunca em stack trace cru.

**Configuração padrão:** `localhost:5432`, banco `saam_gestao`, usuário `postgres`.

---

## 4. Segurança e Regras de Negócio

- **Senha nunca armazenada em texto puro.** `HashUtil` gera o hash SHA-256 (64 caracteres hexadecimais) antes de qualquer gravação; a comparação no login (`HashUtil.matches`) também é feita por hash.
- **Política de senha** (`util.PasswordPolicy`): mínimo de 6 caracteres, com ao menos 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial. A tela de Cadastro mostra um checklist visual que fica verde conforme cada regra é atendida enquanto o usuário digita.
- **Proteção contra SQL Injection:** toda consulta em `UsuarioDAO` e `FuncionarioDAO` usa `PreparedStatement` com parâmetros bindados (`?`) — nenhuma concatenação de string em SQL.
- **Duplicidade de e-mail:** verificada antes do cadastro (`existePorEmail`, comparação case-insensitive) e reforçada no próprio banco com `UNIQUE` na coluna `email`.
- **Validações de negócio** (`AuthService` e `FuncionarioService`): nome obrigatório (mín. 3 caracteres), e-mail em formato válido, data de admissão não pode ser futura, salário deve ser maior que zero.
- **Gestão de conexões:** toda conexão é aberta em bloco `try-with-resources`, garantindo fechamento mesmo em caso de erro (sem vazamento de conexão).

---

## 5. Interface (Telas)

As três telas foram construídas no **GUI Builder do NetBeans (Matisse)**, com o arquivo `.form` de cada uma sincronizado com o código gerado (`initComponents()`), de forma que a aba Design do NetBeans reflete exatamente o que é executado.

- **`LoginView`** — campos de e-mail e senha, botão de mostrar/ocultar senha, atalho de Enter para autenticar, link para a tela de cadastro.
- **`CadastroUsuarioView`** — campos de nome, e-mail, senha e confirmação, checklist de complexidade de senha em tempo real, botão de mostrar/ocultar senha nos dois campos.
- **`FuncionarioView`** — formulário de cadastro (nome, data de admissão, salário, status) acima de uma `JTable` com os registros. Campo de busca filtra a tabela em tempo real (por nome, data, salário ou status), com um contador de total de funcionários cadastrados ao lado. Editar um registro é feito clicando no ícone de lápis da linha (não basta selecionar a linha); o botão de excluir, em destaque à direita, aparece somente após esse clique e pede confirmação antes de excluir. Os valores de salário são exibidos formatados em moeda (`R$ 3.500,00`) tanto na tabela quanto no rótulo do campo de formulário. Botão de sair pede confirmação antes de retornar à tela de Login (sem encerrar a aplicação).

O layout segue uma paleta de cores clara inspirada na identidade visual da SAAM/SISAUDCON (fundo lilás claro, cartões brancos, azul-arroxeado como cor primária), com componentes que reforçam a leitura da interface por proximidade e similaridade (campos relacionados agrupados, botões de mesmo tipo com o mesmo estilo, ação destrutiva — excluir — sinalizada em vermelho).

---

## 6. Tratamento de Erros

Toda operação de banco é envolvida em `try/catch`, distinguindo dois tipos de falha:

- **`ValidationException`** — dado inválido informado pelo usuário (campo vazio, formato incorreto, regra de negócio violada). Mostrada como aviso (`JOptionPane.WARNING_MESSAGE`).
- **`DatabaseException`** — falha de conexão ou erro de SQL. Mostrada como erro (`JOptionPane.ERROR_MESSAGE`), sempre com mensagem legível, nunca com stack trace na tela.

Cenários testados manualmente: login com senha incorreta, cadastro com e-mail duplicado, banco de dados inacessível (simulado apontando para uma porta inexistente) — em todos os casos a aplicação respondeu com mensagem clara, sem travar.

---

## 7. Testes Automatizados

**48 testes**, em 4 classes, cobrindo a camada de regras de negócio com DAOs falsos em memória (não dependem do PostgreSQL estar rodando):

| Classe de teste | O que cobre |
|---|---|
| `HashUtilTest` | Geração determinística do hash SHA-256, formato de 64 caracteres hexadecimais |
| `PasswordPolicyTest` | Cada regra de complexidade de senha isoladamente, incluindo valor de fronteira (6 vs. 5 caracteres) |
| `AuthServiceTest` | Cadastro e login: validação de nome/e-mail/senha, e-mail duplicado (case-insensitive), autenticação correta e incorreta, campos nulos/vazios |
| `FuncionarioServiceTest` | Cadastro, edição e exclusão de funcionário: validações de nome/data/salário, valores de fronteira (data de hoje permitida, salário mínimo positivo), caminho completo de editar e excluir um registro existente |

Para rodar: `mvn test` (ou botão direito no projeto → Test, dentro do NetBeans).

---

## 8. Ambiente e Como Executar

**Pré-requisitos:** JDK 17+, PostgreSQL 17 (ou compatível) em execução, NetBeans (Maven embutido) ou Maven na linha de comando.

1. Criar o banco: `CREATE DATABASE saam_gestao;`
2. Ajustar credenciais em `src/main/resources/db.properties`, se necessário.
3. Abrir o projeto no NetBeans (`File > Open Project`, pasta `sistema-gestao-funcionarios`) — reconhecido automaticamente como projeto Maven.
4. Rodar `Main.java` (Shift+F6). O schema é criado automaticamente na primeira execução.

---

## 9. Estrutura de Pastas

```
sistema-gestao-funcionarios/
 ├── pom.xml
 ├── src/main/java/com/saam/gestao/     → código-fonte (config, model, dao, service, exception, util, view)
 ├── src/main/resources/                 → schema.sql, db.properties, ícones (icons/)
 └── src/test/java/com/saam/gestao/      → testes automatizados (JUnit 5)
```
