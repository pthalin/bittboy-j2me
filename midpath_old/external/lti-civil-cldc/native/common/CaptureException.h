class CaptureException
{
public:
	const char *msg;
	int errorCode;

	CaptureException(const char *_msg, int _errorCode)
	{
		msg = _msg;
		errorCode = _errorCode;
	}

	CaptureException(const char *_msg)
	{
		msg = _msg;
		errorCode = 0;
	}
};
