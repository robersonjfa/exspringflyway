-- V4: pedido ganha um campo de observacao (exemplo de evolucao sem perder dados)

ALTER TABLE pedido ADD COLUMN observacao VARCHAR(200);

COMMENT ON COLUMN pedido.observacao IS 'Texto livre do cliente sobre o pedido.';
