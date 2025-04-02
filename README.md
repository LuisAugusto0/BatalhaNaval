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
│   ├── network/               # Comunicação em rede
│   │   └── NetworkManager.java # Gerenciador de conexões TCP/UDP
│   └── ui/                    # Interface gráfica
│       ├── MainWindow.java    # Janela principal
│       ├── MainMenuPanel.java # Painel de menu principal
│       ├── GamePanel.java     # Painel de jogo
│       ├── BoardPanel.java    # Painel do tabuleiro
│       ├── SetupPanel.java    # Painel de configuração de navios
│       ├── OnlineSetupPanel.java # Painel de configuração de rede
│       └── StatusLabel.java   # Componente para mensagens de status
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
  - [x] Menu principal com escolha de modo de jogo (IA ou Online)
  - [x] Tela de configuração para posicionar navios
  - [x] Tela de jogo para atacar o oponente
  - [x] Modo de depuração (visualização de navios inimigos)
  - [x] Indicadores visuais de acertos/erros
  - [x] Painel de status para visualizar condição dos navios
  - [x] Linhas de orientação em acertos para mostrar direção do navio
- [x] Simulação do oponente (jogo local contra IA)
- [x] Multithreading para UI e comunicação de rede (parcial)
  - [x] Thread da interface gráfica (EDT)
  - [x] Threads separadas para operações de rede bloqueantes
  - [x] Atualização segura da UI a partir de threads de rede
- [x] Comunicação em rede via TCP/UDP (parcial)
  - [x] Estabelecimento de conexão TCP cliente/servidor
  - [x] Configuração de portas UDP e troca de informações
  - [x] Envio e recebimento de mensagens UDP de teste
  - [ ] Protocolo completo de jogo via rede
- [ ] Configuração em rede (Packet Tracer)
- [ ] Análise de tráfego (Wireshark)

## Modos de Jogo
1. **Modo Single Player (vs IA)** - Jogue contra um oponente simulado com ataques aleatórios
2. **Modo Online** - Conecte-se a outro jogador:
   - Como **Host**: Crie um jogo e aguarde a conexão de outro jogador
   - Como **Cliente**: Conecte-se a um jogo existente informando o IP do host

## Configuração do Modo de Depuração
Para ativar o modo de depuração e ver os navios do oponente em amarelo, altere a constante `DEBUG_MODE` para `true` na classe `App.java`:

```java
public static final boolean DEBUG_MODE = true;
```

## Próximos Passos
1. Implementar a lógica completa de jogo em rede
   - Sincronização de turnos entre jogadores
   - Envio de ataques via TCP
   - Processamento dos resultados ("Hit", "Miss", "Sunk")
   - Detecção de fim de jogo
2. Implementar notificações via UDP
   - Mensagens de desconexão
   - Informações de latência
   - Notificações de tempo ocioso
3. Configurar a rede no Cisco Packet Tracer
4. Analisar o tráfego com Wireshark
