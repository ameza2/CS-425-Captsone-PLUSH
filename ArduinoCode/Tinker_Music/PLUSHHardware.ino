/**
   PLUSH Arduino Code:
   Authors: Christian Pilley, Abraham Meza
   Team: Team 20(Christian Pilley, Abraham Meza, Tal Zemach, Josh Insorio, Korben Diarchangel)
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


//LiquidCrystal_I2C lcd(0x27, 16, 2); // I2C address 0x27, 16 column and 2 rows
File f;
TMRpcm Audio;

const int stepsPerRevolution = 2048; // According to stepper motor specs
//Logger logger;

// Pin assignments: Variables holding the pin locations for various I/O
const int musicButton      = 4;
const int hugButton        = 5;
const int emergencyButton  = 6;
const int helpButton       = 7;
const int volumeDownButton = 48;
const int volumeUpButton   = 49;
const int speakerOutPin    = 12;
unsigned const LED_R       = 10;
unsigned const LED_G       = 9;
unsigned const LED_B       = 8;

const int buttonInterruptPin = 2;
const int recieverInterruptPin = 3;
int recieverDataPins[] = {24, 25, 26, 27, 28};

Stepper myStepperRS = Stepper(stepsPerRevolution, 36, 38, 37, 39); // Right shoulder (steps per revolution, pins)
Stepper myStepperLA = Stepper(stepsPerRevolution, 40, 42, 41, 43); // Left arm (steps per revolution, pins)
Stepper myStepperRA = Stepper(stepsPerRevolution, 14, 16, 15, 17); // Right arm (steps per revolution, pins)
Stepper myStepperLS = Stepper(stepsPerRevolution, 44, 46, 45, 47); // Left shoulder(steps per revolution, pins)

bool hugFlag = false;
int hugDuration = 5000; // hug duration (ms)
bool endHug = false;
int hugSens = 0;
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
bool musicToggle = true; // boolean  variable: used to store music toggle
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

  if (digitalRead(musicButton)) {
    musicToggle = !musicToggle; // switch the state of the musictoggle
    currentPressedButton = buttonMessages[0];
    buttonChanged = true;
    Serial.println("Music Button Changed");
  }
  else if (digitalRead(hugButton)) {
    currentPressedButton = buttonMessages[1];
    buttonChanged = true;
    hugFlag = !hugFlag;
    Serial.println("Hug Button Changed");
  }
  else if (digitalRead(emergencyButton)) {
    currentPressedButton = buttonMessages[2];
    buttonChanged = true;
    Serial.println("Emergency Button Changed");
  }
  else if (digitalRead(helpButton)) {
    currentPressedButton = buttonMessages[3];
    buttonChanged = true;
    Serial.println("Help Button Pressed");

  }
  else if (digitalRead(volumeDownButton)) {
    newVolume = max(0, newVolume--);
    Serial.println("Volume Down");
  }
  else if (digitalRead(volumeUpButton)) {
    newVolume = min(7, newVolume++);
    Serial.println("Volume Up");
  }
}

void receiverFunc() {
  Serial.println("Inside reciever function");

  int b4 = digitalRead(recieverDataPins[0]);
  int b3 = digitalRead(recieverDataPins[1]);
  int b2 = digitalRead(recieverDataPins[2]);
  int b1 = digitalRead(recieverDataPins[3]);
  int b0 = digitalRead(recieverDataPins[4]);
  Serial.print(b4);
  Serial.print(b3);
  Serial.print(b2);
  Serial.print(b1);
  Serial.println(b0);

  clearRecieverPins();
  if (b4 == LOW && b3 == LOW)
  { // hug
    if (b0 == HIGH)hugFlag = !hugFlag;
    else endHug = true;
  } else if (b4 == LOW && b3 == HIGH)
  { // change volume
    int val = getByteFromReciever(b2, b1, b0);
    Serial.print("Volume changed from reciever, old volume: ");
    Serial.print(newVolume);
    Serial.print(", new volume: ");
    Serial.print(val);
    newVolume = val;
    Serial.println(newVolume);
  } else if (b4 == HIGH && b3 == LOW)
  { // Toggle music
    musicToggle = !musicToggle;
  } else if (b4 == HIGH && b3 == HIGH)
  { // hug sensitivity
    int val = getByteFromReciever(b2, b1, b0);
    hugSens = val;
  }
}

int getByteFromReciever(int b2, int b1, int b0) {
  int val = 0;
  if (b2 == HIGH) val += 4;
  if (b1 == HIGH) val += 2;
  if (b0 == HIGH) val += 1;
  return val;
}
/*
   Helper Functions
*/
void setLEDColor(int redVal, int greenVal, int blueVal) {
  analogWrite(LED_R, redVal);
  analogWrite(LED_G, greenVal);
  analogWrite(LED_B, blueVal);
}
void clearRecieverPins() {
  for (int i = recieverDataPins[0]; i < recieverDataPins[9]; i++) digitalWrite(i, LOW);
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
  
  for (int i = 24; i <= 33; i++) pinMode(i, INPUT); //for datatransfer
  for (int i = 36; i <= 47; i++) pinMode(i, OUTPUT); //for motors
  for (int i = 14; i <= 17; i++) pinMode(i, OUTPUT);

  attachInterrupt(digitalPinToInterrupt(recieverInterruptPin), receiverFunc, RISING);
  attachInterrupt(digitalPinToInterrupt(buttonInterruptPin), buttonInterrupts, RISING);


  // Output Pins //
  pinMode(speakerOutPin, OUTPUT);
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);

