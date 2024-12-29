#include "game.h"
#include "message_parser.h"

int main(){
    char* message = "join|11|\n";

    if(handler(message) == 0){
        return 1;
    }
    return 0;
}