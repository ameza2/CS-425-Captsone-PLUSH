// Team 20 //
// CS 425 - 1001 //
// PLUSH Capstone Project //


/* Things to Work On:

- Add Volume Dial w/ LCD Display
- Add Servos tied to Hug Button?
- Add Speaker tied to Volume Dial and Music Button?

*/


// Header File(s) //

#include <LiquidCrystal.h>

LiquidCrystal lcd(13,12,11,10,9,8); // LCD Display PinMode (Library)

// Button Variable(s) //

unsigned const helpButton = 4; // assign help button digital pin
unsigned const hugButton = 5; // assign hug button digital pin
unsigned const emergencyButton = 6; // assign emergency button digital pin
unsigned const musicButton = 7; // assign music button digital pin

unsigned int buttonStates[] = {0,0,0,0}; // boolean array: used to store button states

// LED Variable(s) //

unsigned const LEDR = 1; // assign LED Red digital pin
unsigned const LEDB = 2; // assign LED Blue digital pin
unsigned const LEDG = 3; // assign LED Green digital pin

// String Dictionary //

String buttonMessages[] = { // string array: used to store LCD button prompts
  "Call button",
  "Hug button",
  "Emergency button",
  "Music button"
};

void setup(){
  
  // Serial Port Check (Debugging) //
  
  Serial.begin(9600); // initialize serial
  while (!Serial) { // while loop: while serial port is closed, wait for serial port to connect (needed for Native USB)
    ; // do nothing
  }
  
  // Random Number Generator //
  
  randomSeed(analogRead(0));
  
  // Input Pins //
  
  pinMode(helpButton, INPUT);
  pinMode(hugButton, INPUT);
  pinMode(emergencyButton, INPUT); 
  pinMode(musicButton, INPUT);
  
  // Output Pins //
  
  pinMode(LEDR, OUTPUT);
  pinMode(LEDG, OUTPUT);
  pinMode(LEDB, OUTPUT);
  
  // LCD Initialization //
  
  lcd.begin(16, 2);  // set up the LCD's number of columns and rows:

}

void loop(){
  // Update Button States //
  
  buttonStates[0] = digitalRead(helpButton); // help button state
  buttonStates[1] = digitalRead(hugButton); // hug button state
  buttonStates[2] = digitalRead(emergencyButton); // emergency button state
  buttonStates[3] = digitalRead(musicButton); // music button state

  // Check Button States: Output Toggle Message //
  
  for(int i = 0; i < sizeof(buttonStates) / sizeof(int); i++) { // for loop: check to see if PLUSH unit buttons have been pressed. If any buttons were pressed, send signal to PLUSH application    
    if(buttonStates[i] == HIGH) { // if statement: if button is pressed, send signal to PLUSH application    
      lcd.clear(); // clear LCD display/data and position cursor in upper-left corner
      
      lcd.setCursor(0,0); // set LCD display cursor back to [0,0]: first row
      
      lcd.print(buttonMessages[i]); // print button state message
      
      lcd.setCursor(0,1); // set LCD display cursor to [0,1]: second row
      
      lcd.print("pressed!"); // print toggle messsage
      
      if(buttonMessages[i] == "Call button") {
        RGB_color(255, 255, 255); // White
        delay((random(10) * 1000));

        RGB_color(0, 0, 255); // Green
        delay((random(10) * 1000));

        RGB_color(0, 0, 0); // Clear 
      }
    }
  }
  
  delay(100); // delay 1ms before checking if another buttoon has been pressed.
}

void RGB_color(int redVal, int greenVal, int blueVal) {
  analogWrite(LEDR, redVal);
  analogWrite(LEDG, greenVal);
  analogWrite(LEDB, blueVal);
}
