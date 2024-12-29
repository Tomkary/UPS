#ifndef SERVER_MESSAGE_PARSER_H
#define SERVER_MESSAGE_PARSER_H

#define MSG_COUNT 8

extern char* id[MSG_COUNT];
extern int (*handler_ptr[])(char*);

int handle_connect(char* message);

int handle_join(char* message);

int handle_rejoin(char* message);

int handle_create(char* message);

int handle_leave(char* message);

int handle_pause(char* message);

int handle_taking(char* message);

int handle_staying(char* message);

int handle_playing(char* message);

int handle_turn(char* message);

int handle_ping(char* message);

int handler(char* message);

#endif //SERVER_MESSAGE_PARSER_H
