-- V2: dados de exemplo (tambem versionados: todo mundo que rodar o projeto ve os mesmos dados)

INSERT INTO cliente (nome, email) VALUES
    ('Ana Souza', 'ana@exemplo.com'),
    ('Bruno Lima', 'bruno@exemplo.com'),
    ('Carla Dias', 'carla@exemplo.com');

INSERT INTO produto (nome, preco, estoque) VALUES
    ('Teclado mecânico', 350.00, 20),
    ('Mouse sem fio', 120.00, 50),
    ('Monitor 27"', 1200.00, 10);

INSERT INTO pedido (cliente_id, produto_id, quantidade) VALUES
    (1, 1, 1),
    (2, 3, 1),
    (3, 2, 2);
