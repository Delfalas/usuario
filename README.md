# 👤 Microserviço de Usuário

Este microserviço é responsável pelo **gerenciamento completo de usuários** dentro da arquitetura de microsserviços, incluindo:

* Cadastro e autenticação
* Atualização de dados
* Gerenciamento de endereços e telefones
* Integração com API externa de CEP

---

## 🧩 Papel na Arquitetura

Este serviço atua como o **core de identidade do sistema**, sendo utilizado pelos demais microsserviços (como tarefas e notificações) para:

* Identificar usuários
* Validar autenticação via JWT
* Fornecer dados cadastrais

---

## 🚀 Funcionalidades

### 👤 Usuário

* ✅ Cadastro de usuário
* 🔐 Login com geração de token JWT
* 🔍 Busca por email
* ✏️ Atualização de dados
* ❌ Exclusão de usuário

---

### 📞 Telefones

* ➕ Cadastro de telefone
* ✏️ Atualização de telefone
* ❌ Remoção de telefone
* 📦 Cadastro em lote

---

### 🏠 Endereços

* ➕ Cadastro de endereço
* ✏️ Atualização de endereço
* ❌ Remoção de endereço
* 📦 Cadastro em lote

---

### 🌎 Integração com CEP

* 🔎 Consulta de endereço via API externa (ViaCEP)

---

## 🔐 Segurança

* Autenticação via **JWT (JSON Web Token)**
* Senhas criptografadas com `PasswordEncoder`
* Operações sensíveis exigem token no header:

```id="authheader"
Authorization: Bearer <token>
```

---

## 📂 Estrutura

### 📌 Service (`UsuarioService`)

Responsável por toda a regra de negócio:

* Validação de email único
* Criptografia de senha
* Conversão DTO ↔ Entity
* Validação de pertencimento (telefone/endereço)
* Operações em lote

---

### 📌 Service Externo (`ViaCepService`)

* Consome API de CEP
* Valida e formata entrada (regex)
* Retorna dados de endereço

---

### 🌐 Controller (`UsuarioController`)

Responsável pelos endpoints REST e documentação com Swagger.

---

## 🔗 Endpoints

## 👤 Usuário

### ➕ Criar usuário

```id="c1"
POST /usuario
```

---

### 🔐 Login

```id="c2"
POST /usuario/login
```

**Resposta:**

```id="c3"
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
```

---

### 🔍 Buscar usuário por email

```id="c4"
GET /usuario/email?email=usuario@email.com
```

---

### ✏️ Atualizar usuário

```id="c5"
PUT /usuario
```

---

### ❌ Deletar usuário

```id="c6"
DELETE /usuario/{email}
```

---

## 📞 Telefones

### ➕ Cadastrar telefone

```id="c7"
POST /usuario/telefone
```

---

### 📦 Cadastrar telefones em lote

```id="c8"
POST /usuario/telefones/lote
```

---

### ✏️ Atualizar telefone

```id="c9"
PUT /usuario/telefone?id=1
```

---

### ❌ Deletar telefone

```id="c10"
DELETE /usuario/telefone/{id}
```

---

## 🏠 Endereços

### ➕ Cadastrar endereço

```id="c11"
POST /usuario/endereco
```

---

### 📦 Cadastrar endereços em lote

```id="c12"
POST /usuario/enderecos/lote
```

---

### ✏️ Atualizar endereço

```id="c13"
PUT /usuario/endereco?id=1
```

---

### ❌ Deletar endereço

```id="c14"
DELETE /usuario/endereco/{id}
```

---

## 🌎 CEP

### 🔎 Buscar dados de endereço

```id="c15"
GET /usuario/endereco/{cep}
```

---

## 🧠 Regras de Negócio Importantes

* ❗ Email deve ser único
* 🔒 Apenas o dono pode alterar/deletar seus dados
* 🔑 Token JWT é utilizado para identificar o usuário
* 📦 Operações em lote otimizam performance
* 📍 CEP deve conter exatamente 8 dígitos numéricos

---

## ⚠️ Tratamento de Erros

* `EmailJaCadastradoException` → Email duplicado
* `ResourceNotFoundException` → Recurso não encontrado
* `ConflictException` → Violação de regra de negócio
* `IllegalArgumentException` → CEP inválido

---

## 🛠️ Tecnologias Utilizadas

* Java 21
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Lombok
* OpenAPI / Swagger

---

## 🔄 Fluxo de Autenticação

1. Usuário realiza login
2. Recebe token JWT
3. Envia token no header das requisições
4. Sistema identifica o usuário via token

---

## 📌 Observações

* Este microserviço é independente e pode ser escalado separadamente
* Serve como base para autenticação de toda a arquitetura
* Integra facilmente com outros serviços (tarefas, notificações, etc.)

---

## 👨‍💻 Autor

Projeto desenvolvido com foco em:

* Arquitetura de microsserviços
* Segurança com JWT
* Boas práticas em APIs REST
* Integração com serviços externos
