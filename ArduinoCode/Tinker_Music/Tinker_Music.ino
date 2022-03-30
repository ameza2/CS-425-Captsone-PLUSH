#include <Stepper.h>

/**
   PLUSH Arduino Code:
   Authors: Christian Pilley, Abraham Meza
   Team: Team20(Christian Pilley, Abraham Meza, Tal Zemach, Josh Insorio, Korben Diarchangel)
   Outside Librarys (code not written by us):
    - LiquidCrystal_I2C.h
    - Pitch.h
    - Volume.h

   Arduino Libraries Used:
    - Wifi.h
    - WifiClient.h
    - WifiServer.h
    - WifiUdp.h
*/
#define TONE_USE_INT
#define TONE_PITCH 440
//#define HANDLE_TAGS
//#include <Pitch.h>
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include <SPI.h>
#include <SD.h>
#include <pcmConfig.h>
#include <pcmRF.h>
#include <TMRpcm.h>
#include <Stepper.h> // stepper motors


LiquidCrystal_I2C lcd(0x27, 16, 2);
File f;
TMRpcm Audio;

//
const int stepsPerRevolution = 2048;
Stepper myStepperL1 = Stepper(stepsPerRevolution, 36, 38, 37, 39); // (steps per revolution, pins)
Stepper myStepperL2 = Stepper(stepsPerRevolution, 40, 42, 41, 43); // (steps per revolution, pins)
//Stepper myStepperR1(stepsPerRevolution, 38, 39, 40, 41); // (steps per revolution, pins)
//Stepper myStepperR2(stepsPerRevolution, 42, 43, 44, 45); // (steps per revolution, pins)
//Logger logger;
/*
   Pin assignments:
   Variables holding the pin locations for various I/O
*/
const int musicButton     = 4;
const int hugButton       = 5;
const int emergencyButton = 6;
const int helpButton      = 7;
const int volumeDownButton= 48;
const int volumeUpButton  = 49;
const int speakerOutPin   = 12;
unsigned const LED_R      = 10;
unsigned const LED_G      = 9;
unsigned const LED_B      = 8;

const int buttonInterruptPin = 2;

bool hugFlag = false;
int hugDuration = 5000; // hug duration (ms)


/*
   Button Dictionary
*/
String buttonMessages[] = {
  "Music button",
  "Hug button",
  "Emergency button",
  "Call button"
};
String currentPressedButton = "";
boolean buttonChanged = false;
/*
   Music Variables
*/
int counter = 0;
bool musicToggle = false; // boolean  variable: used to store music toggle
int newVolume = 0; // variable: used to store new volume setting
int oldVolume = 0; // variable: used to store old volume setting
/*
   Interrupt Functions
*/

void buttonInterrupts() {
  Serial.println("INTERTUPTION OCCURED");
  Serial.print(digitalRead(helpButton));
  Serial.print(digitalRead(emergencyButton));
  Serial.print(digitalRead(hugButton));
  Serial.print(digitalRead(musicButton));
  Serial.print(digitalRead(volumeDownButton));
  Serial.print(digitalRead(volumeUpButton));


  
  if(digitalRead(musicButton)){
    musicToggle = !musicToggle; // switch the state of the musictoggle
    if (musicToggle) counter = 0; // if the music toggle is high (because it doesnt update until after the end of the method) set the counter to 0
    currentPressedButton = buttonMessages[0];
    buttonChanged = true;
    Serial.println("Music Button Changed");
  }
  else if(digitalRead(hugButton)){
    currentPressedButton = buttonMessages[1];
    buttonChanged = true;
    hugFlag = !hugFlag;
    Serial.println("Hug Button Changed");
  }
  else if(digitalRead(emergencyButton)){
    currentPressedButton = buttonMessages[2];
    buttonChanged = true;
    Serial.println("Emergency Button Changed");
  }
  else if(digitalRead(helpButton)){
    currentPressedButton = buttonMessages[3];
    buttonChanged = true;
    
  }
  else if(digitalRead(volumeDownButton)){
    newVolume = max(0, newVolume--);
  }
  else if(digitalRead(volumeUpButton)){
    newVolume = min(10, newVolume++); 
  }
}

