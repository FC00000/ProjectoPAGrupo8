package io.github.projectopa2;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;

enum GameState {
    PLAYING,
    GAME_END
}
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;

    // Texturas
    Texture backgroundTexture, playerTextureDir, playerTextureEsq, playerTextureUp, playerTextureDown;
    Texture enemyTextureDir, enemyTextureEsq, enemyTextureCima, enemyTextureBaixo;
    Texture wallTexture, heartTexture, portalTexture, gameOverTexture, gameWonTexture, treasureTexture;
    Texture laserVerticalTexture, laserHorizontalTexture;

    Sound enemydeathSound;
    BitmapFont font;
    GlyphLayout layout;
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

    //float enemyMoveTimer = 0f;
    //float enemyMoveDelay = 0.75f; // move a cada 0.75 segundos

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(MAP_WIDTH, MAP_HEIGHT);

        backgroundTexture = new Texture("background.png");
        playerTextureDir = new Texture("spaceshipR.png");
        playerTextureEsq = new Texture("spaceshipL.png");
        playerTextureUp = new Texture("spaceshipU.png");
        playerTextureDown = new Texture("spaceshipD.png");
        enemyTextureDir = new Texture("enemyR.png");
        enemyTextureEsq = new Texture("enemyL.png");
        enemyTextureCima = new Texture("enemyU.png");
        enemyTextureBaixo = new Texture("enemyD.png");

        wallTexture = new Texture("rocha.png");
        heartTexture = new Texture("heart.png");
        portalTexture = new Texture("portal.png");
        gameOverTexture = new Texture("gameover.png");
        gameWonTexture = new Texture("gamewon.png");
        treasureTexture = new Texture("treasure.png");

        laserVerticalTexture = new Texture("laserVertical.png");
        laserHorizontalTexture = new Texture("laserHorizontal.png");

        enemydeathSound = Gdx.audio.newSound(Gdx.files.internal("enemydeath.mp3"));

        font = new BitmapFont();
        font.getData().setScale(0.1f);
        layout = new GlyphLayout();

        restartButtonBounds = new Rectangle(6f, 4f, 4f, 2f);

        playerSprite = new Sprite(playerTextureDir);
        playerSprite.setSize(1, 1);
        playerGridPosition = new Vector2();

        projectiles = new Array<>();
        enemies = new Array<>();

        maps = new Array<>();
        maps.add(GameMap.generateFirstMap(enemyTextureEsq, MAP_WIDTH, MAP_HEIGHT));
        maps.add(GameMap.generateSecondMap(enemyTextureEsq, MAP_WIDTH, MAP_HEIGHT));
        maps.add(GameMap.generateThirdMap(enemyTextureEsq, MAP_WIDTH, MAP_HEIGHT));
        loadMap(0);
    }

    private void loadMap(int index) {
        currentMapIndex = index;
        GameMap current = maps.get(index);
        enemies = current.enemies;
        playerGridPosition.set(current.spawnNode.x, current.spawnNode.y);
        updatePlayerWorldPosition();
    }

    private void updatePlayerWorldPosition() {
        playerSprite.setPosition(playerGridPosition.x, playerGridPosition.y);
    }

    private void PlayerInput() {

        if (moveTimer < moveDelay) return; // Se o Time for menor que a delay, não permite movimento

        Vector2 target = new Vector2(playerGridPosition);
        boolean moved = false;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            target.x += 1;
            lastMoveDirection.set(1, 0);
            playerSprite.setTexture(playerTextureDir);
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            target.x -= 1;
            lastMoveDirection.set(-1, 0);
            playerSprite.setTexture(playerTextureEsq);
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            target.y += 1;
            lastMoveDirection.set(0, 1);
            playerSprite.setTexture(playerTextureUp);
            moved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            target.y -= 1;
            lastMoveDirection.set(0, -1);
            playerSprite.setTexture(playerTextureDown);
            moved = true;
        }

        if (isWalkable(target)) playerGridPosition.set(target);
        updatePlayerWorldPosition();

        if (moved) { //
            if (isWalkable(target)) {
                playerGridPosition.set(target);
                updatePlayerWorldPosition();

                moveTimer = 0f;
            }// Faz reset do timer de movimento após tentativa de movimento
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            projectiles.add(new Projectile(playerGridPosition.cpy(), lastMoveDirection.cpy()));
    }

    private void MenuInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch);
            if (restartButtonBounds.contains(touch)) {
                restartGame();
            }
        }
    }


    private boolean isWalkable(Vector2 pos) {
        if (pos.x < 0 || pos.x >= MAP_WIDTH || pos.y < 0 || pos.y >= MAP_HEIGHT) return false;
        return !maps.get(currentMapIndex).nodes[(int) pos.x][(int) pos.y].isWall;
    }

    private void logic() {
        for (Enemy enemy : enemies) {
            GameMap map = maps.get(currentMapIndex);
            MapNode playerNode = map.nodes[(int) playerGridPosition.x][(int) playerGridPosition.y];
            if (enemy.canMove(Gdx.graphics.getDeltaTime())) {
                MapNode enemyNode = map.nodes[(int) enemy.gridPosition.x][(int) enemy.gridPosition.y];
                MapNode nextNode = getNextStep(enemyNode, playerNode);
                Vector2 next = new Vector2(nextNode.x, nextNode.y);
                Vector2 movement = new Vector2(next).sub(enemy.gridPosition);

                enemy.gridPosition.set(next);
                enemy.sprite.setPosition(next.x, next.y);

                if (movement.x > 0) enemy.sprite.setTexture(enemyTextureDir);
                else if (movement.x < 0) enemy.sprite.setTexture(enemyTextureEsq);
                else if (movement.y > 0) enemy.sprite.setTexture(enemyTextureCima);
                else if (movement.y < 0) enemy.sprite.setTexture(enemyTextureBaixo);

                if (enemy.gridPosition.epsilonEquals(playerGridPosition, 0.1f)) {
                    playerLives--;
                    if (playerLives <= 0) gameState = GameState.GAME_END;
                    else playerGridPosition.set(map.spawnNode.x, map.spawnNode.y);
                }
            }
            //enemyMoveTimer = 0f;
        }

        Array<Projectile> toRemove = new Array<>();
        for (Projectile p : projectiles) {
            p.position.add(p.direction);
            if (!isWalkable(p.position)) { toRemove.add(p); continue; }
            for (Enemy enemy : enemies) {
                if (enemy.gridPosition.epsilonEquals(p.position, 0.1f)) {
                    enemies.removeValue(enemy, true);
                    enemydeathSound.play();
                    toRemove.add(p);
                    break;
                }
            }
        }
        projectiles.removeAll(toRemove, true);

        GameMap current = maps.get(currentMapIndex);
        if (current.portalNode != null && playerGridPosition.epsilonEquals(new Vector2(current.portalNode.x, current.portalNode.y), 0.1f))
            if (currentMapIndex + 1 < maps.size) loadMap(currentMapIndex + 1);

        if (current.treasureNode != null && playerGridPosition.epsilonEquals(new Vector2(current.treasureNode.x, current.treasureNode.y), 0.1f))
            gameState = GameState.GAME_END;
    }

    private MapNode getNextStep(MapNode from, MapNode to) {
        MapNode best = from;
        float bestDist = fromDistance(from, to);
        for (MapNode neighbor : from.neighbors) {
            float dist = fromDistance(neighbor, to);
            if (dist < bestDist) {
                best = neighbor;
                bestDist = dist;
            }
        }
        return best;
    }

    private float fromDistance(MapNode a, MapNode b) {
        return Vector2.dst2(a.x, a.y, b.x, b.y);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, MAP_WIDTH, MAP_HEIGHT);
        playerSprite.draw(spriteBatch);
        for (Enemy enemy : enemies) enemy.sprite.draw(spriteBatch);

        GameMap current = maps.get(currentMapIndex);
        if (current.portalNode != null) spriteBatch.draw(portalTexture, current.portalNode.x, current.portalNode.y, 1, 1);
        if (current.treasureNode != null) spriteBatch.draw(treasureTexture, current.treasureNode.x, current.treasureNode.y, 1, 1);

        for (int x = 0; x < MAP_WIDTH; x++)
            for (int y = 0; y < MAP_HEIGHT; y++)
                if (current.nodes[x][y].isWall)
                    spriteBatch.draw(wallTexture, x, y, 1, 1);

        for (int i = 0; i < playerLives; i++)
            spriteBatch.draw(heartTexture, 0.2f + i * 0.7f, MAP_HEIGHT - 0.8f, 0.6f, 0.6f);

        for (Projectile p : projectiles) {
            Texture laser = (Math.abs(p.direction.x) > 0) ? laserHorizontalTexture : laserVerticalTexture;
            spriteBatch.draw(laser, p.position.x, p.position.y, 1, 1);
        }

        spriteBatch.end();

        if (gameState == GameState.GAME_END) {
            spriteBatch.begin();
            float width = 6f, height = 4f;
            float x = (viewport.getWorldWidth() - width) / 2;
            float y = (viewport.getWorldHeight() - height) / 2;
            spriteBatch.draw((playerLives == 0 ? gameOverTexture : gameWonTexture), x, y, width, height);
            spriteBatch.end();
        }
    }

    private void restartGame() {
        playerLives = 3;        // Faz reset das Vidas para 3 (Pode ser alterado)
        projectiles.clear();    // Limpa os projéteis utilizados
        enemies.clear();        // Limpa os inimigos utilizados
        maps.clear();           // Limpa os mapa utilizados
        maps.add(GameMap.generateFirstMap(enemyTextureEsq, MAP_WIDTH, MAP_HEIGHT));
        maps.add(GameMap.generateSecondMap(enemyTextureEsq, MAP_WIDTH, MAP_HEIGHT));
        maps.add(GameMap.generateThirdMap(enemyTextureEsq, MAP_WIDTH, MAP_HEIGHT));
        loadMap(0);                 //Faz load do primeiro mapa
        gameState = GameState.PLAYING;    //Altera o estado do jogo para "PLAYING"
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime(); //define varevel com o tempo em segundos desde o ultimo frame
        moveTimer += delta;         //Adiciona o tempo delta anterior ao timer de movimendo do jogador
        //enemyMoveTimer += delta;    //Adiciona o tempo delta anterior ao timer de movimendo dos inimigos

        if (gameState == GameState.PLAYING) { //Situação de jogo
            PlayerInput();  //Input feito no jogo (Movimento e disparar)
            logic();
        } else if (gameState == GameState.GAME_END) { //Situação de GAME_END (Menu restart)
            MenuInput();    // Input feito no menu (Selecionar o botão restart)
        }
        draw();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        heartTexture.dispose();
    }
}


