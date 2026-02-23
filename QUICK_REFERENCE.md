# ⚡ Quick Reference - ScarTech Backend

## 🚀 Começar Rápido

```bash
# Compilar
mvn clean compile

# Executar
mvn spring-boot:run

# Ligar servidor compilado
java -jar target/*.jar
```

---

## 🔑 Endpoints Mais Usados

### Faturamento

| Operação | Endpoint |
|----------|----------|
| Criar fatura | `POST /api/faturamento` |
| Listar faturas | `GET /api/faturamento` |
| Ver detalhes | `GET /api/faturamento/{id}` |
| Atualizar | `PUT /api/faturamento/{id}` |
| Deletar | `DELETE /api/faturamento/{id}` |
| Ver resumo | `GET /api/faturamento/resumo/mensal` |
| Marcar como pago | `PATCH /api/faturamento/{id}/status?status=pago` |

### Pedidos

| Operação | Endpoint |
|----------|----------|
| Validar | `POST /api/orders/validate` |
| Gerar PDF | `POST /api/orders/generate-pdf` |

### Dados

| Operação | Endpoint |
|----------|----------|
| Adicionar ordem | `POST /api/data/{userId}/ordens/add` |
| Listar ordens | `GET /api/data/{userId}/ordens` |
| Sincronizar | `POST /api/data/{userId}/sync` |

---

## 📝 Estrutura de Request/Response

### Criar Fatura - Sucesso (201)
```json
POST /api/faturamento
{
  "tipo": "ordem",
  "descricao": "Conserto de tela",
  "valor": 350.00
}

Response:
{
  "id": "uuid",
  "tipo": "ordem",
  "descricao": "Conserto de tela",
  "valor": 350.00,
  "status": "pendente",
  "mes": 2,
  "ano": 2026,
  "dataEmissao": "2026-02-23"
}
```

### Erro - Validação (400)
```json
{
  "mensagem": "Valor inválido",
  "erro": "O valor deve ser maior que zero",
  "status": 400
}
```

### Erro - Não Encontrado (404)
```json
{
  "mensagem": "Fatura não encontrada",
  "erro": "ID: abc123",
  "status": 404
}
```

---

## 🗂️ Estrutura do Projeto

```
backend/
├── Controllers         (API REST)
│   ├── FaturamentoController.java    ← PRINCIPAL
│   ├── BillingController.java
│   ├── OrderController.java
│   ├── ProdutoController.java
│   └── UserDataController.java
├── service/            (Lógica)
│   └── FaturamentoService.java       ← USAR PARA FATURAMENTO
├── dto/                (Modelos)
│   ├── Fatura.java
│   ├── FaturaResponse.java
│   └── ErrorResponse.java
├── util/               (Helpers)
│   └── FileUtil.java                 ← USO INTERNO
├── MainApplication.java
└── PythonRunner.java
```

---

## 💻 Adicionar Nova Rota

### 1. No Controller
```java
@GetMapping("/nova")
public ResponseEntity<?> novaRota() {
    try {
        logger.info("GET /api/faturamento/nova");
        // sua lógica
        return ResponseEntity.ok(resultado);
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Erro", e);
        return ResponseEntity.status(500).body(
            new ErrorResponse("Erro", e.getMessage(), 500)
        );
    }
}
```

### 2. Adicionar Método no Service
```java
public TipoRetorno novaOperacao() throws IOException {
    try {
        // lógica
    } catch (IOException e) {
        logger.log(Level.SEVERE, "Erro ao fazer X", e);
        throw e;
    }
}
```

---

## 🔍 Status Codes

| Código | Significado | Quando Usar |
|--------|------------|------------|
| 200 | OK | Requisição bem-sucedida |
| 201 | Created | Recurso criado (POST) |
| 400 | Bad Request | Dados inválidos |
| 404 | Not Found | Recurso não existe |
| 500 | Error | Erro do servidor |

---

## 🧪 Testar Rota

### Com cURL
```bash
curl -X GET http://localhost:8080/api/faturamento/status
```

