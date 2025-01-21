#include "message_parser.h"
#include "message_sender.h"
#include "game.h"
#include "room_list.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

char* id[MSG_COUNT] = {
    //"connect",
  //  "join",
  //  "create",
  //  "rejoin",
 //   "leave",
 //   "pause",
 //   "turn",
 //   "ping"
};

int (*handler_ptr[])(char*)= {
    //    handle_connect,
    //    handle_join,
    //    handle_create,
    //    handle_rejoin,
    //    handle_leave,
    //    handle_pause,
    //    handle_turn,
    //    handle_ping
};

void send_lobby(room_list* rooms, int client_socket){
    char message[100] = "lobby|";
    char num[5];
    int index = 0;
    int count = get_room_list_size(rooms);
    if(count == 0){
        return;
    }

    sprintf(num, "%d|", count);
    strcat(message, num);

    for(int i = 0; i < count; i++){
        int r_id = get_room(rooms, i)->id;
        sprintf(num, "%d|", r_id);
        strcat(message, num);
    }
    strcat(message, "\n");

    while(message[index] != '\0'){
        index++;
    }
    index += 7;

    write(client_socket, message, index);
}

void infrom_start(Room* room, int player_count){
    for(int i = 0; i < MAX_PLAYERS; i++){
        char message[200] = "start|4|";
        char card[2];
        if(room->players[i].id != -1){
            for(int j = 0; j < room->players[i].card_count; j++){
                sprintf(card, "%d", getCard(&room->players[i].cards, j).color);
                strcat(message, card);
                strcat(message, "_");
                sprintf(card, "%d", getCard(&room->players[i].cards, j).value);
                strcat(message, card);
                strcat(message, "|");
            }

            char count[2];
            sprintf(count, "%d", player_count);
            strcat(message, count);           
            strcat(message, "|");

            for(int k = 0; k < MAX_PLAYERS; k++){
                char id[4];
                if(room->players[k].id != -1){
                    sprintf(id, "%d", room->players[k].id);
                    strcat(message, id);
                    strcat(message, ";");
                    strcat(message, room->players[k].name);
                    strcat(message, "|");
                }
            }
            strcat(message, "\n");
            int length = strlen(message);
            write(room->players[i].socket, message, length);
        }
    }
}

void inform_status(Room* room, int player_count){
    for(int i = 0; i < MAX_PLAYERS; i++){

        char message[200] = "status|";
        char count[2];
        sprintf(count, "%d", player_count);
        strcat(message, count);           
        strcat(message, "|");

        if(room->players[i].id != -1){
            for(int k = 0; k < MAX_PLAYERS; k++){
                char info[4];
                if(room->players[k].id != -1){
                    sprintf(info, "%d", room->players[k].id);
                    strcat(message, info);
                    strcat(message, ";");
                    sprintf(info, "%d", room->players[k].card_count);
                    strcat(message, info);
                    strcat(message, ";");
                    sprintf(info, "%d", room->players[k].state);
                    strcat(message, info);
                    strcat(message, "|");
                }
            }

            char game_info[4];
            char card[2];
            sprintf(game_info, "%d", room->game->game_state);
            strcat(message, game_info);
            strcat(message, "|");
            sprintf(game_info, "%d", room->game->change_color);
            strcat(message, game_info);
            strcat(message, "|");

            sprintf(card, "%d", room->game->last_played.color);
            strcat(message, card);
            strcat(message, "_");
            sprintf(card, "%d", room->game->last_played.value);
            strcat(message, card);                
            strcat(message, "|");

            sprintf(game_info, "%d", room->game->current_player_id);
            strcat(message, game_info);
            strcat(message, "|");

            strcat(message, "\n");
            int length = strlen(message);
            write(room->players[i].socket, message, length);
        }
    }
}

void send_take(card_list* takes, int socket){
    char card_take[4] = "";
    char message[200] = "turn|ok|t|";
    char size[3];

    sprintf(size, "%d", getCardCount(takes));
    strcat(message, size);
    strcat(message, "|");

    while(getCardCount(takes) != 0){
        card card = removeCard(takes, 0);
        to_string(card, card_take);
        strcat(message, card_take);
        strcat(message, "|");
    }

    strcat(message, "\n");

    int length = strlen(message);
    write(socket, message, length);
}

