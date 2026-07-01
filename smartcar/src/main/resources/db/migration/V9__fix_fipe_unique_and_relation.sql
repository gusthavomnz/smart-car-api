-- Remove duplicatas mantendo o registro mais recente por (codigo_fipe, codigo_ano) caso existam antes de criar a constraint
DELETE f1 FROM fipe f1
INNER JOIN fipe f2
    ON f1.codigo_fipe = f2.codigo_fipe
    AND f1.codigo_ano = f2.codigo_ano
    AND f1.id < f2.id;

-- Remove a constraint uq_codigo_fipe anterior da tabela fipe
ALTER TABLE fipe DROP INDEX uq_codigo_fipe;

-- Adiciona a nova constraint composta na tabela fipe
ALTER TABLE fipe ADD CONSTRAINT uq_codigo_fipe_ano UNIQUE (codigo_fipe, codigo_ano);

-- Adiciona nova coluna fipe_db_id na tabela avaliacoes para a migração dos dados
ALTER TABLE avaliacoes ADD COLUMN fipe_db_id BIGINT;

-- Atualiza fipe_db_id associando a chave estrangeira (fipe.id) a partir do fipe_id (codigo_fipe) antigo
UPDATE avaliacoes a
JOIN fipe f ON a.fipe_id = f.codigo_fipe
SET a.fipe_db_id = f.id;

-- Limpa quaisquer registros órfãos que não possuem mais Fipe correspondente no banco
DELETE FROM avaliacoes WHERE fipe_db_id IS NULL;

-- Remove a coluna fipe_id antiga (que guardava a String do codigo_fipe)
ALTER TABLE avaliacoes DROP COLUMN fipe_id;

-- Renomeia fipe_db_id para fipe_id
ALTER TABLE avaliacoes RENAME COLUMN fipe_db_id TO fipe_id;

-- Torna fipe_id obrigatória (NOT NULL)
ALTER TABLE avaliacoes MODIFY COLUMN fipe_id BIGINT NOT NULL;

-- Adiciona a constraint de chave estrangeira
ALTER TABLE avaliacoes ADD CONSTRAINT fk_avaliacoes_fipe FOREIGN KEY (fipe_id) REFERENCES fipe(id) ON DELETE CASCADE;
