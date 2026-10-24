-- V1: schema inicial da loja (as mesmas tabelas do laboratorio de PostgreSQL)
-- Quem executa este arquivo e o Flyway, na primeira vez que a aplicacao sobe.
-- Padroes: nomes em snake_case, constraints nomeadas, indices nas FKs, comentarios no dicionario.

CREATE TABLE cliente (
    id BIGSERIAL,
    nome TEXT NOT NULL,
    email TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uq_cliente_email UNIQUE (email)
);

COMMENT ON TABLE cliente IS 'Clientes cadastrados na loja.';
COMMENT ON COLUMN cliente.email IS 'E-mail unico usado como contato.';

CREATE TABLE produto (
    id BIGSERIAL,
    nome TEXT NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    estoque INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_produto PRIMARY KEY (id),
    CONSTRAINT ck_produto_preco_positivo CHECK (preco >= 0),
    CONSTRAINT ck_produto_estoque_nao_negativo CHECK (estoque >= 0)
);

COMMENT ON TABLE produto IS 'Produtos disponiveis para venda.';

CREATE TABLE pedido (
    id BIGSERIAL,
    cliente_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_pedido PRIMARY KEY (id),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE RESTRICT,
    CONSTRAINT fk_pedido_produto FOREIGN KEY (produto_id) REFERENCES produto (id) ON DELETE RESTRICT,
    CONSTRAINT ck_pedido_quantidade_positiva CHECK (quantidade > 0)
);

COMMENT ON TABLE pedido IS 'Pedidos feitos por clientes.';

CREATE INDEX idx_pedido_cliente ON pedido (cliente_id);
CREATE INDEX idx_pedido_produto ON pedido (produto_id);