int inform_win(Room* room){
    char message[50] = "win|";
    char p_id[4];
    int found = 0;

    for(int i = 0; i < MAX_PLAYERS; i++){
        if(room->players[i].id != -1){
            if(room->players[i].card_count == 0){
                sprintf(p_id, "%d", room->players[i].id);
                strcat(message, p_id);
                strcat(message, "|\n");
                found = 1;
                break;
            }
        }
    }

    if(found == 1){
        int length = strlen(message);
        for(int i = 0; i < MAX_PLAYERS; i++){
            if(room->players[i].id != -1){
              write(room->players[i].socket, message, length);
           }
        }
        return 1;
    }

    return 0;
}

void inform_rejoin(player* player, Room* room, int p_count){
            char message[200] = "start|";

            char card_count[3];
            sprintf(card_count, "%d", player->card_count);
            strcat(message, card_count);
            strcat(message, "|");

            char card[2];
            for(int j = 0; j < player->card_count; j++){
                sprintf(card, "%d", getCard(&player->cards, j).color);
                strcat(message, card);
                strcat(message, "_");
                sprintf(card, "%d", getCard(&player->cards, j).value);
                strcat(message, card);
                strcat(message, "|");
            }

            char count[2];
            sprintf(count, "%d", p_count);
            strcat(message, count);           
            strcat(message, "|");

            for(int k = 0; k < MAX_PLAYERS; k++){
                char id[4];
                if(room->players[k].id != -1){
                    sprintf(id, "%d", room->players[k].id);
                    strcat(message, id);
                    strcat(message, ";");
                    strcat(message, room->players[k].name);
                    strcat(message, "|");
                }
            }

            strcat(message, "\n");
            int length = strlen(message);
            write(player->socket, message, length);

            //-------------------------------------//
/*
    for(int i = 0; i < MAX_PLAYERS; i++){

        strcpy(message, "status|");
        strcpy(count, "\0");
        //message = "status|";
        //count[2];
        sprintf(count, "%d", p_count);
        strcat(message, count);           
        strcat(message, "|");

        if(room->players[i].id != -1){
            for(int k = 0; k < MAX_PLAYERS; k++){
                char info[4];
                if(room->players[k].id != -1){
                    sprintf(info, "%d", room->players[k].id);
                    strcat(message, info);
                    strcat(message, ";");
                    sprintf(info, "%d", room->players[k].card_count);
                    strcat(message, info);
                    strcat(message, ";");
                    sprintf(info, "%d", room->players[k].state);
                    strcat(message, info);
                    strcat(message, "|");
                }
            }

            char game_info[4];
            char card[2];
            sprintf(game_info, "%d", room->game->game_state);
            strcat(message, game_info);
            strcat(message, "|");
            sprintf(game_info, "%d", room->game->change_color);
            strcat(message, game_info);
            strcat(message, "|");

            sprintf(card, "%d", room->game->last_played.color);
            strcat(message, card);
            strcat(message, "_");
            sprintf(card, "%d", room->game->last_played.value);
            strcat(message, card);                
            strcat(message, "|");

            sprintf(game_info, "%d", room->game->current_player_id);
            strcat(message, game_info);
            strcat(message, "|");

            strcat(message, "\n");
            int length = strlen(message);
            write(room->players[i].socket, message, length);
        }
    }
*/
}



int handle_connect(char* message, char* player_name){
    char* token = NULL;
    char name[100];
    char message_copy[256];

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message_copy);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }
    //printf("%s\n", token);

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    //name = token;
    strcpy(name, token);
   // printf("%s\n", name);

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }
    //printf("%s\n", name);
    strcpy(player_name, name);

    //printf("%s\n", player_name);

    //connect(player_name);
    //respond_connect();

    return 1;
}

int handle_start(char* message, int* p_id){
    char* token = NULL;
    char player_id[3];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }

    return 1;
}

int handle_join(char* message, int* r_id, int* p_id){
    char* token = NULL;
    char room_id[3];
    char player_id[3];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(room_id, token);
    //room_id = token;

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }
            //pthread_mutex_unlock(&room_mutex);
            //return NULL;

    *r_id = strtol(room_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }
    //printf("%d\n", atoi(room_id));

    //join(atoi(room_id));
    //respond_join();

    return 1;
}

int handle_rejoin(char* message, int* p_id){
    char* token = NULL;
    char player_id[3];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }

    return 1;
}

int handle_create(char* message){
    char* token = NULL;
    char message_copy[256];

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    //printf("%d\n", atoi(room_id));

    //create(room_name);
    //respond_create();

    return 1;
}

