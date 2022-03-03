#include <iostream>
#include <wiringPi.h>
#include <fstream>
#include <cmath>
#include <stdint.h>

#include <pthread.h>
#include <signal.h>

//network
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#define PORT 8080
#define MAXLINE 10
int sockfd;
char buffer[MAXLINE];
struct sockaddr_in servaddr, cliaddr;
int stop_server = 0;

#define TRIG 0
#define ECHO 2

//The pin 25 is physical 22 and Wpi 6.
#define LED 6

#define CLOSE 18
#define MEDIUM 21
#define FAR 60
#define DLIMIT 12

#define POS_LEFT 0.055
#define POS_RIGHT 0.24
#define POS_FORWARD 0.14
using namespace std;

/*
g++  LedBlinkSonar.c -l wiringPi -lpthread -o  LedBlinkSonar
 
*/

//Dato globale
	int cm ;
	int doblink;


void setup() {
	//cout << "setUp " << endl;
	wiringPiSetup();
	pinMode(TRIG, OUTPUT);
	pinMode(ECHO, INPUT);
	//TRIG pin must start LOW
	digitalWrite(TRIG, LOW);
	pinMode(LED, OUTPUT);
	delay(30);
}

void ctrlc_close(int s){
    printf("Exiting...");
    stop_server = 1;
    close(sockfd);
    exit(1);
}
void create_socket(){
    // Creating socket file descriptor
    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) {
        perror("socket creation failed");
        exit(EXIT_FAILURE);
    }
    memset(&servaddr, 0, sizeof(servaddr));
    memset(&cliaddr, 0, sizeof(cliaddr));
    // Filling server information
    servaddr.sin_family    = AF_INET; // IPv4
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(PORT);
    // Bind the socket with the server address
    if ( bind(sockfd, (const struct sockaddr *)&servaddr, sizeof(servaddr)) < 0 ) {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }
    signal(SIGINT, ctrlc_close);
}

void* server_service(void *){
    stop_server = 0;
    socklen_t len;
    while(!stop_server){
        len = sizeof(cliaddr);
    	int n = recvfrom(sockfd, (char *)buffer, MAXLINE, 0, (struct sockaddr*) &cliaddr, &len);
    	buffer[n] = '\0';
    	printf("Client : %s ; ", buffer);
    	sprintf(buffer, "%d", cm);
    	sendto(sockfd, (const char *)buffer, strlen(buffer), 0, (const struct sockaddr *) &cliaddr, len);
    	printf("Reply sent.\n");

    }
    return NULL;
}

void server_thread(){
    static pthread_t server_th;
    int rc = pthread_create(&server_th, NULL, server_service, (void *)0 );
    if (rc) {
	printf("ERORR; return code from pthread_create() is %d\n", rc);
        exit(EXIT_FAILURE);
    }
}


int getCM() {
	//Send trig pulse
	digitalWrite(TRIG, HIGH);
	delayMicroseconds(20);
	digitalWrite(TRIG, LOW);

	//Wait for echo start
	while(digitalRead(ECHO) == LOW);

	//Wait for echo end
	long startTime = micros();
	while(digitalRead(ECHO) == HIGH);
	long travelTime = micros() - startTime;

	//Get distance in cm
	int distance = travelTime / 58;

	return distance;
}

//TODO: attivare un processo: vedi blinkTheLedInThread
void blinkTheLed( int cm ) {
  if( cm < DLIMIT ){
	  for (int i=1; i<=5; i++){
	    digitalWrite (LED, HIGH) ;  // On
	    delay (250 ) ;              // mS
	    digitalWrite (LED, LOW) ;   // Off
	    delay (250 ) ;
	  } 
  }
}

void *blink(void *arg){
  int64_t result = 1;
  int dt = *(int*)arg;
  printf ("Thread %ld \n",  pthread_self() );
  while ( doblink ){
    digitalWrite (LED, HIGH) ;  // On
    delay (dt ) ;              // mS
    digitalWrite (LED, LOW) ;   // Off
    delay (dt ) ;
  } 
  printf ("Thread EXIT   \n"  );
  pthread_exit ((void*)result);
  return NULL;
} 

// standard Posix

void blinkTheLedInThread( int cm ){
	static pthread_t th1;
	int dt = 250;
	int ret;
	void* retval;
	//printf("blinkTheLedInThread cm= %d ", cm);
	if( cm > DLIMIT ){  
 		digitalWrite(LED, LOW);
 		// int pthread_join (pthread_t tid, void **status);
 		// altrimenti rimangono allocate le aree di memoria assegnate al thread
 		if( doblink == 1 ){
            		//printf("stopping blink %ld\n", th1);
 			doblink = 0;
 			//delay ( 1000 ) ;  
	  		ret = pthread_join(th1, &retval);
	  		printf("ret = %d \n", ret);
	  		if (ret != 0) printf ("join fallito  \n " ); 
	        	if (retval == PTHREAD_CANCELED)
	            		printf("The thread was canceled - ");
	        	else
	            		printf("End of thread. Returned value= %d \n", retval);	 
 		}
	}else{ 
		if( doblink == 0 ){
	 		doblink=1;
			// int pthread_create (pthread_t *tid, const p_thread_attr*attr, (void *(*func)(void *), void *arg);
			int rc = pthread_create(&th1, NULL, blink, (void *)&dt );
         		if (rc) {
            			printf("ERORR; return code from pthread_create() is %d\n", rc);
            			exit(EXIT_FAILURE);
        		}
 		}
   	}
}



int main(void) {
	setup();
	create_socket();
	server_thread();
	while(1) {
 		cm = getCM();
 		
 		//blinkTheLed( cm ); //blocca il flusso
 		//cm potrebbe essere > 3000
 		if( cm < 100 ){
 			blinkTheLedInThread( cm );
 			cout <<  cm   << endl ;  //flush after ending a new line
			delay(30);
		}
	}
 	return 0;
}
