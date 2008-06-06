#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <netdb.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/file.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <time.h>

#include "event.h"
#include "rudp.h"
#include "rudp_api.h"

#define RUDP_SUCCESS 1;
#define RUDP_FAILURE -1;

int current_port = 0;

// --- method signature
int handle_timeout(int fd, void* arg);

struct timeval calc_next_timeout();
struct r_datagram* create_datagram(void* data, int len, int type, int seq, struct r_socket* r_sock, struct sockaddr_in remote_addr);
struct r_datagram* push_data_to_buffer(struct r_datagram* buffer,
        struct r_datagram* data);
struct r_datagram* free_buffer_header(struct r_datagram* buffer, int in_seq);

/**
 * Check if this socket's buffer is empty
 * 
 * return  1 socket buffer is empty
 *        -1 socket buffer is NOT empty.
 */
int is_buffer_empty(rudp_socket_t rsocket);

/**
 * check whether remote socket is in database
 *
 * return database from socket
 */
r_database_t get_database(rudp_socket_t r_socket, struct sockaddr_in* remote_addr);

// check whether incoming packet is acceptable
// if acceptable, then throw to upper layer, and return an ACK
// otherwise ignore
void handle_package(struct r_socket* rsocket, r_database_t database, struct r_datagram* in_datagram, struct sockaddr_in* remote_addr);

int send_buffer_data(rudp_socket_t rsocket);
void send_data(struct r_datagram* datagram);
void send_datagram(struct r_datagram* datagram);


// --- method signature

int receiver_side_recv(int fd, void *arg) {
    rudp_socket_t rsocket = (rudp_socket_t) arg;
    char in_data[sizeof (struct r_datagram) ];
    unsigned int sock_len = sizeof (struct sockaddr_in);

    struct r_datagram* in_datagram = (struct r_datagram*) in_data;
    struct sockaddr_in remote_addr;

    int bytesrcvd = recvfrom(rsocket->sd, in_data, sizeof (struct r_datagram), 0,
            (struct sockaddr*) & remote_addr, &sock_len);
    if (bytesrcvd < -1) {
        perror("recvfrom:");
    }

    struct r_datagram* ack_datagram;

    r_database_t db = get_database(rsocket, &remote_addr);

    switch (in_datagram->header.type) {
        case RUDP_SYN:
            printf("RECV RUDP_SYN, seq:%d \n", in_datagram->header.seqno);
            ack_datagram
                    = create_datagram(NULL, 0, RUDP_ACK, in_datagram->header.seqno + 1, rsocket, remote_addr);
            send_datagram(ack_datagram);
            db->last_recv_seq = in_datagram->header.seqno;
            break;
        case RUDP_DATA:
            printf("RECV RUDP_DATA, seq:%d\n", in_datagram->header.seqno);
            printf("SOCK_ADDR: %s\n", inet_ntoa(remote_addr.sin_addr));
            printf("SOCK_PORT: %u\n", ntohs(remote_addr.sin_port));
            handle_package(rsocket, db, in_datagram, &remote_addr);
            break;
        case RUDP_FIN:
            ;
            ack_datagram
                    = create_datagram(NULL, 0, RUDP_ACK, rsocket->last_recv_seq + 1, rsocket, remote_addr);
            send_datagram(ack_datagram);
            break;
    }
    return 1;
}

/**
 * check whether remote socket is in database
 *
 * return database from socket
 */
r_database_t get_database(rudp_socket_t r_socket, struct sockaddr_in* remote_addr) {

    r_database_t iter_database = r_socket->database;
    if (iter_database == NULL) {
        // add remote to database
        iter_database = (struct r_database*) malloc(sizeof (struct r_database));
        memset(iter_database, 0, sizeof (struct r_database));
        iter_database->remote = (struct sockaddr_in*) malloc(sizeof (struct sockaddr_in));
        memcpy(iter_database->remote, remote_addr, sizeof (struct sockaddr_in));
        r_socket->database = iter_database;
        printf("\n--------------new a database 1\n");
        return iter_database;
    }

    r_database_t last_valid = NULL;
    while (iter_database != NULL) {
        if (iter_database->remote->sin_addr.s_addr == remote_addr->sin_addr.s_addr &&
                iter_database->remote->sin_port == remote_addr->sin_port) {
            return iter_database;
        }
        r_database_t last_valid = iter_database;
        iter_database = iter_database->next;
    }

    iter_database = (struct r_database*) malloc(sizeof (struct r_database));
    memset(iter_database, 0, sizeof (struct r_database));
    iter_database->remote = (struct sockaddr_in*) malloc(sizeof (struct sockaddr_in));
    memcpy(iter_database->remote, remote_addr, sizeof (struct sockaddr_in));
    last_valid->next = iter_database;
    printf("\n--------------new a database 2\n");
    return iter_database;

}


