-- Tabela de Usuarios (cadastro e login)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(64) NOT NULL,
    data_cadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);

-- Tabela de Funcionarios
CREATE TABLE IF NOT EXISTS funcionarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    data_admissao DATE NOT NULL,
    salario NUMERIC(12, 2) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_funcionarios_nome ON funcionarios(nome);
