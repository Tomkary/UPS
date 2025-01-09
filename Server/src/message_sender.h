#ifndef SERVER_MESSAGE_SENDER_H
#define SERVER_MESSAGE_SENDER_H

int respond_connect();

int respond_join();

int respond_rejoin();

int respond_create();

int respond_leave();

int respond_pause();

int respond_take();

int respond_stay();

int respond_play();

int respond_ping();

#endif //SERVER_MESSAGE_SENDER_H
