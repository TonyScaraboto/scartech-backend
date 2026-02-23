# API Routes - ScarTech Backend

## Base URL
```
http://localhost:8080/api
```

---

## 🏢 Faturamento `/faturamento`

### 1. Status do Sistema
```
GET /faturamento/status
```
**Descrição:** Retorna o status geral do sistema de faturamento

**Response (200):**
```json
{
  "status": "operacional",
  "versao": "1.0.0",
  "mensagem": "Sistema de faturamento funcionando corretamente"
}
```

---

### 2. Criar Fatura
```
POST /faturamento
```
**Descrição:** Cria uma nova fatura no sistema

**Request Body:**
```json
{
  "tipo": "ordem",
  "descricao": "Conserto de tela",
  "valor": 150.50,
  "status": "pendente"
}
```

**Response (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "tipo": "ordem",
  "descricao": "Conserto de tela",
  "valor": 150.50,
  "status": "pendente",
  "mes": 2,
  "ano": 2026,
  "dataEmissao": "2026-02-23"
}
```

**Validações:**
- `valor`: Obrigatório, deve ser > 0
- `descricao`: Obrigatória, não pode ser vazia

---

### 3. Listar Faturas
```
GET /faturamento?mes=2&ano=2026
```
**Parâmetros (opcionais):**
- `mes`: Mês das faturas (1-12)
- `ano`: Ano das faturas

**Response (200):** Array de faturas

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "tipo": "ordem",
    "descricao": "Conserto de tela",
    "valor": 150.50,
    "status": "pendente",
    "mes": 2,
    "ano": 2026,
    "dataEmissao": "2026-02-23"
  }
]
```

---

### 4. Obter Fatura
```
GET /faturamento/{id}
```
**Parâmetros:**
- `id`: UUID da fatura

**Response (200):** Objeto Fatura

**Erros:**
- 404: Fatura não encontrada

---

### 5. Atualizar Fatura
```
PUT /faturamento/{id}
```
**Request Body:**
```json
{
  "descricao": "Conserto de tela e bateria",
  "valor": 200.00,
  "status": "pago"
}
```

**Response (200):** Fatura atualizada

**Validações:**
- `valor`: Deve ser > 0

**Erros:**
- 404: Fatura não encontrada

---

### 6. Deletar Fatura
```
DELETE /faturamento/{id}
```
**Response (200):**
```json
{
  "mensagem": "Fatura deletada com sucesso"
}
```

**Erros:**
- 404: Fatura não encontrada

---

### 7. Resumo Mensal
```
GET /faturamento/resumo/mensal?mes=2&ano=2026
```
**Parâmetros (opcionais):**
- `mes`: Mês (padrão: mês atual)
- `ano`: Ano (padrão: ano atual)

**Response (200):**
```json
{
  "mes": 2,
  "ano": 2026,
  "totalPago": 500.00,
  "totalPendente": 300.00,
  "totalCancelado": 0.00,
  "totalGeral": 800.00,
  "quantidade": 5
}
```

---

### 8. Atualizar Status da Fatura
```
PATCH /faturamento/{id}/status?status=pago
```
**Parâmetros:**
- `id`: UUID da fatura
- `status`: `pago`, `pendente` ou `cancelado`

**Response (200):** Fatura com status atualizado

**Validações:**
- `status`: Deve ser um dos valores válidos

---

## 📋 Billing `/billing`

### Status de Faturamento (Legacy)
```
GET /billing/summary
```
**Descrição:** Retorna o resumo de faturamento (compatibilidade com versão anterior)

**Response (200):** Mesmo do `/faturamento/resumo/mensal`

---

## 📦 Pedidos `/orders`

### 1. Status do Serviço
```
GET /orders/status
```
**Response (200):**
```json
{
  "status": "operacional",
  "mensagem": "Serviço de ordens funcionando corretamente"
}
```

---

### 2. Gerar PDF da Ordem
```
POST /orders/generate-pdf
```
**Request Body:**
```json
{
  "nomeCliente": "João Silva",
  "documentoCliente": "123.456.789-00",
  "telefoneCliente": "(11) 98765-4321",
  "modeloAparelho": "iPhone 12",
  "defeitoApresentado": "Tela quebrada",
  "valorConserto": 350.00,
  "termoGarantia": "Garantia de 90 dias"
}
```

**Response (200):**
```json
{
  "mensagem": "PDF gerado com sucesso: OS_20260223_142530.pdf",
  "status": "sucesso"
}
```

**Validações:**
- `nomeCliente`: Obrigatório
- Todos os campos da ordem

**Erros:**
- 400: Dados inválidos
- 500: Erro ao gerar PDF

