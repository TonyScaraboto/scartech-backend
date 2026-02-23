# 🔐 Guia de Testes - Autenticação JWT e Acesso Multi-Dispositivo

## 📋 Visão Geral

Este guia demonstra como testar a autenticação JWT implementada e verificar o acesso multi-dispositivo persistente. O sistema permite que um usuário:

1. ✅ Se registre e crie uma conta
2. ✅ Faça login e receba um token JWT válido por 7 dias
3. ✅ Acesse seus dados de qualquer dispositivo usando o token
4. ✅ Compartilhe o mesmo arquivo de dados entre dispositivos (sem perder dados ao desloggar)
5. ✅ Tenha acesso isolado de outros usuários

---

## 🚀 Teste 1: Registro de Novo Usuário

**Endpoint:** `POST /api/auth/register`

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@scartech.com",
    "password": "senha123"
  }'
```

**Resposta Esperada (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZjcyZTBjNC1lYzQ4LTQ4YjMtYjBjZC1mZTQzYjZjYzMzYTkiLCJlbWFpbCI6ImNsaWVudGVAc2NhcnRlY2guY29tIiwiaWF0IjoxNzA5NjI3MjAwLCJleHAiOjE3MTA2MjcyMDB9.xxx",
  "userId": "af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9",
  "email": "cliente@scartech.com",
  "expiresIn": 604800
}
```

**O que verificar:**
- ✅ Status HTTP 201 (Created)
- ✅ Token JWT retornado (string longa com formato base64)
- ✅ userId gerado como UUID
- ✅ expiresIn em segundos (7 dias = 604.800 segundos)
- ✅ Arquivo `user_data/af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9.json` criado vazio

---

## 🚀 Teste 2: Login com Credenciais Existentes

**Endpoint:** `POST /api/auth/login`

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@scartech.com",
    "password": "senha123"
  }'
```

**Resposta Esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZjcyZTBjNC1lYzQ4LTQ4YjMtYjBjZC1mZTQzYjZjYzMzYTkiLCJlbWFpbCI6ImNsaWVudGVAc2NhcnRlY2guY29tIiwiaWF0IjoxNzA5NjI3MzAwLCJleHAiOjE3MTA2MjczMDB9.yyy",
  "userId": "af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9",
  "email": "cliente@scartech.com",
  "expiresIn": 604800
}
```

**Testes de Erro:**

Login com senha incorreta:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@scartech.com",
    "password": "senha_errada"
  }'
```
Resposta esperada: `401 Unauthorized` + mensagem "Senha incorreta"

Login com email não registrado:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "inexistente@scartech.com",
    "password": "qualquer"
  }'
```
Resposta esperada: `401 Unauthorized` + mensagem "Usuário não encontrado"

**O que verificar:**
- ✅ Token retornado para credenciais válidas
- ✅ Mesmo userId retornado em cada login (permite identificar o usuário)
- ✅ Erro 401 para senha incorreta
- ✅ Erro 401 para email inexistente

---

## 🚀 Teste 3: Salvar Dados do Usuário (DISPOSITIVO A)

**Cenário:** Usuário entra com seu laptop e salva dados de vendas

**Endpoint:** `POST /api/user-data/sync`

```bash
# Guardar o token em uma variável para facilitar
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZjcyZTBjNC1lYzQ4LTQ4YjMtYjBjZC1mZTQzYjZjYzMzYTkiLCJlbWFpbCI6ImNsaWVudGVAc2NhcnRlY2guY29tIiwiaWF0IjoxNzA5NjI3MzAwLCJleHAiOjE3MTA2MjczMDB9.yyy"

curl -X POST http://localhost:8080/api/user-data/sync \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ordens": [
      {
        "id": "ORD001",
        "cliente": "João Silva",
        "descricao": "Conserto de celular",
        "valor": 150.00,
        "data": "2024-02-20"
      }
    ],
    "vendas": [
      {
        "id": "VND001",
        "produto": "Tela LCD",
        "quantidade": 2,
        "preco_unitario": 80.00,
        "total": 160.00,
        "data": "2024-02-20"
      }
    ],
    "produtos": [
      {
        "id": "PROD001",
        "nome": "Bateria Samsung",
        "quantidade": 5,
        "preco": 45.00
      }
    ]
  }'
```

