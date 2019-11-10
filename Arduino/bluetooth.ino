// inclui bibilioteca do servomotor
#include <Servo.h>
#include <SoftwareSerial.h>

//Define as portas do bluetooth
SoftwareSerial BT(8, 9); // RX, TX

// Stores response of bluetooth device
char cmd;
String command = "";

//define pinos dos servos
#define pinServ1 2
#define pinServ2 3
#define pinServ3 4
#define pinServ4 5

//pos_inicial, min, max
int lim_m1[]={90,0,180};
int lim_m2[]={60,0,180};
int lim_m3[]={70,0,180};
int lim_m4[]={110,0,180};

//Obtém o numero do motor na string recebida, numero antes do '#'
int get_codigo(String string){
  String n = "";
  for(int i = 0;string[i] != '#'; i++){
     n += string[i];
   }
   return n.toInt();
}
//Obtém, o valor da string recebida, numero após '#'
long get_valor(String string){
  String n = "";
  int i = 0;
  for(; string[i] != '#'; i++){
    continue;
  }
  for(i++; i < string.length(); i++){
     n += string[i];
  }
  return n.toInt();
}

// nomeia os servos
Servo serv1,serv2,serv3,serv4;

// cria as variavies dos angulos de cada motor
int motor1=0,motor2=0,motor3=0,motor4=0;

//Inicia o timer do mostrador e define seu intervalo
unsigned long mostradorTimer = 1;
const unsigned long intervaloMostrador = 2000;

void setup() {
  // HC-06 usually default baud-rate
  BT.begin(9600);
  //inicia o monitor serial
  Serial.begin(9600); 
  // atribui pinos dos servos
  serv1.attach(pinServ1);
  serv2.attach(pinServ2);
  serv3.attach(pinServ3);
  serv4.attach(pinServ4);
  //Volta o robô a uma posição inicial.
  motor1=lim_m1[0];
  motor2=lim_m2[0];
  motor3=lim_m3[0];
  motor4=lim_m4[0];
  
  serv1.write(motor1);
  delay(500);
  serv2.write(motor2);
  delay(500);
  serv3.write(motor3);
  delay(500);
  serv4.write(motor4)
  delay(1600);
}

void loop(){
  if (BT.available()) {
    while(BT.available()){
        delay(10);
        char c = BT.read();
        command += c;
      }
      Serial.println("COMANDO-->"+command);

      int codigo = get_codigo(command);
      int valor = get_valor(command);

      int v = 0;
      switch(codigo){
        case 11:
          v = motor2 + valor;
          motor2 = ( v > lim_m2[2])?lim_m2[2] : v;
          Serial.println("2GT"+motor2);
        break;
        case 12:
          v = motor1 - valor;
          motor1 = ( v < lim_m1[1])?lim_m1[1] : v;
          Serial.println("1GT"+motor1);
        break;
        case 13:
          v = motor2 - valor;
          motor2 = ( v < lim_m2[1])?lim_m2[1] : v;
          Serial.println("2GT"+motor2);
        break;
        case 14:
          v = motor1 + valor;
          motor1 = ( v > lim_m1[2])?lim_m1[2] : v;
          Serial.println("1GT"+motor1);
        break;
        
        case 21:
          v = motor3 - valor;
          motor3 = ( v < lim_m3[1])?lim_m3[1] : v;
          Serial.println("3GT"+motor3);
        break;
        case 22:
          v = motor4 + valor;
          motor4 = ( v > lim_m4[2])?lim_m4[2] : v;
          Serial.println("4GT"+motor4);
        break;
        case 23:
          v = motor3 + valor;
          motor3 = ( v > lim_m3[2])?lim_m3[2] : v;
          Serial.println("3GT"+motor3);    
        break;
        case 24:
          v = motor4 - valor;
          motor4 = ( v < lim_m4[1])?lim_m4[1] : v;
          Serial.println("4GT"+motor4);
        break;
        case 30:
          motor1=lim_m1[0];
          motor2=lim_m2[0];
          motor3=lim_m3[0];
          motor4=lim_m4[0];
        break;
      }

      serv1.write(motor1);
      delay(200);
      serv2.write(motor2);
      delay(200);
      serv3.write(motor3);
      delay(200);
      serv4.write(motor4)
  
      command = "";
      
    }//if (BT.available()) 


    //Envio para o monitor serial do posicionamentos dos motores
    if ((millis() - mostradorTimer) >= intervaloMostrador) {
      Serial.println("**********************************************");
      Serial.print(" Angulo Motor1:");
      Serial.println(serv1.read());
      Serial.print(" Angulo Motor2:");
      Serial.println(serv2.read());
      Serial.print(" Angulo Motor3:");
      Serial.println(serv3.read());
      Serial.print(" Angulo Motor4:");
      Serial.println(serv4.read());
    
        mostradorTimer = millis();
    }

    // tempo de espera para recomeçar
    delay(100);  
    
} //void loop()
