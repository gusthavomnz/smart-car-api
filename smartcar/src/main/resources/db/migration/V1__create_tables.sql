-- Tabela Usuario
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    criado_a DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabela Fipe
CREATE TABLE fipe (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INT NOT NULL,
    codigo_fipe VARCHAR(50) NOT NULL
);

-- Tabela Avaliacoes
CREATE TABLE avaliacoes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    fipe_id VARCHAR(50) NOT NULL,
    preco_desejado DECIMAL(10,2),
    preco_fipe DECIMAL(10,2),
    status_resultado VARCHAR(50),
    criado_a DATETIME DEFAULT CURRENT_TIMESTAMP,
    conservacao VARCHAR(50),
    historico_ativo VARCHAR(50),
    notas_pessoais TEXT,

    CONSTRAINT fk_avaliacoes_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE
);