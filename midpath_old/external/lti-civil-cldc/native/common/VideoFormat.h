// formats:
#define RGB24	1
#define RGB32	2

#define FPS_UNKNOWN -1.f


class VideoFormat
{
public:
	VideoFormat(void* _handle, int _formatType, int _width, int _height, float _fps)
	{	
		handle = _handle;
		formatType = _formatType;
		width = _width;
		height= _height;
		fps = _fps;
	}
	VideoFormat()
	{
		handle = 0;
		formatType = -1;
		width = -1;
		height= -1;
		fps = FPS_UNKNOWN;	
	}

	void *handle;	// arbitrary handle for storing implementation-specific data
	int formatType;
	int width;
	int height;
	float fps;

	int getFormatType() {return formatType;}
	int getWidth() {return width;}
	int getHeight() {return height;}
	float getFPS() {return fps;}

};
