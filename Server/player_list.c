#include "player_list.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Initialize the list
player_list create_player_list(int capacity) {
    player_list list;
    list.players = (player *)malloc(capacity * sizeof(player));
    list.size = 0;
    list.capacity = capacity;
    return list;
}

// Add a player to the list
void add_player(player_list *list, player p) {
    if (list->size >= list->capacity) {
        list->capacity *= 2;
        list->players = (player *)realloc(list->players, list->capacity * sizeof(player));
    }
    list->players[list->size++] = p;
}

// Remove a player by index
void remove_player(player_list *list, int index) {
    if (index < 0 || index >= list->size) return;
    for (int i = index; i < list->size - 1; i++) {
        list->players[i] = list->players[i + 1];
    }
    list->size--;
}

// Get a player by index without removing
player get_player(player_list *list, int index) {
    if (index < 0 || index >= list->size) {
        fprintf(stderr, "Index out of bounds\n");
        exit(EXIT_FAILURE);
    }
    return list->players[index];
}

// Get the size of the player list
int get_player_list_size(player_list *list) {
    return list->size;
}
