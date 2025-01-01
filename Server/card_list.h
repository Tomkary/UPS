#ifndef SERVER_CARD_LIST_H
#define SERVER_CARD_LIST_H

// Card structure
typedef struct {
    int color;
    int value;
} card;

// Dynamic card list structure
typedef struct {
    card* cards;     // Pointer to dynamically allocated array of cards
    int size;         // Current number of cards in the list
    int capacity;     // Current allocated capacity
} card_list;

// Initialize a CardList
void initCardList(card_list *list, int initialCapacity);

// Get the number of cards in the list
int getCardCount(const card_list *list);

// Resize the card list when full
void resizeCardList(card_list *list);

// Add a card to the list
void addCard(card_list *list, int color, int value);

// Remove a card at a specific index
card removeCard(card_list *list, int index);

card getCard(const card_list *list, int index);

// Shuffle the cards in the list
void shuffleCards(card_list *list);

// Free the memory used by the card list
void freeCardList(card_list *list);

#endif //SERVER_CARD_LIST_H
