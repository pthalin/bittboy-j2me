#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include "PThreadStreamThrottle.h"

class PThreadStreamThrottleData
{
public:
#if 0
	//pthread_mutex_t count_mutex;
	pthread_mutex_t condition_mutex;
	pthread_cond_t  condition_cond;

	PThreadStreamThrottleData()
	{
		//count_mutex     = PTHREAD_MUTEX_INITIALIZER;
		condition_mutex = PTHREAD_MUTEX_INITIALIZER;
		condition_cond  = PTHREAD_COND_INITIALIZER;
	}
#endif
};

// TODO: why doesn't this work inside a class?
//pthread_mutex_t count_mutex= PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t condition_mutex= PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t  condition_cond= PTHREAD_COND_INITIALIZER;
bool started = false;

PThreadStreamThrottle::PThreadStreamThrottle()
{
	data = new PThreadStreamThrottleData();
}
PThreadStreamThrottle::~PThreadStreamThrottle()
{
	delete data;
}

void PThreadStreamThrottle::start()
{
	pthread_mutex_lock( &condition_mutex );
	started = true;
	pthread_cond_signal( &condition_cond );
	pthread_mutex_unlock( &condition_mutex );

}
void PThreadStreamThrottle::waitUntilStart()
{
	printf("PThreadStreamThrottle::waitUntilStart()\n");
	fflush(stdout);
	pthread_mutex_lock( &condition_mutex );
	//printf("PThreadStreamThrottle::waitUntilStart got lock\n");
	//fflush(stdout);
	while (!started)
		pthread_cond_wait( &condition_cond, &condition_mutex );
	//printf("PThreadStreamThrottle::waitUntilStart condition is true\n");
	//fflush(stdout);
	pthread_mutex_unlock( &condition_mutex );

	printf("PThreadStreamThrottle::waitUntilStart() - done waiting\n");
	fflush(stdout);

}

