# Implementação de Hover para Batalha Naval

## Funcionalidades Implementadas

As funções solicitadas foram implementadas e adaptadas para funcionar com hover nos quadrados dos tabuleiros em vez da posição global do mouse. Isso é mais adequado para o sistema UDP que transmitirá as posições de "mira" entre jogadores.

## Classes Criadas

### 1. **GameInfoManager**
Localizada em: `src/main/java/com/batalhanaval/core/GameInfoManager.java`

**Principais funcionalidades:**

#### **Pontuação**
- `getScores()` - Retorna pontuação de ambos os jogadores
- Sistema de pontuação: 1 ponto por acerto, 5 pontos por navio afundado, 50 pontos por vitória

#### **Posição do Mouse/Hover**
- `setPlayerBoardHover(Position)` - Define hover no tabuleiro do jogador
- `setOpponentBoardHover(Position)` - Define hover no tabuleiro do oponente
- `getPlayerBoardHover()` - Obtém hover atual no tabuleiro do jogador
- `getOpponentBoardHover()` - Obtém hover atual no tabuleiro do oponente (para transmitir via UDP)
- `getAllHoverPositions()` - Obtém todos os hovers
- `getGlobalMousePosition()` - Posição global do mouse (para debug)

#### **Posições dos Navios**
- `getPlayerShipPositions()` - Navios do jogador
- `getOpponentShipPositions()` - Navios do oponente
- `getAllShipPositions()` - Navios de ambos os times

#### **Transmissão UDP**
- `formatHoverForTransmission(Position)` - Formata hover para envio UDP ("row,col")
- `parseHoverFromTransmission(String)` - Parseia hover recebido via UDP

### 2. **HoverManager**
Localizada em: `src/main/java/com/batalhanaval/core/HoverManager.java`

**Funcionalidades:**
- Conecta `BoardPanel`s com `GameInfoManager`
- Gerencia hovers de ambos os tabuleiros
- Processa dados UDP de hover
- Envia hovers via UDP (implementação placeholder)

### 3. **BoardPanel (Modificado)**
Localizada em: `src/main/java/com/batalhanaval/ui/BoardPanel.java`

**Novas funcionalidades:**
- Tracking de hover do mouse nos quadrados
- Visualização de hover próprio (amarelo transparente)
- Visualização de hover do oponente (laranja transparente)
- Handlers para eventos de hover

### 4. **GameInfoExample**
Localizada em: `src/main/java/com/batalhanaval/core/GameInfoExample.java`

**Demonstrações:**
- Como obter pontuações
- Como rastrear hovers
- Como trabalhar com posições dos navios
- Métodos simplificados para acesso rápido

## Como Usar

### Configuração Básica

```java
// 1. Criar o GameInfoManager
GameState gameState = new GameState(10);
GameInfoManager gameInfoManager = new GameInfoManager(gameState);

// 2. Criar o HoverManager
HoverManager hoverManager = new HoverManager(gameInfoManager);

// 3. Conectar os BoardPanels
hoverManager.setPlayerBoardPanel(playerBoardPanel);
hoverManager.setOpponentBoardPanel(opponentBoardPanel);
```

### Obter Informações

```java
// Pontuações
Map<String, Integer> scores = gameInfoManager.getScores();
int playerScore = scores.get("player");
int opponentScore = scores.get("opponent");

// Hover atual (para transmitir via UDP)
Position hover = gameInfoManager.getOpponentBoardHover();
String hoverForUDP = gameInfoManager.formatHoverForTransmission(hover);

// Posições dos navios
List<GameInfoManager.ShipInfo> playerShips = gameInfoManager.getPlayerShipPositions();
List<GameInfoManager.ShipInfo> opponentShips = gameInfoManager.getOpponentShipPositions();
```

### Transmissão UDP (Placeholder)

```java
// Quando o jogador faz hover no tabuleiro inimigo
String hoverData = hoverManager.getHoverForTransmission();
// Enviar via UDP: "HOVER:" + hoverData (ex: "HOVER:3,5")

// Quando recebe dados UDP do oponente
String receivedData = "7,2"; // dados recebidos via UDP
hoverManager.processReceivedHover(receivedData);
```

## Efeitos Visuais

### Cores de Hover
- **Amarelo transparente**: Hover do jogador atual
- **Laranja transparente**: Hover do oponente (recebido via UDP)

### Onde Aparecem
- Hover próprio: aparece em ambos os tabuleiros quando você move o mouse
- Hover do oponente: aparece apenas no SEU tabuleiro mostrando onde o OPONENTE está mirando

## Fluxo UDP Planejado

1. **Jogador A** move o mouse sobre o tabuleiro do **Jogador B**
2. `BoardPanel` detecta hover e chama `HoverManager`
3. `HoverManager` formata a posição e envia via UDP
4. **Jogador B** recebe a posição via UDP
5. `HoverManager` do **Jogador B** processa e atualiza o `BoardPanel`
6. **Jogador B** vê onde **Jogador A** está "mirando" em laranja

## Métodos Simplificados

Para acesso rápido, há métodos que retornam arrays simples:

```java
GameInfoExample example = new GameInfoExample(gameState);

// Pontuações: [playerScore, opponentScore]
int[] scores = example.getSimpleScores();

// Hover no tabuleiro oponente: [row, col] ou null
int[] opponentHover = example.getSimpleOpponentBoardHover();

// Hover no tabuleiro próprio: [row, col] ou null
int[] playerHover = example.getSimplePlayerBoardHover();

// Resumo dos navios
String[] shipSummaries = example.getShipSummaries();
```

## Próximos Passos

1. **Integrar com NetworkManager**: Conectar o `HoverManager` com a classe que gerencia UDP
2. **Implementar sending**: Substituir o placeholder `sendHoverToOpponent()` por envio UDP real
3. **Integrar com GamePanel**: Conectar o sistema de hover com o painel principal do jogo
4. **Throttling**: Implementar throttling para não enviar muitos pacotes UDP por segundo
5. **Cleanup**: Limpar hovers quando jogador desconecta

## Estrutura dos Dados UDP

**Formato sugerido:**
- `"HOVER:3,5"` - Jogador está fazendo hover na posição (3,5)
- `"HOVER:null"` - Jogador não está fazendo hover em lugar nenhum

As funções de formatação e parsing já estão implementadas no `GameInfoManager`. 