### Com PowerShell
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/faturamento" `
  -Method GET
$response.Content | ConvertFrom-Json
```

### Com Python
```python
import requests
resp = requests.get("http://localhost:8080/api/faturamento")
print(resp.json())
```

---

## 📊 Consultar Dados

### Listar Tudo
```bash
curl http://localhost:8080/api/faturamento
```

### Com Filtro de Mês
```bash
curl "http://localhost:8080/api/faturamento?mes=2&ano=2026"
```

### Resumo Mensal
```bash
curl http://localhost:8080/api/faturamento/resumo/mensal
```

---

## 🐛 Debug

### Ver Logs
- Abra o console do Maven
- Procure por `INFO`, `WARNING`, `SEVERE`
- Cada operação loga automáticamente

### Problema: Compilação falha
```bash
# Limpar cache
mvn clean

# Recompilar
mvn compile
```

### Problema: Porta 8080 ocupada
```bash
# Mudar porta no application.properties (crie se não existir)
server.port=8081
```

### Problema: "Script não encontrado"
- Certifique-se que os arquivos Python estão em `python/`
- Verifique se o script existe: `pdf_generator.py`, etc

---

## 📚 Arquivos Importantes

| Arquivo | Propósito |
|---------|----------|
| `FaturamentoController.java` | Rotas de faturamento |
| `FaturamentoService.java` | Lógica de faturamento |
| `Fatura.java` | Modelo de fatura |
| `ErrorResponse.java` | Formato de erro |
| `API_ROUTES.md` | Documentação completa |
| `EXAMPLES.md` | Exemplos de requisições |

---

## ⚙️ Configurações

### application.properties (criar em `src/main/resources/`)
```properties
server.port=8080
spring.application.name=scartech-backend
logging.level.backend=INFO
```

### CORS
- Habilitado em todos os controllers: `@CrossOrigin(origins = "*")`
- Mude `"*"` por domínios específicos em produção

---

## 🔄 Fluxo Típico

```
Cliente
  ↓
Request HTTP → Controller
  ↓
Validação → Service
  ↓
Lógica de Negócio
  ↓
Salvar em arquivo JSON
  ↓
Response → Cliente
```

---

## 📍 Validações Automáticas

| Campo | Validação |
|-------|-----------|
| `valor` | > 0 |
| `descricao` | Não vazio |
| `status` | pago/pendente/cancelado |
| `mes` | 1-12 |
| `ano` | Número positivo |

---

## 🎯 Checklist para Nova Feature

- [ ] Adicionar método no Service
- [ ] Adicionar endpoint no Controller
- [ ] Adicionar logging
- [ ] Adicionar validação
- [ ] Adicionar error handling
- [ ] Testar localmente
- [ ] Documentar em API_ROUTES.md
- [ ] Adicionar exemplo em EXAMPLES.md

---

## 🚨 Erros Comuns

### "HTTP 400 - Valor inválido"
✓ Certifique-se que `valor > 0`

### "HTTP 404 - Fatura não encontrada"
✓ Verifique se o UUID está correto

### "HTTP 500 - Script não encontrado"
✓ Verifique caminho de `python/pdf_generator.py`

### "Compilação falha"
✓ Execute `mvn clean compile`

---

## 📞 Ajuda Rápida

**Código compila mas não inicia?**
```bash
mvn spring-boot:run -X
# -X mostra mais detalhes
```

**Quer resetar banco de dados?**
```bash
# Delete o arquivo faturamento_db.json em python/
# Será recriado automaticamente
```

**Testar sem iniciar o servidor?**
- Abra `FaturamentoService.java`
- Crie main() e chame métodos diretamente

---

## 🔗 Links Úteis

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [REST API Best Practices](https://restfulapi.net/)
- [HTTP Status Codes](https://httpwg.org/specs/rfc7231.html#status.codes)
- [JSON Schema](https://json-schema.org/)

---

**Última atualização: 23/02/2026**
**Versão: 1.0.0**
