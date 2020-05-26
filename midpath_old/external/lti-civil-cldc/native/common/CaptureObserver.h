class CaptureStream;
class CaptureException;
class Image;

class CaptureObserver
{
public:
	virtual void onNewImage(CaptureStream *sender, Image *image) = 0;
	virtual void onError(CaptureStream *sender, CaptureException *e) = 0;
};
