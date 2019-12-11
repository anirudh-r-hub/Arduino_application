#include <ESP8266WiFi.h>
#include <PubSubClient.h>

const char* ssid = "TP-LINK_1784";
const char* password = "asdfghjkl";

const char* mqtt_server = "tailor.cloudmqtt.com";

const char *mqtt_user = "ghleymma";
const char *mqtt_pass = "jmvoCCetDGiy";

WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;
char msg[50];
int value = 0;

void setup_wifi()
{
  delay(100);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  
}

void reconnect() {
  while(!client.connected()) {

      String clientId = "ESP8266Client-";
      clientId += String(random(0xffff), HEX);

      if(client.connect(clientId.c_str(), mqtt_user, mqtt_pass)) {
        Serial.println("connected");

        client.publish("outTopic", "hello world");

        client.subscribe("inTopic");
      } else {
        Serial.print("failed");
        delay(5000);
      }
  }
  
}

void callback(char* topic, byte* payload, unsigned int length) {
  //pinMode(LED_BUILTIN, OUTPUT);
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");

  char payloadstr[100];

  //payloadstr1[0] = 'a';
  //payloadstr1[1] = '\0';
  //Serial.println(payloadstr1);

  int i;
  for(i=0;i< length;i++) {
    Serial.print((char)payload[i]);
    payloadstr[i] = (char)payload[i];
    
  }
  payloadstr[i] = '\0';

  Serial.println();
  //Serial.println(payloadstr1);

  
  //if else to on/off led
  
  if(strcmp(payloadstr, "ON") == 0)
  {
    //on the led
    Serial.println("on led success");    
    digitalWrite(LED_BUILTIN, LOW);
    
  } else if(strcmp(payloadstr, "OFF") == 0){
    Serial.println("OFF led success");    
    digitalWrite(LED_BUILTIN, HIGH);
    
  } else {
    Serial.print(strcmp((char*)payload, "OFF"));    
    Serial.print("message is ");
    Serial.println((char*)payload);
    //Serial.println(payload);
  }

}



void setup() {
  // put your setup code here, to run once:
  //pinMode(LED_BUILTIN, OUTPUT);
  Serial.begin(9600);
  setup_wifi();
  client.setServer(mqtt_server, 13968); //server name and port*************
  client.setCallback(callback);
  reconnect();
  if(client.subscribe("abc", 0) == true)
  {
    Serial.println("subscription successful!");  
  }
  else {
    Serial.println("subscription failed!");  
  }
  

}

void loop() {
  // put your main code here, to run repeatedly:
  if(!client.connected()) {
    reconnect();
  }
  client.loop();
 
    //digitalWrite(LED_BUILTIN, LOW);
    //delay(5000);
    //digitalWrite(LED_BUILTIN, HIGH);


  static int h = 30; //random*************************
  static int t = 20; //random************************

  String hh = String(h);
  String msg = String(t);

  Serial.print("Publish message: ");
  Serial.println(msg);

  int numt = t;
  char cstr[16];
  itoa(numt, cstr, 10);

  int numh = h;
  char cshr[16];
  itoa(numh, cshr, 10);

  delay(5000);
  client.publish("dht", cstr);
  client.publish("bmp", cshr);

  h++;
  t++;
}
