#include <Wire.h>

/*
 * USING EXTERNAL LIBRARY MADE BY FRANK DE BABANDER
 * https://github.com/fdebrabander/Arduino-LiquidCrystal-I2C-library
 * 
 */

#include <LiquidCrystal_I2C.h>

/*Setting up Global Variables*/

int buttonStates[] = {0,0,0};

String buttonMessages[] = {
  "Call button",
  "Hug button",
  "Emergency button"
};

LiquidCrystal_I2C lcd(0x27, 16,2);

/*Arduino setup function*/

void setup() {
    // put your setup code here, to run once:

    //Setting input pins for buttons
    pinMode(2, INPUT);
    pinMode(3, INPUT);
    pinMode(4, INPUT);

    //Starting serial monitor with baud rate of 9600 (this is for debugging)
    Serial.begin(9600);

    //Starting and configuring LCD Monitor.
    //starts the LCD monitor, turns on the backlight, and set its cursor to 0,0
    lcd.begin();
    lcd.backlight();
    lcd.setCursor(0,0);
}

/*Arduino loop function*/
void loop() {
    // put your main code here, to run repeatedly:

    //Reading in the states of the pins and saving them to the array
    buttonStates[0] = digitalRead(2);
    buttonStates[1] = digitalRead(3);
    buttonStates[2] = digitalRead(4);

    //iterating over the array
    for(int i = 0; i < 3; i++){
        //check if the button is pressed
        if(buttonStates[i] == HIGH) {
            //display to serial monitor (debugging, Serial should return null if not connected to a computer)
            if(Serial) Serial.print(buttonMessages[i]);

            //clear the LCD
            lcd.clear();

            //set its cursor back to 0,0
            lcd.setCursor(0,0);

            //print the message
            lcd.print(buttonMessages[i]);

            //set the cursor to the second row
            lcd.setCursor(0,1);
            
            //print the message
            lcd.print("pressed!");
        }
    }
    //have a small delay before checking if another button is pressed.
    delay(100);
}