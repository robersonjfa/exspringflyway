/* Front-end simples (HTML + CSS + JS puro) consumindo a API REST do Spring.
   Nada de framework: só fetch, createElement e textContent. */

const API = '/api';
const $ = (id) => document.getElementById(id);

const dinheiro = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const dataHora = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

/* ---------- utilidades ---------- */

function el(tag, props = {}, filhos = []) {
  const node = document.createElement(tag);
  for (const [chave, valor] of Object.entries(props)) {
    if (chave === 'class') node.className = valor;
    else if (chave === 'text') node.textContent = valor;
    else if (chave.startsWith('on')) node.addEventListener(chave.slice(2), valor);
    else node.setAttribute(chave, valor);
  }
  for (const filho of [].concat(filhos)) {
    if (filho != null) node.append(filho.nodeType ? filho : document.createTextNode(filho));
  }
  return node;
}

let toastTimer;
function toast(mensagem, tipo = 'ok') {
  const caixa = $('toast');
  caixa.textContent = mensagem;
  caixa.className = `toast ${tipo}`;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => caixa.classList.add('escondido'), 4200);
}

async function chamar(metodo, caminho, corpo) {
  const opcoes = { method: metodo, headers: {} };
  if (corpo !== undefined) {
    opcoes.headers['Content-Type'] = 'application/json';
    opcoes.body = JSON.stringify(corpo);
  }
  const resposta = await fetch(API + caminho, opcoes);
  if (resposta.status === 204) return null;

  const texto = await resposta.text();
  const dados = texto ? JSON.parse(texto) : null;

  if (!resposta.ok) {
    const campos = dados?.campos ? Object.values(dados.campos).join(' · ') : '';
    throw new Error(campos || dados?.detail || `Erro ${resposta.status}`);
  }
  return dados;
}

function validade(booleano, textoSim, textoNao) {
  const span = el('span', { class: `badge ${booleano ? 'on' : 'off'}`, text: booleano ? textoSim : textoNao });
  return span;
}

/* ---------- abas ---------- */

document.querySelectorAll('.aba').forEach((botao) => {
  botao.addEventListener('click', () => {
    document.querySelectorAll('.aba').forEach((b) => b.classList.remove('ativa'));
    document.querySelectorAll('.tela').forEach((t) => t.classList.remove('ativa'));
    botao.classList.add('ativa');
    $(`tela-${botao.dataset.tela}`).classList.add('ativa');
  });
});

/* ---------- clientes ---------- */

async function carregarClientes() {
  const clientes = await chamar('GET', '/clientes');
  const corpo = $('tabela-clientes');
  corpo.replaceChildren();

  if (clientes.length === 0) {
    corpo.append(el('tr', {}, [el('td', { colspan: '6', text: 'Nenhum cliente cadastrado.' })]));
    return;
  }

  for (const c of clientes) {
    corpo.append(el('tr', {}, [
      el('td', { text: c.id }),
      el('td', { text: c.nome }),
      el('td', { text: c.email }),
      el('td', {}, [validade(c.ativo, 'ativo', 'inativo')]),
      el('td', { text: c.criadoEm ? dataHora.format(new Date(c.criadoEm)) : '—' }),
      el('td', {}, [
        el('button', { class: 'link-acao', text: 'editar', onclick: () => editarCliente(c) }),
        el('button', { class: 'link-acao perigo', text: 'excluir', onclick: () => excluirCliente(c) })
      ])
    ]));
  }
}

function editarCliente(c) {
  $('cliente-id').value = c.id;
  $('cliente-nome').value = c.nome;
  $('cliente-email').value = c.email;
  $('cliente-ativo').checked = c.ativo;
  $('titulo-form-cliente').textContent = `Editando cliente #${c.id}`;
  $('cancelar-cliente').classList.remove('escondido');
  $('cliente-nome').focus();
}

function limparFormularioCliente() {
  $('form-cliente').reset();
  $('cliente-id').value = '';
  $('cliente-ativo').checked = true;
  $('titulo-form-cliente').textContent = 'Novo cliente';
  $('cancelar-cliente').classList.add('escondido');
}

