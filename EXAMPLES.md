# 🧪 Exemplos de Requisições - cURL

## 📌 Base URL
```
http://localhost:8080/api
```

---

## 🏢 FATURAMENTO

### 1. Verificar Status do Sistema
```bash
curl -X GET http://localhost:8080/api/faturamento/status
```

### 2. Criar uma Fatura
```bash
curl -X POST http://localhost:8080/api/faturamento \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "ordem",
    "descricao": "Conserto de tela iPhone 12",
    "valor": 350.00,
    "status": "pendente"
  }'
```

### 3. Listar Faturas do Mês Atual
```bash
curl -X GET "http://localhost:8080/api/faturamento"
```

### 4. Listar Faturas de um Mês Específico
```bash
curl -X GET "http://localhost:8080/api/faturamento?mes=2&ano=2026"
```

### 5. Obter Detalhes de uma Fatura
```bash
curl -X GET "http://localhost:8080/api/faturamento/{id}"
# Exemplo:
curl -X GET "http://localhost:8080/api/faturamento/550e8400-e29b-41d4-a716-446655440000"
```

### 6. Atualizar uma Fatura
```bash
curl -X PUT "http://localhost:8080/api/faturamento/{id}" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Conserto de tela e bateria",
    "valor": 450.00,
    "status": "pago"
  }'
```

### 7. Deletar uma Fatura
```bash
curl -X DELETE "http://localhost:8080/api/faturamento/{id}"
```

### 8. Obter Resumo Mensal
```bash
curl -X GET "http://localhost:8080/api/faturamento/resumo/mensal"
```

### 9. Obter Resumo de um Mês Específico
```bash
curl -X GET "http://localhost:8080/api/faturamento/resumo/mensal?mes=2&ano=2026"
```

### 10. Atualizar Status de uma Fatura
```bash
curl -X PATCH "http://localhost:8080/api/faturamento/{id}/status?status=pago"
```

---

## 💰 BILLING (Legacy)

### Status de Faturamento
```bash
curl -X GET http://localhost:8080/api/billing/summary
```

---

## 📦 PEDIDOS

### 1. Status do Serviço de Ordens
```bash
curl -X GET http://localhost:8080/api/orders/status
```

### 2. Validar Dados de Ordem
```bash
curl -X POST http://localhost:8080/api/orders/validate \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCliente": "João Silva",
    "documentoCliente": "123.456.789-00",
    "telefoneCliente": "(11) 98765-4321",
    "modeloAparelho": "iPhone 12",
    "defeitoApresentado": "Tela quebrada",
    "valorConserto": 350.00,
    "termoGarantia": "Garantia de 90 dias"
  }'
```

### 3. Gerar PDF da Ordem
```bash
curl -X POST http://localhost:8080/api/orders/generate-pdf \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCliente": "João Silva",
    "documentoCliente": "123.456.789-00",
    "telefoneCliente": "(11) 98765-4321",
    "modeloAparelho": "iPhone 12",
    "defeitoApresentado": "Tela quebrada",
    "valorConserto": 350.00,
    "termoGarantia": "Garantia de 90 dias"
  }'
```

---

## 🏷️ ACESSÓRIOS

### 1. Catalogar Acessório
```bash
curl -X POST http://localhost:8080/api/acessorios/catalogar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Tela iPhone 12",
    "descricao": "Tela OLED original, 6.1 polegadas",
    "preco": 200.00,
    "estoque": 10
  }'
```

### 2. Catalogar Produto
```bash
curl -X POST http://localhost:8080/api/acessorios/catalogar-produto \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Bateria iPhone 12",
    "descricao": "Bateria original 3000mAh",
    "preco": 150.00,
    "estoque": 20
  }'
```

---

## 👤 DADOS DO USUÁRIO

### 1. Obter Todos os Dados
```bash
curl -X GET "http://localhost:8080/api/data/user123"
```

### 2. Listar Ordens do Usuário
```bash
curl -X GET "http://localhost:8080/api/data/user123/ordens"
```

### 3. Salvar Ordens (Substituir todas)
```bash
curl -X POST "http://localhost:8080/api/data/user123/ordens" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": 1,
      "cliente": "João",
      "valor": 100.00,
      "status": "concluída"
    },
    {
      "id": 2,
      "cliente": "Maria",
      "valor": 200.00,
      "status": "pendente"
    }
  ]'
```

### 4. Adicionar uma Ordem
```bash
curl -X POST "http://localhost:8080/api/data/user123/ordens/add" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 3,
    "cliente": "Pedro",
    "valor": 150.00,
    "status": "emprocesso"
  }'
```

