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
Stepper myStepperL1(32, 30, 31, 32, 33); // (steps per revolution, pins)
Stepper myStepperL2(32, 34, 35, 36, 37); // (steps per revolution, pins)
Stepper myStepperR1(32, 38, 39, 40, 41); // (steps per revolution, pins)
Stepper myStepperR2(32, 42, 43, 44, 45); // (steps per revolution, pins)
//Logger logger;
/*
   Pin assignments:
   Variables holding the pin locations for various I/O
*/
const int musicButton     = 19;
const int hugButton       = 18;
const int emergencyButton = 2;
const int helpButton      = 3;
const int speakerOutPin   = 12;
unsigned const volumeDial = A5;
unsigned const LED_R      = 10;
unsigned const LED_B      = 8;
unsigned const LED_G      = 9;

bool hugFlag = false;

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
unsigned int newVolume = 0; // variable: used to store new volume setting
unsigned int oldVolume = 0; // variable: used to store old volume setting
/*
   Interrupt Functions
*/
void toggleMusic() {
  musicToggle = !musicToggle; // switch the state of the musictoggle
  if (musicToggle) counter = 0; // if the music toggle is high (because it doesnt update until after the end of the method) set the counter to 0
  currentPressedButton = buttonMessages[0];
  buttonChanged = true;
}
void LCDPrintHelp() {
  currentPressedButton = buttonMessages[3];
  buttonChanged = true;
}
void LCDPrintEmergency() {
  currentPressedButton = buttonMessages[2];
  buttonChanged = true;
}
void LCDPrintHug() {
  currentPressedButton = buttonMessages[1];
  buttonChanged = true;
  hugFlag = !hugFlag;
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
  for(int i = 30; i < 46; i++) pinMode(i, OUTPUT);
  
  attachInterrupt(digitalPinToInterrupt(musicButton), toggleMusic, RISING);
  attachInterrupt(digitalPinToInterrupt(helpButton), LCDPrintHelp, RISING);
  attachInterrupt(digitalPinToInterrupt(emergencyButton), LCDPrintEmergency, RISING);
  attachInterrupt(digitalPinToInterrupt(hugButton), LCDPrintHug, RISING);

  // Output Pins //
  pinMode(speakerOutPin, OUTPUT);
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);
  //vol.begin();
  lcd.begin();
  lcd.backlight();

  if (!SD.begin()) {
    Serial.println("SD CARD UNINITIALIZED");
  }
  if (!SD.exists("NGGYU.wav")) {
    Serial.println("NO WAV FILE FOUND WITH THIS NAME");
  }
  f = SD.open("NGGYU.wav");
  Audio.speakerPin = 12;
  Audio.play("NGGYU.wav");
  Audio.setVolume(7);

  myStepperL1.setSpeed(600); // set to 60 rpm
  myStepperL2.setSpeed(600);
  myStepperR1.setSpeed(600);
  myStepperR2.setSpeed(600);
}
/*
   Loop Function
*/
void loop() {
  if(hugFlag){
    myStepperL1.step(2000);
    myStepperL2.step(2000);
    myStepperR1.step(2000);
    myStepperR2.step(2000);
    hugFlag = false;
  }
  Serial.print(Audio.isPlaying());
  newVolume = analogRead(volumeDial);
  newVolume = map(newVolume, 0, 1024, 0, 100);

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
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print(currentPressedButton);
    lcd.setCursor(0, 1);
    lcd.print("pressed!");
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
    lcd.setCursor(12, 1);
    lcd.print(newVolume);
  }
  oldVolume = newVolume;
  buttonChanged = false;
  currentPressedButton = "";
}
