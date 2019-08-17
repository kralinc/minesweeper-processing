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
    text("Controls:\nLeft Click: Uncover tile\nRight Click: Flag Tile\nEnter: Reset\nBackspace: Back to Menu", width/4, height/1.7);
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
