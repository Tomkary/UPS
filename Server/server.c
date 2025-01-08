#include "game.h"
#include "message_parser.h"
#include "card_list.h"
#include "room_list.h"
#include "player_list.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 10020
#define MAX_CLIENTS 20


player_list Players;
room_list Rooms;
int free_player_id = 0;
int free_room_id = 0;


void infrom_lobby(){
    int size = get_player_list_size(&Players);
    for(int i = 0; i < size; i++){
        player* player = get_player(&Players, i);
        if(player->room_id == -1){
            send_lobby(&Rooms, player->socket);
        }           
    }
}

// Function to handle incoming client connections
void *handle_client(void *arg) {
    int client_socket = *(int *)arg;
    free(arg);
    char* token;

    char buffer[256];
    int bytes_read;

    //TODO connect

    if((bytes_read = read(client_socket, buffer, sizeof(buffer))) > 0){
        buffer[bytes_read] = '\0';
        if(strncmp(buffer, "connect|", 8) == 0) {

            //check if correct message
            char name[100];
            if(handle_connect(buffer, name) == 0){
                write(client_socket, "connect|err|10|\n", 17);
                close(client_socket);
                return NULL;
            }

            for(int i = 0; i < get_player_list_size(&Players); i++){
                if(strcmp(get_player(&Players, i)->name, name) == 0){
                    write(client_socket, "connect|err|1|\n", 16);
                    close(client_socket);
                    return NULL;
                }
            }

            //create new player
            player player = {
                    .id = free_player_id,
                    .socket = client_socket,
                    .state = 1,
                    .room_id = -1,
                    .card_count = 0
            };
            strcpy(player.name, name);
            add_player(&Players, player);
            free_player_id++;

            //TODO ping thread

            char message[30] = "connect|ok|";
            char p_id[6];
            sprintf(p_id, "%d|\n", player.id);
            strcat(message, p_id);
            write(client_socket, message, 18);
            send_lobby(&Rooms, client_socket);
        }
        else{
            close(client_socket);
            return NULL;
        }
    }

    while ((bytes_read = read(client_socket, buffer, sizeof(buffer))) > 0) {
        buffer[bytes_read] = '\0';

        //TODO dis
        //TODO leave
        //TODO turn
        //TODO ping
        //TODO rejoin

    // create
        if (strncmp(buffer, "create|", 7) == 0) {

            //check if correct message
            if(handle_create(buffer) == 0){
                write(client_socket, "create|err|10|\n", 16);
                close(client_socket);
                return NULL;
            }


            //create new room
            Room room = {
                    .id = free_room_id,
                    .players[0].id = -1,
                    .players[1].id = -1,
                    .players[2].id = -1,
                    .players[3].id = -1
            };
            room.game = malloc(sizeof(Game));
            room.game->started = 0;
            free_room_id++;
            add_room(&Rooms, room);

            write(client_socket, "create|ok|\n", 12);
            //send_lobby(&Rooms, client_socket);
            infrom_lobby();

    //join
        } else if (strncmp(buffer, "join|", 5) == 0) {
            int room_id;
            int player_id;
            
            //check message
            if(handle_join(buffer, &room_id, &player_id) == 0){
                write(client_socket, "join|err|10|\n", 14);
                close(client_socket);
                return NULL;
            }

            //check valid room id
            if (room_id >= 0 && room_id <= get_room_list_size(&Rooms)) {
                Room* room = get_room(&Rooms, room_id);
                player* player = get_player(&Players, player_id);

                if(player->room_id != -1){
                    write(client_socket, "join|err|10|\n", 14);
                    close(client_socket);
                    return NULL;
                }

                //check if game already started
                if(room->game->started == 1){
                    write(client_socket, "join|err|3|\n", 13);
                    continue;
                }

                //set player to room & test if full
                int i;
                for(i = 0; i < MAX_PLAYERS; i++){
                    if(room->players[i].id == -1){
                        player->room_id = room_id;
                        room->players[i] = *player;
                        break;
                    }
                }
                if(i == MAX_PLAYERS){
                    write(client_socket, "join|err|2|\n", 13);
                    continue;
                }

                write(client_socket, "join|ok|\n", 10);
                continue;
                
            } else {
                write(client_socket, "join|err|10|\n", 14);
                close(client_socket);
                return NULL;
            }
        } else if (strncmp(buffer, "leave|", 6) == 0) {
            int player_id;
            player empty = {
                .id = -1
            };
            
            //check message
            if(handle_leave(buffer, &player_id) == 0){
                write(client_socket, "leave|err|10|\n", 15);
                close(client_socket);
                return NULL;
            }

            //check if in any room
            int found = 0;
            int room_count = get_room_list_size(&Rooms);
            for(int i = 0; i < room_count; i++){
                Room* room = get_room(&Rooms, i);
                for(int j = 0; j < MAX_PLAYERS; j++){
                    //printf("id: %d\n", room->players[j].id);
                    if(room->players[j].id == player_id){
                        found = 1;
                        room->players[j] = empty;
                        break;
                    }
                }
                if(found == 1){
                    break;
                }
            }
            if(found == 0){
                write(client_socket, "leave|err|9|\n", 14);
                continue;
            }

            get_player(&Players, player_id)->room_id = -1;

            write(client_socket, "leave|ok|\n", 11);
            send_lobby(&Rooms, client_socket);
            continue;
        } else if (strncmp(buffer, "start|", 6) == 0) {
            int player_id;
            int found = 0;
            
            //check message
            if(handle_start(buffer, &player_id) == 0){
                write(client_socket, "start|err|10|\n", 15);
                close(client_socket);
                return NULL;
            }

            //check if player in room
            Room* room;           
            int room_count = get_room_list_size(&Rooms);
            for(int i = 0; i < room_count; i++){
                room = get_room(&Rooms, i);
                for(int j = 0; j < MAX_PLAYERS; j++){
                    //printf("id: %d\n", room->players[j].id);
                    if(room->players[j].id == player_id){
                        found = 1;
                        break;
                    }
                }
                if(found == 1){
                    break;
                }
            }
            if(found == 0){
                write(client_socket, "start|err|9|\n", 14);
                continue;
            }

            //check if room atleast 2 players
            int count = 0;
            for(int i = 0; i < MAX_PLAYERS; i++){
                if(room->players[i].id != -1){
                    count++;
                }
            }
            if(count < 2){
                write(client_socket, "start|err|9|\n", 14);
                continue;
            }

            //start the game - inform all in room
            init_game(room->game, room->players, count);
            
            infrom_start(room, count);

            inform_status(room, count);
        }
    }

    close(client_socket);
    return NULL;
}