### 5. Listar Vendas do Usuário
```bash
curl -X GET "http://localhost:8080/api/data/user123/vendas"
```

### 6. Adicionar uma Venda
```bash
curl -X POST "http://localhost:8080/api/data/user123/vendas/add" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "produto": "Tela iPhone",
    "quantidade": 2,
    "valor": 400.00
  }'
```

### 7. Listar Produtos
```bash
curl -X GET "http://localhost:8080/api/data/user123/produtos"
```

### 8. Adicionar Produto
```bash
curl -X POST "http://localhost:8080/api/data/user123/produtos/add" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "nome": "iPhone 12",
    "descricao": "Smartphone última geração",
    "preco": 1200.00
  }'
```

### 9. Sincronizar Todos os Dados
```bash
curl -X POST "http://localhost:8080/api/data/user123/sync" \
  -H "Content-Type: application/json" \
  -d '{
    "ordens": [
      { "id": 1, "cliente": "João", "valor": 100.00 }
    ],
    "vendas": [
      { "id": 1, "produto": "Tela", "valor": 200.00 }
    ],
    "produtos": [
      { "id": 1, "nome": "Bateria", "preco": 50.00 }
    ]
  }'
```

---

## 🐍 Exemplos em Python

### Criar Fatura com Python
```python
import requests
import json

url = "http://localhost:8080/api/faturamento"
headers = {"Content-Type": "application/json"}

data = {
    "tipo": "ordem",
    "descricao": "Conserto de bateria",
    "valor": 120.50,
    "status": "pendente"
}

response = requests.post(url, json=data, headers=headers)
print(response.json())
```

### Listar Faturas com Python
```python
import requests

url = "http://localhost:8080/api/faturamento"
response = requests.get(url)
faturas = response.json()

for fatura in faturas:
    print(f"ID: {fatura['id']}")
    print(f"Descrição: {fatura['descricao']}")
    print(f"Valor: R$ {fatura['valor']}")
    print(f"Status: {fatura['status']}")
    print("---")
```

### Obter Resumo Mensal com Python
```python
import requests

url = "http://localhost:8080/api/faturamento/resumo/mensal"
response = requests.get(url)
resumo = response.json()

print(f"Mês: {resumo['mes']}/{resumo['ano']}")
print(f"Total Pago: R$ {resumo['totalPago']}")
print(f"Total Pendente: R$ {resumo['totalPendente']}")
print(f"Total Geral: R$ {resumo['totalGeral']}")
print(f"Quantidade: {resumo['quantidade']}")
```

---

## 📱 Exemplos em JavaScript/Fetch

### Criar Fatura com JavaScript
```javascript
const url = "http://localhost:8080/api/faturamento";

const fatura = {
    tipo: "venda",
    descricao: "Venda de acessório",
    valor: 89.90,
    status: "pendente"
};

fetch(url, {
    method: "POST",
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify(fatura)
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error("Erro:", error));
```

### Listar Faturas com JavaScript
```javascript
const url = "http://localhost:8080/api/faturamento";

fetch(url)
    .then(response => response.json())
    .then(faturas => {
        faturas.forEach(fatura => {
            console.log(`${fatura.descricao}: R$ ${fatura.valor}`);
        });
    })
    .catch(error => console.error("Erro:", error));
```

### Obter Resumo com JavaScript
```javascript
const url = "http://localhost:8080/api/faturamento/resumo/mensal";

fetch(url)
    .then(response => response.json())
    .then(resumo => {
        console.log(`Faturamento de ${resumo.mes}/${resumo.ano}`);
        console.log(`Total: R$ ${resumo.totalGeral}`);
    })
    .catch(error => console.error("Erro:", error));
```

---

## ⚙️ Testando com Postman

1. **Importar coleção:**
   - Abra Postman
   - Clique em "Import"
   - Cole os exemplos acima como requisições individuais

2. **Variáveis úteis:**
   ```
   {{base_url}} = http://localhost:8080/api
   {{user_id}} = user123
   {{fatura_id}} = {id_da_fatura}
   ```

3. **Salvar em coleção:**
   - Organize por pastas: Faturamento, Pedidos, Dados do Usuário
   - Use pré-requisitos para extrair IDs automaticamente

---

## 🔍 Dicas de Teste

1. **Verificar Compilação:**
   ```bash
   mvn clean compile
   ```

2. **Executar Servidor:**
   ```bash
   mvn spring-boot:run
   ```

3. **Testar Endpoint:**
   ```bash
   curl http://localhost:8080/api/faturamento/status
   ```

4. **Ver Logs:**
   - Verifique o console do Maven para logs detalhados
   - Cada operação gera logs de INFO, WARNING ou SEVERE

---

**Última atualização:** 23/02/2026
