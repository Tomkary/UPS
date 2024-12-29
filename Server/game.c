#include "game.h"
#include "card_list.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

/**
 * normal - 1
 * seven was played - 2
 * ace was played - 3
 * change was played - 4
 */
int game_state = 1;

/**
 * not changing - 0
 * changed to 1 - 1
 * changed to 2 - 2
 * changed to 3 - 3
 * changed to 4 - 4
 */
int change_color = 0;

/**
 * last played card in game
 */
card last_played = {
        .color = 0,
        .value = 0
};

card_list deck;

card_list dis_deck;

int current_player_id = -1;

int player_count = 0;

player* game = NULL;

void init_decks(){
    int i, j;
    initCardList(&deck, MAX_CARDS);
    for(i = 1; i <= COLORS; i++){
        for(j = 1; j <= VALUES; j++){
            addCard(&deck, i, j);
        }
    }
    initCardList(&dis_deck, (MAX_CARDS / 2));
/*
    for (i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }
*/
//    printf("\n\n");
    shuffleCards(&deck);
/*
    for (i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }
*/
}

void init_game(player players[], int player_num){
    int i, j = 0;
    card temp;

    init_decks();

    for(i = 0; i < player_num; i++){
        players[i].card_count = 4;
        initCardList(&(players[i].cards), 4);
        for(j = 0; j < 4; j++) {
            temp = removeCard(&deck, 0);
            addCard(&(players[i].cards), temp.color, temp.value);
        }
    }

    last_played = removeCard(&deck, 0);
    addCard(&dis_deck, last_played.color, last_played.value);

    game = players;
    player_count = player_num;
    current_player_id = players[0].id;

    //TODO poslat zpravu o zahrani karty vsem hracum - asi poslat STATUS

    /*
    for(int i = 0; i < player_count; i ++){
        printf("hrac: %s | id: %d | stav: %d | karet: %d\n", game[i].name, game[i].id, game[i].state, game[i].card_count);
        printf("karty: ");
        while (getCardCount(&(game[i].cards)) != 0){
            card temp = removeCard(&(game[i].cards), 0);
            printf("%d_%d ", temp.color, temp.value);
        }
        printf("\n");
    }
     */
}

void change_player(int player_id){
    size_t i = 0;

    for(i = 0; i < player_count; i++){
        if(game[i].id == player_id){
            current_player_id = game[i + 1].id;
            break;
        }
    }
    if((i + 1) == player_count){
        current_player_id = game[0].id;
    }
}

void check_empty_deck(){
    if(getCardCount(&deck) <= 0){
        freeCardList(&deck);
        deck = dis_deck;
        initCardList(&dis_deck, (MAX_CARDS / 2));
    }
}

