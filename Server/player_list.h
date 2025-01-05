#ifndef SERVER_PLAYER_LIST_H
#define SERVER_PLAYER_LIST_H

#include "card_list.h"

#define CAPACITY 100
#define MAX_NAME_LENGTH 50

typedef struct {
    char name[MAX_NAME_LENGTH];
    int id;
    card_list cards;
    int card_count;
    int state;
    int room_id;
    int socket;
} player;

typedef struct {
    player *players;
    int size;
    int capacity;
} player_list;

player_list create_player_list(int capacity);

// Add a player to the list
void add_player(player_list *list, player p);

// Remove a player by index
void remove_player(player_list *list, int index);

// Get a player by index without removing
player get_player(player_list *list, int index);

// Get the size of the player list
int get_player_list_size(player_list *list);

#endif //SERVER_PLAYER_LIST_H