---

### 3. Validar Ordem
```
POST /orders/validate
```
**Request Body:** Mesmos campos de `/orders/generate-pdf`

**Response (200):**
```json
{
  "valido": true,
  "mensagem": "Ordem validada com sucesso"
}
```

**Response (400):** Erro de validação
```json
{
  "mensagem": "Valor inválido",
  "erro": "Valor não pode ser negativo",
  "status": 400
}
```

---

## 🏷️ Acessórios `/acessorios`

### 1. Catalogar Acessório
```
POST /acessorios/catalogar
```
**Request Body:**
```json
{
  "nome": "Tela iPhone 12",
  "descricao": "Tela OLED original",
  "preco": 200.00,
  "estoque": 10
}
```

**Response (201):**
```json
{
  "mensagem": "Acessório catalogado com sucesso",
  "status": "sucesso",
  "acessorio": { ... }
}
```

**Validações:**
- `nome`: Obrigatório

---

### 2. Catalogar Produto
```
POST /acessorios/catalogar-produto
```
**Request Body:**
```json
{
  "nome": "Bateria iPhone",
  "descricao": "Bateria original 3000mAh",
  "preco": 150.00,
  "estoque": 20
}
```

**Response (201):** Mesma estrutura de `/acessorios/catalogar`

**Validações:**
- `nome`: Obrigatório

---

## 👤 Dados do Usuário `/data`

### 1. Obter Todos os Dados
```
GET /data/{userId}
```
**Response (200):**
```json
{
  "ordens": [...],
  "vendas": [...],
  "produtos": [...]
}
```

---

### 2. Obter Ordens
```
GET /data/{userId}/ordens
```
**Response (200):** Array de ordens

---

### 3. Salvar Ordens
```
POST /data/{userId}/ordens
```
**Request Body:**
```json
[
  { "id": 1, "cliente": "João", "valor": 100.00 },
  { "id": 2, "cliente": "Maria", "valor": 200.00 }
]
```

**Response (200):**
```json
{
  "success": true,
  "message": "Ordens salvas com sucesso",
  "quantidade": 2
}
```

---

### 4. Adicionar Ordem
```
POST /data/{userId}/ordens/add
```
**Request Body:**
```json
{
  "id": 3,
  "cliente": "Pedro",
  "valor": 150.00
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Ordem adicionada"
}
```

---

### 5. Obter Vendas
```
GET /data/{userId}/vendas
```
**Response (200):** Array de vendas

---

### 6. Salvar Vendas
```
POST /data/{userId}/vendas
```
**Request Body:** Array de vendas

---

### 7. Adicionar Venda
```
POST /data/{userId}/vendas/add
```
**Request Body:** Objeto de venda

---

### 8. Obter Produtos
```
GET /data/{userId}/produtos
```
**Response (200):** Array de produtos

---

### 9. Salvar Produtos
```
POST /data/{userId}/produtos
```
**Request Body:** Array de produtos

---

### 10. Adicionar Produto
```
POST /data/{userId}/produtos/add
```
**Request Body:** Objeto de produto

---

### 11. Sincronizar Dados
```
POST /data/{userId}/sync
```
**Request Body:**
```json
{
  "ordens": [...],
  "vendas": [...],
  "produtos": [...]
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Dados sincronizados com sucesso"
}
```

---

## 📊 Status Codes

| Código | Significado |
|--------|-------------|
| 200 | OK - Requisição bem-sucedida |
| 201 | Created - Recurso criado com sucesso |
| 400 | Bad Request - Dados inválidos |
| 404 | Not Found - Recurso não encontrado |
| 500 | Internal Server Error - Erro do servidor |

---

## 🔄 Fluxo Recomendado

### Criar e Processar Uma Fatura

1. **Validar Dados** (opcional)
   ```
   POST /orders/validate
   ```

2. **Gerar PDF** (se for ordem)
   ```
   POST /orders/generate-pdf
   ```

3. **Criar Fatura**
   ```
   POST /faturamento
   ```

4. **Listar Faturas do Mês**
   ```
   GET /faturamento?mes=2&ano=2026
   ```

5. **Obter Resumo**
   ```
   GET /faturamento/resumo/mensal
   ```

6. **Atualizar Status**
   ```
   PATCH /faturamento/{id}/status?status=pago
   ```

---

## 📝 Exemplo de Erro

```json
{
  "mensagem": "Valor inválido",
  "erro": "O valor deve ser maior que zero",
  "status": 400
}
```

---

## 🔐 CORS

Todas as rotas permitem requisições de qualquer origem:
```
Access-Control-Allow-Origin: *
```

---

**Última atualização:** 23/02/2026
