/**
 * PLUSH Arduino Code:
 * Authors: Christian Pilley, Abraham Meza
 * Team: Team20(Christian Pilley, Abraham Meza, Tal Zemach, Josh Insorio, Korben Diarchangel)
 * Outside Librarys (code not written by us): 
 *  - LiquidCrystal_I2C.h
 *  - Pitch.h
 *  - Volume.h
 *  
 * Arduino Libraries Used:
 *  - Wifi.h
 *  - WifiClient.h
 *  - WifiServer.h
 *  - WifiUdp.h
 */
#define TONE_USE_INT
#define TONE_PITCH 440
#include <Pitch.h>
#include "Volume.h"
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>

LiquidCrystal_I2C lcd(0x27, 16,2);
Volume vol;
/*
 * Pin assignments:
 * Variables holding the pin locations for various I/O
 */
const int musicButton     = 19;
const int hugButton       = 18;
const int emergencyButton = 2;
const int helpButton      = 3; 
const int speakerPin      = 13;
unsigned const volumeDial = A5;
unsigned const LED_R      = 10;
unsigned const LED_B      = 8; 
unsigned const LED_G      = 9;

/*
 * Button Dictionary
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
 * Music Variables
 */
int counter = 0;
bool musicToggle = false; // boolean  variable: used to store music toggle
unsigned int newVolume = 0; // variable: used to store new volume setting
unsigned int oldVolume = 0; // variable: used to store old volume setting
int testMelody[] = {
  NOTE_C5, NOTE_D5, NOTE_F5, NOTE_D5, NOTE_A5, NOTE_A5, NOTE_G5, NOTE_C5, NOTE_D5, NOTE_F5, NOTE_D5, NOTE_G5, NOTE_G5
  , NOTE_F5, NOTE_E5, NOTE_D5, NOTE_C5, NOTE_D5, NOTE_F5, NOTE_D5, NOTE_F5, NOTE_G5, NOTE_E5, NOTE_D5, NOTE_C5, NOTE_C5, NOTE_C5, NOTE_G5, NOTE_F5
};
int noteDurations[] = {
  100,100,100,100,250,250,600,100,100,100,100,250,250,200,100,150,100,100,100,100,300,150,250,100,150,150,150,300,1000
};

/*
 * Interrupt Functions
 */
void toggleMusic(){
  musicToggle = !musicToggle; // switch the state of the musictoggle
  if(musicToggle) counter = 0; // if the music toggle is high (because it doesnt update until after the end of the method) set the counter to 0
  currentPressedButton = buttonMessages[0];
  buttonChanged = true;
}
void LCDPrintHelp(){
  currentPressedButton = buttonMessages[3];
  buttonChanged = true;
}
void LCDPrintEmergency(){
  currentPressedButton = buttonMessages[2];
  buttonChanged = true;
}
void LCDPrintHug(){
  currentPressedButton = buttonMessages[1];
  buttonChanged = true;
}
/*
 * Helper Functions
 */
void setLEDColor(int redVal, int greenVal, int blueVal) { 
  analogWrite(LED_R, redVal);
  analogWrite(LED_G, greenVal);
  analogWrite(LED_B, blueVal);
}

/*
 * Setup Function
 */
void setup(){
  // Serial Port Check (Debugging) //
  Serial.begin(9600);
  while (!Serial) { 
    ; // do nothing
  }
  randomSeed(analogRead(0));

  //vol.DEFAULT_PIN
  // Input Pins //
  pinMode(musicButton, INPUT_PULLUP);
  pinMode(helpButton, INPUT_PULLUP);
  pinMode(emergencyButton, INPUT_PULLUP);
  pinMode(hugButton, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(musicButton), toggleMusic, RISING);
  attachInterrupt(digitalPinToInterrupt(helpButton), LCDPrintHelp, RISING);
  attachInterrupt(digitalPinToInterrupt(emergencyButton), LCDPrintEmergency, RISING);
  attachInterrupt(digitalPinToInterrupt(hugButton), LCDPrintHug, RISING);
  
  // Output Pins //
  pinMode(speakerPin, OUTPUT);
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);
  vol.alternatePin(true);
  vol.begin();
  lcd.begin();
  lcd.backlight();
}
/*
 * Loop Function
 */
void loop() {
  newVolume = analogRead(volumeDial);
  newVolume = map(newVolume, 0, 1024, 0, 100); 
  
  // Play PLUSH Music 
  if (musicToggle) { 
    //tone(speakerPin, testMelody[counter], 100); 
    vol.tone(testMelody[counter], map(newVolume,0,100,0,255));
    vol.fadeOut(noteDurations[counter]);
    vol.delay(noteDurations[counter] * 1.3);
    
    counter++; 
    if(counter >= *(&testMelody + 1) - testMelody){
      counter = 0;
    }
  }else{
    vol.noTone();
  }
  //update LCD
  if(buttonChanged){
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print(currentPressedButton);
    lcd.setCursor(0,1);
    lcd.print("pressed!");
  }
  //update LED
  if(currentPressedButton == buttonMessages[3]){
    setLEDColor(0, 0, 0);
    setLEDColor(255, 255, 255);
    vol.end();
    delay((random(10) * 1000));
    setLEDColor(0, 255, 0);
    delay(5000);
    vol.begin();
    setLEDColor(0, 0, 0);
    currentPressedButton = "";
  }
  //change volume
  if (oldVolume != newVolume) { 
    lcd.setCursor(12,1); 
    lcd.print(newVolume); 
  }
  oldVolume = newVolume;
  buttonChanged = false;
}
