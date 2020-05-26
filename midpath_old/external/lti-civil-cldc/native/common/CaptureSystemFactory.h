class CaptureSystem;

class CaptureSystemFactory
{
public:
	virtual CaptureSystem *createCaptureSystem() = 0;// throws CaptureException
};
