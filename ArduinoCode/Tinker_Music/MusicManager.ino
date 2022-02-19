#include <pcmConfig.h>
#include <pcmRF.h>
#include <TMRpcm.h>
#include <SPI.h>
#include <SD.h>

class MusicManager{
private:
  File[] musicQueue;
  static MusicManager instance;
  Audio a;

  
  MusicManager(){
    musicQueue = new File[];
    SD.begin();
    
  }
public:
  void play(){
    
  }
  void pause(){
    
  }
  void unpause(){
    
  }
  void shuffle(){
    
  }
  MusicManager getInstance(){
    if(instance == null){
      instance = new MusicManager();
    }
    return instance;
  }
}