$('form-cliente').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const id = $('cliente-id').value;
  const corpo = {
    nome: $('cliente-nome').value.trim(),
    email: $('cliente-email').value.trim(),
    ativo: $('cliente-ativo').checked
  };
  try {
    if (id) {
      await chamar('PUT', `/clientes/${id}`, corpo);
      toast('Cliente atualizado.');
    } else {
      await chamar('POST', '/clientes', corpo);
      toast('Cliente cadastrado.');
    }
    limparFormularioCliente();
    await carregarClientes();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
});

$('cancelar-cliente').addEventListener('click', limparFormularioCliente);

async function excluirCliente(c) {
  if (!confirm(`Excluir o cliente "${c.nome}"?`)) return;
  try {
    await chamar('DELETE', `/clientes/${c.id}`);
    toast('Cliente excluído.');
    await carregarClientes();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

/* ---------- produtos ---------- */

async function carregarProdutos() {
  const produtos = await chamar('GET', '/produtos');
  const corpo = $('tabela-produtos');
  corpo.replaceChildren();

  if (produtos.length === 0) {
    corpo.append(el('tr', {}, [el('td', { colspan: '5', text: 'Nenhum produto cadastrado.' })]));
    return;
  }

  for (const p of produtos) {
    const estoque = p.estoque === 0
      ? el('span', { class: 'badge baixo', text: 'sem estoque' })
      : String(p.estoque);
    corpo.append(el('tr', {}, [
      el('td', { text: p.id }),
      el('td', { text: p.nome }),
      el('td', { class: 'num', text: dinheiro.format(p.preco) }),
      el('td', {}, [estoque]),
      el('td', {}, [
        el('button', { class: 'link-acao', text: 'editar', onclick: () => editarProduto(p) }),
        el('button', { class: 'link-acao perigo', text: 'excluir', onclick: () => excluirProduto(p) })
      ])
    ]));
  }
}

function editarProduto(p) {
  $('produto-id').value = p.id;
  $('produto-nome').value = p.nome;
  $('produto-preco').value = p.preco;
  $('produto-estoque').value = p.estoque;
  $('titulo-form-produto').textContent = `Editando produto #${p.id}`;
  $('cancelar-produto').classList.remove('escondido');
  $('produto-nome').focus();
}

function limparFormularioProduto() {
  $('form-produto').reset();
  $('produto-id').value = '';
  $('titulo-form-produto').textContent = 'Novo produto';
  $('cancelar-produto').classList.add('escondido');
}

$('form-produto').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const id = $('produto-id').value;
  const corpo = {
    nome: $('produto-nome').value.trim(),
    preco: Number($('produto-preco').value),
    estoque: Number($('produto-estoque').value)
  };
  try {
    if (id) {
      await chamar('PUT', `/produtos/${id}`, corpo);
      toast('Produto atualizado.');
    } else {
      await chamar('POST', '/produtos', corpo);
      toast('Produto cadastrado.');
    }
    limparFormularioProduto();
    await carregarProdutos();
    await preencherSelectsPedido();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
});

$('cancelar-produto').addEventListener('click', limparFormularioProduto);

async function excluirProduto(p) {
  if (!confirm(`Excluir o produto "${p.nome}"?`)) return;
  try {
    await chamar('DELETE', `/produtos/${p.id}`);
    toast('Produto excluído.');
    await carregarProdutos();
    await preencherSelectsPedido();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

/* ---------- pedidos ---------- */

async function preencherSelectsPedido() {
  const [clientes, produtos] = await Promise.all([
    chamar('GET', '/clientes'),
    chamar('GET', '/produtos')
  ]);

  const selectCliente = $('pedido-cliente');
  const selectProduto = $('pedido-produto');
  const clienteAtual = selectCliente.value;
  const produtoAtual = selectProduto.value;

  selectCliente.replaceChildren(el('option', { value: '', text: 'selecione...' }));
  for (const c of clientes) {
    selectCliente.append(el('option', { value: c.id, text: `${c.nome}${c.ativo ? '' : ' (inativo)'}` }));
  }

  selectProduto.replaceChildren(el('option', { value: '', text: 'selecione...' }));
  for (const p of produtos) {
    selectProduto.append(el('option', {
      value: p.id,
      text: `${p.nome} — ${dinheiro.format(p.preco)} (${p.estoque} em estoque)`
    }));
  }

  if (clienteAtual) selectCliente.value = clienteAtual;
  if (produtoAtual) selectProduto.value = produtoAtual;
}

async function carregarPedidos() {
  const [pedidos, resumo] = await Promise.all([
    chamar('GET', '/pedidos'),
    chamar('GET', '/pedidos/resumo')
  ]);

  const corpo = $('tabela-pedidos');
  corpo.replaceChildren();
  if (pedidos.length === 0) {
    corpo.append(el('tr', {}, [el('td', { colspan: '8', text: 'Nenhum pedido registrado.' })]));
  }
  for (const p of pedidos) {
    corpo.append(el('tr', {}, [
      el('td', { text: p.id }),
      el('td', { text: p.clienteNome }),
      el('td', { text: p.produtoNome }),
      el('td', { class: 'num', text: p.quantidade }),
      el('td', { class: 'num', text: dinheiro.format(p.total) }),
      el('td', { text: p.observacao || '—' }),
      el('td', { text: p.criadoEm ? dataHora.format(new Date(p.criadoEm)) : '—' }),
      el('td', {}, [
        el('button', { class: 'link-acao', text: 'editar', onclick: () => editarPedido(p) }),
        el('button', { class: 'link-acao perigo', text: 'excluir', onclick: () => excluirPedido(p) })
      ])
    ]));
  }

  const corpoResumo = $('tabela-resumo');
  corpoResumo.replaceChildren();
  for (const r of resumo) {
    corpoResumo.append(el('tr', {}, [
      el('td', { text: r.cliente }),
      el('td', { class: 'num', text: r.pedidos }),
      el('td', { class: 'num', text: r.itens }),
      el('td', { class: 'num', text: dinheiro.format(r.total) })
    ]));
  }
}

function editarPedido(p) {
  $('pedido-id').value = p.id;
  $('pedido-cliente').value = p.clienteId;
  $('pedido-produto').value = p.produtoId;
  $('pedido-quantidade').value = p.quantidade;
  $('pedido-observacao').value = p.observacao || '';
  $('titulo-form-pedido').textContent = `Editando pedido #${p.id}`;
  $('cancelar-pedido').classList.remove('escondido');
  $('pedido-cliente').focus();
}

function limparFormularioPedido() {
  $('form-pedido').reset();
  $('pedido-id').value = '';
  $('pedido-quantidade').value = 1;
  $('titulo-form-pedido').textContent = 'Novo pedido';
  $('cancelar-pedido').classList.add('escondido');
}

$('form-pedido').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const id = $('pedido-id').value;
  const corpo = {
    clienteId: Number($('pedido-cliente').value),
    produtoId: Number($('pedido-produto').value),
    quantidade: Number($('pedido-quantidade').value),
    observacao: $('pedido-observacao').value.trim() || null
  };
  try {
    if (id) {
      await chamar('PUT', `/pedidos/${id}`, corpo);
      toast('Pedido atualizado.');
    } else {
      await chamar('POST', '/pedidos', corpo);
      toast('Pedido registrado.');
    }
    limparFormularioPedido();
    await carregarPedidos();
    await carregarProdutos();
    await preencherSelectsPedido();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
});

$('cancelar-pedido').addEventListener('click', limparFormularioPedido);

async function excluirPedido(p) {
  if (!confirm(`Excluir o pedido #${p.id}? O estoque será devolvido.`)) return;
  try {
    await chamar('DELETE', `/pedidos/${p.id}`);
    toast('Pedido excluído e estoque devolvido.');
    await carregarPedidos();
    await carregarProdutos();
    await preencherSelectsPedido();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

/* ---------- início ---------- */

(async function iniciar() {
  try {
    await Promise.all([carregarClientes(), carregarProdutos(), carregarPedidos()]);
    await preencherSelectsPedido();
  } catch (erro) {
    toast(`Não foi possível carregar os dados: ${erro.message}`, 'erro');
  }
})();
