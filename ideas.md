Okay, baseado no PRD, aqui está uma sugestão para a estrutura de pastas e arquivos do projeto Java, juntamente com uma ordem inicial para o desenvolvimento e testes:

### Estrutura de Pastas e Arquivos (Sugestão)

Assumindo um projeto Java padrão (que pode ser gerenciado por Maven ou Gradle, opcionalmente), a estrutura dentro do seu workspace `/home/rafaelfleury/Documentos/GitHub/BatalhaNaval` poderia ser:

```
BatalhaNavalRede/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── yourdomain/  # Ou o nome do seu grupo/organização
│   │   │           └── batalhanaval/
│   │   │               ├── Main.java           # Ponto de entrada, talvez para escolher Cliente/Servidor
│   │   │               ├── Constants.java      # Constantes do jogo (tamanho tabuleiro, portas)
│   │   │               ├── core/               # Lógica central do jogo (independente de rede/UI)
│   │   │               │   ├── Board.java
│   │   │               │   ├── Ship.java
│   │   │               │   ├── Position.java
│   │   │               │   └── GameState.java    # Gerencia estado, turnos, regras
│   │   │               ├── network/            # Código relacionado à rede
│   │   │               │   ├── NetworkManager.java # Classe principal para gerenciar conexões
│   │   │               │   ├── TCPClient.java    # Lógica específica do cliente TCP
│   │   │               │   ├── TCPServer.java    # Lógica específica do servidor TCP
│   │   │               │   ├── UDPListener.java  # Lógica para escuta UDP
│   │   │               │   └── Protocol.java     # (Opcional) Definições de mensagens/comandos
│   │   │               └── ui/                 # Componentes da Interface Gráfica (Swing)
│   │   │                   ├── MainWindow.java     # Janela principal
│   │   │                   ├── BoardPanel.java     # Painel para desenhar o tabuleiro
│   │   │                   ├── SetupPanel.java     # Painel para posicionar navios
│   │   │                   ├── GamePanel.java      # Painel durante o jogo (ataque)
│   │   │                   └── StatusLabel.java    # Componente para exibir mensagens
│   │   └── resources/          # Recursos não-código (imagens, etc.)
│   └── test/
│       └── java/
│           └── com/
│               └── yourdomain/
│                   └── batalhanaval/
│                       ├── core/
│                       │   └── BoardTest.java  # Exemplo de teste unitário
│                       └── network/
│                           └── ...             # Testes de rede (mais complexos)
├── network_simulation/
│   └── BatalhaNavalTopology.pkt # Arquivo do Cisco Packet Tracer
├── docs/
│   └── prd.md                 # O documento de requisitos que você forneceu
├── .gitignore                 # Para ignorar arquivos gerados (build, logs, IDE configs)
└── README.md                  # Instruções básicas de como compilar/rodar
```

**Explicação:**

*   **`src/main/java`**: Contém o código-fonte principal da aplicação.
*   **`com.yourdomain.batalhanaval`**: Pacote base. Substitua `yourdomain` por um nome apropriado.
*   **`core`**: Isola a lógica pura do jogo (tabuleiros, navios, regras). Isso facilita testes unitários e reutilização.
*   **`network`**: Agrupa toda a lógica de comunicação (TCP, UDP, Cliente, Servidor).
*   **`ui`**: Contém todas as classes relacionadas à interface gráfica Swing.
*   **`src/main/resources`**: Para arquivos que não são código Java, como ícones ou imagens para a UI.
*   **`src/test/java`**: Contém os testes unitários e de integração. A estrutura de pacotes aqui deve espelhar a de `src/main/java`.
*   **`network_simulation`**: Diretório para guardar o arquivo do Packet Tracer, mantendo-o junto ao projeto.
*   **`docs`**: Para documentação, incluindo o PRD.
*   **`README.md` e `.gitignore`**: Arquivos padrão de boas práticas em projetos de software.

### Ordem Sugerida para Desenvolvimento Inicial e Testes

Seguindo a sugestão do próprio PRD e focando em construir incrementalmente:

1.  **[CORE] Lógica do Jogo Local:**
    *   **Código:** Implementar as classes no pacote `core` (`Board.java`, `Ship.java`, `Position.java`, `GameState.java`). Foque nas regras: adicionar navios, validar posições, registrar ataques (acerto/erro/afundou), verificar condição de vitória/derrota.
    *   **Teste:** Criar testes unitários em `src/test/java/.../core/` para validar cada parte dessa lógica. Use `JUnit` ou similar. Teste exaustivamente as regras do jogo *sem* se preocupar com UI ou rede ainda.

2.  **[UI] Interface Swing Básica:**
    *   **Código:** Implementar as classes básicas da UI no pacote `ui` (`MainWindow.java`, `BoardPanel.java`). Faça uma janela que consiga *desenhar* um tabuleiro vazio e talvez exibir mensagens de status simples.
    *   **Teste:** Teste visualmente. Veja se a janela aparece, se o tabuleiro é desenhado corretamente.

3.  **[CORE + UI] Integração Local (Posicionamento):**
    *   **Código:** Conectar a UI (`SetupPanel.java`, `BoardPanel.java`) com a lógica do `core` (`Board.java`). Permita que o usuário clique no `BoardPanel` para posicionar os navios definidos no `core`. Adicione feedback visual.
    *   **Teste:** Teste interativamente. Posicione navios, veja se as regras de posicionamento (não sobrepor, dentro dos limites) estão funcionando e se a UI reflete o estado do objeto `Board`.

4.  **[NETWORK] Comunicação TCP Básica (Localhost):**
    *   **Código:** Implementar `TCPServer.java` e `TCPClient.java` (ou uma classe `NetworkManager` que os controle) para estabelecer uma conexão TCP simples em `localhost`. Envie e receba uma string "PING" / "PONG" para verificar se a conexão funciona. Use `ServerSocket` e `Socket`.
    *   **Teste:** Rode duas instâncias da sua aplicação (uma como servidor, uma como cliente) na mesma máquina. Verifique no console ou logs se a conexão é estabelecida e as mensagens são trocadas.

5.  **[CORE + UI + NETWORK] Integração Inicial (Ataque via TCP):**
    *   **Código:**
        *   Modifique a UI (`GamePanel.java`) para permitir clicar no tabuleiro do oponente para atacar.
        *   O Cliente envia as coordenadas do ataque via TCP.
        *   O Servidor recebe as coordenadas, usa o `GameState` para processar o ataque no tabuleiro do oponente (que está no servidor).
        *   O Servidor envia a resposta ("Hit", "Miss", "Sunk") via TCP.
        *   O Cliente recebe a resposta e atualiza sua UI (`BoardPanel` do oponente, `StatusLabel`).
    *   **Teste:** Rode duas instâncias em `localhost`. Jogue uma partida simples. Verifique se os ataques são registrados corretamente em ambos os lados e se o estado do jogo é consistente.

**Próximos Passos (Após a base funcionar):**

*   Implementar Multithreading (essencial para a UI não travar com a rede).
*   Adicionar comunicação UDP para notificações.
*   Implementar a lógica completa do Servidor atuando também como Jogador.
*   Configurar e testar na rede simulada do Packet Tracer.
*   Análise com Wireshark.

Essa abordagem incremental, começando pelo núcleo lógico e adicionando camadas (UI, Rede) progressivamente, com testes em cada etapa, ajuda a gerenciar a complexidade do projeto.
