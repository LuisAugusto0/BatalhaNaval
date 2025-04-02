# BatalhaNaval
Um jogo de batalha naval em Java Swing com comunicação TCP/UDP e multithreading.

## Descrição
Este projeto implementa o clássico jogo de Batalha Naval com uma interface gráfica em Java Swing e comunicação em rede, permitindo que dois jogadores disputem partidas remotamente. O jogo utiliza comunicação TCP para as jogadas principais e UDP para notificações assíncronas, além de implementar conceitos de multithreading para gerenciar UI e comunicação de rede simultaneamente.

## Tecnologias Utilizadas
- Java 11+
- Java Swing (Interface gráfica)
- Sockets TCP/UDP (Comunicação em rede)
- Multithreading
- Maven (Gerenciamento de dependências)
- JUnit 5 (Testes unitários)

## Estrutura do Projeto
```
BatalhaNaval/
├── src/main/java/com/batalhanaval/
│   ├── Constants.java         # Constantes do jogo
│   ├── App.java               # Classe principal
│   ├── core/                  # Lógica do jogo
│   │   ├── Board.java         # Tabuleiro
│   │   ├── Ship.java          # Navio
│   │   ├── Position.java      # Posição no tabuleiro
│   │   └── GameState.java     # Estado do jogo
│   ├── network/               # Comunicação em rede (a implementar)
│   └── ui/                    # Interface gráfica (a implementar)
└── src/test/                  # Testes unitários
```

## Como Executar
1. Clone o repositório
   ```
   git clone https://github.com/seu-usuario/BatalhaNaval.git
   cd BatalhaNaval
   ```

2. Compile e execute os testes
   ```
   mvn clean test
   ```

3. Execute a demonstração da lógica do jogo (sem interface gráfica)
   ```
   mvn exec:java -Dexec.mainClass="com.batalhanaval.App"
   ```

## Funcionalidades Implementadas
- [x] Lógica central do jogo (tabuleiros, navios, regras)
- [x] Testes unitários para a lógica central
- [ ] Interface gráfica com Java Swing
- [ ] Comunicação em rede via TCP/UDP
- [ ] Multithreading para UI e comunicação
- [ ] Configuração em rede (Packet Tracer)
