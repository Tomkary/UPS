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
    "rejoin",
 //   "leave",
 //   "pause",
    "turn",
    "ping"
};

int (*handler_ptr[])(char*)= {
    //    handle_connect,
    //    handle_join,
    //    handle_create,
        handle_rejoin,
    //    handle_leave,
    //    handle_pause,
        handle_turn,
        handle_ping
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

    *p_id = atoi(player_id);

    return 1;
}

int handle_join(char* message, int* r_id, int* p_id){
    char* token = NULL;
    char room_id[3];
    char player_id[3];
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

    *r_id = atoi(room_id);
    *p_id = atoi(player_id);
    //printf("%d\n", atoi(room_id));

    //join(atoi(room_id));
    //respond_join();

    return 1;
}

int handle_rejoin(char* message){

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

    *p_id = atoi(player_id);
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

int handle_taking(char* message){
    char* token = NULL;
    char* player_id = NULL;
    char message_copy[256];

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
    player_id = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

    //take(atoi(player_id));
    //respond_take();

    return 1;
}

int handle_staying(char* message){
    char* token = NULL;
    char* player_id = NULL;
    char message_copy[256];

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
    player_id = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

    //stay(atoi(player_id));
    //respond_stay();

    return 1;
}

int handle_playing(char* message){
    char* token = NULL;
    char* player_id = NULL;
    char* color_change = NULL;
    char* card = NULL;
    char message_copy[256];

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
    player_id = token;

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    card = token;

    token = strtok(NULL, "|");
    if(!token){
        return 0;
    }
    color_change = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

    //play(card, atoi(color_change), atoi(player_id));
    //respond_play();

    return 1;
}

int handle_turn(char* message){
    char* token = NULL;
    char* identifier = NULL;
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

    if(strcmp(identifier, "t") == 0){
        return handle_taking(message);
    }
    else if(strcmp(identifier, "s") == 0){
        return handle_staying(message);
    }
    else if(strcmp(identifier, "p") == 0){
        return handle_playing(message);
    }
    else{
        return 0;
    }
}

int handle_ping(char* message){

    return 1;
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
