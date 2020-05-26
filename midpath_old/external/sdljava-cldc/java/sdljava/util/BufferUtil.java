package sdljava.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Some usefull methods for reading URLs or streams and returning the data as Direct Buffers
 *
 * @author Ivan Z. Ganza
 * @author Robert Schuster
 * @author Bart LEBOEUF
 * @version $Id: BufferUtil.java,v 1.9 2005/09/11 05:19:37 ivan_ganza Exp $
 */
public class BufferUtil {
	
	public static byte[] readInputStream(InputStream in) throws IOException {
		byte[] buf = new byte[1000];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		
		int readBytes = 0;
		while((readBytes = in.read(buf)) != -1) {
			baos.write(buf, 0, readBytes);
		}
		
		return baos.toByteArray();
	}
	
}