int handle_leave(char* message, int* p_id){
    char* token = NULL;
    char player_id[3];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);
    //player_id = token;

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }
    //leave(atoi(player_id));
    //respond_leave();

    return 1;
}

int handle_pause(char* message){
    char* token = NULL;
    char* player_id = NULL;
    char* identifier = "fail";
    char message_copy[256];

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    identifier = token;

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    player_id = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

    if(strcmp(identifier, "start") == 0){
        //start_pause(atoi(player_id));
        //respond_start_pause();
    }
    else if(strcmp(identifier, "end") == 0){
        //end_pause(atoi(player_id));
        //respond_end_pause();
    }
    else{
        return 0;
    }

    return 1;
}

int handle_taking(char* message, int* p_id){
    char* token = NULL;
    char player_id[4];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    //already checked in handle_turn
    strtok(message_copy, "|");
    strtok(NULL, "|");

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);
    //player_id = token;

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }

    //take(atoi(player_id));
    //respond_take();

    return 1;
}

int handle_staying(char* message, int* p_id){
    char* token = NULL;
    char player_id[4];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    //already checked in handle_turn
    strtok(message_copy, "|");
    strtok(NULL, "|");

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);
    //player_id = token;

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }
    //stay(atoi(player_id));
    //respond_stay();

    return 1;
}

int handle_playing(char* message, char card[], int* p_id, int* color){
    char* token = NULL;
    char player_id[4];
    char color_change[2];
    //char* card = NULL;
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    //already checked in handle_turn
    strtok(message_copy, "|");
    strtok(NULL, "|");

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(card, token);
    //card = token;

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(color_change, token);
    //color_change = token;

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(player_id, token);
    //player_id = token;

    token = strtok(NULL, "|");
    if(!token || strncmp(token, "\n", 1) != 0){
        return 0;
    }

    *p_id = strtol(player_id, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }

    *color = strtol(color_change, &endptr, 10);
    if(*endptr != '\0'){
        return 0;
    }

    //play(card, atoi(color_change), atoi(player_id));
    //respond_play();

    return 1;
}

int handle_turn(char* message, char card[], int* p_id, int* color){
    char* token = NULL;
    char identifier[2];
    char message_copy[256];

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    strcpy(identifier, token);
    //identifier = token;

    if(strcmp(identifier, "t") == 0){
        if(handle_taking(message, p_id) != 0){
            return 1;
        }
    }
    else if(strcmp(identifier, "s") == 0){
        if(handle_staying(message, p_id) != 0){
            return 2;
        }
        //return handle_staying(message);
    }
    else if(strcmp(identifier, "p") == 0){
        if(handle_playing(message, card, p_id, color) != 0){
            return 3;
        }
        //return handle_playing(message);
    }
    else{
        return 0;
    }

    return 0;
}

int handle_ping(char* message, int* p_id){
    char* token = NULL;
    char player_id[3];
    char message_copy[256];
    char* endptr;

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    //printf("%s\n", message);

    token = strtok(message_copy, "|");
    if(!token){
        return 0;
    }

    token = strtok(NULL, "|");
    if(!token){
       return 0;
    }

    if(strncmp(token, "ok", 2) == 0){
        token = strtok(NULL, "|");
        if(!token){
             return 0;
        }
        strcpy(player_id, token);

        token = strtok(NULL, "|");
        if(!token || strncmp(token, "\n", 1) != 0){
            return 0;
        }

        *p_id = strtol(player_id, &endptr, 10);
        if(*endptr != '\0'){
            return 0;
        }

        return 2;
    }
    else{
        strcpy(player_id, token);

        token = strtok(NULL, "|");
        if(!token || strncmp(token, "\n", 1) != 0){
            return 0;
        }
        return 1;
    }
}

int handler(char* message) {
    size_t i = 0;
    char* token = NULL;
    char message_copy[256];

    strncpy(message_copy, message, sizeof(message_copy) - 1);
    message_copy[sizeof(message_copy) - 1] = '\0';

    token = strtok(message_copy, "|");
    if (!token) {
        return 0;
    }

    for (i = 0; i < MSG_COUNT; i++) {
        if (strcmp(id[i], token) == 0) {
            break;
        }
    }

    if (i < MSG_COUNT) {
        if (handler_ptr[i](message) != 0) {
            return 1;
        }
    }

    //kick();
    return 0;
}