// check whether incoming packet is acceptable
// if acceptable, then throw to upper layer, and return an ACK
// otherwise ignore

void handle_package(struct r_socket* rsocket, r_database_t database, struct r_datagram* in_datagram, struct sockaddr_in* remote_addr) {
    struct r_datagram* ack_datagram;
    if ((database->last_recv_seq + 1) == in_datagram->header.seqno) {
        rsocket->super_rudp_receiver(rsocket, remote_addr, in_datagram->data,
                in_datagram->len);
        database->last_recv_seq++;
        ack_datagram
                = create_datagram(NULL, 0, RUDP_ACK, in_datagram->header.seqno + 1, rsocket, *remote_addr);
        send_datagram(ack_datagram);
    } else if ((rsocket->last_recv_seq + 1) > in_datagram->header.seqno) {
        ack_datagram
                = create_datagram(NULL, 0, RUDP_ACK, in_datagram->header.seqno + 1, rsocket, *remote_addr);
        send_datagram(ack_datagram);
    }
};


// ---- call back functions ( sender side), use for receiving ACK

int handle_rudp_recv(int fd, void *arg) {

    rudp_socket_t rsocket = (rudp_socket_t) arg;
    char in_data[sizeof (struct r_datagram) ];
    unsigned int sock_len;
    int bytesrcvd;
    sock_len = sizeof (struct sockaddr_in);
    struct r_datagram* in_datagram = (struct r_datagram*) in_data;

    struct sockaddr_in remote_addr;

    bytesrcvd = recvfrom(rsocket->sd, in_data, sizeof (struct r_datagram), 0,
            (struct sockaddr *) & remote_addr, &sock_len);
    if (bytesrcvd < -1) {
        perror("recvfrom:");
    }

    struct r_datagram* ack_datagram;
    switch (in_datagram->header.type) {
        case RUDP_ACK:
            printf("RECV RUDP_ACK, seq:%d\n", in_datagram->header.seqno);
            if (rsocket->last_recv_seq < in_datagram->header.seqno) {
                // receive available ACK, free buffer, record new ACK seq which is used for forwarding window 
                rsocket->datagram_buffer = free_buffer_header(rsocket->datagram_buffer, in_datagram->header.seqno);
                rsocket->last_recv_seq = in_datagram->header.seqno;
            } else {
                return 0;
            }
            // sender
            switch (rsocket->session_state) {
                case RSESSION_START:
                    rsocket->session_state = RSESSION_TRANSFER;
                case RSESSION_TRANSFER:
                    send_buffer_data(rsocket);
                    break;
                case RSESSION_CLOSED:
                    //check if the buffer is empty
                    if (is_buffer_empty(rsocket) == 1) {
                        //buffer is empty, creat a FIN package and send
                        rsocket->last_send_seq++;
                        ack_datagram
                                = create_datagram(NULL, 0, RUDP_FIN, rsocket->last_send_seq, rsocket, remote_addr);
                        rsocket->fin_seq = rsocket->last_send_seq;
                        send_data(ack_datagram);
                        rsocket->session_state = WAIT_FOR_ACK_OF_FIN;
                        printf("\n^^^^^^^^^^^^^ send out a fin");
                    } else {
                        //buffer is not empty, send buffer dates
                        send_buffer_data(rsocket);
                    }
                    break;
                case WAIT_FOR_ACK_OF_FIN:
                    // get the reply for FIN, make a RUDP_EVENT_CLOSE event.
                    rsocket->super_event_handler(rsocket, RUDP_EVENT_CLOSED, NULL);
                    /* TODO: discuss if need free the resource,
                     *  it is find to free the resource (rsocket)
                     *  but what if upper layer get another ACK (a very late and slow one), 
                     *  and this rsocket has been freed --------> Segment fault?
                     */

                    break;

            }
            break;
    }
    return 1;
}

