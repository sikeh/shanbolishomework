#include "rudp.h"

#ifndef RUDP_API_H
#define	RUDP_API_H

#define TRUE = 1;
#define FALSE = 0;
#define RUDP_MAXPKTSIZE 1000	/* Number of data bytes that can sent in a
				 * packet, RUDP header not included */

/*
 * Event types for callback notifications
 */

typedef enum {
    RUDP_EVENT_TIMEOUT,
    RUDP_EVENT_CLOSED,
} rudp_event_t;

typedef enum {
    RSOCKET_RECIEVER,
    RSOCKET_SENDER,
} enum_socket;

typedef enum {
    RSESSION_START,
    RSESSION_TRANSFER,
    RSESSION_CLOSED,
    WAIT_FOR_ACK_OF_FIN
} enum_session;

struct r_datagram {
    struct rudp_hdr header;
    char data[RUDP_MAXPKTSIZE];
    int len;
    int has_ack;
    int num_retrans;
    struct r_datagram* next;
    struct sockaddr_in remote_addr;
    struct r_socket* rsocket;
    struct r_database* database;
};

struct r_database {
    struct sockaddr_in *remote;
    int is_initialed; // -1 = false; 1 = true;
    int last_recv_seq;
    int last_send_seq;

    enum_session session_state;

    struct r_datagram* datagram_buffer;
    struct r_database* next;
    struct r_database* pre;
};
typedef struct r_database* r_database_t;

struct r_socket {
    int (*super_rudp_receiver)(struct r_socket*, struct sockaddr_in*, char*, int);
    int (*super_event_handler)(struct r_socket*, rudp_event_t, struct sockaddr_in *);


    int sd;

    // used by recevier to filter incoming file (identified by remote_socket and seq)
    struct r_database* database;
};
typedef struct r_socket* rudp_socket_t;


/*
 * RUDP socket handle
 */


/*
 * Prototypes
 */

/* 
 * Socket creation 
 */
rudp_socket_t rudp_socket(int port);

/* 
 * Socket termination
 */
int rudp_close(rudp_socket_t rsocket);

/* 
 * Send a datagram 
 */
int rudp_sendto(rudp_socket_t rsocket, void* data, int len,
        struct sockaddr_in* to);

/* 
 * Register callback function for packet receiption 
 * Note: data and len arguments to callback function 
 * are only valid during the call to the handler
 */
int rudp_recvfrom_handler(rudp_socket_t rsocket,
        int (*handler)(rudp_socket_t,
        struct sockaddr_in *,
        char *, int));
/*
 * Register callback handler for event notifications
 */
int rudp_event_handler(rudp_socket_t rsocket,
        int (*handler)(rudp_socket_t,
        rudp_event_t,
        struct sockaddr_in *));
#endif /* RUDP_API_H */
