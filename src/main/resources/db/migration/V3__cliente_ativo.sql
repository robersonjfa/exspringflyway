-- V3: evolucao do schema - cliente passa a ter "ativo"
-- O banco ja tem dados: a coluna entra com DEFAULT e o Flyway aplica SO este script
-- (V1 e V2 nao rodam de novo - quem controla e a tabela flyway_schema_history).

ALTER TABLE cliente ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN cliente.ativo IS 'Indica se o cliente pode fazer pedidos.';
