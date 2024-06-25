# EventosTech

EventosTech é uma aplicação backend desenvolvida para gerenciar eventos de tecnologia, permitindo o cadastro, listagem, filtragem e detalhamento de eventos, bem como a associação de cupons de desconto.

## Objetivo

Desenvolver o backend de uma aplicação para gerenciar eventos de tecnologia, permitindo o cadastro, listagem, filtragem e detalhamento de eventos, bem como a associação de cupons de desconto.

## Funcionalidades

### Cadastro de Evento

O sistema permite que o usuário cadastre um evento com os seguintes campos:
- **Título** (obrigatório)
- **Descrição** (opcional)
- **Data** (obrigatório)
- **Local** (obrigatório, se presencial)
- **Imagem** (opcional)
- **URL do evento** (obrigatório, se remoto)

### Classificação de Eventos

Os eventos são classificados como remotos ou presenciais.

### Associação de Cupons de Desconto

O sistema permite que o usuário associe um ou mais cupons de desconto a um evento. Cada cupom possui os seguintes campos:
- **Código do cupom** (obrigatório)
- **Desconto percentual ou valor fixo** (obrigatório)
- **Data de validade** (opcional)

### Listagem de Eventos

O sistema lista os eventos cadastrados, com paginação. A listagem inclui:
- **Título**
- **Data**
- **Local**
- **Tipo** (remoto ou presencial)
- **Banner**
- **Descrição**

### Retorno de Eventos

O sistema retorna somente eventos que ainda não aconteceram.

### Filtragem de Eventos

O sistema permite que o usuário filtre a lista de eventos pelos seguintes critérios:
- **Título**
- **Data**
- **Local**

### Detalhamento de Evento

O sistema permite que o usuário consulte todos os detalhes de um evento específico, incluindo:
- **Título**
- **Descrição**
- **Data**
- **Local**
- **Imagem**
- **URL do evento**
- **Lista de cupons ativos**, com seus respectivos detalhes (código do cupom, desconto, data de validade)

## Modelagem de Dados

### Diagrama ER

mermaid
erDiagram
EVENT {
UUID id
String title
String description
Datetime date
Boolean remote
String img_url
String event_url
}

    COUPON {
        UUID id
        Integer discount
        String code
        Datetime valid
        UUID event_id
    }

    ADDRESS {
        UUID id
        String url
        String city
        UUID event_id
    }

    EVENT ||--o{ COUPON : has
    EVENT ||--o{ ADDRESS : located_at


## Tecnologias Utilizadas

- **Java**
- **Spring Framework**
- **PostgreSQL**
- **AWS (Amazon Web Services)**

## Como Executar

### Pré-requisitos

- Java 21 ou superior
- Maven
- PostgreSQL
- Conta na AWS (para S3 e RDS)

### Passos para Executar

1. Clone o repositório:
   bash
   git clone https://github.com/pblda13/eventostech-api.git
   cd eventostech


2. Configure o banco de dados PostgreSQL.

3. Configure suas credenciais AWS para acesso ao S3 e RDS.

4. Compile e execute a aplicação:
   bash
   mvn clean install
   mvn spring-boot:run


## Contribuição

1. Faça um fork do projeto.
2. Crie uma branch para sua feature (`git checkout -b feature/sua-feature`).
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`).
4. Faça o push para a branch (`git push origin feature/sua-feature`).
5. Crie um novo Pull Request.
