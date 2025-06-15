package io.github.projectopa2;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;

enum GameState { // Estados do Jogo
    PLAYING,     // A jogar, utilizador tem controlo da nave
    GAME_END     // No menu, utilizador só tem controlo do rato
}

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch; // Usado para gerar imagens 2D no ecrã
    FitViewport viewport;

    // Texturas
    Texture backgroundTexture;
    Texture playerTextureR; // Texturas da nave do jogador
    Texture playerTextureL;
    Texture playerTextureU;
    Texture playerTextureD;
    Texture enemyTextureR; // Texturas do inimigo
    Texture enemyTextureL;
    Texture enemyTextureU;
    Texture enemyTextureD;
    Texture wallTexture;     //textura da parede
    Texture heartTexture;    //textura do coração das vidas
    Texture portalTexture;   //textura do portal
    Texture gameOverTexture; //textura do menu game-over
    Texture gameWonTexture;  //textura do menu game-won
    Texture treasureTexture; // Textura do tesouro
    Texture laserVerticalTexture; // Texturas do laser
    Texture laserHorizontalTexture;

    Sound enemydeathSound;
    Rectangle restartButtonBounds;

    Sprite playerSprite;
    Vector2 playerGridPosition;

    Array<Enemy> enemies;
    Array<Projectile> projectiles;
    Array<GameMap> maps;

    Vector2 lastMoveDirection = new Vector2(1, 0);

    final int MAP_WIDTH = 16;
    final int MAP_HEIGHT = 10;

    int playerLives = 3;
    int currentMapIndex = 0;
    GameState gameState = GameState.PLAYING;

    float moveDelay = 0.15f; // delay em segundos de cada movimento do jogador
    float moveTimer = 0f;    // timer para o movimento do jogador (É feito o reset após movimento)

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(MAP_WIDTH, MAP_HEIGHT);

        backgroundTexture = new Texture("background.png");
        playerTextureR = new Texture("spaceshipR.png");
        playerTextureL = new Texture("spaceshipL.png");
        playerTextureU = new Texture("spaceshipU.png");
        playerTextureD = new Texture("spaceshipD.png");
        enemyTextureR = new Texture("enemyR.png");
        enemyTextureL = new Texture("enemyL.png");
        enemyTextureU = new Texture("enemyU.png");
        enemyTextureD = new Texture("enemyD.png");
        wallTexture = new Texture("rock.png");
        heartTexture = new Texture("heart.png");
        portalTexture = new Texture("portal.png");
        gameOverTexture = new Texture("gameover.png");
        gameWonTexture = new Texture("gamewon.png");
        treasureTexture = new Texture("treasure.png");
        laserVerticalTexture = new Texture("laserVertical.png");
        laserHorizontalTexture = new Texture("laserHorizontal.png");

        enemydeathSound = Gdx.audio.newSound(Gdx.files.internal("enemydeath.mp3"));

        restartButtonBounds = new Rectangle(6f, 4f, 4f, 2f);

        playerSprite = new Sprite(playerTextureR);
        playerSprite.setSize(1, 1);
        playerGridPosition = new Vector2();

        projectiles = new Array<>();
        enemies = new Array<>();

        maps = new Array<>();
        maps.add(GameMap.generateFirstMap(enemyTextureL, MAP_WIDTH, MAP_HEIGHT));
        maps.add(GameMap.generateSecondMap(enemyTextureL, MAP_WIDTH, MAP_HEIGHT));
        maps.add(GameMap.generateThirdMap(enemyTextureL, MAP_WIDTH, MAP_HEIGHT));

        loadMap(0);
    }

    private void loadMap(int index) { //Carr
        currentMapIndex = index;            // Carrega o mapa colocado na função (0, 1 ou 2)
        GameMap current = maps.get(index);  // Coloca o mapa em questão como mapa corrente
        enemies = current.enemies;          // Carrega os inimigos do mapa
        playerGridPosition.set(current.spawnNode.x, current.spawnNode.y); // Atualiza a posição do jogador
        updatePlayerWorldPosition();        // Atualiza a posição da sprite do jogador
    }

    private void updatePlayerWorldPosition() { // Metodo para atualizar a posição da sprite do jogador no mapa
        playerSprite.setPosition(playerGridPosition.x, playerGridPosition.y);
    }

    private void PlayerInput() {

        if (moveTimer < moveDelay) return; // Se o moveTimer for menor que a delay, não permite movimento , garante que o movimento não é instantaneo

        Vector2 target = new Vector2(playerGridPosition); // Define o "target" como a posição atual
        boolean moved = false; // Define

    // Controlos de movimneto do Jogador
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            target.x += 1;
            lastMoveDirection.set(1, 0);
            playerSprite.setTexture(playerTextureR);
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            target.x -= 1;
            lastMoveDirection.set(-1, 0);
            playerSprite.setTexture(playerTextureL);
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            target.y += 1;
            lastMoveDirection.set(0, 1);
            playerSprite.setTexture(playerTextureU);
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            target.y -= 1;
            lastMoveDirection.set(0, -1);
            playerSprite.setTexture(playerTextureD);
            moved = true;
        }

        if (moved) { // Se moveu
            if (isWalkable(target)) { // Se a posição permite avançar
                playerGridPosition.set(target); // Atualiza a posição do jogador para onde este quer ir
                updatePlayerWorldPosition();

            moveTimer = 0f;
            }// Faz reset do timer de movimento após tentativa de movimento
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { // Valida o Clique no "Espaço"
            projectiles.add(new Projectile(playerGridPosition.cpy(), lastMoveDirection.cpy()));
        } // Cria um novo laser na posição do jogador com a direção do jogador
    }

    private void MenuInput() { // função de input do menu para clicar no restart
        if (Gdx.input.justTouched()) { //Valida se o utilizador clicou no ecrã
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY()); //Guarda a posição do click
            viewport.unproject(touch); //Altera a posição do click para o tipo de cordenadas utilizadas pelo FitViewport
            if (restartButtonBounds.contains(touch)) { // Se o click foi dentro do botão restart
                restartGame(); // Restart do jogo
            }
        }
    }

    private boolean isWalkable(Vector2 pos) { //Função valida se uma determinada posição do mapa permite andar
        if (pos.x < 0 || pos.x >= MAP_WIDTH || pos.y < 0 || pos.y >= MAP_HEIGHT) {
            return false; //Se a posição está fora do mapa, devolve false
        }
        return !maps.get(currentMapIndex).nodes[(int) pos.x][(int) pos.y].isWall; //valida a posição da node inserida na função com o mapa relevante e devolve false (Se não poder andar) ou true (se for permito andar)
    }

    private void logic() {

        for (Enemy enemy : enemies) {
            GameMap map = maps.get(currentMapIndex); // Devolve o mapa corrente
            MapNode playerNode = map.nodes[(int) playerGridPosition.x][(int) playerGridPosition.y]; // Devolve a node do jogador

            if (enemy.canMove(Gdx.graphics.getDeltaTime())) {
                //MapNode enemyNode = map.nodes[(int) enemy.gridPosition.x][(int) enemy.gridPosition.y]; // Devolve as nodes dos inimigos
                MapNode nextNode = enemy.getNextStep(map, playerNode);
                Vector2 next = new Vector2(nextNode.x, nextNode.y);            // Coloca a node anterior no "next" (Proxima node)
                Vector2 direction = new Vector2(next).sub(enemy.gridPosition); // Devolve a direção do movimento a subtraindo a posição nova pela posição anterior

                enemy.gridPosition.set(next);             // Define a posição do inimigo para a melhor node (next) - Logicamente
                enemy.sprite.setPosition(next.x, next.y); // Define a posição do inimigo para a melhor node (next) - Visualmente

                if (direction.x > 0) { // Texturas Direcionais basedas na direção dos inimigos
                    enemy.sprite.setTexture(enemyTextureR); //
                }
                else if (direction.x < 0) {
                    enemy.sprite.setTexture(enemyTextureL);
                }
                else if (direction.y > 0) {
                    enemy.sprite.setTexture(enemyTextureU);
                }
                else if (direction.y < 0) {
                    enemy.sprite.setTexture(enemyTextureD);
                }

                if (enemy.gridPosition.equals(playerGridPosition)) { // Se a posição do inimigo for igual à do jogador
                    playerLives--;      // Retira uma vida
                    if (playerLives == 0) {
                        gameState = GameState.GAME_END; // Se as vidas forem iguais a zero, altera o estado do jogo para GAME_END
                    }
                    else {
                        playerGridPosition.set(map.spawnNode.x, map.spawnNode.y); //Se não, faz spawn do jogador na node do spawn (Logicamente)
                        updatePlayerWorldPosition();
                    }
                }
            }
        }

        Array<Projectile> toRemove = new Array<>(); // Lista para colocar todos os projéteis/lasers que se vai remover

        for (Projectile p : projectiles) { // Para todos os projéteis/lasers
            p.position.add(p.direction);   // Adiciona à posição do laser a sua direção e movimento
            if (!isWalkable(p.position)) { // Se a posição não for uma superficie onde o jogador ou o inimigo podem andar
                toRemove.add(p); continue; // Remove o laser do mapa
            }
            for (Enemy enemy : enemies) { // Para todos os inimigos
                if (enemy.gridPosition.equals(p.position)) { // Se a posição do inimigo for igual à posição do laser
                    enemies.removeValue(enemy, true); // Remove o inimigo do mapa
                    enemydeathSound.play();  //  Toca um som
                    toRemove.add(p); // Remove o Laser do mapa
                    break;
                }
            }
        }
        projectiles.removeAll(toRemove, true); // Remove todos os projéteis/lasers desnecessários

        GameMap current = maps.get(currentMapIndex); // Devolve o mapa em que o jogador está
        if (current.portalNode != null && playerGridPosition.equals(new Vector2(current.portalNode.x, current.portalNode.y))) {
                loadMap(currentMapIndex + 1); // Se existe um portal e o jogador está na node do portal faz load do próximo mapa
        }
        if (current.treasureNode != null && playerGridPosition.equals(new Vector2(current.treasureNode.x, current.treasureNode.y))) {
            gameState = GameState.GAME_END; // Se existe um tesouro e o jogador está na node do tesouro altera o estado do jogo para //GAME_END
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK); // Limpa o ecrã.
        viewport.apply();               // Aplica o tamanho definida anteriormente para o viewport
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined); //Garante que tudo é desenhado na posição e tamanho correto.

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, MAP_WIDTH, MAP_HEIGHT); // Desenha o background com o tamanho da janela
        playerSprite.draw(spriteBatch);     // Desenha a sprite do jogador
        for (Enemy enemy : enemies) {       // Para todos os inimigos
            enemy.sprite.draw(spriteBatch); // Desenha a sprite dos inimigos
        }

        GameMap current = maps.get(currentMapIndex); //Devolve o mapa a ser usado no momento
        if (current.portalNode != null) { //Se exitirem portais no mapa
            spriteBatch.draw(portalTexture, current.portalNode.x, current.portalNode.y, 1, 1); // Usa a textura do portal nas nodes relevantes
        }
        if (current.treasureNode != null) { //Se exitirem tesouros no mapa
            spriteBatch.draw(treasureTexture, current.treasureNode.x, current.treasureNode.y, 1, 1); // Usa a textura do tesouro nas nodes relevantes
        }

        for (int x = 0; x < MAP_WIDTH; x++)                                 // Para este x
            for (int y = 0; y < MAP_HEIGHT; y++)                            // Para este y
                if (current.nodes[x][y].isWall)                             // Se a node nestas cordenadas for uma parede
                    spriteBatch.draw(wallTexture, x, y, 1, 1);  // Usar a textura da parede para essa node

        for (int i = 0; i < playerLives; i++) // Para o numero de vidas que o jogador tem
            spriteBatch.draw(heartTexture, 0.2f + i * 0.7f, MAP_HEIGHT - 0.8f, 0.6f, 0.6f); // Criar o mesmo numero de corações no topo esquerdo do ecrã

        for (Projectile p : projectiles) {      // Para todos os projéteis
            Texture laser;                      // textura do laser
            if (Math.abs(p.direction.x) > 0) {  // Se o absoluto da direção der um x > 0, ou seja, horizontal
                laser = laserHorizontalTexture; // Usar textura horizontal
            } else {
                laser = laserVerticalTexture;   // Usar textura vertical
            }
            spriteBatch.draw(laser, p.position.x, p.position.y, 1, 1); // Carrega a textura escolha no if anterior
        }
        spriteBatch.end();

        if (gameState == GameState.GAME_END) { // Se o estado do jogo for "GAME_END"
            spriteBatch.begin();
            float width = 6f;  // Tamanho da imagem (Largura)
            float height = 4f; // Tamanho da imagem (Altura)
            float x = (viewport.getWorldWidth() - width) / 2;   // Calcula o ponto inicial vertical para a nova imagem ficar centrada
            float y = (viewport.getWorldHeight() - height) / 2; // Calcula o ponto inicial horizontal para a nova imagem ficar centrada
            spriteBatch.draw((playerLives == 0 ? gameOverTexture : gameWonTexture), x, y, width, height); //
            if (playerLives == 0) { //Se as Vidas forem igual a 0
                spriteBatch.draw(gameOverTexture, x, y, width, height); //Mostra a imagem do Game-Over
            } else { //Se não
                spriteBatch.draw(gameWonTexture, x, y, width, height);  //Mostra a imagem do Game-Won
            }
            spriteBatch.end();
        }
    }

    private void restartGame() {    // Recomeça o Jogo
        playerLives = 3;            // Faz reset das Vidas para 3 (Pode ser alterado)
        projectiles.clear();        // Limpa os projéteis utilizados
        enemies.clear();            // Limpa os inimigos utilizados
        maps.clear();               // Limpa os mapa utilizados
        maps.add(GameMap.generateFirstMap(enemyTextureL, MAP_WIDTH, MAP_HEIGHT));  //Carrega o primeiro Mapa
        maps.add(GameMap.generateSecondMap(enemyTextureL, MAP_WIDTH, MAP_HEIGHT)); //Carrega o segundo Mapa
        maps.add(GameMap.generateThirdMap(enemyTextureL, MAP_WIDTH, MAP_HEIGHT));  //Carrega o terceiro Mapa
        loadMap(0);                 //Faz load do primeiro mapa
        gameState = GameState.PLAYING;    //Altera o estado do jogo para "PLAYING"
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Atualiza a nova resolução da janela e centra a imagem
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime(); //define variável com o tempo em segundos desde o ultimo frame
        moveTimer += delta;             //Adiciona o tempo delta anterior ao timer de movimendo do jogador

        if (gameState == GameState.PLAYING) { //Situação de jogo
            PlayerInput();  //Input feito no jogo (Movimento e disparar)
            logic();
        } else if (gameState == GameState.GAME_END) { //Situação de GAME_END (Menu restart)
            MenuInput();    //Input feito no menu (Selecionar o botão restart)
        }
        draw();
    }

    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void dispose() {
        heartTexture.dispose(); //Remove a textura da memória
    }
}


