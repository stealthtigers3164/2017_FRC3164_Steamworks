#include <Wire.h>
#include <LIDARLite.h>

#define PWM_PIN 52
/*
 Connections:
  LIDAR-Lite 5 Vdc (red) to Arduino 5v
  LIDAR-Lite I2C SCL (green) to Arduino SCL
  LIDAR-Lite I2C SDA (blue) to Arduino SDA
  LIDAR-Lite Ground (black) to Arduino GND
  
 */


volatile int pwm_value = 0;
volatile int prev_time = 0;

LIDARLite lidar;

void setup() {
  Serial.begin(115200);
  attachInterrupt(PWM_PIN, ultra, CHANGE);
  lidar.begin(0, true);
  /*
    configure(int configuration, char lidarliteAddress)

    Selects one of several preset configurations.

    Parameters
    ----------------------------------------------------------------------------
    configuration:  Default 0.
      0: Default mode, balanced performance.
      1: Short range, high speed. Uses 0x1d maximum acquisition count.
      2: Default range, higher speed short range. Turns on quick termination
          detection for faster measurements at short range (with decreased
          accuracy)
      3: Maximum range. Uses 0xff maximum acquisition count.
      4: High sensitivity detection. Overrides default valid measurement detection
          algorithm, and uses a threshold value for high sensitivity and noise.
      5: Low sensitivity detection. Overrides default valid measurement detection
          algorithm, and uses a threshold value for low sensitivity and noise.
    lidarliteAddress: Default 0x62. Fill in new address here if changed. See
      operating manual for instructions.
  */
  lidar.configure(0); // Change this number to try out alternate configurations
}
 
void loop() {
  String output = "|u:";
  output = output + pwm_value + "|l:";
  int distLidar = lidar.distance() * 10;
  output = output + distLidar;
  Serial.println(output + "|"); 
  delay(0.06*1000); 
}



 
void ultra() {
  if(digitalRead(PWM_PIN) == HIGH) {
    prev_time = micros();
  } else {
    if(prev_time != 0) {
      pwm_value = micros() - prev_time;
      prev_time = 0;
    }
  }
}

