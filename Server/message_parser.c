#include "message_parser.h"
#include "message_sender.h"
#include "game.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

char* id[MSG_COUNT] = {
    "connect",
    "join",
    "create",
    "rejoin",
    "leave",
    "pause",
    "turn",
    "ping"
};

int (*handler_ptr[])(char*)= {
        handle_connect,
        handle_join,
        handle_create,
        handle_rejoin,
        handle_leave,
        handle_pause,
        handle_turn,
        handle_ping
};

int handle_connect(char* message){
    char* token = NULL;
    char* player_name = NULL;
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
    player_name = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

    //printf("%s\n", player_name);

    //connect(player_name);
    //respond_connect();

    return 1;
}

int handle_join(char* message){
    char* token = NULL;
    char* room_id = NULL;
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
    room_id = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

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
    char* room_name = NULL;
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
    room_name = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
        return 0;
    }

    //printf("%d\n", atoi(room_id));

    //create(room_name);
    //respond_create();

    return 1;
}

int handle_leave(char* message){
    char* token = NULL;
    char* player_id = NULL;
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
    player_id = token;

    token = strtok(NULL, "|");
    if(!token || strcmp(token, "\n") != 0){
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
