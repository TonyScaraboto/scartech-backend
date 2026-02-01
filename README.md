# Backend - ScarTech Assistência Técnica

API REST para o sistema de assistência técnica ScarTech.

## Deploy na Koyeb

### Passo a Passo:

1. **Crie uma conta na Koyeb:** https://app.koyeb.com/

2. **Conecte seu GitHub** na Koyeb

3. **Suba o código para o GitHub:**
   ```bash
   cd backend
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/SEU_USUARIO/scartech-backend.git
   git push -u origin main
   ```

4. **Na Koyeb:**
   - Clique em "Create App"
   - Selecione "GitHub"
   - Escolha o repositório `scartech-backend`
   - Configure:
     - **Builder:** Dockerfile
     - **Port:** 8080
     - **Region:** Washington (mais próximo do Brasil)
   - Clique em "Deploy"

5. **Aguarde o deploy** (cerca de 2-5 minutos)

6. **Copie a URL** gerada (ex: `https://scartech-backend-xxx.koyeb.app`)

## Endpoints Disponíveis

- `GET /api/billing/summary` - Resumo de faturamento
- `GET /api/orders/status` - Status de ordens
- `POST /api/orders/generate-pdf` - Gerar PDF de ordem
- `POST /api/acessorios/catalogar` - Catalogar acessório

## Tecnologias

- Java 17
- Spring Boot 3.2.0
- Python 3 (scripts auxiliares)
