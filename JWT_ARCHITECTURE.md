# 🔐 Arquitetura de Autenticação JWT - ScarTech

## 📚 Índice
1. [Visão Geral](#visão-geral)
2. [Fluxo de Autenticação](#fluxo-de-autenticação)
3. [Estrutura do Token JWT](#estrutura-do-token-jwt)
4. [Componentes do Sistema](#componentes-do-sistema)
5. [Fluxo Multi-Dispositivo](#fluxo-multi-dispositivo)
6. [Segurança](#segurança)
7. [Tratamento de Erros](#tratamento-de-erros)

---

## Visão Geral

A autenticação do ScarTech implementa um sistema **JWT (JSON Web Token)** stateless que permite:

✅ **Autenticação sem servidor de sessão:** Cada token encapsula informações verifícaveis  
✅ **Multi-dispositivo:** Mesmo usuário em laptop, smartphone e tablet simultâneamente  
✅ **Persistência:** Dados salvos em arquivo JSON permanecem após logout  
✅ **Segurança:** Tokens assinados com HS256 (HMAC-SHA256)  
✅ **Escalabilidade:** Sem estado no servidor (stateless architecture)

---

## Fluxo de Autenticação

### 1️⃣ Registro (First Time User)

```
┌─────────────┐
│   Cliente   │ POST /api/auth/register
│  (Laptop)   │ {"email": "user@example.com", "password": "123456"}
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│  AuthenticationController   │
│  - Valida email/password    │
│  - Chama UserAuthService    │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────┐
│  UserAuthService        │
│  - Cria UUID (userId)   │
│  - Hash da senha        │
│  - Salva em users_db.json
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│  JwtUtil                │
│  - Gera token JWT       │
│  - Validade: 7 dias     │
│  - Assinado com HS256   │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│  Response: 201 Created  │
│  {                      │
│    "token": "eyJ...",   │
│    "userId": "uuid",    │
│    "expiresIn": 604800  │
│  }                      │
└─────────────────────────┘
```

**Arquivo criado:** `users_db.json`
```json
{
  "users": [
    {
      "userId": "a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6",
      "email": "user@example.com",
      "passwordHash": "1234567890",  // Apenas exemplo
      "createdAt": "2024-02-20T14:30:00Z"
    }
  ]
}
```

**Arquivo criado:** `user_data/a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6.json`
```json
{
  "ordens": [],
  "vendas": [],
  "produtos": []
}
```

---

### 2️⃣ Login (Obter Token)

```
┌─────────────┐
│   Cliente   │ POST /api/auth/login
│ (Smartphone)│ {"email": "user@example.com", "password": "123456"}
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│  AuthenticationController   │
│  - Valida credenciais       │
│  - Chama UserAuthService    │
└──────────┬──────────────────┘
           │
           ▼
┌──────────────────────────────┐
│  UserAuthService             │
│  - Busca email em users_db   │
│  - Compara hash da senha     │
│  - Se OK, gera novo token    │
└──────────┬───────────────────┘
           │
           ▼
┌─────────────────────────┐
│  JwtUtil.generateToken()│
│  - userId: extrai do DB │
│  - email: extrai do DB  │
│  - issuedAt: agora      │
│  - expiration: +7 dias  │
└──────────┬──────────────┘
           │
           ▼
┌────────────────────────────┐
│  Response: 200 OK          │
│  {                         │
│    "token": "eyJ...",      │
│    "userId": "a1b2c3d4...", │
│    "email": "user@...",    │
│    "expiresIn": 604800     │
│  }                         │
└────────────────────────────┘
```

**Tempo de Validade:** 7 dias = 604.800 segundos

---

### 3️⃣ Acesso a Dados (Usando Token)

```
┌─────────────────────────┐
│   Cliente (qualquer     │
│   dispositivo)          │
│   GET /api/user-data    │
│   Authorization: Bearer │
│   eyJ0eXAiOiJKV9U...    │
└──────────┬──────────────┘
           │
           ▼
┌──────────────────────────────┐
│  UserDataControllerAuth      │
│  1. Extrai token do header   │
│  2. Valida assinatura        │
│  3. Extrai userId do token   │
│  4. Carrega user_data/{uid}.json
│  5. Retorna dados JSON       │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│  JwtUtil.validateToken()     │
│  └─> Verifica assinatura HS256
│  └─> Verifica expiração      │
│  └─> Retorna true/false      │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│  Response: 200 OK            │
│  {                           │
│    "ordens": [...],          │
│    "vendas": [...],          │
│    "produtos": [...]         │
│  }                           │
└──────────────────────────────┘
```

---

## Estrutura do Token JWT

### Formato Base64

Um JWT tem 3 partes separadas por ponto (`.`):

```
eyJhbGciOiJIUzI1NiJ9 . eyJzdWIiOiJ1c2VyMTIzIn0 . SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
└────────────────────┘   └──────────────────────┘   └──────────────────────────────────────────────┘
      Header                   Payload                       Signature (HS256)
```

### Header (Decoded)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Explicação:**
- `alg`: HMAC-SHA256 (algoritmo de assinatura)
- `typ`: Tipo de token (JWT)

### Payload (Decoded)
```json
{
  "sub": "a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6",
  "email": "user@example.com",
  "iat": 1708456200,
  "exp": 1709061000
}
```

**Explicação:**
- `sub` (subject): ID do usuário (UUID)
- `email`: Email do usuário (para referência)
- `iat` (issued at): Quando foi criado (unix timestamp)
- `exp` (expiration): Quando expira (unix timestamp)

### Assinatura (HS256)
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  "scartech-secret-key-min-256-bits-for-hs256-algorithm-safe-production"
)
```

**Segurança:** Qualquer modificação no header ou payload invalida a assinatura.

---

## Componentes do Sistema

### 1. `JwtUtil.java` 🔑

**Responsabilidade:** Gerar, validar e extrair informações de tokens JWT

**Métodos Principais:**

```java
// Gera novo token
String generateToken(String userId, String email)
  → Retorna token válido por 7 dias
  
// Valida assinatura e expiração
boolean validateToken(String token)
  → Retorna true se válido, false se expirado/inválido
  
// Extrai userId do token
String extractUserId(String token)
  → Retorna "a1b2c3d4-e5f6-..." ou null se inválido
  
// Extrai email do token
String extractEmail(String token)
  → Retorna "user@example.com" ou null se inválido
  
// Retorna tempo de expiração
long getExpirationTimeSeconds()
  → Retorna 604800 (7 dias em segundos)
```

**Implementação de Segurança:**
- Chave de 256+ bits para HS256
- Valida assinatura antes de extrair claims
- Trata exceções de validation silenciosamente (logs apenas)

---

### 2. `UserAuthService.java` 👤

**Responsabilidade:** Gerenciar usuários, autenticação e persistência

**Métodos Principais:**

```java
// Registra novo usuário
User registrarUsuario(String email, String password)
  → Cria UUID, salva em users_db.json, retorna User
  → Throws: EmailJaRegistradoException
  
// Autentica e retorna token
String autenticar(String email, String password)
  → Busca em users_db.json, valida senha
  → Retorna token JWT, null se falhar
  
// Obtém usuário por ID
User obterUsuario(String userId)
  → Busca em users_db.json
  
// Valida token mantendo sintaxe simples
boolean validarToken(String token)
  → Chama JwtUtil.validateToken()
  
// Extrai userId do token
String extrairUserId(String token)
  → Chama JwtUtil.extractUserId()
  
// Extrai email do token
String extrairEmail(String token)
  → Chama JwtUtil.extractEmail()
```

**Armazenamento de Usuários:**
- Arquivo: `users_db.json` na raiz do projeto
- Formato: JSON array com objetos User
- Hash de Senha: `Object.hashCode()` (simples)
- ⚠️ **TODO em Produção:** Usar BCrypt em vez de hashCode()

---

### 3. `AuthenticationController.java` 🌐

**Responsabilidade:** Endpoints HTTP para autenticação

**Endpoints:**

```
POST /api/auth/register
  Corpo: {"email": "...", "password": "..."}
  Resposta: 201 {token, userId, email, expiresIn}
  Erros: 400 (validação), 409 (email duplicado)

POST /api/auth/login
  Corpo: {"email": "...", "password": "..."}
  Resposta: 200 {token, userId, email, expiresIn}
  Erros: 401 (credenciais inválidas)

POST /api/auth/logout
  Header: Authorization: Bearer {token}
  Resposta: 200 {message: "Logout realizado"}
  Nota: Logout é lado-cliente (remover token no app)

GET /api/auth/verify
  Header: Authorization: Bearer {token}
  Resposta: 200 {valid: true, userId}
  Erros: 401 se token inválido

GET /api/auth/me
  Header: Authorization: Bearer {token}
  Resposta: 200 {userId, email, createdAt}
  Erros: 401 se token inválido
```

---

### 4. `UserDataControllerAuth.java` 📊

**Responsabilidade:** CRUD de dados com validação JWT

**Endpoints:**

```
GET /api/user-data
  ✅ Requer: Authorization header com token válido
  Retorna: {ordens, vendas, produtos} do usuário
  
POST /api/user-data/sync
  ✅ Requer: Authorization header
  Corpo: {ordens: [], vendas: [], produtos: []}
  Salva tudo de uma vez em user_data/{userId}.json

GET /api/user-data/ordens
POST /api/user-data/ordens
POST /api/user-data/ordens/add

GET /api/user-data/vendas
POST /api/user-data/vendas
POST /api/user-data/vendas/add

GET /api/user-data/produtos
POST /api/user-data/produtos
POST /api/user-data/produtos/add
```

**Todos os endpoints:**
1. Extraem token do header `Authorization: Bearer {token}`
2. Validam com `JwtUtil.validateToken()`
3. Extraem userId com `JwtUtil.extractUserId()`
4. Retornam 401 se token inválido
5. Usam userId para carregar `user_data/{userId}.json`
6. Retornam dados isolados do usuário

---

## Fluxo Multi-Dispositivo

### Cenário: Usuário com Laptop + Smartphone

```
SEGUNDA-FEIRA, 10:00
┌──────────────────┐
│  LAPTOP          │
│  1. Faz login    │  POST /api/auth/login
│  2. Token: JWT1  │  Recebe: {token: "JWT1", userId: "uuid123"}
│  3. Salva dados  │  POST /api/user-data/sync com JWT1
│     - 3 vendas   │
│     - 2 ordens   │  ✅ Salvos em: user_data/uuid123.json
└──────────────────┘

                       users_db.json: {"uuid123": {"email": "...", "password": "..."}}
                       user_data/uuid123.json: {"ordens": [...], "vendas": [...]}

SEGUNDA-FEIRA, 14:00
┌──────────────────┐
│  SMARTPHONE      │
│  1. Faz login    │  POST /api/auth/login (MESMO email)
│  2. Token: JWT2  │  Recebe: {token: "JWT2", userId: "uuid123"} ← MESMO userId!
│  3. Busca dados  │  GET /api/user-data com JWT2
│  4. Vê 3 vendas  │  ✅ Dados do arquivo lido automaticamente
│     2 ordens     │  SEM NENHUMA CHAMADA ESPECIAL!
└──────────────────┘

SEGUNDA-FEIRA, 15:00
┌──────────────────┐
│  SMARTPHONE      │
│  1. Adiciona nova│  POST /api/user-data/vendas/add
│     venda nº 4   │  {"id": "VND004", ...} com JWT2
│     (em user_    │
│     data/uuid...) │ ✅ Arquivo atualizado: 4 vendas agora
└──────────────────┘
                       user_data/uuid123.json: {"vendas": [n1, n2, n3, n4]}

SEGUNDA-FEIRA, 16:00
┌──────────────────┐
│  LAPTOP          │
│  1. Refresh      │  GET /api/user-data com JWT1 (ainda válido)
│  2. Vê 4 vendas! │  ✅ VENDA Nº4 DO SMARTPHONE VISÍVEL!
│     2 ordens     │
└──────────────────┘
```

**Resultado Final:** ✅ **Sincronização Automática - SUCESSO!**

---

## Persistência Após Logout

```
SEGUNDA-FEIRA 10:00 - Logout do Laptop
┌──────────────┐
│  LAPTOP      │
│  1. Logout   │ POST /api/auth/logout com JWT1
│  2. deleta   │ (remove JWT1 do localStorage/sessionStorage)
│  3. Sai do   │
│     app      │
└──────────────┘
              💾 user_data/uuid123.json PERMANCE INTACTO EM DISCO

TERÇA-FEIRA 09:00 - Login em Novo Dispositivo
┌──────────────┐
│  TABLET      │
│  1. Login    │ POST /api/auth/login (MESMO email)
│  2. Token:   │ Recebe: {token: "JWT3", userId: "uuid123"}
│     JWT3     │
│  3. Busca    │ GET /api/user-data com JWT3
│     dados    │
│  4. VÊ mesma │ ✅ 4 VENDAS, 2 ORDENS, TUDO AINDA LÁ!
│     estrutura│    DADOS NUNCA FORAM PERDIDOS!
└──────────────┘
```

**¿Por que não perde?**
- Dados são salvos em **arquivo JSON em disco** (`user_data/{userId}.json`)
- Token apenas **controla acesso** aos dados
- Remover token não deleta arquivo
- Novo login gera novo token para MESMA conta (mesmo userId)
- Arquivo ainda existe e é retornado

---

## Segurança

### 1. Assinatura JWT (HS256)

```
Token gerado em LAPTOP com secret-key-A
┌─────────────────┐
│  Payload        │
│  {sub: "uuid",  │ + secret-key-A → Assinado
│   email: "..."}│
└─────────────────┘

Tentativa de modificação:
┌─────────────────┐
│  Payload        │ MODIFICADO
│  {sub: "uuid2", │ (outro userId)
│   email: "..."}│
└─────────────────┘
          ↓
Servidor tenta validar:
HMACSHA256(payload_modificado) ≠ assinatura_original
                ❌ TOKEN REJEITADO
```

### 2. Expiração de Token (7 dias)

```
Token gerado em: 2024-02-20 14:30:00 UTC
Expira em:       2024-02-27 14:30:00 UTC

Acesso no dia 1-6: ✅ Token válido
Acesso no dia 7:   ✅ Ainda válido (mesma hora exata)
Acesso no dia 8:   ❌ Token expirado (401 Unauthorized)
                      → Usuário faz login novamente
                      → Novo token por mais 7 dias
```

### 3. Isolamento de Dados

```
User A (uuid123) → user_data/uuid123.json
User B (uuid456) → user_data/uuid456.json

User B tenta:
  GET /api/user-data
  Authorization: Bearer {token_B}
                ↓
  Server extrai userId do token_B → uuid456
  Carrega APENAS user_data/uuid456.json
  ❌ Nunca consegue acessar uuid123.json

Segurança: arquivo é sempre determinístico pelos claims do token
```

### 4. Proteção Contra Replay Attacks

```
Alice captura token de Bob: JWT_Bob
Alice tenta usar em outro servidor:

Servidor novo com secret-key-DIFERENTE
   HMACSHA256(payload) com secret-key-novo ≠ assinatura_JWT_Bob
                ❌ TOKEN REJEITADO

Conclusão: Token só funciona no servidor que o criou
```

### 5. Header Authorization Obrigatório

```
Tentativa de acesso sem token:
  GET /api/user-data
  (sem Authorization header)
           ↓
  Server não encontra token
  ❌ 401 UNAUTHORIZED

Tentativa com formato inválido:
  Authorization: "eyJhbG..." (sem "Bearer ")
           ↓
  Server espera "Bearer " como prefixo
  ❌ 401 UNAUTHORIZED
```

---

## Tratamento de Erros

### Erros de Autenticação

| Erro | HTTP | Causa | Solução |
|------|------|-------|---------|
| Usuário não encontrado | 401 | Email não registrado | Usar /api/auth/register |
| Senha incorreta | 401 | Password não bate | Verificar senha |
| Email duplicado | 409 | Já registrado antes | Usar outro email |
| Token inválido | 401 | Modificado, expirado | Fazer login novamente |
| Token ausente | 401 | Sem header Authorization | Adicionar Authorization header |
| Formato token errado | 401 | Sem "Bearer " prefix | Formato: "Bearer {token}" |

### Erros de Dados

| Erro | HTTP | Causa | Solução |
|------|------|-------|---------|
| Dados vazios | 400 | Nenhum campo preenchido | Enviar dados válidos |
| Arquivo corrompido | 500 | JSON inválido em disco | Contact admin |
| Erro I/O | 500 | Problema ao salvar | Tentar novamente |

### Exemplos de Resposta de Erro

```json
// 401 - Sem token
{
  "erro": "Autenticação requerida",
  "mensagem": "Adicione um token válido no header: Authorization: Bearer {token}",
  "codigo": 401
}

// 401 - Token expirado
{
  "erro": "Autenticação requerida",
  "mensagem": "Token inválido ou expirado",
  "codigo": 401
}

// 409 - Email duplicado
{
  "error": "Email já está registrado",
  "email": "user@example.com",
  "statusCode": 409
}

// 400 - Validação
{
  "erro": "Email inválido",
  "mensagem": "Formato de email não válido",
  "codigo": 400
}
```

---

## Diagrama Geral da Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                   CLIENTE (Qualquer dispositivo)    │
│                 app web, Android, iOS, etc.         │
└────────────────────┬────────────────────────────────┘
                     │
                     │ HTTP/HTTPS
                     │
         ┌───────────┴──────────┐
         │                      │
         ▼                      ▼
    ┌─────────────┐      ┌──────────────────┐
    │ Sem Token   │      │ Com Token        │
    │ (Público)   │      │ (Privado)        │
    └─────────────┘      └──────────────────┘
         │                      │
    ┌────▼─────────────────────▼────┐
    │   SPRING BOOT                  │
    │   @RestController              │
    └────┬──────────────┬────────────┘
         │              │
    ┌────▼────────┐  ┌──▼─────────────────┐
    │  Authen...  │  │ UserDataController │
    │  Controller │  │ (requer JWT)       │
    └────┬────────┘  └──┬─────────────────┘
         │              │
         │         ┌─────▼─────────┐
         │         │  JwtUtil      │
         │         │  - validate() │
         │         │  - extract()  │
         │         └─────┬─────────┘
         │               │
    ┌────▼───────────────▼─────────┐
    │   UserAuthService            │
    │   - registrar()              │
    │   - autenticar()             │
    │   - obter()                  │
    └────┬────────────────────────┘
         │
    ┌────▼────────────────────────┐
    │   DISCO (Persistência)       │
    │   1. users_db.json           │
    │      {"users": [...]}        │
    │   2. user_data/               │
    │      └─ {uuid1}.json         │
    │      └─ {uuid2}.json         │
    │      └─ {uuid3}.json         │
    └──────────────────────────────┘
```

---

## Configuração em Produção

⚠️ **IMPORTANTE:** Antes de usar em produção:

1. **Chave Secreta:**
   ```java
   // ❌ NÃO USAR HARDCODED
   private static final String SECRET_KEY = "...";
   
   // ✅ USAR VARIÁVEL DE AMBIENTE
   String secretKey = System.getenv("JWT_SECRET_KEY");
   ```

2. **Hash de Senha:**
   ```java
   // ❌ NÃO USAR
   password.hashCode()
   
   // ✅ USAR BCrypt
   String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
   ```

3. **Token Refresh:**
   ```
   Implementar refresh tokens para renovar sem fazer login
   access_token: curta duração (1 hora)
   refresh_token: duração longa (7 dias)
   ```

4. **HTTPS:**
   ```
   ✅ SEMPRE usar HTTPS em produção
   ❌ NUNCA transmitir tokens em HTTP
   ```

5. **Logout Blacklist (Opcional):**
   ```
   Manter lista de tokens invalidados após logout
   (trade-off: perde característica stateless)
   ```

---

## Conclusão

✅ **Sistema implementado com:**
- Autenticação JWT stateless
- Persistência multi-dispositivo
- Isolamento de dados entre usuários
- Tokens válidos por 7 dias
- Acesso via HTTP Bearer tokens

🚀 **Pronto para produção com ajustes de segurança conforme recomendações acima.**