/*
int remove_session(rudp_socket_t rsocket, struct sockaddr_in *remoteaddr) {
    rudp_session_t pSession, tSession;

    for (pSession = rsocket->r_session, tSession = NULL; pSession != NULL;) {
        if (remoteaddr->sin_addr.s_addr == pSession->local_socket_addr.sin_addr.s_addr
                && remoteaddr->sin_port == pSession->local_socket_addr.sin_port) {

            if (tSession == NULL)
                rsocket->r_session = pSession->next;
            else
                tSession->next = pSession->next;
            free(pSession);
            return RUDP_SUCCESS;

        }
        tSession = pSession;
        pSession = pSession->next;
    }

    return RUDP_SUCCESS;
}
 */

/*

int remove_allsessions(rudp_socket_t rsocket) {
    rudp_session_t pSession, tSession;

    for (pSession = rsocket->r_session; pSession != NULL;) {
        tSession = pSession->next;
        free(pSession);
        pSession = tSession;
    }

    return RUDP_SUCCESS;
}
 */

int handle_timeout(int fd, void* arg) {
    struct r_datagram* datagram = (struct r_datagram*) arg;

    //TODO: these codes are used for debug, remove them before release:
    if (datagram == NULL) {
        printf("\n");
    } else if (datagram->rsocket == NULL) {
        printf("\n");
    }


    //end of DEBUG

    struct r_socket* rsocket = datagram->rsocket;
    rsocket->super_event_handler(rsocket, RUDP_EVENT_TIMEOUT, &(datagram->remote_addr));
    return 1;
}
// --- call back functions

/* 
 * Signature designed by Teacher
 * rudp_socket: Create a RUDP socket. 
 * May use a random port by setting port to zero.
 * 
 * It is the first method which was called by upper layer 
 * New a socket here
 * handle session here
 */
rudp_socket_t rudp_socket(int port) {
    printf("rudp_socket\n");
    int sd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sd < 0) {
        printf("fail to create socket");
        return NULL;
    }
    struct sockaddr_in socket_addr;
    socket_addr.sin_family = AF_INET;
    socket_addr.sin_addr.s_addr = htonl(INADDR_ANY);

    if (port == 0) {
        srand(time(NULL));
        if (0 == current_port) {
            current_port = rand() % 90 + 11240;
        }
        printf("randomize port = %d\n", current_port);
        socket_addr.sin_port = htons(current_port++);

    } else {
        socket_addr.sin_port = htons(port);
    }

    int fd = bind(sd, (struct sockaddr *) & socket_addr, sizeof (socket_addr));
    if (fd < 0) {
        printf("fail to bind socket");
        return NULL;
    }

    struct r_socket* session = NULL;
    session = (struct r_socket*) malloc(sizeof (struct r_socket));
    memset(session, 0, sizeof (struct r_socket));
    session->is_initialed = -1;
    session->sd = sd;
    session->local_socket_addr = &socket_addr;

    if (0 == port) {
        // this is sander side
        if (event_fd(sd, handle_rudp_recv, session, "handle_rudp_revv") < 0) {
            printf("fail to register handle_rudp_recv");
            return NULL;
        }
    }else {
        // this is receiver side
        if (event_fd(sd, receiver_side_recv, session, "receiver_side_recv") < 0) {
            printf("fail to register handle_rudp_recv");
            return NULL;
        }
    }



    return session;
}

/* 
 *rudp_close: Close socket 
 */

int rudp_close(rudp_socket_t rsocket) {
    rsocket->session_state = RSESSION_CLOSED;
    return 0;
}

/** 
 *rudp_recvfrom_handler: Register receive callback function 
 */
int rudp_recvfrom_handler(rudp_socket_t rsocket, int (*handler)(rudp_socket_t rsocket, struct sockaddr_in *remote, char *buf, int len)) {
    rsocket->super_rudp_receiver = handler;
    return 0;
}

/** 
 *rudp_event_handler: Register event handler callback function 
 */
int rudp_event_handler(rudp_socket_t rsocket, int (*handler)(rudp_socket_t, rudp_event_t,
        struct sockaddr_in *)) {
    // save handler reference to rsocket->event_handler
    rsocket->super_event_handler = handler;
    return 0;
}

