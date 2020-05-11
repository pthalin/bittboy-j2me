class CaptureDeviceInfo
{
	// TODO: this does not handle unicode.
public:
	CaptureDeviceInfo(const wchar_t *_deviceID, const wchar_t *_description)
	{	deviceID = _deviceID;
		description = _description;
	}
	const wchar_t *getDeviceID() {return deviceID;}
	const wchar_t *getDescription() {return description;}

private:
	const wchar_t *deviceID;
	const wchar_t *description;

};
