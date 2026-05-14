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