/*
   Helper Functions
*/
void setLEDColor(int redVal, int greenVal, int blueVal) {
  analogWrite(LED_R, redVal);
  analogWrite(LED_G, greenVal);
  analogWrite(LED_B, blueVal);
}

/*
   Setup Function
*/
void setup() {
  // Serial Port Check (Debugging) //
  Serial.begin(9600);
  while (!Serial) {
    ; // do nothing
  }
  //logger = Logger.getInstance();
  randomSeed(analogRead(0));

  //vol.DEFAULT_PIN
  // Input Pins //
  pinMode(musicButton, INPUT_PULLUP);
  pinMode(helpButton, INPUT_PULLUP);
  pinMode(emergencyButton, INPUT_PULLUP);
  pinMode(hugButton, INPUT_PULLUP);
  pinMode(volumeDownButton, INPUT_PULLUP);
  pinMode(volumeUpButton, INPUT_PULLUP);
  for(int i = 36; i <= 43; i++) pinMode(i, OUTPUT); //for motors
  
  attachInterrupt(digitalPinToInterrupt(buttonInterruptPin), buttonInterrupts, RISING);
  

  // Output Pins //
  pinMode(speakerOutPin, OUTPUT);
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);
  //vol.begin();
  lcd.begin();
  lcd.backlight();

//  if (!SD.begin()) {
//    Serial.println("SD CARD UNINITIALIZED");
//  }
//  if (!SD.exists("NGGYU.wav")) {
//    Serial.println("NO WAV FILE FOUND WITH THIS NAME");
//  }
//  f = SD.open("NGGYU.wav"); 
  Audio.speakerPin = 12;
  Audio.play("NGGYU.wav");
  Audio.setVolume(7);
  
  // in rpm
  myStepperL1.setSpeed(5);
  myStepperL2.setSpeed(5);
  //myStepperR1.setSpeed(1000);
  //myStepperR2.setSpeed(1000);
}


/*
   Loop Function
*/
void loop() {
  if(hugFlag){
    // # of steps
    myStepperL1.step(stepsPerRevolution/4);
    myStepperL2.step(stepsPerRevolution/4);
//    myStepperR1.step(20);
//    myStepperR2.step(20);

    delay(hugDuration);
    
    myStepperL2.step(stepsPerRevolution/4 * -1);
    myStepperL1.step(stepsPerRevolution/4 * -1);
  
    hugFlag = !hugFlag;
  }

  
  //Serial.print(Audio.isPlaying());
  //newVolume = analogRead(volumeDial);
  //newVolume = map(newVolume, 0, 1024, 0, 100);

  // Play PLUSH Music
  if (musicToggle) {
    if (!Audio.isPlaying()) {
      Audio.pause();
    }
  } else {
    if (Audio.isPlaying()) {
      Audio.pause();
    }
  }
  //update LCD
  if (buttonChanged) {
    lcd.setCursor(0, 1);
    lcd.print("                ");  
    lcd.setCursor(0,1);  
    lcd.print(currentPressedButton);
  }
  //update LED
  if (currentPressedButton == buttonMessages[3]) {
    setLEDColor(0, 0, 0);
    setLEDColor(255, 255, 255);
    delay((random(10) * 1000));
    setLEDColor(0, 255, 0);
    delay(5000);
    setLEDColor(0, 0, 0);
    currentPressedButton = "";
  }
  //change volume
  if (oldVolume != newVolume) {
    lcd.setCursor(14, 1);
    lcd.print("  ");
    lcd.setCursor(14, 1);
    lcd.print(newVolume);
  }
  oldVolume = newVolume;
  buttonChanged = false;
  currentPressedButton = "";
}
