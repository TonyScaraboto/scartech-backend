# 🚀 Guia Prático de Início - Sistema de Autenticação JWT

## ⚡ Comece em 5 Minutos

### Passo 1: Iniciar o Servidor

```bash
cd c:\Users\46\scartech-backend
mvn spring-boot:run
```

Aguarde a mensagem:
```
Tomcat initialized with port 8080
Initializing Spring embedded WebApplicationContext
```

### Passo 2: Registrar Primeiro Usuário

#### Terminal PowerShell:
```powershell
$body = @{
    email = "seu.email@scartech.com"
    password = "SenhaForte123!"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri http://localhost:8080/api/auth/register `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

Write-Output "UserId: $(($response.Content | ConvertFrom-Json).userId)"
```

#### Terminal Linux/Mac (cURL):
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"seu.email@scartech.com","password":"SenhaForte123!"}'
```

**Resposta esperada:**
```json
{
  "userId": "a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6",
  "email": "seu.email@scartech.com",
  "mensagem": "Usuário registrado com sucesso"
}
```

### Passo 3: Fazer Login e Obter Token

#### PowerShell:
```powershell
$body = '{"email":"seu.email@scartech.com","password":"SenhaForte123!"}'

$response = Invoke-WebRequest -Uri http://localhost:8080/api/auth/login `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$data = $response.Content | ConvertFrom-Json
$TOKEN = $data.token

Write-Output "Token obtido com sucesso!"
Write-Output "Válido por: $($data.expiresIn) segundos (7 dias)"
Write-Output ""
Write-Output "Token: $($TOKEN.SubString(0, 50))..."
```

#### Linux/Mac:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu.email@scartech.com","password":"SenhaForte123!"}' | jq -r .token)

echo "Token obtido: ${TOKEN:0:50}..."
```

**Resposta esperada:**
```json
{
  "email": "seu.email@scartech.com",
  "userId": "a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhMWIyYzNkNCIsImVtYWlsIjoic2V1LmVtYWlsQHNjYXJ0ZWNoLmNvbSIsImlhdCI6MTcwOTYyNzIwMCwiZXhwIjoxNzEwMjMyMDAwfQ.xxx",
  "expiresIn": 604800
}
```

### Passo 4: Salvar Dados com Token

#### PowerShell:
```powershell
# Token obtido no passo anterior
$TOKEN = "eyJhbGciOiJIUzI1NiJ9..."

$dados = @{
    ordens = @(
        @{
            id = "ORD001"
            cliente = "João Silva"
            descricao = "Conserto de celular"
            valor = 150
        }
    )
    vendas = @(
        @{
            id = "VND001"
            produto = "Tela LCD"
            quantidade = 2
            preco = 80
        }
    )
    produtos = @()
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $TOKEN"
}

$response = Invoke-WebRequest -Uri http://localhost:8080/api/user-data/sync `
  -Method POST `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $dados

Write-Output "✅ Dados salvos com sucesso!"
```

#### Linux/Mac:
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/api/user-data/sync \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ordens": [{"id": "ORD001", "cliente": "João", "descricao": "Conserto", "valor": 150}],
    "vendas": [{"id": "VND001", "produto": "Tela", "quantidade": 2, "preco": 80}],
    "produtos": []
  }'
```

### Passo 5: Verificar Dados (Laptop)

#### PowerShell:
```powershell
$headers = @{ "Authorization" = "Bearer $TOKEN" }

$response = Invoke-WebRequest -Uri http://localhost:8080/api/user-data `
  -Method GET `
  -Headers $headers

Write-Output "✅ Dados recuperados:"
($response.Content | ConvertFrom-Json) | ConvertTo-Json
```

#### Linux/Mac:
```bash
curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta:**
```json
{
  "ordens": [
    {
      "id": "ORD001",
      "cliente": "João Silva",
      "descricao": "Conserto de celular",
      "valor": 150
    }
  ],
  "vendas": [
    {
      "id": "VND001",
      "produto": "Tela LCD",
      "quantidade": 2,
      "preco": 80
    }
  ],
  "produtos": []
}
```

---

## 📱 Teste Multi-Dispositivo (Smartphone)

### Passo 1: Fazer Login em Novo Dispositivo

Faça login no smartphone com **MESMO EMAIL E SENHA**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu.email@scartech.com","password":"SenhaForte123!"}'
```

Recebe novo token (JWT_SMARTPHONE) com **MESMO userId**

### Passo 2: Verificar Dados (Smartphone)

```bash
curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer JWT_SMARTPHONE"
```

🎉 **Resultado:** Recebe os MESMOS dados salvos no laptop!

```json
{
  "ordens": [
    {
      "id": "ORD001",
      "cliente": "João Silva",
      "descricao": "Conserto de celular",
      "valor": 150
    }
  ],
  "vendas": [...],
  "produtos": []
}
```

### Passo 3: Adicionar Novos Dados (Smartphone)

```bash
curl -X POST http://localhost:8080/api/user-data/ordens/add \
  -H "Authorization: Bearer JWT_SMARTPHONE" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "ORD002",
    "cliente": "Maria Santos",
    "descricao": "Troca de bateria",
    "valor": 80
  }'
```

### Passo 4: Verificar Sincronização (Laptop)

Volta para o laptop e faz GET:

```bash
curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer JWT_LAPTOP"
```

🎉 **Magia!** Agora aparecem 2 ordens (a de João + a de Maria added no smartphone)

---

## 🔑 Dicas de Segurança

### ✅ Como Guardar o Token