int play(char* card_played, int color_changing, int player_id){
    int color = 0;
    int value = 0;
    int i = 0;
    char* endptr = NULL;
    int player_index = -1;
    int card_index = -1;
    card temp;


    //check player
    if(player_id != current_player_id){
        return 0;
    }
    for(i = 0; i < player_count; i++){
        if(game[i].id == player_id){
            player_index = i;
            break;
        }
    }
    if(player_index >= player_count || player_index < 0){
        return 0;
    }

    //check card
    color = strtol(card_played, &endptr, 10);
    endptr++;
    value = strtol(endptr, &endptr, 10);

    if(card_played[1] != '_' || card_played[3] != '\0'){
        return 0;
    }
    if(color < 1 || color > 4 || value < 1 || value > 8){
        return 0;
    }
    if(color_changing < 0 || color_changing > 4){
        return 0;
    }
    //printf("color: %d || value: %d", color, value);

    //check if player has card
    for(i = 0; i < getCardCount(&(game[player_index].cards)); i++){
        temp = getCard(&(game[player_index].cards), i);
        if(temp.value == value && temp.color == color){
            card_index = i;
            break;
        }
    }
    if(card_index == -1){
        return 0;
    }

    //check game conditions

    //changing color
    if(value == CHANGE){
        if(game_state == 2 || game_state == 3){
            return 0;
        }
        if(color_changing == 0){
            return 0;
        }
        game_state = 4;
        change_color = color_changing;
        temp = removeCard(&(game[player_index].cards), card_index);
        addCard(&dis_deck, temp.color, temp.value);
        change_player(player_id);
        last_played.color = color;
        last_played.value = value;
        return 1;
    }

    //playing card after changing color
    if(game_state == 4){
        if(color == change_color){
            if(value == SEVEN){
                game_state = 2;
            }
            else if(value == ACE){
                game_state = 3;
            }
            else{
                game_state = 0;
            }
            change_color = color_changing;
            temp = removeCard(&(game[player_index].cards), card_index);
            addCard(&dis_deck, temp.color, temp.value);
            change_player(player_id);
            last_played.color = color;
            last_played.value = value;
            return 1;
        }
        else{
            return 0;
        }
    }

    //playing color on color
    if(color == last_played.color){
        if(value == SEVEN){
            game_state = 2;
        }
        else if(value == ACE){
            game_state = 3;
        }
        else{
            game_state = 1;
        }
        change_color = color_changing;
        temp = removeCard(&(game[player_index].cards), card_index);
        addCard(&dis_deck, temp.color, temp.value);
        change_player(player_id);
        last_played.color = color;
        last_played.value = value;
        return 1;
    }

    //playing value on value
    if(value == last_played.value){
        if(value == SEVEN){
            game_state = 2;
        }
        else if(value == ACE){
            game_state = 3;
        }
        else{
            game_state = 1;
        }
        change_color = color_changing;
        temp = removeCard(&(game[player_index].cards), card_index);
        addCard(&dis_deck, temp.color, temp.value);
        change_player(player_id);
        last_played.color = color;
        last_played.value = value;
        return 1;
    }

    //non above -> not valid move
    return 0;
}

int take(int player_id){
    card_list take;
    int i = 0;
    card temp;
    int last_index = 0;
    int taking = 0;
    int player_index = -1;

    if(player_id != current_player_id){
        return 0;
    }

    for(i = 0; i < player_count; i++){
        if(game[i].id == player_id){
            player_index = i;
            break;
        }
    }
    if(player_index >= player_count || player_index < 0){
        return 0;
    }

    initCardList(&take, 1);

    if(game_state == 1 || game_state == 4){
        temp = removeCard(&deck, 0);
        check_empty_deck();
        addCard(&(game[player_index].cards), temp.color, temp.value);
        game_state = 1;
        change_player(player_id);
        freeCardList(&take);
        return 1;
    }
    else if(game_state == 2){
        last_index = (getCardCount(&dis_deck) - 1);
        temp = getCard(&dis_deck, last_index);
        while(temp.value == 1){
            taking += 2;
            last_index--;
            if(last_index < 0){
                break;
            }
            temp = getCard(&dis_deck, last_index);
        }
        if(taking != 0){
            for(i = 0; i < taking; i++){
                temp = removeCard(&deck, 0);
                check_empty_deck();
                addCard(&(game[player_index].cards), temp.color, temp.value);
            }
            game_state = 1;
            change_player(player_id);
            freeCardList(&take);
            return 1;
        }
        else{
            freeCardList(&take);
            return 0;
        }
    }
    else{
        freeCardList(&take);
        return 0;
    }
}

int stay(int player_id){
    if(game_state == 2 && last_played.value == 8 && current_player_id == player_id){
        game_state = 1;
        change_player(player_id);
        return 1;
    }
    else{
        return 0;
    }
}
/*
void shuffel(){
    int elesize = sizeof(int);
    int i;
    int r;
    int src [20];
    int tgt [20];

    srand ((unsigned int)time(0));

    for (i = 20; i > 0; i --)
    {
        r = rand() % i;
        memcpy (&tgt [20 - i], &src [r], elesize);
        memcpy (&src [r], &src [i - 1], elesize);
    }
}
*/