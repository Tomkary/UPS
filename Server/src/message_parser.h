#ifndef SERVER_MESSAGE_PARSER_H
#define SERVER_MESSAGE_PARSER_H

//#define MSG_COUNT 8
#include "room_list.h"

#define MSG_COUNT 6

extern char* id[MSG_COUNT];
extern int (*handler_ptr[])(char*);

void send_lobby(room_list* rooms, int client_socket);

void infrom_start(Room* room,int player_count);

void inform_status(Room* room, int player_count);

void send_take(card_list* takes, int socket);

int inform_win(Room* room);

int handle_connect(char* message, char* player_name);

int handle_start(char* message, int* p_id);

int handle_join(char* message, int* room_id, int* player_id);

int handle_rejoin(char* message);

int handle_create(char* message);

int handle_leave(char* message, int* player_id);

int handle_pause(char* message);

int handle_taking(char* message, int* player_id);

int handle_staying(char* message, int* player_id);

int handle_playing(char* message, char card[], int* player_id, int* color);

int handle_turn(char* message, char card[], int* player_id, int* color);

int handle_ping(char* message);

int handler(char* message);

#endif //SERVER_MESSAGE_PARSER_H