**Resposta Esperada (200 OK):**
```json
{
  "success": true,
  "message": "Dados sincronizados com sucesso. Seus dados estão disponíveis em qualquer dispositivo."
}
```

**O que verificar:**
- ✅ Status HTTP 200
- ✅ Arquivo `user_data/af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9.json` atualizado com os dados
- ✅ Erro 401 se tentar sem o header Authorization
- ✅ Erro 401 se token inválido/expirado

---

## 🚀 Teste 4: Recuperar Dados (DISPOSITIVO B - Smartphone)

**Cenário:** Mesma hora, usuário pega seu smartphone e quer ver os mesmos dados

**Passo 1:** Faz login no app do smartphone
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "cliente@scartech.com",
    "password": "senha123"
  }'
```
Token retornado será o mesmo usuário (userId igual), permitindo acessar os mesmos dados.

**Passo 2:** Recupera dados com o novo token
```bash
TOKEN_SMARTPHONE="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZjcyZTBjNC1lYzQ4LTQ4YjMtYjBjZC1mZTQzYjZjYzMzYTkiLCJlbWFpbCI6ImNsaWVudGVAc2NhcnRlY2guY29tIiwiaWF0IjoxNzA5NjI3NTAwLCJleHAiOjE3MTA2Mjc1MDB9.zzz"

curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer $TOKEN_SMARTPHONE"
```

**Resposta Esperada (200 OK):**
```json
{
  "ordens": [
    {
      "id": "ORD001",
      "cliente": "João Silva",
      "descricao": "Conserto de celular",
      "valor": 150.00,
      "data": "2024-02-20"
    }
  ],
  "vendas": [
    {
      "id": "VND001",
      "produto": "Tela LCD",
      "quantidade": 2,
      "preco_unitario": 80.00,
      "total": 160.00,
      "data": "2024-02-20"
    }
  ],
  "produtos": [
    {
      "id": "PROD001",
      "nome": "Bateria Samsung",
      "quantidade": 5,
      "preco": 45.00
    }
  ]
}
```

**RESULTADO CRÍTICO:**
🎉 **OS MESMOS DADOS APARECEM NO SMARTPHONE!**

**O que verificar:**
- ✅ Dados retornados são EXATAMENTE os mesmos salvos no laptop
- ✅ Nenhum logout necessário - dados persistem
- ✅ Dispositivo B pode fazer logout sem afetar laptop
- ✅ Fazendo login novamente em B, mesmos dados retornam

---

## 🚀 Teste 5: Adicionar Dados em Outro Dispositivo

**Cenário:** No smartphone, adiciona uma nova venda

```bash
TOKEN_SMARTPHONE="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZjcyZTBjNC1lYzQ4LTQ4YjMtYjBjZC1mZTQzYjZjYzMzYTkiLCJlbWFpbCI6ImNsaWVudGVAc2NhcnRlY2guY29tIiwiaWF0IjoxNzA5NjI3NTAwLCJleHAiOjE3MTA2Mjc1MDB9.zzz"

curl -X POST http://localhost:8080/api/user-data/vendas/add \
  -H "Authorization: Bearer $TOKEN_SMARTPHONE" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "VND002",
    "produto": "Vidro Temperado",
    "quantidade": 1,
    "preco_unitario": 25.00,
    "total": 25.00,
    "data": "2024-02-21"
  }'
```

**Resposta Esperada (201 Created):**
```json
{
  "success": true,
  "message": "Venda adicionada"
}
```

**Teste de Sincronização Cruzada:**

Agora volta para o laptop e checa GET /api/user-data:

```bash
TOKEN_LAPTOP="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZjcyZTBjNC1lYzQ4LTQ4YjMtYjBjZC1mZTQzYjZjYzMzYTkiLCJlbWFpbCI6ImNsaWVudGVAc2NhcnRlY2guY29tIiwiaWF0IjoxNzA5NjI3MzAwLCJleHAiOjE3MTA2MjczMDB9.yyy"

curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer $TOKEN_LAPTOP"
```

**🎉 RESULTADO ESPERADO:**
A lista de vendas agora contém TAMBÉM a VND002 adicionada no smartphone!

```json
{
  "ordens": [...],
  "vendas": [
    { "id": "VND001", ... },
    { "id": "VND002", ... }  // ← NOVO, adicionado no smartphone!
  ],
  "produtos": [...]
}
```

**O que verificar:**
- ✅ Dados adicionados em um dispositivo aparecem automaticamente em outro
- ✅ Nenhuma chamada de sincronização manual necessária
- ✅ Arquivo JSON em disco reflete todas as mudanças

---

## 🚀 Teste 6: Isolamento de Dados Entre Usuários

**Cenário:** Garante que dados do usuário A não são visíveis ao usuário B

```bash
# Registrar segundo usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "outro@scartech.com",
    "password": "outrasenhа"
  }'
```

Recebe userId diferente: `b1234567-bc12-34b5-c6de-fg78h9ij0k1l`

Tenta acessar dados do usuário 1 com token do usuário 2:
```bash
TOKEN_USER2="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiMTIzNDU2Ny1iYzEyLTM0YjUtYzZkZS1mZzc4aDlpajBrMWwiLCJlbWFpbCI6Im91dHJvQHNjYXJ0ZWNoLmNvbSIsIWF0IjoxNzA5NjI3NjAwLCJleHAiOjE3MTA2Mjc2MDB9.aaa"

curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer $TOKEN_USER2"
```

**Resposta Esperada (200 OK com dados vazios):**
```json
{
  "ordens": [],
  "vendas": [],
  "produtos": []
}
```

**O que verificar:**
- ✅ Arquivo separado `user_data/b1234567-bc12-34b5-c6de-fg78h9ij0k1l.json` para usuário 2
- ✅ Usuário 2 não consegue ver dados do usuário 1 (isolamento total)
- ✅ Cada usuário só acessa seus próprios dados usando seu token

---

## 🚀 Teste 7: Validação de Token Expirado

**Token com expiração futura (real):**
```bash
# Token será válido por 7 dias
# Após 7 dias, tentar esta chamada retornará 401

curl -X GET http://localhost:8080/api/user-data \
  -H "Authorization: Bearer $TOKEN_EXPIRADO"