**Opção 1: Variável de Ambiente (Recomendado)**
```powershell
# PowerShell
$env:SCARTECH_TOKEN = "eyJhbGciOiJIUzI1NiJ9..."
$headers = @{ "Authorization" = "Bearer $env:SCARTECH_TOKEN" }
```

**Opção 2: Arquivo .env (NÃO em produção)**
```
TOKEN=eyJhbGciOiJIUzI1NiJ9...
```

**Opção 3: localStorage (Aplicações Web)**
```javascript
localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiJ9...');

// Depois
const token = localStorage.getItem('token');
fetch('http://localhost:8080/api/user-data', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### ⚠️ NÃO FAÇA:

```bash
# ❌ Evite token em URL
curl http://localhost:8080/api/user-data?token=eyJ...

# ❌ Evite token em query params
curl http://localhost:8080/api/user-data?auth=eyJ...

# ❌ Evite compartilhar token
# Não envie token para chat, email, mensagens

# ❌ Evite guardar em arquivo .txt visível
# TOKEN=eyJ... # NO arquivo de configuração pública
```

---

## 🐛 Troubleshooting

### Problema: "401 Unauthorized"

**Solução 1:** Verificar se token está no formato correto
```
❌ Authorization: eyJhbGciOiJIUzI1NiJ9...
✅ Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
               ↑
          Precisa de "Bearer "
```

**Solução 2:** Verificar se token expirou
```bash
# Token válido por 7 dias
# Após 7 dias, fazer novo login

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu@email.com","password":"senha"}'
```

**Solução 3:** Verificar header HTTP
```bash
curl -v http://localhost:8080/api/user-data \
  -H "Authorization: Bearer $TOKEN"

# Deve mostrar:
# > GET /api/user-data HTTP/1.1
# > Authorization: Bearer eyJ...
```

### Problema: "Usuário não encontrado"

**Causa:** Email não registrado  
**Solução:**
```bash
# 1. Registrar novo usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"novo@email.com","password":"senha123"}'

# 2. Depois fazer login com este email
```

### Problema: "Senha incorreta"

**Solução:** Verificar se digitou a senha certa
```bash
# Senha registrada: "SenhaForte123!"
# Tentando: "senhaforte123!" ❌ (letras minúsculas)

# Deve ser exatamente igual à hora do registro
```

---

## 📊 Verificar Status do Servidor

```bash
# Verificar se servidor está rodando na porta 8080
netstat -ano | findstr ":8080"

# Resultado esperado:
# TCP    [::1]:8080             [::]:0                 LISTENING    12345

# Verificar saúde do servidor
curl http://localhost:8080/

# Ou testar um endpoint que não requer autenticação
curl -X GET http://localhost:8080/api/faturas/resumo-mensal
```

---

## 🎓 Exemplos Práticos

### Exemplo 1: Script de Login Automático

#### PowerShell:
```powershell
# script-login.ps1
param([string]$email, [string]$password)

$body = @{
    email = $email
    password = $password
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri http://localhost:8080/api/auth/login `
  -Method POST -ContentType "application/json" -Body $body

$token = ($response.Content | ConvertFrom-Json).token
return $token
```

Usar:
```powershell
$TOKEN = .\script-login.ps1 -email "seu@email.com" -password "senha123"
```

#### Bash:
```bash
#!/bin/bash
# script-login.sh
EMAIL=$1
PASSWORD=$2

TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" | jq -r .token)

echo $TOKEN
```

Usar:
```bash
TOKEN=$(./script-login.sh seu@email.com senha123)
```

---

### Exemplo 2: Adicionar Venda Rapidamente

```bash
#!/bin/bash
# add-venda.sh

TOKEN=$1
PRODUTO=$2
QTD=$3
PRECO=$4

curl -X POST http://localhost:8080/api/user-data/vendas/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": \"VND_$(date +%s)\",
    \"produto\": \"$PRODUTO\",
    \"quantidade\": $QTD,
    \"preco\": $PRECO,
    \"data\": \"$(date +%Y-%m-%d)\"
  }"
```

Usar:
```bash
./add-venda.sh $TOKEN "Tela LCD Samsung" 2 89.90
```

---

## 📚 Documentação Adicional

| Documento | Propósito |
|-----------|-----------|
| [AUTH_TEST_GUIDE.md](AUTH_TEST_GUIDE.md) | 8 cenários de teste completos |
| [JWT_ARCHITECTURE.md](JWT_ARCHITECTURE.md) | Explicação detalhada da arquitetura |
| [TEST_RESULTS.md](TEST_RESULTS.md) | Resultados dos testes executados |
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) | Resumo geral do projeto |

---

## ✅ Checklist de Implementação

- [x] Servidor rodando na porta 8080
- [x] Endpoint de registro funcionando
- [x] Endpoint de login retornando JWT
- [x] Endpoint de sincronização de dados
- [x] Endpoint de recuperação de dados
- [x] Token válido por 7 dias
- [x] Multi-dispositivo testado
- [x] Documentação completa

---

## 🎯 Próximas Implementações

1. **App Web Frontend** - Interface para usuários
2. **App Android** - Acesso mobile via smartphone
3. **App iOS** - Acesso mobile via iPhone
4. **Desktop App** - Aplicação de desktop

---

## 📞 Suporte Rápido

```
Erro 401: Adicione token no header com "Bearer " prefix
Erro 400: Verifique JSON válido no corpo da requisição
Erro 404: Verifique a rota (ex: /api/user-data, não /api/user-data/)
Erro 500: Servidor error, verifique logs no console
```

---

**🚀 Agora é só usar! Boa sorte com o ScarTech!**
