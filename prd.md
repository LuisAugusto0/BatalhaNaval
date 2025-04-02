# **Product Requirements Document – Batalha Naval em Rede**

### **Visão Geral**
Desenvolver um jogo multiplayer de Batalha Naval com comunicação TCP e UDP, interface em Java Swing e roteamento IP entre redes distintas, conforme especificado no projeto.

---

### **Objetivo**
Criar uma aplicação interativa de rede que simula o jogo Batalha Naval entre dois jogadores, aplicando conceitos de redes de computadores, sockets TCP/UDP, multithreading, roteamento IP e interface gráfica.

---

### **Componentes Principais**
- Aplicação em Java com:
  - Interface gráfica (Swing)
  - Cliente (entrar na sala)
  - Servidor (criar sala)
  - Comunicação TCP e UDP
  - Multithreading
- Rede com roteadores e IPs distintos (Cisco Packet Tracer)
- Análise de tráfego com Wireshark

---

### **Funcionalidades Obrigatórias (Checklist)**

#### **[~] Interface Java Swing (Cliente e Servidor)**
- [x] Tela para posicionar navios
- [x] Tela para atacar o oponente
- [x] Indicador de turno e mensagens de status
- [x] Botões e feedback visual para jogadas e resultados

#### **[ ] TCP (Jogo)**
- [ ] Cliente envia jogadas
- [ ] Servidor processa e responde ("Hit", "Miss", "Sunk")
- [ ] Sincronização de turnos entre os jogadores
- [ ] Mensagens de início e fim de partida

#### **[ ] UDP (Notificações)**
- [ ] Mensagens assíncronas (ex: desconexão, latência, tempo ocioso)
- [ ] Escuta em paralelo ao TCP sem bloquear o jogo

#### **[ ] Multithreading**
- [ ] Thread da interface gráfica
- [ ] Thread para escutar mensagens TCP
- [ ] Thread separada para escutar mensagens UDP
- [ ] No servidor: thread principal e threads por cliente (caso multi)

#### **[ ] Escolha dinâmica entre Cliente e Servidor**
- [ ] Jogador pode criar sala (torna-se servidor)
- [ ] Jogador pode entrar na sala (atua como cliente)
- [ ] O servidor obrigatoriamente roda no PC2 (R3)
- [ ] **Servidor também é um jogador completo**, com todas as funcionalidades de um cliente:
  - Posicionamento de navios
  - Interface gráfica
  - Jogadas de ataque
  - Recebimento de feedback
  - Além disso, controla lógica do jogo (validação de jogadas, turnos, estado da partida)

---

### **Rede e Infraestrutura (Cisco Packet Tracer)**

#### **[ ] Topologia da Rede**
- [ ] 3 Roteadores (R1, R2, R3)
- [ ] PC1 conectado ao R1 (cliente)
- [ ] PC2 conectado ao R3 (servidor)

#### **[ ] Faixas de IP**
- [ ] R1: 192.168.0.0/16
- [ ] R2: 172.16.0.0/12
- [ ] R3: 10.0.0.0/8

#### **[ ] Roteamento**
- [ ] Rotas estáticas entre os roteadores para garantir comunicação
- [ ] Testar conectividade entre PC1 e PC2

#### **[ ] Redirecionamento de Porta**
- [ ] No R3, redirecionar a porta TCP usada no jogo para o IP do PC2 (servidor)

---

### **Análise com Wireshark**
- [ ] Captura da conexão TCP entre cliente e servidor
- [ ] Captura da troca de jogadas
- [ ] Captura de pacotes UDP de notificação
- [ ] Comprovação de tráfego entre IPs de faixas distintas
- [ ] Destaque do funcionamento dos protocolos TCP e UDP

---

### **Etapas de Desenvolvimento (Sugestão)**
1. [x] Desenvolver lógica do jogo localmente (localhost)
2. [x] Criar interface Swing
3. [ ] Implementar comunicação TCP
4. [ ] Adicionar comunicação UDP
5. [ ] Implementar multithreading
6. [ ] Testar funcionalidade em rede local (LAN)
7. [ ] Reproduzir a rede no Cisco Packet Tracer
8. [ ] Configurar IPs, roteadores e redirecionamento
9. [ ] Validar com Wireshark
10. [ ] Montar relatório, vídeo/apresentação

---

### **Entregas**
- [x] Código-fonte Java (parcial - lógica do jogo e interface gráfica)
- [ ] Projeto da rede no Cisco Packet Tracer
- [ ] Relatório de desenvolvimento com:
  - Objetivo
  - Metodologia
  - Telas da aplicação
  - Telas de configuração da rede
  - Desafios enfrentados
- [ ] Capturas do Wireshark
- [ ] Vídeo assíncrono ou apresentação síncrona

---

### **Funcionalidades Implementadas na Interface Gráfica**
- [x] Tela de configuração (SetupPanel) para posicionar navios
- [x] Tela de jogo (GamePanel) para atacar o oponente
- [x] Simulação de oponente (jogador contra máquina)
- [x] Representação visual de acertos e erros (vermelho e azul)
- [x] Linhas de orientação em acertos para indicar direção do navio
- [x] Painel de status para visualizar condição dos navios
- [x] Modo de depuração (DEBUG_MODE) para visualizar navios inimigos
- [x] Sistema de turnos (jogador e oponente)
- [x] Verificação de condições de vitória/derrota
- [x] Botão de rendição com confirmação
