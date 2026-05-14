Sistema de gerenciamento de biblioteca desenvolvido em Java.


## Integrantes
- André Marcelino Watanabe - 14558311
- Nome  - NUSP
- Nome  - NUSP

# 📚 JavaLibrary - Sistema de Gestão de Biblioteca

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)

## 🖥️ Sobre o Projeto
O **JavaLibrary** é um sistema completo de automação para bibliotecas, desenvolvido como projeto prático para a disciplina de Programação Orientada a Objetos (POO). O sistema permite o gerenciamento de acervo, usuários e o controle rigoroso de empréstimos, incluindo o cálculo automático de multas por atraso.

## 🚀 Funcionalidades Principais
- **Gestão de Acervo:** Cadastro, edição e consulta de livros (ISBN, Autor, Título, Ano).
- **Controle de Usuários:** Cadastro de alunos vinculado ao RA e dados de contato.
- **Sistema de Empréstimos:** - Registro de saída com data de devolução automática (prazo de 14 dias).
    - Verificação de disponibilidade de exemplares.
- **Cálculo de Multas:** O sistema identifica automaticamente livros em atraso e calcula o valor devido (R$ 2,00 por dia).
- **Persistência de Dados:** Todos os dados são salvos em um arquivo binário (`biblioteca_dados.dat`), garantindo que as informações não se percam ao fechar o app.

## 🎨 Mockups e Interface
Abaixo estão as capturas de tela da interface desenvolvida em Java Swing:

| Dashboard / Livros | Gerenciar Usuários | Tela de Checkout |
| :---: | :---: | :---: |
| ![Livros](link_da_sua_imagem_1) | ![Usuarios](link_da_sua_imagem_2) | ![Checkout](link_da_sua_imagem_3) |

> *Nota: Substitua os links acima pelas imagens que você salvou no repositório.*

## 🔄 Fluxograma do Processo
O fluxo de operação do sistema segue a lógica de validação de disponibilidade e status do aluno:

```mermaid
graph TD
    A[Início: Dashboard] --> B{Ação do Usuário}
    B -->|Consulta| C[Pesquisar Livro]
    B -->|Empréstimo| D[Selecionar Livro e Aluno]
    D --> E{Há exemplares?}
    E -->|Sim| F[Gerar Empréstimo]
    E -->|Não| G[Aviso: Indisponível]
    B -->|Devolução| H[Verificar Data]
    H --> I{Atrasado?}
    I -->|Sim| J[Calcular Multa]
    I -->|Não| K[Finalizar e Liberar Livro]

## 🛠️ Conceitos de POO Aplicados

Este projeto foi estruturado utilizando os pilares fundamentais da Programação Orientada a Objetos, conforme implementado nos arquivos fonte:

1. **Herança:** A classe `Book.java` estende uma classe base (como `LibraryItem`), herdando comportamentos e atributos comuns. Isso permite que o sistema seja expandido para outros tipos de mídia (revistas, DVDs) sem duplicar código.
2. **Encapsulamento:** Os dados em `User.java`, `Book.java` e `Loan.java` são protegidos por modificadores de acesso `private`. O acesso a esses dados é feito estritamente através de métodos *Getters* e *Setters*, garantindo a validação e integridade das informações.
3. **Polimorfismo:** Utilizado através da sobrescrita de métodos (`@Override`). A classe `Book`, por exemplo, provê sua própria implementação para métodos de exibição de detalhes e verificação de disponibilidade.
4. **Abstração:** O sistema utiliza classes e interfaces para representar entidades do mundo real (Livro, Usuário, Empréstimo), focando apenas nos atributos e comportamentos essenciais para a regra de negócio da biblioteca.
5. **Persistência com Serialização:** O uso da interface `Serializable` e da classe `DataManager` demonstra o conceito de salvar o estado de objetos em arquivos binários (`.dat`), permitindo que os dados persistam entre diferentes execuções do programa.

## 📦 Como Executar

Siga os passos abaixo para rodar o projeto em seu ambiente local:

1. **Pré-requisitos:**
   - Possuir o **Java JDK 17** ou superior instalado.
   - Uma IDE de sua preferência (IntelliJ, Eclipse ou VS Code).

2. **Clonagem e Preparação:**
   ```bash
   # Clone o repositório
   git clone [https://github.com/seu-usuario/nome-do-repositorio.git](https://github.com/seu-usuario/nome-do-repositorio.git)

   # Entre na pasta do projeto
   cd nome-do-repositorio
