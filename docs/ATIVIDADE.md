# Atividade prática — Evoluindo a loja com ORM + Flyway

**Disciplina:** Banco de Dados II — Integração BD/RAD
**Entrega:** repositório do aluno + dois prints (histórico do Flyway e tela funcionando)

A loja precisa registrar a **marca** de cada produto (ex.: Logitech, Dell, Multilaser).
Hoje a tabela `produto` só tem `nome`, `preco` e `estoque`.

O trabalho é levar essa informação nova do **banco** até a **tela** — do jeito certo: com uma
migração, sem apagar o banco e sem perder os dados que já existem.

> Regra da atividade: **toda mudança de banco é um arquivo novo em `db/migration`.**
> Nunca altere um `V1`, `V2`… que já rodou.

---

## Passo 1 — Criar a migração

Crie o arquivo `src/main/resources/db/migration/V5__produto_marca.sql`:

```sql
ALTER TABLE produto ADD COLUMN marca VARCHAR(60);

COMMENT ON COLUMN produto.marca IS 'Fabricante do produto.';
```

Pare e suba a aplicação (`Run As → Java Application`). No console deve aparecer:

```
Migrating schema "public" to version "5 - produto marca"
Successfully applied 1 migration to schema "public", now at version v5
```

**Conferir no banco** (pgAdmin ou psql):

```sql
select version, description, success from flyway_schema_history order by installed_rank;
select column_name, data_type from information_schema.columns
 where table_name = 'produto' order by ordinal_position;
```

✔️ A coluna `marca` existe e a `V5` aparece no histórico.

---

## Passo 2 — Ajustar a entidade (ORM)

Em `domain/Produto.java`, acrescente o atributo e o par getter/setter, como os outros:

```java
@Column(length = 60)
private String marca;

public String getMarca() { return marca; }
public void setMarca(String marca) { this.marca = marca; }
```

> Se você esquecer este passo e reiniciar, o `validate` do Hibernate **não reclama** (a coluna
> existe no banco). O problema aparece no passo seguinte: a marca nunca é gravada.

---

## Passo 3 — Levar a marca na API

**`web/dto/ProdutoRequest.java`** — aceite a marca na entrada:

```java
@Size(max = 60, message = "marca deve ter no máximo 60 caracteres")
String marca
```

**`web/dto/ProdutoResponse.java`** — devolva a marca na saída:

```java
public record ProdutoResponse(Long id, String nome, BigDecimal preco, Integer estoque, String marca) {
    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getPreco(), p.getEstoque(), p.getMarca());
    }
}
```

**`service/ProdutoService.java`** — grave e atualize o campo nos métodos `criar` e `atualizar`:

```java
produto.setMarca(req.marca());
```

**Teste a API** (Insomnia, Postman ou curl):

```bash
curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teclado mecânico","preco":350.00,"estoque":20,"marca":"Logitech"}'
```

✔️ O `GET /api/produtos` deve devolver `"marca":"Logitech"`.

---

## Passo 4 — Mostrar na tela

**`static/index.html`** — na tabela de produtos, acrescente a coluna:

```html
<th>Marca</th>
```

e, no formulário, o campo:

```html
<div class="campo">
  <label for="produto-marca">Marca</label>
  <input id="produto-marca" type="text" maxlength="60" placeholder="opcional">
</div>
```

**`static/app.js`** — no `renderProdutos`, inclua a célula:

```js
td(p.marca || '—'),
```

e, ao montar o corpo do POST/PUT, envie o valor:

```js
marca: document.getElementById('produto-marca').value.trim() || null,
```

Não esqueça de limpar o campo ao editar/cancelar, como já é feito com os outros.

✔️ Na tela: cadastrar um produto com marca, editar a marca e ver a coluna atualizada.

---

## Passo 5 (desafio) — Categoria com valor padrão

A loja agora quer classificar os produtos por **categoria** (ex.: Periféricos, Monitores).
Crie `V6__produto_categoria.sql` de forma que os produtos que **já existem** fiquem com
`'Geral'` e nunca fiquem sem categoria:

```sql
ALTER TABLE produto ADD COLUMN categoria VARCHAR(40) NOT NULL DEFAULT 'Geral';
```

Responda na entrega: **por que o `DEFAULT` é obrigatório aqui?**
(e o que aconteceria com `NOT NULL` sozinho, com a tabela já populada)

Depois, faça a categoria aparecer na API e na tela, como você fez com a marca.

---

## Desafio extra (para quem terminar antes)

1. Apague o arquivo `V5__produto_marca.sql` **depois** de já ter rodado e suba a aplicação.
   Anote a mensagem de erro do Flyway e explique, com suas palavras, o que ela está protegendo.
2. Volte o arquivo e confirme que a aplicação sobe de novo.

---

<div class="quebra"></div>

## Entrega

1. Link do seu repositório no GitHub (com os commits das migrações).
2. Print do `select * from flyway_schema_history` mostrando até a `V5` (ou `V6`).
3. Print da tela com um produto cadastrado **com marca** (e categoria, se fez o desafio).

**Critérios (10 pontos)**

| Item | Pontos |
|---|---|
| Migração `V5` correta, aplicada e visível no histórico | 3,0 |
| Entidade, DTO e API devolvendo a marca | 3,0 |
| Tela exibindo e permitindo editar a marca | 2,0 |
| Desafio da `V6` (categoria com `DEFAULT`) explicado | 2,0 |

---

## Erros comuns (e o que significam)

| Sintoma | Causa provável | Como resolver |
|---|---|---|
| App não sobe: `Schema-validation: missing column [marca]` | criou a entidade, mas não criou a migração | criar a `V5` e reiniciar |
| `Validate failed: migration checksum mismatch for version 5` | alterou um arquivo de migração que já tinha rodado | desfazer a alteração ou criar uma nova migração |
| `Detected applied migration not resolved locally: 5` | apagou o arquivo `V5` do projeto | devolver o arquivo (não apagar migração aplicada) |
| A marca não aparece no `GET` | faltou incluir o campo no `ProdutoResponse` | ajustar o DTO |
| A marca não é gravada | faltou `produto.setMarca(req.marca())` no service | ajustar o service |
| A migração não roda | nome do arquivo errado (ex.: `V5-produto.sql`) | usar `V5__produto_marca.sql` (dois underscores) |
| Funciona sem migração | alguém trocou para `ddl-auto: update` | voltar para `validate` — o schema é do Flyway |
