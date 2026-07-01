-- Remove duplicatas mantendo o registro mais recente por codigo_fipe
DELETE f1 FROM fipe f1
INNER JOIN fipe f2
    ON f1.codigo_fipe = f2.codigo_fipe
    AND f1.id < f2.id;

-- Troca a constraint composta por unique somente em codigo_fipe
ALTER TABLE fipe DROP INDEX uq_fipe;
ALTER TABLE fipe ADD CONSTRAINT uq_codigo_fipe UNIQUE (codigo_fipe);
