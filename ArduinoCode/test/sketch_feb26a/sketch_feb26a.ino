//#include <LiquidCrystal_I2C.h>

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266mDNS.h>
#include <ESP8266WebServer.h>
#include <WiFiUDP.h>

char plush_id[255] = "789123456";

ESP8266WiFiMulti wifiMulti;     // Create an instance of the ESP8266WiFiMulti class, called 'wifiMulti'

//https://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/udp-examples.html
WiFiUDP udp; // Create a UDP address for recieving broadcasts
unsigned int udpPort = 4210;
char incomingPacket[256];

int musicVol = -1;
int hugSen = -1;
int currSong = -1;
int alert = 0;
enum CMD {VOL, HUG, TGM, SEN};
//using enum CMD;
ESP8266WebServer server(80);    // Create a webserver object that listens for HTTP request on port 80

//LiquidCrystal_I2C lcd(0x27, 16, 2); // (default address, rows, columns)


void handleRoot();              // function prototypes for HTTP handlers
void handleLogin();
void handleNotFound();

void valToByte(int* dbp, int val);

String prevData;

void setup(void) {
  pinMode(0, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(16, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(12, OUTPUT);
  Serial.begin(115200);         // Start the Serial communication to send messages to the computer
  delay(10);
  Serial.println('\n');
  Serial.println("Version Code: 0011"); // Debugging, just to see if reset worked

  randomSeed(analogRead(0)); // Set up random seed (pin 0 needs to be disconnected)

  wifiMulti.addAP("PLUSH", "password123");   // add Wi-Fi networks you want to connect to
  //wifiMulti.addAP("ssid_from_AP_2", "your_password_for_AP_2");
  //wifiMulti.addAP("ssid_from_AP_3", "your_password_for_AP_3");

  Serial.println("Connecting ...");
  int i = 0;
  while (wifiMulti.run() != WL_CONNECTED) { // Wait for the Wi-Fi to connect: scan for Wi-Fi networks, and connect to the strongest of the networks above
    delay(250);
    Serial.print('.');
  }
  Serial.println('\n');
  Serial.print("Connected to ");
  Serial.println(WiFi.SSID());               // Tell us what network we're connected to
  Serial.print("IP address:\t");
  Serial.println(WiFi.localIP());            // Send the IP address of the ESP8266 to the computer

  if (MDNS.begin("esp8266")) {              // Start the mDNS responder for esp8266.local
    Serial.println("mDNS responder started");
  } else {
    Serial.println("Error setting up MDNS responder!");
  }

  server.on("/", HTTP_GET, handleRoot);        // Call the 'handleRoot' function when a client requests URI "/"
  server.onNotFound(handleNotFound);           // When a client requests an unknown URI (i.e. something other than "/"), call function "handleNotFound"

  server.begin();                            // Actually start the server
  Serial.println("HTTP server started");

  udp.begin(udpPort);

  //lcd.begin();
  //lcd.backlight();
  //lcd.setCursor(0,0);
  //lcd.print(WiFi.localIP()); // print IP on LCD
}

void loop(void) {
  server.handleClient();
  if ( server.hasArg("data") ) {
    String data = server.arg("data");
    if (prevData != data) { // only print when something changes
      //lcd.setCursor(0,0);
      //lcd.print("                ");
      //lcd.setCursor(0,0);
      Serial.println(data);
      //lcd.print(data);
    }
    server.send(200, "text/plain", server.arg("data"));
    prevData = data;
  }

  int packetSize = udp.parsePacket();
  if (packetSize)
  {

    //Serial.printf("Received %d bytes from %s, port %d\n", packetSize, udp.remoteIP().toString().c_str(), udp.remotePort());
    int len = udp.read(incomingPacket, 255);
    if (len > 0)
    {
      incomingPacket[len] = '\0';
    }
    //Serial.printf("UDP packet contents: %s\n", incomingPacket);

    char cmd[5]; // Get the command needed
    memcpy(cmd, &incomingPacket[0], 4);
    cmd[4] = '\0';

    char action[255]; // Get the action
    memcpy(action, &incomingPacket[5], 255);


    //=====================================================================================
    // Command CONN: App wants to connect to unit, send IP of this unit if the ID's match.
    //=====================================================================================
    if (strcmp(cmd, "CONN") == 0) {
      if (strcmp(action, plush_id) == 0) {
        udp.beginPacket(udp.remoteIP(), udp.remotePort());
        udp.write(WiFi.localIP().toString().c_str());
        udp.endPacket();
      }
    }

    //=====================================================================================
    // Command UPDT: App asks for updates on the unit's button set volume and hug sensitivity.
    //=====================================================================================
    if (strcmp(cmd, "UPDT") == 0) {
      //Serial.printf("Update Requested");

      // PARSING
      char newHugSen[255];
      char newMusicVol[255];
      char newMusicSong[255];
      newHugSen[0] = '\0';
      newMusicVol[0] = '\0';
      newMusicSong[0] = '\0';
      int t = 0;

      for (int i = 0; i < 255; i++) {
        if (action[i] == '/') {
          t++;
        }
        if (action[i] == '\0') {
          break;
        }
        if (isdigit(action[i])) {
          switch (t) {
            case 0: // Probably can be written nicer
              char newChar0[1];
              newChar0[0] = action[i];
              strcat(newHugSen, newChar0);
              break;
            case 1:
              char newChar1[1];
              newChar1[0] = action[i];
              strcat(newMusicVol, newChar1);
              break;
            case 2:
              char newChar2[1];
              newChar2[0] = action[i];
              if (newChar2[0] == '1') {
                alert = 0;
              }
              break;
            case 3:
              char newChar3[1];
              newChar3[0] = action[i];
              strcat(newMusicSong, newChar3);
            default:
              break;
          }
        }
      }

      if (hugSen == -1 || musicVol == -1) {
        hugSen = atoi(newHugSen);
        musicVol = atoi(newMusicVol);

        if (newMusicSong[0] != '\0') {
          setMusic(atoi(newMusicSong));
        }
      }


      char packetToSend[256];
      sprintf(packetToSend, "HS: %d / MV: %d / AL: %d", hugSen, musicVol, alert);

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(packetToSend);
      udp.endPacket();

      // FOR TESTING PURPOSES ONLY: The app will randomly alert.

      /*long rand = random(1000);
        if (rand < 10) {
        alert = 1;
        }
        if (rand > 990) {
        alert = 2;
        }*/

    }

    //=====================================================================================
    // Command HSEN: App orders unit to adjust the hug sensitivity.
    //=====================================================================================
    if (strcmp(cmd, "HSEN") == 0) {
      hugSen = atoi(action);
      Serial.printf("New sensitivity: %d\n", hugSen);
      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
      sendMessageToMain(SEN, hugSen-1, false);
    }

    //=====================================================================================
    // Command MVOL: App orders unit to change the volume of the speakers.
    //=====================================================================================
    if (strcmp(cmd, "MVOL") == 0) {
      musicVol = atoi(action);
      Serial.printf("New volume: %d\n", musicVol);
      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
      sendMessageToMain(VOL, map(musicVol, 0, 100, 0, 7), false);
    }

    //=====================================================================================
    // Command HUGP: App orders bear to hug/stop hugging patient.
    //=====================================================================================
    if (strcmp(cmd, "HUGP") == 0) {
      int toHug = atoi(action);

      if (toHug == 1) {
        startHug();
      }
      else {
        stopHug();
      }

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }

    //=====================================================================================
    // Command PMUS: App orders music to play/stop playing from speakers.
    //=====================================================================================
    if (strcmp(cmd, "PMUS") == 0) {
      int toPlay = atoi(action);

      if (toPlay == 1) {
        startMusic();
      }
      else {
        stopMusic();
      }

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }
    //=====================================================================================
    // Command SMUS: App orders unit to change the music to be played.
    //=====================================================================================
    if (strcmp(cmd, "SMUS") == 0) {
      int musToSet = atoi(action);

      setMusic(musToSet);

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }

    //=====================================================================================
    // Command ACKA: App acknolwedges that the alert has been called, and will turn off the light.
    //=====================================================================================
    if (strcmp(cmd, "ACKA") == 0) {

      acknowledgeAlert();

      udp.beginPacket(udp.remoteIP(), udp.remotePort());
      udp.write(incomingPacket);
      udp.endPacket();
    }
  }
}

void handleRoot() {                          // When URI / is requested, send a web page with a button to toggle the LED
  if ( server.hasArg("data") ) {
    Serial.println(server.arg("data"));
    server.send(200, "text/plain", server.arg("data"));
  }
}

void handleNotFound() {
  server.send(404, "text/plain", "404: Not found"); // Send HTTP status 404 (Not Found) when there's no handler for the URI in the request
}

//---------------------------------------------------------
// This is where the hardware code comes in
//---------------------------------------------------------

void startHug() {
  Serial.printf("Started Hug!\n");
  sendMessageToMain(HUG, 0, true);
}

void stopHug() {
  Serial.printf("Stopped Hug!\n");
  sendMessageToMain(HUG, 0, false);
}

void startMusic() {
  Serial.printf("Started Music!\n");
  sendMessageToMain(TGM, 0, true);
}

void stopMusic() {
  Serial.printf("Stopped Music!\n");
  sendMessageToMain(TGM, 0, false);
}

void setMusic(int m) {
  Serial.printf("Set Music: %d\n", m);
}

void acknowledgeAlert() {
  Serial.printf("Alert acknlowedged!\n");
}
/*
  void valToByte(int* dbp, int val) {
  if (val > 63) return;

  digitalWrite(28, HIGH);
  digitalWrite(29, HIGH);
  digitalWrite(30, HIGH);
  digitalWrite(31, HIGH);
  digitalWrite(32, HIGH);
  digitalWrite(33, HIGH);

  Serial.println(val);

  if (val > 31) {
    digitalWrite(28, HIGH);
    val -= 32;
    Serial.println(val);
  }
  if (val > 15) {
    digitalWrite(29, HIGH);
    val -= 16;
    Serial.println(val);
  }
  if (val > 7) {
    digitalWrite(30, HIGH);
    val -= 8;
    Serial.println(val);
  }
  if (val > 3) {
    digitalWrite(31, HIGH);
    val -= 4;
    Serial.println(val);
  }
  if (val > 1) {
    digitalWrite(32, HIGH);
    val -= 2;
    Serial.println(val);
  }
  if (val == 1) {
    digitalWrite(33, HIGH);
  }
  Serial.print(digitalRead(28));
  Serial.print(digitalRead(29));
  Serial.print(digitalRead(30));
  Serial.print(digitalRead(31));
  Serial.print(digitalRead(32));
  Serial.println(digitalRead(33));
  }*/

void sendMessageToMain(CMD command, int value, bool optionalFlag) {
  Serial.println("\n\n\nSending message to main board");
  int pinToInterrupt = 0;
  int digitalBytePins[] = { 2, 16, 5, 4,12};

  /*
    00|000
    ^  ^ 
    |  |________ Bytes for sending
    |___________ 00 = HUG
                 01 = VOL
                 10 = TGM
                 11 = SEN
     
  */

  digitalWrite(digitalBytePins[0], LOW);
  digitalWrite(digitalBytePins[1], LOW);
  digitalWrite(digitalBytePins[2], LOW);
  digitalWrite(digitalBytePins[3], LOW);
  digitalWrite(digitalBytePins[4], LOW);


  switch (command) {
    case HUG:
    Serial.println("HUG");
      digitalWrite(digitalBytePins[0], LOW);
      digitalWrite(digitalBytePins[1], LOW);
      digitalWrite(digitalBytePins[2], LOW);
      digitalWrite(digitalBytePins[3], LOW);
      digitalWrite(digitalBytePins[4], LOW);
      if (optionalFlag) {
        digitalWrite(digitalBytePins[4], HIGH);
      }
      break;
    case VOL:
     Serial.println("VOL");
      digitalWrite(digitalBytePins[0], LOW);
      digitalWrite(digitalBytePins[1], HIGH);
      if (value > 3) {
        digitalWrite(digitalBytePins[2], HIGH);
        value -= 4;
        Serial.println(value);
      }
      if (value > 1) {
        digitalWrite(digitalBytePins[3], HIGH);
        value -= 2;
        Serial.println(value);
      }
      if (value == 1) {
        digitalWrite(digitalBytePins[4], HIGH);
      };
      break;
    case TGM:
     Serial.println("TGM");
      digitalWrite(digitalBytePins[0], HIGH);
      digitalWrite(digitalBytePins[1], LOW);
      digitalWrite(digitalBytePins[2], LOW);
      digitalWrite(digitalBytePins[3], LOW);
      digitalWrite(digitalBytePins[4], LOW);
      if (optionalFlag) {
        digitalWrite(digitalBytePins[4], HIGH);
      }
      break;
    case SEN:
     Serial.println("SEN");
      digitalWrite(digitalBytePins[0], HIGH);
      digitalWrite(digitalBytePins[1], HIGH);
      if (value > 3) {
        digitalWrite(digitalBytePins[2], HIGH);
        value -= 4;
        Serial.println(value);
      }
      if (value > 1) {
        digitalWrite(digitalBytePins[3], HIGH);
        value -= 2;
        Serial.println(value);
      }
      if (value == 1) {
        digitalWrite(digitalBytePins[4], HIGH);
      };
      break;
  }
  digitalWrite(pinToInterrupt, HIGH);
  Serial.print(digitalRead(digitalBytePins[0]));
  Serial.print(digitalRead(digitalBytePins[1]));
  Serial.print(digitalRead(digitalBytePins[2]));
  Serial.print(digitalRead(digitalBytePins[3]));
  Serial.println(digitalRead(digitalBytePins[4]));
}
