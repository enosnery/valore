# Valore

Aplicação web Spring Boot para gestão inicial de usuários, fornecedores, produtos, tabelas de preço e cotações,
com controle de acesso por tela baseado em permissões cadastradas no banco.

## Permissões de tela

O acesso a cada tela (`/usuarios`, `/cotacoes`, `/fornecedores`, `/produtos`, `/tabelas-preco`, `/permissoes`)
é controlado pela tabela `permissoes_usuario`, que associa um `Usuario` a uma `Tela`. Não há papéis fixos no
código: um interceptor consulta o banco a cada requisição e redireciona para `/home` quem não tiver a
permissão correspondente. A tela `/permissoes` (gerenciada em `/permissoes`) permite atribuir telas a cada
usuário e, por sua vez, também é protegida pela mesma tabela de permissões (tela `PERMISSOES`).

O tipo do usuário (`COMPRADOR`/`FORNECEDOR`) continua existindo como regra de negócio — define quem pode criar
cotações e quem precisa estar vinculado a um fornecedor — mas não decide visibilidade de tela.

### Usuários de teste (seed automático)

| Login | Senha | Tipo | Fornecedor vinculado | Telas liberadas |
|---|---|---|---|---|
| `admin` | `admin` | COMPRADOR | — | Usuários, Permissões, Fornecedores, Produtos |
| `comprador1` | `comprador1` | COMPRADOR | — | Cotações |
| `fornecedor1` | `fornecedor1` | FORNECEDOR | Fornecedor Alpha Ltda. | Tabela de preço |

## Tabelas de preço

Cada fornecedor pode criar várias tabelas de preço (`/tabelas-preco`). Uma tabela é um cabeçalho (nome +
situação ativa/inativa) que agrupa itens (`produto`, `valor`, `validade do valor`). Apenas **uma tabela pode
estar ativa por fornecedor**: ao ativar uma tabela, as demais do mesmo fornecedor são desativadas
automaticamente.

Somente itens de tabelas **ativas** e com validade ainda vigente aparecem disponíveis para os compradores na
tela de cotação.

## Cotações

Usuários compradores podem acessar `/cotacoes/nova`. A cotação é criada com o
usuário logado e pode receber vários itens de tabelas de preço diferentes (de fornecedores distintos). O
mesmo item de tabela de preço só pode aparecer uma vez na mesma cotação.

Em `/cotacoes`, o comprador consulta suas cotações, abre uma cotação para
editar os itens, salva a cotação ou a exclui.

Os CRUDs de `/fornecedores` e `/produtos` permitem consultar, cadastrar,
editar e excluir registros. A exclusão é bloqueada quando existem usuários,
tabelas de preço ou itens de tabela de preço vinculados ao registro.

## Executar

Requisitos: Java 21 e Maven 3.9+.

```bash
mvn spring-boot:run
```

Acesse http://localhost:8080 e utilize um dos usuários de teste listados acima.

O banco H2 fica em memória e o console está disponível em http://localhost:8080/h2-console:

- JDBC URL: `jdbc:h2:mem:valore`
- Usuário: `sa`
- Senha: (vazia)

## Empacotar

```bash
mvn clean package
java -jar target/valore-0.0.1-SNAPSHOT.war
```

O empacotamento é WAR executável, portanto o arquivo gerado poderá também ser
implantado em um Tomcat externo. Para executar em container:

```bash
docker build -t valore .
docker run --rm -p 8080:8080 valore
```
