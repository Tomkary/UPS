#ifndef SERVER_ROOM_LIST_H
#define SERVER_ROOM_LIST_H

#include "player_list.h"
#include "game.h"

#define MAX_PLAYERS 4

typedef struct {
    int id;
    Game* game;
    player players[MAX_PLAYERS];
} Room;

typedef struct {
    Room *rooms;
    int size;
    int capacity;
} room_list;

// Initialize the list
room_list create_room_list(int capacity);

// Add a room to the list
void add_room(room_list *list, Room r);

// Remove a room by index
void remove_room(room_list *list, int index);

// Get a room by index without removing
Room* get_room(room_list *list, int index);

// Get the size of the room list
int get_room_list_size(room_list *list);

#endif //SERVER_ROOM_LIST_H
