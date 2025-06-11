# QR Code API

Uma API REST em Scala para geração, leitura e gerenciamento de QR codes utilizando Scalatra e ZXing , Feita para reforçar o aprendizado na linguagem Scala.

## ✨ Funcionalidades

- 🔗 **Geração** de QR codes a partir de texto
- 📱 **Leitura** de QR codes de imagens base64
- 📂 **Histórico** completo com filtros por tipo
- 💾 **Download** direto das imagens
- 🗑️ **Exclusão** de QR codes

## 🛠️ Tecnologias

- **Scala** com Scalatra Framework
- **ZXing** para processamento de QR codes
- **JSON4S** para serialização JSON
- **Java ImageIO** para manipulação de imagens

## 📡 Rotas da API

### Geração de QR Code

```http
POST /generate
```

**Body:**
```json
{
  "content": "Seu texto aqui"
}
```

**Response (201):**
```json
{
  "id": "uuid-gerado",
  "content": "Seu texto aqui",
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "qrType": "GENERATED",
  "createdAt": "2025-06-11T10:30:00"
}
```

### Leitura de QR Code

```http
POST /read
```

**Body:**
```json
{
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA..."
}
```

**Response (200):**
```json
{
  "id": "uuid-gerado",
  "content": "Texto decodificado",
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "qrType": "READ",
  "createdAt": "2025-06-11T10:30:00"
}
```

### Buscar QR Code por ID

```http
GET /:id
```

**Response (200):**
```json
{
  "id": "uuid-do-qr",
  "content": "Conteúdo do QR",
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "qrType": "GENERATED",
  "createdAt": "2025-06-11T10:30:00"
}
```

### Download da Imagem

```http
GET /download/:id
```

**Response:** Arquivo PNG com o QR code

**Headers:**
- `Content-Type: image/png`
- `Content-Disposition: attachment; filename=qrcode-{id}.png`

### Excluir QR Code

```http
DELETE /:id
```

**Response (204):** Sem conteúdo

### Histórico

```http
GET /history
```

**Parâmetros opcionais:**
- `type`: `read` ou `generated`

**Exemplos:**
```http
GET /history
GET /history?type=generated
GET /history?type=read
```

**Response (200):**
```json
[
  {
    "id": "uuid-1",
    "content": "Primeiro QR",
    "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
    "qrType": "GENERATED",
    "createdAt": "2025-06-11T10:30:00"
  },
  {
    "id": "uuid-2",
    "content": "Segundo QR",
    "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
    "qrType": "READ",
    "createdAt": "2025-06-11T10:25:00"
  }
]
```

## 📋 Tipos de QR Code

| Tipo | Descrição |
|------|-----------|
| `GENERATED` | QR codes criados via API |
| `READ` | QR codes lidos de imagens |

## ⚡ Exemplo de Uso

### Gerando um QR Code

```bash
curl -X POST http://localhost:8080/generate \
  -H "Content-Type: application/json" \
  -d '{"content": "https://github.com"}'
```

### Lendo um QR Code

```bash
curl -X POST http://localhost:8080/read \
  -H "Content-Type: application/json" \
  -d '{"imageBase64": "iVBORw0KGgoAAAANSUhEUgAA..."}'
```

### Baixando um QR Code

```bash
curl -X GET http://localhost:8080/download/uuid-do-qr \
  -o qrcode.png
```

## 🚀 Executando

1. Configure as dependências no `build.sbt`
2. Execute o projeto com SBT
3. Acesse `http://localhost:8080`

## 📝 Notas

- As imagens são armazenadas em formato base64
- O histórico é ordenado por data de criação (mais recente primeiro)
- Tamanho padrão dos QR codes: 300x300 pixels
- Armazenamento em memória usando `TrieMap`
