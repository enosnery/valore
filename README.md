# Valore

Aplicação web Spring Boot para gestão inicial de usuários, fornecedores, produtos, preços e cotações.

Usuários compradores podem acessar `/cotacoes/nova`. A cotação é criada com o
usuário logado e pode receber vários itens de tabelas de preço diferentes. A
mesma referência de tabela de preço só pode aparecer uma vez na mesma cotação.

Em `/cotacoes`, o comprador consulta suas cotações, abre uma cotação para
editar os itens, salva a cotação ou a exclui.

## Executar

Requisitos: Java 21 e Maven 3.9+.

```bash
mvn spring-boot:run
```

Acesse http://localhost:8080. Na primeira execução é criado um usuário inicial persistido:
`admin` / `admin`. Depois disso, o login consulta os usuários cadastrados no banco.

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
