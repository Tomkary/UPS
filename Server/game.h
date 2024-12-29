#ifndef SERVER_GAME_H
#define SERVER_GAME_H

#include "card_list.h"

#define MAX_CARDS 32
#define COLORS 4
#define VALUES 8
#define CHANGE 6
#define SEVEN 1
#define ACE 8
#define MAX_NAME_LENGTH 50

// Player structure
typedef struct {
    char name[MAX_NAME_LENGTH];
    int id;
    card_list cards;
    int card_count;
    int state;
} player;

void init_decks();

void init_game(player players[], int player_num);

void check_empty_deck();

void change_player(int player_id);

int play(char* card, int color_changing, int player_id);

int take(int player_id);

int stay(int player_id);

#endif //SERVER_GAME_H