int main() {
    int server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);

    // Create server socket
    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Bind and listen
    if (bind(server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }
    if (listen(server_socket, MAX_CLIENTS) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server started on port %d.\n", PORT);

    Rooms = create_room_list(10);
    Players = create_player_list(20);

    // Accept clients
    while ((client_socket = accept(server_socket, (struct sockaddr *)&client_addr, &addr_len)) >= 0) {
        int *client_socket_ptr = malloc(sizeof(int));
        *client_socket_ptr = client_socket;

        pthread_t client_thread;
        pthread_create(&client_thread, NULL, handle_client, client_socket_ptr);
        pthread_detach(client_thread);
    }

    close(server_socket);
    return 0;
}
/*
int main(){
    /*
    char* message = "join|11|\n";

    if(handler(message) == 0){
        return 1;
    }
    */
/*
    card_list deck;
    initCardList(&deck, 4);

    // Add some cards
    addCard(&deck, 1, 10); // Example: Barva 1, Hodnota 10
    addCard(&deck, 2, 5);
    addCard(&deck, 3, 12);
    addCard(&deck, 4, 8);
    addCard(&deck, 5, 1);
    addCard(&deck, 6, 15);

    printf("Number of cards: %d\n", getCardCount(&deck));

    for (int i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }

    // Shuffle the deck
    printf("Shuffling cards...\n");
    shuffleCards(&deck);

    // Display shuffled cards
    for (int i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }

    // Shuffle the deck
    printf("Shuffling cards...\n");
    shuffleCards(&deck);

    // Display shuffled cards
    for (int i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }

    // Shuffle the deck
    printf("Shuffling cards...\n");
    shuffleCards(&deck);

    // Display shuffled cards
    for (int i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }

    // Shuffle the deck
    printf("Shuffling cards...\n");
    shuffleCards(&deck);

    // Display shuffled cards
    for (int i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }

    // Remove a card
    printf("Removing card at index 1...\n");
    card remo = removeCard(&deck, 1);
    printf("Removed card: Barva %d, Hodnota %d\n", remo.color, remo.value);

    // Display remaining cards
    for (int i = 0; i < getCardCount(&deck); i++) {
        printf("Card %d: Barva %d, Hodnota %d\n", i, deck.cards[i].color, deck.cards[i].value);
    }

    // Free the list
    freeCardList(&deck);
*//*
    player room1[4] = {
            {.name = "A", .id = 1, .state = 1},
            {.name = "B", .id = 4, .state = 1},
            {.name = "C", .id = 8, .state = 1}
    };

    //init_game(room1, 3);
    //play("1_2", 0, 1);

    return 0;
}
 */