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
│   └── ui/                    # Interface gráfica
│       ├── MainWindow.java    # Janela principal
│       ├── GamePanel.java     # Painel de jogo
│       ├── BoardPanel.java    # Painel do tabuleiro
│       └── SetupPanel.java    # Painel de configuração
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

3. Execute o jogo com interface gráfica
   ```
   mvn clean compile exec:java -Dexec.mainClass="com.batalhanaval.App"
   ```

## Funcionalidades Implementadas
- [x] Lógica central do jogo (tabuleiros, navios, regras)
- [x] Testes unitários para a lógica central
- [x] Interface gráfica com Java Swing
  - [x] Tela de configuração para posicionar navios
  - [x] Tela de jogo para atacar o oponente
  - [x] Modo de depuração (visualização de navios inimigos)
  - [x] Indicadores visuais de acertos/erros
  - [x] Painel de status para visualizar condição dos navios
  - [x] Linhas de orientação em acertos para mostrar direção do navio
- [x] Simulação do oponente (jogo local sem rede)
- [ ] Comunicação em rede via TCP/UDP
- [ ] Multithreading para UI e comunicação
- [ ] Configuração em rede (Packet Tracer)

## Configuração do Modo de Depuração
Para ativar o modo de depuração e ver os navios do oponente em amarelo, altere a constante `DEBUG_MODE` para `true` na classe `App.java`:

```java
public static final boolean DEBUG_MODE = true;
```

## Próximos Passos
1. Implementar comunicação em rede usando TCP para jogadas e UDP para notificações
2. Adicionar suporte para multithreading
3. Configurar a rede no Cisco Packet Tracer
4. Analisar o tráfego com Wireshark
