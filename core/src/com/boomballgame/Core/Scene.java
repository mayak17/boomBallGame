package com.boomballgame.Core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Scene implements IDrawable{

    private Sizei appSize;
    private Sizei gridSize;
    private float cellSize;

    private Texture redBallImg;
    private Texture greenBallImg;
    private Texture blueBallImg;
    private Texture gameOverTexture;
    private Texture boomBallTexture;

    //Главное поле, двумерный массив
    private Ball[][] gridCells;
    //Массивы случайного выбора
    private BallColor[] colorVariants;
    private Texture[] textureVariants;

    private int BALLS_COUNT = 4;

    public Scene(Sizei appSize, float cellSize){
        this.appSize = appSize;
        this.cellSize = cellSize;
        gridSize = new Sizei(0,0);
        gridSize.height = (int)Math.floor( (float)appSize.height / cellSize);
        gridSize.width = (int)Math.floor( (float)appSize.width / cellSize);
        gridCells = new Ball[gridSize.height][gridSize.width];
        ballsStackTrace = new Stack<>();

        textureVariants = new Texture[BALLS_COUNT];
        colorVariants = new BallColor[BALLS_COUNT];
    }
    //Вызывается когда шары доходят до края
    private boolean isGameOver = false;
    private float spawnInterval = 2f;

    public void GameOver(){
        isGameOver = true;
        Timer.instance().stop();
        for(int x = 0; x < gridSize.width; x++){
            for(int y = 0; y < gridSize.height; y++){
                gridCells[y][x] = null;
            }
        }
//        Gdx.app.exit();
    }
    public void Start(){
        TimerSetup();
    }

    private void TimerSetup(){
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                SpawnRow();
            }
        };
        Timer.schedule(task, 0, spawnInterval);
        Timer.instance().start();
    }
    public void TouchBall(float x, float y){
        int xpos = (int)Math.floor(x / cellSize);
        int ypos = (int)Math.floor(y / cellSize);
        if(xpos >= 0 && ypos >= 0 && xpos < gridSize.width && ypos < gridSize.height){
            Ball ball = gridCells[ypos][xpos];
            if(ball != null){
                BoomBall(ball, xpos, ypos);
                DowningBalls();
            }
        }
    }
    public void SpawnRow(){
        //Проверяем не до верха ли экрана заполнено
        for(int x = 0; x < gridSize.width; x++){
            int y = gridSize.height - 1;
            if(gridCells[y][x] != null){
                GameOver();
            }
        }
        //Поднимаем позицию шаров на 1
        for(int x = 0; x < gridSize.width; x++){
            for(int y = gridSize.height - 2; y >= 0; y--){
                gridCells[y + 1][x] = gridCells[y][x];
            }
        }
        //Создаем шары, выбираем случайно число из вариантов
        Random random = new Random();
        for(int x = 0, y = 0; x < gridSize.width; x++){
            int index = random.nextInt(colorVariants.length);
            BallColor color = colorVariants[index];
            Texture texture = textureVariants[index];
            Ball ball = new Ball(texture, color, cellSize);
            gridCells[y][x] = ball;
        }
    }
    private Stack<Ball> ballsStackTrace;
    private ArrayList<Ball> borderTrace = new ArrayList<>();
    private ArrayList<Ball> traceList = new ArrayList<>();

    private int boomBallArea = 5;

    private void CrestBoom(Ball ball, int x, int y){
        if(ball != null){
            int left = x - 1;
            int right = x + 1;
            int up = y + 1;
            int down = y - 1;
            if(down >= 0){
                gridCells[down][x] = null;
            }
            if(left >= 0){
                gridCells[y][left] = null;
            }
            if(up < gridSize.height){
                gridCells[up][x] = null;
            }
            if(right < gridSize.width){
                gridCells[y][right] = null;
            }
            gridCells[y][x] = null;
        }
    }
    //Поиск в глубину соседних шаров со стеком
    public void BoomBall(Ball ball, int gridX, int gridY){
        if(ball.getBallColor() != BallColor.BOOM){
            ballsStackTrace.clear();
            checkNeighBoor(ball, gridX, gridY);
            for(int x = 0; x < gridSize.width; x++){
                for(int y = 0; y < gridSize.height; y++){
                    Ball gridBall =  gridCells[y][x];

                    if(ballsStackTrace.contains(gridBall)){
                        gridCells[y][x] = null;
                    }
                }
            }

        }
        else {
            CrestBoom(ball, gridX, gridY);
//            int leftBoomBorder = -(int)Math.ceil ((float)boomBallArea / 2);
//            int rightBoomBorder = boomBallArea + leftBoomBorder;
//
//            for(int x = 0; x < gridSize.width; x++){
//                for(int y = 0; y < gridSize.height; y++){
//                    Ball gridBall =  gridCells[y][x];
//                    if(gridBall == ball){
//                        for (int xx = leftBoomBorder; xx < rightBoomBorder; xx++){
//                            for(int yy = leftBoomBorder; yy < rightBoomBorder; yy++){
//                                int posX = x + xx;
//                                int posY = y + yy;
//                                if(posX < gridSize.width && posX >= 0 && posY < gridSize.height && posY >= 0){
//                                    gridCells[posY][posX] = null;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
    //Опускаем шары, вызывается после взрыва
    private void DowningBalls(){
        for(int x = 0; x < gridSize.width; x++){
            for(int y = 0; y < gridSize.height; y++){
                if(gridCells[y][x] != null) {
                    int down = y - 1;
                    while (down >= 0 && gridCells[down][x] == null) {
                        gridCells[down][x] = gridCells[down + 1][x];
                        gridCells[down + 1][x] = null;
                        down -= 1;
                    }
                }
            }
        }
    }
    //Рекурсивная проверка сосдних шаров на соотвествие цвету и занос в стек если подошли под условия
    private void checkNeighBoor(Ball ball, int posX, int posY){
        ballsStackTrace.push(ball);

        int left = posX - 1;
        int right = posX + 1;
        int up = posY + 1;
        int down = posY - 1;

        if(left >= 0){
            Ball checked = gridCells[posY][left];
            if(checked != null && checked.getBallColor() == ball.getBallColor() && !ballsStackTrace.contains(checked)){
                checkNeighBoor(checked, left, posY);
            }
        }
        if(right < gridSize.width){
            Ball checked = gridCells[posY][right];
            if(checked != null && checked.getBallColor() == ball.getBallColor() && !ballsStackTrace.contains(checked)){
                checkNeighBoor(checked, right, posY);
            }
        }
        if(down >= 0){
            Ball checked = gridCells[down][posX];
            if(checked != null && checked.getBallColor() == ball.getBallColor() && !ballsStackTrace.contains(checked)){
                checkNeighBoor(checked, posX, down);
            }
        }
        if(up < gridSize.height){
            Ball checked = gridCells[up][posX];
            if(checked != null && checked.getBallColor() == ball.getBallColor() && !ballsStackTrace.contains(checked)){
                checkNeighBoor(checked, posX, up);
            }
        }
    }
    //Загрузка текстур
    public void ResourceLoad(){

        redBallImg = new Texture("sphere_red.png");
        greenBallImg = new Texture("sphere_green.png");
        blueBallImg = new Texture("sphere_blue.png");
        gameOverTexture = new Texture("game_over.png");
        boomBallTexture = new Texture("sphere_boom.png");

        textureVariants[0] = redBallImg;
        textureVariants[1] = greenBallImg;
        textureVariants[2] = blueBallImg;
        textureVariants[3] = boomBallTexture;
        colorVariants[0] = BallColor.RED;
        colorVariants[1] = BallColor.GREEN;
        colorVariants[2] = BallColor.BLUE;
        colorVariants[3] = BallColor.BOOM;
    }
    //Выгрузка текстур
    public void ResourceUnload(){
        redBallImg.dispose();
        greenBallImg.dispose();
        blueBallImg.dispose();
        gameOverTexture.dispose();
    }
    //Рисуем сцену
    public void Draw(SpriteBatch sb){
        for(int x = 0; x < gridSize.width; x++){
            for(int y = 0; y < gridSize.height; y++){
                Ball item = gridCells[y][x];
                if(item != null){
                    item.setDrawPosition(x, y);
                    item.Draw(sb);
                }
            }
        }
        if(isGameOver){
            sb.draw(gameOverTexture, (float)appSize.width / 2, appSize.height - 100);
        }
    }
}