```

**Resposta Esperada (401):**
```json
{
  "erro": "Autenticação requerida",
  "mensagem": "Adicione um token válido no header: Authorization: Bearer {token}",
  "codigo": 401
}
```

**O que verificar:**
- ✅ Token válido permite acesso
- ✅ Token expirado retorna 401
- ✅ Token inválido (modificado) retorna 401

---

## 🚀 Teste 8: Cenário Completo Multi-Dispositivo

### Dia 1 - Segunda-feira
1. **10h - Laptop:** Usuário faz login
2. **10h - Laptop:** Salva 3 vendas, 2 ordens
3. **14h - Smartphone:** Usuário faz login (MESMO userId)
4. **14h - Smartphone:** Pode ver as 3 vendas e 2 ordens do laptop ✅
5. **14h - Smartphone:** Adiciona 1 nova venda
6. **15h - Laptop:** Refresh - agora vê 4 vendas (incluindo a do smartphone) ✅

### Dia 2 - Terça-feira (Depois do logout de ambos)
1. **09h - Tablet:** Usuário faz login (token antigo no smartphone expirou)
2. **09h - Tablet:** Recebe NOVO token
3. **09h - Tablet:** Pode ver as 4 vendas do dia anterior (dados PERSISTEM) ✅
4. **09h - Tablet:** Nenhum logout necessário - dados já estavam salvos

### Dia 8 - Domingo (Depois de 7 dias)
1. **12h - Laptop:** Token expirou
2. **12h - Laptop:** Faz login novamente
3. **12h - Laptop:** Recebe novo token válido por mais 7 dias
4. **12h - Laptop:** Pode ver TODOS os dados dos últimos 7 dias ✅

---

## 📊 Verificação em Disco

Os dados devem estar estruturados assim:

```
projeto-root/
├── user_data/
│   ├── af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9.json    ← Usuário 1
│   ├── b1234567-bc12-34b5-c6de-fg78h9ij0k1l.json    ← Usuário 2
│   └── c9876543-dcba-4321-efgh-ijklmnop5678.json    ← Usuário 3
├── users_db.json    ← Credenciais de todos os usuários
└── ...
```

**Conteúdo do users_db.json:**
```json
{
  "users": [
    {
      "userId": "af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9",
      "email": "cliente@scartech.com",
      "passwordHash": "1234567890",
      "createdAt": "2024-02-20T10:30:00Z"
    },
    {
      "userId": "b1234567-bc12-34b5-c6de-fg78h9ij0k1l",
      "email": "outro@scartech.com",
      "passwordHash": "0987654321",
      "createdAt": "2024-02-20T14:45:00Z"
    }
  ]
}
```

**Conteúdo do user_data/af72e0c4-ec48-48b3-b0cd-fe43b6cc33a9.json:**
```json
{
  "ordens": [...todas as ordens sincronizadas...],
  "vendas": [...todas as vendas sincronizadas...],
  "produtos": [...todos os produtos sincronizados...]
}
```

---

## ✅ Checklist de Sucesso

Marque cada item conforme teste:

- [ ] **Registro:** Novo usuário criado com userId único
- [ ] **Login:** Token JWT retornado com 7 dias de expiração
- [ ] **Persistência:** Dados salvos em arquivo JSON persistem
- [ ] **Multi-Dispositivo:** Dados aparecem em dispositivo B logo após adicionar no dispositivo A
- [ ] **Isolamento:** Usuários não conseguem acessar dados uns dos outros
- [ ] **Sem Perda de Dados:** Logout do dispositivo A não afeta dispositivo B
- [ ] **Token Expirado:** 401 retornado após 7 dias
- [ ] **Novo Login:** Novo token gerado permite acesso aos mesmos dados

---

## 🔍 Endpoints Disponíveis - Resumo Rápido

| Método | Endpoint | Requer Token | Descrição |
|--------|----------|-------------|-----------|
| POST | `/api/auth/register` | ❌ | Criar novo usuário |
| POST | `/api/auth/login` | ❌ | Autenticar e receber token |
| POST | `/api/auth/logout` | ✅ | Logout (lado cliente) |
| GET | `/api/auth/verify` | ✅ | Verificar se token é válido |
| GET | `/api/auth/me` | ✅ | Obter perfil do usuário autenticado |
| GET | `/api/user-data` | ✅ | Obter todos os dados |
| GET | `/api/user-data/ordens` | ✅ | Obter ordens |
| POST | `/api/user-data/ordens` | ✅ | Salvar lista de ordens |
| POST | `/api/user-data/ordens/add` | ✅ | Adicionar uma ordem |
| GET | `/api/user-data/vendas` | ✅ | Obter vendas |
| POST | `/api/user-data/vendas` | ✅ | Salvar lista de vendas |
| POST | `/api/user-data/vendas/add` | ✅ | Adicionar uma venda |
| GET | `/api/user-data/produtos` | ✅ | Obter produtos |
| POST | `/api/user-data/produtos` | ✅ | Salvar lista de produtos |
| POST | `/api/user-data/produtos/add` | ✅ | Adicionar um produto |
| POST | `/api/user-data/sync` | ✅ | Sincronizar todos os dados de uma vez |

---

## 🐛 Troubleshooting

**Problema:** "Token inválido"
- Solução: Verifique se incluiu "Bearer " antes do token no header

**Problema:** "Autenticação requerida"
- Solução: O header Authorization não foi enviado ou tem formato incorreto

**Problema:** Dados não persistem entre logins
- Solução: Verificar se arquivo `user_data/{userId}.json` existe e tem conteúdo

**Problema:** Email já registrado
- Solução: Use um email diferente ou delete o arquivo `users_db.json` para resetar

---

## 📝 Conclusão

✅ **Sistema implementado com sucesso!**

O backend agora oferece:
- 🔐 Autenticação JWT segura com tokens de 7 dias
- 💾 Persistência de dados em disco (não perde ao deslogar)
- 📱 Acesso multi-dispositivo (laptop + smartphone + tablet)
- 🔒 Isolamento de dados entre usuários
- 🚀 Arquitetura escalável e sem estado (stateless)

**Próximos Passos:**
1. Implementar refresh tokens (renovar token sem fazer login novamente)
2. Usar BCrypt para hash de senhas (em vez de simples hash)
3. Implementar logout blacklist (invalidar tokens no servidor)
4. Adicionar autenticação OAuth2 (Google, Microsoft, etc)
