import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import Scene.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Minesweeper2 extends PApplet {

int scene = 0;
int NUM_TILES;
Game game = new Game();
Menu menu = new Menu();

Scene[] scenes = {menu, game};
Scene cScene;

public void setup(){
  
  cScene = scenes[0];
}

public void draw(){
  cScene.onDraw();
  
  int next = cScene.nextScene();
  if (next != scene){
   scene = next;
   cScene = scenes[scene];
   cScene.init();
  }
  
}

public void keyPressed(){
  cScene.onKey(keyCode);
}

public void mousePressed(){
 cScene.onMouse(mouseButton); 
}
int NUM_MINES;
int TILE_SIZE;
Board board;
Timer timer;
int clickedTiles;
int flags;
boolean won;

public class Game implements Scene{
  
  int nextScene;

  Game(){
  }
  
  public void init(){
    NUM_TILES = (NUM_TILES < 4) ? 4 : NUM_TILES;
    NUM_MINES = (int)((NUM_TILES * NUM_TILES) / 6.4f);
    TILE_SIZE = round(width / NUM_TILES);
    board = new Board();
    timer = new Timer();
    board.create();
    timer.off();
    timer.pause();
    clickedTiles = 0;
    flags = NUM_MINES;
    won = false;
    nextScene = 1;
  }
  
  public void onDraw(){
    background(0);
    timer.count();
    board.render();
  }
  
  public void onMouse(int b){
    if (hovering()){
     int tileX = ceil(mouseX / TILE_SIZE);
     int tileY = ceil((mouseY - (height-width)) / TILE_SIZE);
       if (b == LEFT){
         if (timer.ms == 0){
           board.createMines(tileX, tileY);
         }
         board.floodFill(tileX, tileY);
       if (board.getTile(tileX, tileY).clicked()){
          board.lose(); 
       }else if (clickedTiles == ((NUM_TILES * NUM_TILES) - NUM_MINES)){
          board.win(); 
       }
     }else{
       Tile tile = board.getTile(tileX, tileY);
       if (!tile.isClicked()){
        tile.setFlagged(!tile.isFlagged());
       }
     }
    }
  }
  
  public void onKey(int k){
    if (k == ENTER){
      init(); 
    }else if (k == BACKSPACE){
      --nextScene;
    }
  }
  
  public int nextScene(){
    return nextScene;
  }
  
  public boolean hovering(){
    return (mouseX >= 0 && mouseY > (height - width) && mouseX <= width && mouseY <= height);
  }
  
}

class Board{
 Tile[][] tiles = new Tile[NUM_TILES][NUM_TILES];
 
 public void create(){
  //Create tiles.
  for (int x = 0; x < NUM_TILES; ++x){
   for (int y = 0; y < NUM_TILES; ++y){
    tiles[x][y] = new Tile(new PVector(x*TILE_SIZE, (y*TILE_SIZE) + (height-width)), TILE_SIZE, false);
   }
  }
 }
 
 public void createMines(int tileX, int tileY){
  //Create mines.
  for (int i = 0; i < NUM_MINES; ++i){
   Tile tile;
   int x;
   int y;
   
   do{
     x = (int)random(0, NUM_TILES);
     y = (int)random(0, NUM_TILES);
     tile = tiles[x][y];
   }while(tile.isMined() || isAdjacent(x, y, tileX, tileY));
   
   tile.setMined(true);
   
   //I don't know if there's a better way to do this
   boolean left = (x - 1 >= 0);
   boolean right = (x + 1 < NUM_TILES);
   boolean up = (y - 1 >= 0);
   boolean down = (y + 1 < NUM_TILES);
   
   if (right){
    tiles[x + 1][y].inc(); 
   }
   if (left){
    tiles[x - 1][y].inc(); 
   }
   if (down){
    tiles[x][y + 1].inc(); 
   }
   if (up){
    tiles[x][y - 1].inc(); 
   }
   if (right && down){
     tiles[x + 1][y + 1].inc();
   }
   if (right && up){
    tiles[x + 1][y - 1].inc(); 
   }
   if (left && down){
    tiles[x - 1][y + 1].inc(); 
   }
   if (left && up){
    tiles[x - 1][y - 1].inc(); 
   }
   
  } 
 }
 
 //Draws the board and rest of the interface.
 public void render(){
   stroke(255);
   for (int x = 0; x < NUM_TILES; ++x){
   for (Tile tile : tiles[x]){
     
    if (tile.isMined() && won){
      fill (0, 150, 0);
    }else if (tile.isClicked() && !tile.isMined()){
     fill(150); 
    }else if (tile.isHovering()){
     fill(125); 
    }else if(tile.isClicked() && tile.isMined()){
      fill (100,0,0);
    }else if(tile.isFlagged()){
      fill(0, 0, 200); 
    }else{
      fill(100);
    }
    
    PVector p = tile.getPos();
    
    rect(p.x, p.y, tile.getSize(), tile.getSize());
    
    if (tile.isClicked() && tile.getValue() > 0 && !tile.isMined()){
     textSize(TILE_SIZE/3);
     switch (tile.getValue()){
      case 1:
        fill(0,0,255);
        break;
      case 2:
        fill(0,255,0);
        break;
      case 3:
        fill(200,250,0);
        break;
      case 4:
        fill(0,0,150);
        break;
      default:
        fill(200,0,0);
        break;
     }
     text(tile.getValue(), tile.getPos().x + (TILE_SIZE/2.5f), tile.getPos().y + (TILE_SIZE/2));
    }
    
    String time = timer.getM() + ":" + timer.getS();
    String flagCount = "Left: " + flags;
    textSize(20);
    fill(255);
    text(time, 150, 30);
    text(flagCount, 250, 30);
   }
  }
 }
 
 public Tile getTile(int x, int y){
   return tiles[x][y];
 }
 
 public void floodFill(int x, int y){
   
   if (x < 0 || x >= NUM_TILES || y < 0 || y >= NUM_TILES){
    return; 
   }
   
   Tile tile = tiles[x][y];
   if (!tile.isClicked() && !tile.isMined()){
     tile.setClicked(true);
     timer.on();
     ++clickedTiles;
     if (tile.getValue() < 1){
       floodFill(x + 1, y);
       floodFill(x, y + 1);
       floodFill(x - 1, y);
       floodFill(x, y - 1);
       floodFill(x + 1, y + 1);
       floodFill(x - 1, y - 1);
       floodFill(x + 1, y - 1);
       floodFill(x - 1, y + 1);
     }
   }else{
    return; 
   }
 }
 
 public void lose(){
   timer.pause();
   for (int x = 0; x < NUM_TILES; ++x){
   for (int y = 0; y < NUM_TILES; ++y){
    Tile tile = tiles[x][y];
    tile.setClicked(true);
   }
  }
 }
 
 public void win(){
  timer.pause();
  won = true;
 }
 
 public boolean isAdjacent(int x, int y, int tileX, int tileY){
   try{
    return ((x == tileX && y == tileY) || (x == tileX-1 && y == tileY)
    || (x == tileX && y == tileY-1) || (x == tileX + 1 && y == tileY)
    || (x == tileX && y == tileY + 1) || (x == tileX + 1 && y == tileY + 1)
    || (x == tileX -1 && y == tileY + 1) || (x == tileX -1 && y == tileY - 1)
    || (x == tileX + 1&& y == tileY - 1));
   }catch(Exception e){
    return true; 
   }
 }
 
}


class Tile{
  PVector pos;
  int size;
  int value;
  boolean clicked;
  boolean hovering;
  boolean mined;
  boolean flagged;
  
  Tile(PVector pos_, int size_, boolean mined_){
   pos = pos_;
   size = size_;
   value = 0;
   clicked = false;
   mined = mined_;
   flagged = false;
  }
  
  public void inc(){
    ++value;
  }
  
  public void setHovering(boolean b){
    hovering = b;
  }
  
  public boolean isHovering(){
   return hovering; 
  }
  
  public boolean isMined(){
   return mined; 
  }
  
  public void setMined(boolean b){
   mined = b; 
  }
  
  public boolean isClicked(){
   return clicked; 
  }
  
  public void setClicked(boolean b){
    clicked = b;
    flagged = false;
  }
  
  public boolean clicked(){
   return (mined) ? true : false;
  }
  
  public boolean isFlagged(){
   return flagged; 
  }
  
  public void setFlagged(boolean b){
   flagged = b;
   flags = (b) ? --flags : ++flags;
  }
  
  public PVector getPos(){
   return pos; 
  }
  
  public int getSize(){
   return size; 
  }
  
  public int getValue(){
   return value; 
  }
  
}
public class Menu implements Scene{
  int numTiles = 8;
  int nextScene = 0;
  
  Menu(){
    
  }
  
  public void init(){
    nextScene = 0;
  }
  
  public void onDraw(){
    background(0);
    textSize(25);
    text("Number of tiles squared: " + numTiles, width/4, height/6);
    text("Use the up and down arrows to \nchange the number of tiles.\nPress enter or click to begin.\n", width/5, height/3);
    text("Controls:\nLeft Click: Uncover tile\nRight Click: Flag Tile\nEnter: Reset\nBackspace: Back to Menu", width/4, height/1.7f);
  }
  
  public void onMouse(int b){
    NUM_TILES = numTiles;
    ++nextScene;
  }
  
  public void onKey(int k){
    if (k == UP){
     ++numTiles; 
    }else if (k == DOWN){
     numTiles = (numTiles > 4) ? --numTiles : numTiles;
    }else if (k == ENTER){
      NUM_TILES = numTiles;
      ++nextScene;
    }
  }
  
  public int nextScene(){
    return nextScene;
  }
}
class Timer{
 
  private int h;
  private int m;
  private int s;
  private int ms;
  private int startTime;
  private int oldMillis;
  private boolean on;
 
  Timer(){
    h = 0;
    m = 0;
    s = 0;
    ms = 0;
    startTime = 0;
    oldMillis = 0;
    on = false;
  }
  
  public void on(){
    on = true;
    oldMillis = 0;
  }
  
  public void count(){
   if (on){
     if (m >= 60){
      m = 0;
      ++h;
     }
     if (s >= 60){
      s = 0;
      ++m;
     }
     ms = millis() - startTime;
     if (ms >= 1000){
       startTime = millis();
       ++s;
     }
   }else{
    startTime += millis() - oldMillis;
    oldMillis = millis();
   }
   
  }
  
  public void pause(){
    on = false;
    oldMillis = millis();
  }
  
  public void off(){
    h = 0;
    m = 0;
    s = 0;
    ms = 0;
    on = false;
  }
  
  public int getms(){
   return ms; 
  }
  
  public int getS(){
    return s;
  }
  public int getM(){
   return m; 
  }
  
  public int getH(){
   return h; 
  }
  
  public boolean isOn(){
   return on;
  }
  
}
  public void settings() {  size(900, 1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Minesweeper2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
