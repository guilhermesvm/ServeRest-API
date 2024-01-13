<div align="center">
    <h1> Plano de Teste </h1>
</div>

# 1. Projeto: ServeTest
- Verificar e testar os endpoints "/Login", "/Usuários", "/Produtos" e "/Carrinho" e seus respectivos verbos HTTP (GET, POST, PUT, DELETE) da API [ServeRest](https://serverest.dev/#/). 
- Para rodar os testes é necessário instalar o Node.js, esse irá criar o localhost para teste, e digitar o comando:```npx serverest@latest``` em algum terminal

# 2. Resumo
O objetivo do teste é garantir que as áreas descritas acima sejam testadas utilizando RestAssured no Java, para que o projeto consiga seguir o fluxo de compras de maneira funcional, com o fim de atingir seu objetivo final, ou seja, cadastrar/logar/vender um produto. Para isso, foi baseado nos critérios de aceitação presentes nas User Stories (US): US|001 Usuários, US|002 Login, US|003 Produtos e US|004 Carrinho.


# 3. Pessoas Envolvidas
- Equipe Testadora: Guilherme Machado;
- Público-alvo: Compradores e Vendedores da E-Commerce artificial ServeRest.

# 4. Funcionalidades e Módulos à Serem Testados

## 4.1 👫 **[/Usuarios]**

### **DoR e DoD**
- DoR
    - Banco de dados e infraestrutura para desenvolvimento disponibilizados;
    - Ambiente de testes disponibilizado.
- DoD

    - CRUD de cadastro de vendedores (usuários) implementado (CRIAR, ATUALIZAR, LISTAR E DELETAR);
    - Análise de testes cobrindo todos verbos;
    - Matriz de rastreabilidade atualizada;
    - Automação de testes baseado na análise realizada;

### **Critérios de Aceitação**
- Todos os usuários deverão possuir um cadastro contendo os seguintes campos: NOME, EMAIL, PASSWORD e ADMINISTRADOR;
- Não deverá ser possível fazer ações com usuários inexistentes;
- Não deve ser possível criar um usuário com e-mail já utilizado;
- Um novo usuário deverá ser criado caso não seja encontrado ID existente informado no PUT;
- Não deve ser possível cadastrar usuário com e-mail já utilizado;
- Não deverá ser possível cadastrar usuários com e-mails Gmail e HOTmail;
- E-mails devem seguir um padrão válido de e-mail para o cadastro;
- As senhas devem possuír no mínimo 5 caracteres e no máximo 10 caracteres;

###  **Casos de Teste**
- CT_001: Buscar usuários;
- CT_002: Buscar um usuário com ID existente;
- CT_003: Buscar um usuário com ID inexistente;
- CT_004: Criar um usuário preenchendo TODOS os campos (nome, email, senha e administrador);
- CT_005: Criar um usuário deixando ALGUNS/TODOS dos campos em branco;
- CT_006: Criar um usuário utilizando caracteres inválidos (especiais) em ALGUNS/TODOS os campos;
- CT_007: Criar um usuário com email já cadastrado;
- CT_008: Criar um usuário com email inválido (Hotmail);
- CT_009: Criar um usuário com email inválido (Gmail);
- CT_010: Criar um usuário com email sem o '@', para verificar o padrão válido de email;
- CT_011: Criar um usuário com senha inválida. (senha < 5 caracteres);
- CT_012: Criar um usuário com senha inválida. (senha > 10 caracteres);
- CT_013: Criar um usuário com senha inválida. (senha em branco);
- CT_014: Alterar dados de um usuário com o ID dele.
- CT_015: Alterar dados de um usuário com ID inexistente.
- CT_016: Alterar ALGUNS/TODOS os dados (nome, email, senha, administrador) válidos por um campo em branco;
- CT_017: Alterar ALGUNS/TODOS os dados válidos por caracteres inválidos (especiais);
- CT_018: Alterar um email válido para um padrão inválido (Hotmail/Gmail);
- CT_019: Alterar email de um cadastro por um email já utilizado;
- CT_020: Alterar email de um cadastro por um email sem o '@';
- CT_021: Alterar senha para um padrão inválido (5 > senha > 10, em branco)
- CT_022: Alterar um usuário com email válido por email não existente, para verificar cadastro de conta automático;
- CT_023: Excluir um usuário com ID existente;
- CT_024: Excluir um usuário com ID inexistente;
- CT_025: Excluir um usuário COM/SEM produtos no carrinho;
- CT_026: Excluir um usuário previamente excluído;

### **Testes Candidatos à Automação**
- Todos.


## 4.2 **🔑 [/Login]**


### **DoR e DoD**
- **DoR**
    - Banco de dados e infraestrutura para desenvolvimento disponibilizados;
    - API de cadastro de usuários implementada;
    - Ambiente de testes disponibilizado.

- **DoD**
    - Autenticação com geração de token Bearer implementada;
    - Análise de testes cobrindo a rota de login;
    - Matriz de rastreabilidade atualizada;
    - Automação de testes baseado na análise realizada;

###  **Critérios**
- Usuários não cadastrados não deverão conseguir autenticar;
- Usuários com senha inválida não deverão conseguir autenticar;
- No caso de não autenticação, deverá ser retornado um status code 401 (Unauthorized);
- Usuários existentes e com a senha correta deverão ser autenticados;
- A autenticação deverá gerar um token Bearer;
- A duração da validade do token deverá ser de 10 minutos;

###  **Casos de Teste**
- CT_001: Logar com email e senha corretos (já cadastrado);
- CT_002: Logar com email e senha corretos, mas com o mesmo email em CAPSLOCK;
- CT_003: Logar com email e senha corretos, mas com a mesma senha em CAPSLOCK;
- CT_004: Logar com email e senha inválidos (não cadastrados);
- CT_005: Logar com email correto, mas com senha incorreta;
- CT_006: Logar com email incorreto, mas com senha correta;
- CT_007: Logar com o(s) campo(s) 'email' e 'senha' em branco;
- CT_008: Após sucesso ao autenticar, verificar se foi gerado o Token de acesso;
- CT_009: Verificar se o token Bearer tem duração prevista para 10 minutos;
- CT_010: Verificar o recebimento de Status Code "401 Unauthorized" em caso de não autenticação;
- CT_011: Verificar o recebimento de Status Code "401 Unauthorized" após a expiração do token Bearer;


###  **Testes Candidatos à Automação**
- Todos.

## 4.3 **📦 [/Produtos]**

### **DoR e DoD**
- **DoR**
    - Banco de dados e infraestrutura para desenvolvimento disponibilizados;
    - API de cadastro de usuários implementada;
    - API de autenticação implementada;
    - Ambiente de testes disponibilizado.

- **DoD**
    - CRUD de cadastro de Produtos implementado (CRIAR, ATUALIZAR, LISTAR E DELETAR);
    - Análise de testes cobrindo a rota de produtos;
    - Matriz de rastreabilidade atualizada;
    - Automação de testes baseado na análise realizada;

### **Critérios**
- Usuários não autenticados não devem conseguir realizar ações na rota Produtos;
- Não deve ser possível realizar o cadastro de produtos com nomes já utilizados;
- Caso não exista produto com o o ID informado na hora do UPDATE, um novo produto deverá ser criado;
- Produtos criados através do PUT não poderão ter nomes previamente cadastrados;
- Não deve ser possível excluir produtos que estão dentro de carrinhos (dependência API Carrinhos);

### **Casos de Teste**
- CT_001: Acessar a aba 'Produtos' ou 'Produto/{_id} como usuário não autenticado;
- CT_002: Listar produtos;
- CT_003: Listar produto por ID existente;
- CT_004: Listar produto por ID inexistente;
- CT_005: Cadastrar um produto com os campos 'nome', 'preço', 'quantidade' vazios;
- CT_006: Cadastrar um produto com nome já utilizado por outro produto;
- CT_007: Cadastrar produto após com token Bearer, ausente, inválido ou expirado;
- CT_008: Cadastrar um produto com os campos 'nome', 'preço', 'quantidade' com espaço branco;
- CT_009: Alterar o nome, preço, descrição e quantidade de um produto.
- CT_010: Alterar o nome de um produto por outro com nome já utilizado;
- CT_011: Cadastrar um produto com preço 0 ou negativo.
- CT_012: Excluir um produto com ID existente;
- CT_013: Excluir um produto com ID inexistente;
- CT_014: Excluir um produto com com token Bearer, ausente, inválido ou expirado;
- CT_015: Alterar o nome de um produto com token Bearer, ausente, inválido ou expirado;
- CT_016: Cadastrar um produto com informações válidas.


### **Testes Candidatos à Automação**
- Todos.

## 4.4 **🛒 [/Carrinho]**

### **DoR e DoD**
- **DoR**
    - Banco de dados e infraestrutura para desenvolvimento disponibilizados;
    - API de cadastro de usuários implementada;
    - API de autenticação implementada;
    - API de menu de produtos implementada;
    - Ambiente de testes disponibilizado.

- **DoD**
    - CRUD de criação de carrinhos implementado (CRIAR, ATUALIZAR,  LISTAR E DELETAR);
    - Análise de testes cobrindo a rota de carrinho;
    - Matriz de rastreabilidade atualizada;
    - Automação de testes baseado na análise realizada;

### **Critérios**
- Usuários não cadastrados não deverão conseguir realizar ações na rota Carrinho;
- Valor total do carrinho é atualizado automaticamente ao adicionar/excluir/alterar a quantidade de produtos;
- Não é possível adicionar produto não existente no carrinho;
- Não é possível ter mais de um carrinho por usuário
- Não é possível adicionar a quantidade de um produto maior do que a do estoque;
- Não é possível excluir carrinhos com produtos;
- Sistema deve verificar se o produto ainda está em estoque, bem como sua quantidade, ao ser colocado em um carrinho;
- Itens poderão ser removidos e ter sua quantidade alterada no carrinho;
- Carrinho deve ser vinculado ao token;
- Após a conclusão de compra, o carrinho deve ser excluído;
- Após o cancelamento da compra, o estoque deve ser reabastecidos com a mesma quantidade de produtos que saiu;

### **Casos de Teste**
- CT_001: Listar carrinhos;
- CT_002: Listar carrinho por ID existente;
- CT_003: Listar carrinho por ID inexistente;
- CT_004: Cadastrar um produto no carrinho sem estar autenticado;
- CT_005: Cadastrar um produto no carrinho;
- CT_006: Cadastrar um produto com quantidades maiores do que a do estoque no carrinho;
- CT_007: Cadastrar um produto inexistente no carrinho;
- CT_008: Cadastrar um produto com a quantidade 0;
- CT_009: Cadastrar um produto que já existe no carrinho.
- CT_010: Concluir uma compra sem estar autenticado;
- CT_011: Concluir uma compra;
- CT_012: Concluir uma compra com carrinho inexistente;
- CT_013: Cancelar uma compra sem estar autenticado;
- CT_014: Cancelar uma compra;
- CT_015: Cancelaruma compra com carrinho inexistente;

### **Testes Candidatos à Automação**
- Todos.

# 5. Local dos Testes
Todos os testes foram criados e testados localmente em meu computador pessoal pelo endereço: http://localhost:3000, o qual emula o ambiente da API.

# 6. Recursos Necessários
- Infraestrutura:
    - Hardware: computador;
    - Internet.

- Softwares:
	- Eclipse (Java IDE);
    - RestAssured;
    - JUnit;
    - Bibliotecas Maven;
	- Swagger;
	- Node.js.

## 7. Cronograma
| Tipo de Teste      | Data de Início  | Data de Término  |
| ------------------ | --------------- | ---------------- |
| Planejamento       | 03/07/2023      | 07/07/2023
| Execução           | 10/07/2023      | 14/07/2023       |
| Avaliação          | 16/07/2023      | 16/07/2023       |

