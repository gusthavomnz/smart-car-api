ALTER TABLE fipe
ADD COLUMN preco          DECIMAL(10,2) NOT NULL,
ADD COLUMN combustivel    VARCHAR(30)   NOT NULL,
ADD COLUMN referencia_mes VARCHAR(30)   NOT NULL,
ADD COLUMN atualizado_a   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
                          ON UPDATE CURRENT_TIMESTAMP,
ADD CONSTRAINT uq_fipe UNIQUE (codigo_fipe, ano, combustivel);