int scene = 0; //<>//
int NUM_TILES;
Game game = new Game();
Menu menu = new Menu();

Scene[] scenes = {menu, game};
Scene cScene;

void setup(){
  size(900, 1000);
  cScene = scenes[0];
}

void draw(){
  cScene.onDraw();
  
  int next = cScene.nextScene();
  if (next != scene){
   scene = next;
   cScene = scenes[scene];
   cScene.init();
  }
  
}

void keyPressed(){
  cScene.onKey(keyCode);
}

void mousePressed(){
 cScene.onMouse(mouseButton); 
}
