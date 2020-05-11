class PThreadStreamThrottleData;

class PThreadStreamThrottle
{
private:
	PThreadStreamThrottleData *data;
public:
	PThreadStreamThrottle();
	~PThreadStreamThrottle();
	void start();
	void waitUntilStart();
};
