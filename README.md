# 🏭 Bancada Smart 4.0

Sistema desenvolvido para o gerenciamento e monitoramento de uma bancada didática voltada aos conceitos da **Indústria 4.0**, permitindo o controle da produção, estoque e expedição em tempo real.

> Projeto desenvolvido durante o **3º semestre do Curso Técnico em Desenvolvimento de Sistemas**.

---

## 📖 Sobre o projeto

O Bancada Smart 4.0 foi desenvolvido com o objetivo de simular um ambiente industrial automatizado, permitindo o acompanhamento das operações realizadas por uma bancada física integrada a um CLP (Controlador Lógico Programável).

A aplicação oferece uma interface web para monitoramento da produção, gerenciamento do estoque e controle da expedição, comunicando-se com um backend responsável por processar as informações da bancada e disponibilizá-las em tempo real.

O projeto busca aplicar conceitos de:

- Indústria 4.0
- Sistemas Supervisórios
- Internet das Coisas (IoT)
- Arquitetura Cliente-Servidor
- APIs REST
- Banco de Dados
- Integração entre Software e Hardware

---

## ✨ Funcionalidades

- 📦 Cadastro e gerenciamento de pedidos
- 🏭 Controle da produção
- 📊 Monitoramento do estado da bancada
- 📍 Visualização das posições do estoque
- 🚚 Controle da expedição
- 🔄 Atualização automática das informações
- 🌐 Comunicação entre frontend e backend por API REST

---

## 🏗️ Arquitetura

O sistema é dividido em duas aplicações principais:

```
Frontend (Angular)
        │
        │ HTTP
        ▼
Backend (Spring Boot)
        │
        │
        ▼
Banco de Dados
        │
        ▼
Bancada / CLP
```

O frontend é responsável pela interface do usuário, enquanto o backend realiza toda a lógica de negócio, comunicação com o banco de dados e integração com a bancada.

---

## 🛠 Tecnologias utilizadas

### Frontend

- Angular
- TypeScript
- HTML5
- CSS3

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Maven

### Banco de Dados

- MySQL

### Outros

- Git
- GitHub
- REST API

---

## 📁 Estrutura do projeto

```
Bancada-Smart4.0
│
├── frontend/
│   ├── core
│   │   │─── model
│   │   └─── service
│   ├── feature
│   │   │─── configuracao
│   │   │─── dashboard
│   │   │─── home
│   │   │─── monitoramento
│   │   └─── pedidos
│   ├── layout
│   │   │─── footer
│   │   └─── navbar
│   └── shared
│       │─── components
│       └─── utils
│ 
├── backend/
│   ├── config
│   ├── event
│   ├── mapper
│   ├── controller
│   ├── service
│   │   │─── clp
│   │   │─── esp
│   │   └─── sse
│   ├── repository
│   ├── model
│   │   └─── clp
│   └── dto
│
└── README.md
```

---

## 🚀 Como executar

### Pré-requisitos

- Java 17
- Maven
- Node.js
- Angular CLI
- MySQL

### Backend

Clone o repositório:

```bash
git clone https://github.com/vinicius-andreazza/Bancada-Smart4.0.git
```

Entre na pasta do backend:

```bash
cd bancada-smart
```

Preencha o arquivo properties ou use .env

Execute:

```bash
mvn spring-boot:run
```

---

### Frontend

Entre na pasta do frontend:

```bash
cd front-bancada4.0
```

Instale as dependências:

```bash
npm install
```

Execute:

```bash
ng serve
```

A aplicação ficará disponível em:

```
http://localhost:4200
```

---


## 👨‍💻 Desenvolvido por

**Vinicius Andreazza**

Projeto acadêmico desenvolvido para o **3º semestre do Curso Técnico em Desenvolvimento de Sistemas**.

---

## 📄 Licença

Este projeto foi desenvolvido exclusivamente para fins acadêmicos.
