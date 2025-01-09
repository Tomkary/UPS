#include "room_list.h"
#include <stdio.h>
#include <stdlib.h>

// Initialize the list
room_list create_room_list(int capacity) {
    room_list list;
    list.rooms = (Room *)malloc(capacity * sizeof(Room));
    list.size = 0;
    list.capacity = capacity;
    return list;
}

// Add a room to the list
void add_room(room_list *list, Room r) {
    if (list->size >= list->capacity) {
        list->capacity *= 2;
        list->rooms = (Room *)realloc(list->rooms, list->capacity * sizeof(Room));
    }
    list->rooms[list->size++] = r;
}

// Remove a room by index
void remove_room(room_list *list, int index) {
    if (index < 0 || index >= list->size) return;
    for (int i = index; i < list->size - 1; i++) {
        list->rooms[i] = list->rooms[i + 1];
    }
    list->size--;
}

// Get a room by index without removing
Room* get_room(room_list *list, int index) {
    if (index < 0 || index >= list->size) {
        fprintf(stderr, "Index out of bounds\n");
        exit(EXIT_FAILURE);
    }
    return &(list->rooms[index]);
}

// Get the size of the room list
int get_room_list_size(room_list *list) {
    return list->size;
}
