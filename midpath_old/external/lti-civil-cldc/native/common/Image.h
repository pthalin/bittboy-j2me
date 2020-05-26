
class Image
{

private:
	VideoFormat format;
	unsigned char *bytes;
	int length;	// length of bytes

public:
	
	Image(VideoFormat &_format, unsigned char *_bytes, int _length)
	{	format = _format;
		bytes = _bytes;
		length = _length;
	}
	VideoFormat &getFormat() {return format;}
	unsigned char *getBytes() {return bytes;}
	int getLength() {return length;}
};
