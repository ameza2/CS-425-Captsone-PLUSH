// Team 20 //
// CS 425 - 1001 //
// Music Library //


// Link: https://www.tinkercad.com/things/aqP4iGdKYbR //

// Button Variable(s) //

unsigned const musicButton = 13; // assign music button digital pin
unsigned int buttonStates[] = {0}; // boolean array: used to store button states

// Music Variable(s) //

unsigned const speakerPin = 12; // assign speaker digital pin
bool musicToggle = false; // boolean  variable: used to store music toggle

// String Dictionary //

String buttonMessages[] = { // string array: used to store LCD button prompts
  "Music button"
};

void setup(){
  
  // Serial Port Check (Debugging) //
  
  Serial.begin(9600); // initialize serial
  while (!Serial) { // while loop: while serial port is closed, wait for serial port to connect (needed for Native USB)
    ; // do nothing
  }
  
  // Input Pins //

  pinMode(musicButton, INPUT_PULLUP);
  
  // Output Pins //

  pinMode(speakerPin, OUTPUT);
}

void loop() {
  
  // Update Button State //
  
  buttonStates[0] = digitalRead(musicButton); // music button state
  
  // Check for Button Toggle //
  
  if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
    musicToggle = !musicToggle; // toggle music feature
  }
  
  ////////// TESTING ////////////////
  
  // Serial.println(musicToggle);  //
  
  ///////////////////////////////////
  
  // Play PLUSH Music //
  
  if (musicToggle == true) { // if statement: if musicToggle == True -> play speaker tune (temp song = Mario Theme) until music toggle (OFF)
    tone(speakerPin, 659, 100); // E5 tone (659 Hz) 
    delay(150); // delay (1.5 ms)
    
    // Update Button State and Check for Music Toggle //
   
    buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
    }

    tone(speakerPin, 659, 100); // E5 tone (659 Hz)  
    delay(300); // delay (3ms)
    
    // Update Button State and Check for Music Toggle //
    
    buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
    }
    
    tone(speakerPin, 659, 100); // E5 tone (659 Hz) 
    delay(300); // delay (3ms)
    
    // Update Button State and Check for Music Toggle //
    
    buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
    }
    
    tone(speakerPin, 523, 100); // C5 tone (523 Hz)
    delay(100); // delay (1ms)
    
    // Update Button State and Check for Music Toggle //
    
    buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
    }
    
    tone(speakerPin, 659, 100); // E5 tone (659 Hz) 
    delay(300); // delay (3ms)
    
    // Update Button State and Check for Music Toggle //
    
   	buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
    }
    
    tone(speakerPin, 784, 100); // G5 tone (784 Hz) 
    delay(550); // delay (5ms)
    
    // Update Button State and Check for Music Toggle //
    
    buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
    }
    
    tone(speakerPin, 392, 100); // G4 tone (392 Hz)
    delay(2000); // delay (2s)
    
    // Update Button State and Check for Music Toggle //
    
    buttonStates[0] = digitalRead(musicButton); // music button state
    if (buttonStates[0] == HIGH) { // if statement: if music button is toggled, toggle music feature
      musicToggle = !musicToggle; // toggle music feature
  	}
  }  
}
