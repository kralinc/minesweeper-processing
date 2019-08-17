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
