#ifndef SERVER_GAME_H
#define SERVER_GAME_H

#include "card_list.h"
#include "player_list.h"

#define MAX_CARDS 32
#define COLORS 4
#define VALUES 8
#define CHANGE 6
#define SEVEN 1
#define ACE 8

typedef struct {
    int game_state;
    int change_color;
    card last_played;
    card_list deck;
    card_list dis_deck;
    int current_player_id;
    int player_count;
    char last_turn;
    player* game;
    int started;
} Game;

void init_decks(Game* curr_game);

void init_game(Game* curr_game, player players[], int player_num);

void check_empty_deck(Game* curr_game);

void change_player(Game* curr_game, int player_id);

int play(Game* curr_game, char* card, int color_changing, int player_id);

int take(Game* curr_game, int player_id, int* take_count, card_list* card_arr);

int stay(Game* curr_game, int player_id);

#endif //SERVER_GAME_H
