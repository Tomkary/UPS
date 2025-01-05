#include "game.h"
#include "message_parser.h"
#include "card_list.h"
#include <stdio.h>

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
*/
    player room1[4] = {
            {.name = "A", .id = 1, .state = 1},
            {.name = "B", .id = 4, .state = 1},
            {.name = "C", .id = 8, .state = 1}
    };

    //init_game(room1, 3);
    //play("1_2", 0, 1);

    return 0;
}