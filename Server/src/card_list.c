#include "card_list.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Initialize a CardList
void initCardList(card_list *list, int initialCapacity) {
    list->cards = malloc(initialCapacity * sizeof(card));
    list->size = 0;
    list->capacity = initialCapacity;
}

// Get the number of cards in the list
int getCardCount(const card_list *list) {
    return list->size;
}

// Resize the card list when full
void resizeCardList(card_list *list) {
    card* temp = NULL;
    list->capacity *= 2;
    temp = realloc(list->cards, list->capacity * sizeof(card));
    if (!temp) {
        fprintf(stderr, "Memory allocation failed during resize.\n");
        exit(EXIT_FAILURE);
    }
    list->cards = temp;
}

// Add a card to the list
void addCard(card_list *list, int color, int value) {
    if (list->size == list->capacity) {
        resizeCardList(list);
    }
    list->cards[list->size].color = color;
    list->cards[list->size].value = value;
    list->size++;
}

// Remove a card at a specific index
card removeCard(card_list *list, int index) {
    if (index < 0 || index >= list->size) {
        fprintf(stderr, "Index out of bounds. Returning an empty card.\n");
        return (card){-1, -1}; // Return a card with invalid values to indicate failure
    }
    card removedCard = list->cards[index]; // Save the card to return it
    for (int i = index; i < list->size - 1; i++) {
        list->cards[i] = list->cards[i + 1];
    }
    list->size--;
    return removedCard;
}

// Get a card at a specific index without removing it
card getCard(const card_list *list, int index) {
    if (index < 0 || index >= list->size) {
        fprintf(stderr, "Index out of bounds. Returning an empty card.\n");
        return (card){-1, -1}; // Return a card with invalid values to indicate failure
    }
    return list->cards[index]; // Return the card at the specified index
}

// Shuffle the cards in the list
void shuffleCards(card_list *list) {
    srand(time(NULL)); // Seed the random number generator
    for (int i = list->size - 1; i > 0; i--) {
        int j = rand() % (i + 1);
        // Swap cards[i] and cards[j]
        card temp = list->cards[i];
        list->cards[i] = list->cards[j];
        list->cards[j] = temp;
    }
}

void to_string(card card, char string[]){
    char col[2];
    char val[2];
    char string_card[4] = "";

    sprintf(col, "%d", card.color);
    strcat(string_card, col);
    strcat(string_card, "_");
    sprintf(val, "%d", card.value);
    strcat(string_card, val);

    strcpy(string, string_card);  
}

// Free the memory used by the card list
void freeCardList(card_list *list) {
    free(list->cards);
    list->cards = NULL;
    list->size = 0;
    list->capacity = 0;
}
