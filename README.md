# Sobre o Projeto

ParkSys e um sistema de gestao de estacionamento desenvolvido em Java para a disciplina ARQDEOO. O projeto permite registrar entradas e saidas de veiculos, controlar vagas por status, calcular tarifas conforme o tipo de veiculo, persistir dados em arquivo serializado, exportar relatorios em texto e demonstrar o uso de multithreading.

O estacionamento possui 30 vagas, distribuidas nas fileiras A e B, numeradas de 01 a 15. Cada vaga e identificada por codigos como A01, A02 e B15.

## Tecnologias

- Java
- Swing, previsto para a etapa de interface grafica
- Collections Framework
- Serializacao com `ObjectOutputStream` e `ObjectInputStream`
- Threads com `Thread`, `Runnable`, `synchronized` e daemon
- IDE utilizada: VSCode e IntelliJ

Para compatibilidade com Java 8, o projeto pode ser compilado com:

```bash
javac --release 8 -d out src\parksys\enums\*.java src\parksys\entities\*.java src\parksys\services\*.java src\parksys\main\*.java
```

## Estrutura de Pacotes

- `parksys.main`: ponto de entrada da aplicacao, com `Principal`.
- `parksys.entities`: entidades de dominio, como `Vaga`, `Veiculo`, `Registro` e `Mensalista`.
- `parksys.enums`: enumeracoes de negocio, como `TipoVeiculo` e `StatusVaga`.
- `parksys.exceptions`: reservado para excecoes personalizadas do dominio.
- `parksys.services`: regras de negocio, persistencia, serializacao e tarefas concorrentes.
- `parksys.observer`: reservado para a implementacao do padrao Observer.
- `parksys.ui`: reservado para as telas Swing.

## Como Executar

Compile o projeto:

```bash
javac --release 8 -d out src\parksys\enums\*.java src\parksys\entities\*.java src\parksys\services\*.java src\parksys\main\*.java
```

Execute a classe principal:

```bash
java -cp out parksys.main.Principal
```

A execucao atual demonstra o processamento concorrente de entradas, o monitoramento de vagas e o comportamento do campo `threadOrigem` apos a desserializacao.

## Conceitos Aplicados

- Enums: `TipoVeiculo` armazena nome legivel, tarifa por hora e quantidade de vagas ocupadas. `StatusVaga` armazena descricao e disponibilidade.
- Collections: `GerenciadorEstacionamento` usa `HashMap<String, Vaga>` para vagas, `ArrayList<Registro>` para registros, `LinkedList<Mensalista>` para mensalistas e `TreeSet<Registro>` para ordenacao cronologica.
- Serializacao: `GerenciadorArquivo` serializa e desserializa dados do ParkSys usando `DadosParkSys` como objeto auxiliar.
- Arquivos: o sistema exporta relatorios em `.txt` com `BufferedWriter`.
- Multithreading: `EntradaRunnable` processa entradas em threads separadas, `MonitorRunnable` monitora as vagas e `GerenciadorEstacionamento` usa `synchronized` para evitar race condition.
- Campos `transient`: `Registro.threadOrigem` nao e persistido; apos a desserializacao, o campo retorna como `null`.

## Branches

- `main`: setup inicial, merges das funcionalidades concluidas e estrutura base do projeto.
- `feature/enums`: implementacao de `TipoVeiculo` e `StatusVaga`; uso dos enums em regras de tarifa e ocupacao de vagas.
- `feature/entities`: criacao das entidades serializaveis `Vaga`, `Veiculo`, `Registro` e `Mensalista`.
- `feature/services`: implementacao de `GerenciadorEstacionamento`, Collections e regras de negocio.
- `feature/serializacao`: implementacao de `GerenciadorArquivo` e `DadosParkSys` para serializacao, desserializacao e exportacao de relatorios.
- `feature/threads`: implementacao de `EntradaRunnable`, `MonitorRunnable`, sincronizacao e demonstracao de threads no `Principal`.
- `feature/patterns`: pendente; sera usada para Singleton, Observer e MVC.
- `feature/ui`: pendente; sera usada para as telas Swing e integracao final.

## Requisitos Implementados Ate o Momento

- T01, T02, T03, T04
- C01, C02, C03, C04, C05
- S01, S02, S03, S04, S05
- M01, M02, M03, M04, M05, M06, M07

## Requisitos Pendentes

- T05: preencher ComboBox com `TipoVeiculo.values()` em `TelaRegistroEntrada`.
- C06: exibir relatorio em `TelaRelatorio` usando `entrySet()` e for-each.
- S06: desserializar ao iniciar a aplicacao e serializar no `windowClosing()`.
- P01 a P06: Singleton, Observer, MVC e `PainelMonitor`.
- Telas Swing e integracao final da interface grafica.

## Autor(es)

- Ellen Pinheiro Goncalves - Turma SUP.14324 (ARQDEOO)
- Ariane Minguini Sanga - Turma SUP.14324 (ARQDEOO)