//  //vol.begin();
//  lcd.begin();
//  lcd.backlight();

  if (!SD.begin()) {
    Serial.println("SD CARD UNINITIALIZED");
  } else {
    Serial.println("SD CARD INITIALIZED");
  }
  if (!SD.exists("SWEDEN.wav")) {
    Serial.println("NO WAV FILE FOUND WITH THIS NAME");
  }
  f = SD.open("SWEDEN.wav");
  Audio.speakerPin = 12;
  Audio.play("SWEDEN.wav");
  Audio.setVolume(7);

  // in rpm
  myStepperLA.setSpeed(5);
  myStepperLS.setSpeed(5);
  myStepperRA.setSpeed(5);
  myStepperRS.setSpeed(5);
}



/*
   Loop Function
*/
void loop() {

//  Serial.print(digitalRead(helpButton));
//  Serial.print(digitalRead(emergencyButton));
//  Serial.print(digitalRead(hugButton));
//  Serial.print(digitalRead(musicButton));
//  Serial.print(digitalRead(volumeDownButton));
//  Serial.println(digitalRead(volumeUpButton));

  //DO HUG IF HUG FLAG IS SET
  if (hugFlag) {
    // # of steps
    for (int i = 0; i < 100; i++) {
      myStepperRA.step(stepsPerRevolution / -400);
      myStepperRS.step(stepsPerRevolution / 800);
      myStepperLA.step(stepsPerRevolution / 400);
      myStepperLS.step(stepsPerRevolution / -800);
    }
    delay(hugDuration); //i dont like this delay call, will find a workaround so it doesnt muck up the system.
    for (int i = 0; i < 100; i++) {
      myStepperRA.step(stepsPerRevolution / 400);
      myStepperRS.step(stepsPerRevolution / -800);
      myStepperLA.step(stepsPerRevolution / -400);
      myStepperLS.step(stepsPerRevolution / 800);
    }
    hugFlag = !hugFlag;
  }
  if (musicToggle) {
    if (!Audio.isPlaying()) {
      Audio.pause();
    }
  } else {
    if (Audio.isPlaying()) {
      Audio.pause();
    }
  }

//  //update LCD
//  if (buttonChanged) {
//    lcd.setCursor(0, 1);
//    lcd.print("                ");
//    lcd.setCursor(0, 1);
//    lcd.print(currentPressedButton);
//  }

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
//    lcd.setCursor(14, 1);
//    lcd.print("  ");
//    lcd.setCursor(14, 1);
//    lcd.print(newVolume);
    Audio.setVolume(newVolume);
  }
  oldVolume = newVolume;
  buttonChanged = false;
  currentPressedButton = "";
}
