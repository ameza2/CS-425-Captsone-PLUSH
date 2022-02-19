class Logger{
  public:
    //Log level is used for denoting importance of messages sent to log files.
    enum LEVEL{
      Warning = 0;
      Error,
      Debug,
    }
    String[] level_as_string = {
      "Warning",
      "Error"
      "Debug"
    }
  
    Logger& static getInstance(){
      if(inst ance == null){
        instance = new Logger();
      }
      return instance;
    }
    void log(LEVEL l, const char* message){
      String logMessage = "";
      logMessage += "[";
      logMessage += asString(l);
      logMessage += "]" + message;
      logFile.println(logMessage);
    }
    
  
  private:
    static Logger instance;
    //filename will follow format of plush_day_month_year<_num>.log
    // _num is only if there are multiple logs from the same day exist.
    // latest.log will be the latest log file generated for ease of finding it
    // when a new latest log file is generated, the old one will be renamed to the first date in the log.
    String fileName = "";
    File logFile;
    Logger(){
      if(SD.exists("latest.log")){
        File lat = SD.open("logs/latest.log");
        
      }
      logFile = SD.open("logs/latest.log",FILE_WRITE);
    }
    String asString(LEVEL l){
      return level_as_string[l];
    }
}