/* 
 * rudp_sendto: Send a block of data to the receiver. 
 */
int rudp_sendto(rudp_socket_t rsocket, void* data, int len,
        struct sockaddr_in* to) {
    // TODO check session list

    if (rsocket->is_initialed < 0) {
        rsocket->is_initialed = 1;
        rsocket->last_recv_seq = -1;
        rsocket->last_send_seq = -1;

        rsocket->local_socket_addr = to;

        struct sockaddr_in* a_socket =
                (struct sockaddr_in*) malloc(sizeof (struct sockaddr_in));
        memset(a_socket, 0, sizeof (struct sockaddr_in));
        memcpy(a_socket, to, sizeof (struct sockaddr_in));

        printf("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        printf("here is a new session, sd = %d", rsocket->sd);
        printf("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");

        rsocket->datagram_buffer = NULL;
        rsocket->local_socket_addr = a_socket;
        rsocket->last_send_seq = 23;
        rsocket->last_recv_seq = 23;
        rsocket->socket_type = RSOCKET_SENDER;
        rsocket->session_state = RSESSION_START;
        // starting sequence from 24, which is a SYN packet
        struct r_datagram* syn_datagram =
                create_datagram(NULL, 0, RUDP_SYN, 24, rsocket, *to);
        printf("\nsend RUDP_SYN, seq 24\n");
        //TODO: change send_data signature to sute with rsocket
        send_data(syn_datagram);

        // send the first packet
        rsocket->last_send_seq = 25;
        struct r_datagram* data_datagram = create_datagram(data, len,
                RUDP_DATA, rsocket->last_send_seq, rsocket, *to);
        send_data(data_datagram);
    } else {
        rsocket->last_send_seq++;
        struct r_datagram* datagram = create_datagram(data, len, RUDP_DATA,
                rsocket->last_send_seq, rsocket, *to);
        send_data(datagram);
    }

    return 0;
}

struct r_datagram* create_datagram(void* data, int len, int type, int seq, struct r_socket* r_sock, struct sockaddr_in remote_addr) {
    struct r_datagram* datagram =
            (struct r_datagram*) malloc(sizeof (struct r_datagram));
    memset(datagram, 0, sizeof (struct r_datagram));
    datagram->header.seqno = seq;
    datagram->header.type = type;
    datagram->len = len;
    datagram->rsocket = r_sock;
    datagram->remote_addr = remote_addr;
    memset(datagram->data, 0, RUDP_MAXPKTSIZE);
    memcpy(datagram->data, data, len);
    return datagram;
}

struct timeval calc_next_timeout() {
    /*
     printf("calc_next_timeout\n");
     */
    struct timeval time_val;
    struct timeval tmp_time1, tmp_time2;
    gettimeofday(&tmp_time1, NULL);
    tmp_time2.tv_sec = 1;
    tmp_time2.tv_usec = RUDP_TIMEOUT;
    timeradd(&tmp_time1, &tmp_time2, &time_val);
    return time_val;
}

struct r_datagram* push_data_to_buffer(struct r_datagram* buffer,
        struct r_datagram* data) {

    struct r_datagram* buffer_head = buffer;

    if (NULL == buffer)
        return data;

    while (buffer ->next != NULL) {
        buffer = buffer->next;
    }
    buffer->next = data;

    return buffer_head;
}

void send_data(struct r_datagram* datagram) {
    rudp_socket_t rsocket = datagram->rsocket;
    //    struct sockaddr_in addr = datagram->remote_addr;



    rsocket->datagram_buffer = push_data_to_buffer(rsocket->datagram_buffer, datagram);
    int window = rsocket->last_send_seq - rsocket->last_recv_seq + 1;
    if (window >= RUDP_WINDOW) {
        // no window, do nothing here
        return;
    }else {
        struct r_datagram* a_datagram = rsocket->datagram_buffer;

        while (NULL != a_datagram && (rsocket->last_send_seq - rsocket->last_recv_seq + 1) <= RUDP_WINDOW) {
            if (a_datagram->header.seqno > rsocket->last_send_seq) {
                send_datagram(a_datagram);
                rsocket->last_send_seq = a_datagram->header.seqno;
            }
            a_datagram = a_datagram->next;
        }
        return;
    }

}

void send_datagram(struct r_datagram* datagram) {
    int bytessent;
    printf("\nsend_datagram: \n");
    printf("Type = %d\n", datagram->header.type);
    printf("seqno = %d\n", datagram->header.seqno);
    printf("data = %s\n", datagram->data);
    printf("len = %d\n", datagram->len);
    printf("\n");

    rudp_socket_t rsocket = datagram ->rsocket;
    struct sockaddr_in addr = datagram ->remote_addr;

    bytessent = sendto(rsocket->sd, (void*) datagram,
            sizeof (struct r_datagram), 0, (struct sockaddr*) & addr,
            sizeof (struct sockaddr));
    if (bytessent < 0) {
        perror("senddatagram:");
    }
    if (datagram->header.type != RUDP_ACK) {
        struct timeval time_val = calc_next_timeout();
        //TODO use a counter to record timeout time and throw a TIME_OUT_EVENT
        //   event_timeout(time_val, handle_timeout, datagram, "handle_time_out");
    }
}

int send_buffer_data(rudp_socket_t rsocket) {
    printf("send_buffer_data\n");
    if (rsocket == NULL) {
        return -2;
    }
    if (rsocket->datagram_buffer == NULL) {
        return -2;
    }
    struct r_datagram* a_datagram = rsocket->datagram_buffer;

    //search buffer and send data, but limit with window size
    int i = 0;
    for (i = 0; i < RUDP_WINDOW; i++) {
        if (a_datagram != NULL) {
            send_datagram(a_datagram);
            a_datagram = a_datagram->next;
        }else {
            break;
        }
    }
    return 1;
}

/**
 * Check if this socket's buffer is empty
 * 
 * return  1 socket buffer is empty
 *        -1 socket buffer is NOT empty.
 */
int is_buffer_empty(rudp_socket_t rsocket) {
    if (rsocket == NULL) {
        return 1;
    }
    if (rsocket->datagram_buffer == NULL) {
        return 1;
    }
    return -1;
}

//TODO: check inside this method, espassially timeout handler.

/**
 *free buffer, from current till in_seq - 1,
 * DO NOT free No. in_seq
 */
struct r_datagram* free_buffer_header(struct r_datagram* buffer, int in_seq) {
    struct r_datagram* new_header = NULL;
    if (buffer != NULL) {
        new_header = buffer->next;
    } else {
        return buffer;
    }


    struct r_datagram* to_free = buffer;
    if (event_timeout_delete(handle_timeout, to_free) < 0) {
        printf("\n*************deregister failed***************in single\n");
    }else {
        printf("\n$$$$$$$$$$$$$$$$$deregister successful$$$$$$$$$$$$$$$\n");
    }
    free(to_free);

    while (new_header != NULL) {
        if (new_header->header.seqno >= in_seq) {
            break;
        }

        to_free = new_header;
        new_header = new_header->next;
        //TODO: check unregister timeout event
        if (event_timeout_delete(handle_timeout, to_free) < 0) {
            printf("\n*************deregister failed*************** inwhile\n");
        }else {
            printf("\n$$$$$$$$$$$$$$$$$deregister successful$$$$$$$$$$$$$$$\n");
        }
        free(to_free);
    }

    return new_header;
}

/*
void free_all(struct r_datagram* buffer) {

    struct r_datagram* new_header = NULL;
    if (buffer != NULL) {
        new_header = buffer->next;
    } else {
        return buffer;
    }
    struct r_datagram* to_free = buffer;



    while (new_header != NULL) {
        if (event_timeout_delete(handle_timeout, to_free) < 0) {
            printf("\n*************deregister failed*************** inwhile\n");
        }else {
            printf("\n$$$$$$$$$$$$$$$$$deregister successful$$$$$$$$$$$$$$$\n");
        }
        if (to_free != NULL) {
            free(to_free);
        }
        to_free = new_header;
        new_header = new_header->next;
    }
    if (event_timeout_delete(handle_timeout, to_free) < 0) {
        printf("\n*************deregister failed***************in single\n");
    }else {
        printf("\n$$$$$$$$$$$$$$$$$deregister successful$$$$$$$$$$$$$$$\n");
    }

};
 */

