# Guia Multiplayer - Batalha Naval

## 🎮 Como Jogar Multiplayer

O jogo agora possui um sistema multiplayer completo com comunicação TCP/UDP em tempo real!

### 🚀 Iniciando uma Partida Multiplayer

#### 1. **Jogador Host (Servidor)**
1. Execute o jogo: `mvn exec:java -Dexec.mainClass="com.batalhanaval.App"`
2. No menu principal, clique em **"Online Game"**
3. Clique em **"Create Game (Host)"**
4. Aguarde a mensagem "Server started! You can now proceed to ship setup."
5. Clique em **"Proceed to Ship Setup"**
6. Posicione seus navios no tabuleiro
7. Clique em **"Start Game"** quando terminar

#### 2. **Jogador Cliente**
1. Execute o jogo em outro computador/terminal
2. No menu principal, clique em **"Online Game"**
3. Digite o IP do host no campo "Server IP"
4. Clique em **"Join Game"**
5. Aguarde a mensagem "Connected to server! You can now proceed to ship setup."
6. Clique em **"Proceed to Ship Setup"**
7. Posicione seus navios no tabuleiro
8. Clique em **"Start Game"** quando terminar

### 🎯 Jogabilidade Multiplayer

#### **Mecânicas do Jogo**
- **Turnos alternados**: O servidor sempre começa primeiro
- **Ataques em tempo real**: Clique no tabuleiro do oponente para atacar
- **Feedback visual**: Acertos (vermelho), erros (azul), navios afundados (cinza)
- **Hover em tempo real**: Veja onde o oponente está mirando (laranja)
- **Placar dinâmico**: Pontuação atualizada em tempo real

#### **Sistema de Pontuação**
- **1 ponto** por acerto
- **5 pontos** por afundar um navio
- **50 pontos** por vitória total
- O jogador com maior pontuação aparece em **dourado**

#### **Controles Durante o Jogo**
- **Clique esquerdo**: Atacar posição no tabuleiro inimigo
- **Hover**: Mover o mouse sobre o tabuleiro para mostrar mira ao oponente
- **Surrender**: Botão para se render (disponível apenas no seu turno)

### 🌐 Configuração de Rede

#### **Portas Utilizadas**
- **TCP 6969**: Comunicação principal do jogo (ataques, resultados)
- **UDP 6970**: Comunicação em tempo real (hover, ping/pong)

#### **Firewall e Roteador**
Para jogar entre redes diferentes:
1. **Libere as portas** 6969 (TCP) e 6970 (UDP) no firewall
2. **Configure port forwarding** no roteador do host:
   - TCP 6969 → IP interno do host
   - UDP 6970 → IP interno do host

#### **Teste de Conexão**
- Use o botão **"Test UDP Message"** para verificar a comunicação
- Mensagens de status mostram o progresso da conexão

### 🔧 Protocolo de Comunicação

#### **Mensagens TCP (Jogo Principal)**
- `ATTACK:row,col` - Enviar ataque
- `ATTACK_RESULT:HIT:row,col` - Resultado do ataque
- `READY_TO_START` - Jogador pronto
- `GAME_START:true/false` - Iniciar jogo (primeiro jogador?)
- `GAME_OVER:true/false` - Fim de jogo (vencedor?)
- `SURRENDER` - Rendição
- `DISCONNECT` - Desconexão

#### **Mensagens UDP (Tempo Real)**
- `HOVER:row,col` - Posição do mouse
- `PING` / `PONG` - Teste de latência

### 🎨 Interface Visual

#### **Cores dos Tabuleiros**
- **Verde**: Seus navios (modo debug)
- **Amarelo transparente**: Sua mira
- **Laranja transparente**: Mira do oponente
- **Vermelho**: Acertos
- **Azul**: Erros
- **Cinza**: Navios afundados

#### **Placar em Tempo Real**
- **YOU**: Sua pontuação e navios restantes
- **OPPONENT**: Pontuação do oponente
- **Dourado**: Jogador na liderança

### 🐛 Solução de Problemas

#### **Problemas de Conexão**
1. **Verifique o firewall**: Libere as portas 6969 e 6970
2. **Teste local primeiro**: Use "localhost" como IP
3. **Verifique a rede**: Ambos devem estar na mesma rede ou com port forwarding

#### **Problemas de Sincronização**
1. **Aguarde as mensagens**: "Ready signal sent. Waiting for opponent..."
2. **Não clique rapidamente**: Aguarde a resposta do oponente
3. **Reconecte se necessário**: Feche e abra o jogo novamente

#### **Problemas de Performance**
1. **Feche outros programas**: Para melhor performance de rede
2. **Use conexão cabeada**: Para menor latência
3. **Verifique a qualidade da internet**: Ping alto pode causar delays

### 🏆 Estratégias Multiplayer

#### **Dicas de Posicionamento**
- **Espalhe os navios**: Não agrupe todos em uma área
- **Use as bordas**: Navios nas bordas são mais difíceis de encontrar
- **Varie o padrão**: Não use sempre horizontal ou vertical

#### **Dicas de Ataque**
- **Observe o hover**: Veja onde o oponente está olhando
- **Ataque sistemático**: Use padrões de busca eficientes
- **Aproveite acertos**: Continue atacando ao redor de acertos

### 🔄 Fluxo Completo do Jogo

1. **Conexão**: Host cria → Cliente conecta
2. **Setup**: Ambos posicionam navios
3. **Início**: Servidor determina quem começa
4. **Batalha**: Turnos alternados até vitória
5. **Fim**: Placar final e opção de nova partida

---

## 🎉 Recursos Implementados

✅ **Multiplayer TCP/UDP completo**  
✅ **Hover em tempo real**  
✅ **Placar dinâmico**  
✅ **Turnos sincronizados**  
✅ **Protocolo robusto de comunicação**  
✅ **Interface visual moderna**  
✅ **Sistema de pontuação**  
✅ **Detecção de vitória/derrota**  
✅ **Suporte a rendição**  
✅ **Feedback visual completo**

---

**Divirta-se jogando Batalha Naval Multiplayer! 🚢⚓** 