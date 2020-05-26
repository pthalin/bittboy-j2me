/*
 *
 * Copyright  1990-2006 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions. 
 */

package com.sun.midp.io.j2me.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;

import com.sun.midp.io.ConnectionBaseAdapter;
import com.sun.midp.io.IOToolkit;
import com.sun.midp.log.Logging;
import com.sun.midp.security.SecurityToken;

/**
 * This class implements the necessary functionality
 * for a File connection.
 */
public class Protocol extends ConnectionBaseAdapter implements FileConnection {

    /** Security token for using FileConnection API from PIM */
    private SecurityToken classSecurityToken;

    /** Stores file connection mode */
    private int mode;

    /** File name string */
    private String fileName;

    /** File path string including root filesystem */
    private String filePath;

    /** Root filesystem for the file */
    private String fileRoot;

    /** File original URL */
    private String fileURL;

    /** A peer to the native file */
    private BaseFileHandler fileHandler;

    /** Indicates if there is a need to try to load alternative file handler */
    private static boolean hasOtherFileHandler = true;

    /** Input stream associated with this connection */
    InputStream fis;

    /** Output stream associated with this connection */
    OutputStream fos;

    /**
     * Constructor for file connection implementation.
     */
    public Protocol() {
        connectionOpen = false;
        fileHandler = null;
    }

    /**
     * Opens the file connection.
     * @param name URL path fragment
     * @param mode access mode
     * @param timeouts flag to indicate that timeouts allowed
     * @return an opened Connection
     * @throws IOException if some other kind of I/O error occurs.
     */
    public Connection openPrim(String name, int mode, boolean timeouts)
            throws IOException {

        if (!name.startsWith("//")) {
            throw new IllegalArgumentException("Missing protocol separator");
        }

        int rootStart = name.indexOf('/', 2);

        if (rootStart == -1) {
            throw new IllegalArgumentException("Malformed File URL");
        }

        String sep = System.getProperty("file.separator");
        if (sep == null) {
            throw new IllegalArgumentException(
                "The system property 'file.separator' is not defined");
        }
        if (name.indexOf("/../", rootStart) != -1 ||
            name.indexOf("/./", rootStart) != -1 ||
            name.endsWith("/..") ||
            name.endsWith("/.") ||
            !"/".equals(sep) && name.indexOf(sep, rootStart) != -1 ||
            name.indexOf('\\') != -1) {
                throw new
                    IllegalArgumentException("/. or /.. is not supported "
                    + "or other illegal characters found");
        }

        name = EscapedUtil.getUnescapedString(name);
        String fileURL = "file:" + name;

        // Perform security checks before any object state changes since
        // this method is used not only by Connector.open() but
        // by FileConnection.setFileConnection() too.
        switch (mode) {
        case Connector.READ:
            checkReadPermission(fileURL, mode);
            maxOStreams = 0;
            break;
        case Connector.WRITE:
            checkWritePermission(fileURL, mode);
            maxIStreams = 0;
            break;
        case Connector.READ_WRITE:
            checkReadPermission(fileURL, mode);
            checkWritePermission(fileURL, mode);
            break;
        default:
            throw new IllegalArgumentException("Invalid mode");
        }

        this.fileURL = fileURL;
        this.mode = mode;

        int nameLength = name.length();
        int pathStart = name.indexOf('/', rootStart + 1);

        if (pathStart == -1) {
            throw new IllegalArgumentException("Root is not specified");
        }

        if (pathStart == (nameLength - 1)) {
            fileName = "";
            fileRoot = name.substring(rootStart + 1);
            filePath = name.substring(rootStart);
        } else {
            fileRoot = name.substring(rootStart + 1, pathStart + 1);

            int fileStart = name.lastIndexOf('/', nameLength - 2);

            if (fileStart <= pathStart) {
                fileName = name.substring(pathStart + 1);
                filePath = name.substring(rootStart, pathStart + 1);
            } else {
                filePath = name.substring(rootStart, fileStart + 1);
                fileName = name.substring(fileStart + 1);
            }
        }
        
        if (Logging.TRACE_ENABLED) {
        	System.out.println("[DEBUG] file: Protocol.openPrim(): filePath: " + filePath);
        	System.out.println("[DEBUG] file: Protocol.openPrim(): fileName:" + fileName);
        }

        connectionOpen = true;
        return this;
    }

    /**
     * Opens the file connection and receive security token.
     * @param token security token from PIM
     * @param name URL path fragment
     * @return an opened Connection
     * @throws IOException if some other kind of I/O error occurs.
     */
    public Connection openPrim(SecurityToken token, String name)
            throws IOException {
        return openPrim(token, name, Connector.READ_WRITE);
    }

    /**
     * Opens the file connection and receive security token.
     * @param token security token from PIM
     * @param name URL path fragment
     * @param mode access mode
     * @return an opened Connection
     *  @throws IOException if some other kind of I/O error occurs.
     */
    public Connection openPrim(SecurityToken token, String name, int mode)
            throws IOException {
        classSecurityToken = token;
        return openPrim(name, mode, false);
    }

    // JAVADOC COMMENT ELIDED
    public boolean isOpen() {
        return connectionOpen;
    }

    // JAVADOC COMMENT ELIDED
    public InputStream openInputStream() throws IOException {

        checkReadPermission();

        try {
            ensureOpenAndConnected();
        } catch (ConnectionClosedException e) {
            throw new IOException(e.getMessage());
        }

        // IOException when target file doesn't exist
        if (!fileHandler.exists()) {
            throw new IOException("Target file doesn't exist");
        }

        if (!fileHandler.canRead()) { // no read access
            throw new SecurityException("No read access");
        }

        fileHandler.openForRead();

        fis = super.openInputStream();

        return fis;
    }

    // JAVADOC COMMENT ELIDED
    public OutputStream openOutputStream() throws IOException {
        return openOutputStream(0);
    }

    // JAVADOC COMMENT ELIDED
    public OutputStream openOutputStream(long byteOffset) throws IOException {
        if (byteOffset < 0) {
            throw new IllegalArgumentException("Offset has a negative value");
        }

        checkWritePermission();

        try {
            ensureOpenAndConnected();
        } catch (ConnectionClosedException e) {
            throw new IOException(e.getMessage());
        }

        // IOException when target file doesn't exist
        if (!fileHandler.exists()) {
            throw new IOException("Target file doesn't exist");
        }

        if (!fileHandler.canWrite()) {
            // no write access
            throw new SecurityException("No write access");
        }

        fileHandler.openForWrite();
        fileHandler.positionForWrite(byteOffset);

        fos = super.openOutputStream();

        return fos;
    }

    // JAVADOC COMMENT ELIDED
    public long totalSize() {
        long size = -1;

        try {
            checkRootReadPermission();

            ensureOpenAndConnected();

            size = fileHandler.totalSize();
        } catch (IOException e) {
            size = -1;
        }

        return size;
    }

    // JAVADOC COMMENT ELIDED
    public long availableSize() {
        long size = -1;

        try {
            checkRootReadPermission();

            ensureOpenAndConnected();

            size = fileHandler.availableSize();
        } catch (IOException e) {
            size = -1;
        }

        return size;
    }

    // JAVADOC COMMENT ELIDED
    public long usedSize() {
        long size = -1;

        try {
            checkRootReadPermission();

            ensureOpenAndConnected();

            size = fileHandler.usedSize();
        } catch (IOException e) {
            size = -1;
        }

        return size;
    }

    // JAVADOC COMMENT ELIDED
    public long directorySize(boolean includeSubDirs) throws IOException {
        long size = 0;

        // Permissions and ensureOpenAndConnected called by exists()
        if (exists()) {
            if (!isDirectory()) {
                throw new
                    IOException("directorySize is not invoked on directory");
            }
        } else {
            return -1L;
        }

        try {
            Enumeration fileList = listInternal(null, true);

            while (fileList.hasMoreElements()) {
                String fname = (String) fileList.nextElement();
                String furl = getURL() + "/" + fname;
                FileConnection tfc = null;

                try {
                    tfc = (FileConnection)Connector.open(furl);
                    if (fname.charAt(fname.length()-1) != '/') {
                        size += tfc.fileSize();
                    } else if (includeSubDirs) {
                        size += tfc.directorySize(includeSubDirs);
                    }
                } catch (IOException e1) {
                } finally {
                    if (tfc != null) {
                        tfc.close();
                    }
                }
            }
        } catch (IOException e) {
            size = -1;
        }

        return size;
    }

    // JAVADOC COMMENT ELIDED
    public long fileSize() throws IOException {
        long size = -1;

        checkReadPermission();

        if (isDirectory()) {
            throw new IOException("fileSize invoked on a directory");
        }

        try {
            ensureOpenAndConnected();

            size = fileHandler.fileSize();
        } catch (IOException e) {
            size = -1;
        }

        return size;
    }

    // JAVADOC COMMENT ELIDED
    public boolean canRead() {
        boolean res = false;

        try {
            checkReadPermission();

            ensureOpenAndConnected();

            res = fileHandler.canRead();
        } catch (IOException e) {
            res = false;
        }

        return res;
    }

    // JAVADOC COMMENT ELIDED
    public boolean canWrite() {
        boolean res = false;

        try {
            checkReadPermission();

            ensureOpenAndConnected();

            res = fileHandler.canWrite();
        } catch (IOException e) {
            res = false;
        }

        return res;
    }

    // JAVADOC COMMENT ELIDED
    public boolean isHidden() {
        boolean res = false;

        try {
            checkReadPermission();

            ensureOpenAndConnected();

            res = fileHandler.isHidden();
        } catch (IOException e) {
            res = false;
        }

        return res;
    }

    // JAVADOC COMMENT ELIDED
    public void setReadable(boolean readable) throws IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        fileHandler.setReadable(readable);
    }

    // JAVADOC COMMENT ELIDED
    public void setWritable(boolean writable) throws IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        fileHandler.setWritable(writable);
    }

    // JAVADOC COMMENT ELIDED
    public void setHidden(boolean hidden) throws IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        fileHandler.setHidden(hidden);
    }

    // JAVADOC COMMENT ELIDED
    public Enumeration list() throws IOException {
        return listInternal(null, false);
    }

    // JAVADOC COMMENT ELIDED
    public Enumeration list(String filter, boolean includeHidden)
        throws IOException {

        if (filter == null) {
            throw new NullPointerException("List filter is null");
        }

        return listInternal(EscapedUtil.getUnescapedString(filter),
            includeHidden);
    }

    // JAVADOC COMMENT ELIDED
    public void create() throws IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        if (fileName.charAt(fileName.length() - 1) == '/') {
            throw new IOException("Can not create directory");
        }

        fileHandler.create();
    }

    // JAVADOC COMMENT ELIDED
    public void mkdir() throws IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        fileHandler.mkdir();
    }

    // JAVADOC COMMENT ELIDED
    public boolean exists() {
        boolean res = false;

        try {
            checkReadPermission();

            ensureOpenAndConnected();

            res = fileHandler.exists();
        } catch (IOException e) {
            res = false;
        }

        return res;
    }

    // JAVADOC COMMENT ELIDED
    public boolean isDirectory() {
        boolean res = false;

        try {
            checkReadPermission();

            ensureOpenAndConnected();

            res = fileHandler.isDirectory();
        } catch (IOException e) {
            res = false;
        }

        return res;
    }

    // JAVADOC COMMENT ELIDED
    public void delete() throws java.io.IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        try {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        } catch (IOException e) {
            // Ignore silently
        }

        try {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            // Ignore silently
        }

        try {
            fileHandler.close();
        } catch (IOException e) {
            // Ignore silently
        }

        fileHandler.delete();
    }

    // JAVADOC COMMENT ELIDED
    public void rename(String newName) throws IOException {
        checkWritePermission();

        newName = EscapedUtil.getUnescapedString(newName);
        // Following line will throw NullPointerException if newName is null
        int dirindex = newName.indexOf("/");
        if (dirindex != -1 && dirindex != (newName.length() - 1)) {
            throw new
              IllegalArgumentException("New name contains path specification");
        }

        String sep = System.getProperty("file.separator");
        if (sep == null) {
            throw new IllegalArgumentException(
                "The system property 'file.separator' is not defined");
        }

        if (!"/".equals(sep) && newName.indexOf(sep) != -1) {
            throw new
              IllegalArgumentException("New name contains path specification");
        }

        ensureOpenAndConnected();
        checkIllegalChars(newName);

        try {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        } catch (IOException e) {
            // Ignore silently
        }

        try {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            // Ignore silently
        }

        try {
            fileHandler.close();
        } catch (IOException e) {
            // Ignore silently
        }

        fileHandler.rename(filePath + newName);

        fileName = newName;
        fileURL = "file://" + filePath + fileName;
    }

    // JAVADOC COMMENT ELIDED
    public void truncate(long byteOffset) throws IOException {
        checkWritePermission();

        ensureOpenAndConnected();

        if (byteOffset < 0) {
            throw new IllegalArgumentException("offset is negative");
        }

        try {
            if (fos != null) {
                fos.flush();
            }
        } catch (IOException e) {
            // Ignore silently
        }

        fileHandler.truncate(byteOffset);
    }

    // JAVADOC COMMENT ELIDED
    public void setFileConnection(String fileName) throws IOException {
        ensureOpenAndConnected();

        // Note: permissions are checked by openPrim method

        // Following line will throw NullPointerException if fileName is null
        int dirindex = fileName.indexOf("/");
        if (dirindex != -1 && dirindex != (fileName.length() - 1)) {
            throw new IllegalArgumentException(
                "Contains any path specification");
        }

        if (fileName.equals("..") && this.fileName.length() == 0) {
            throw new IOException(
                "Cannot set FileConnection to '..' from a file system root");
        }

        String sep = System.getProperty("file.separator");
        if (sep == null) {
            throw new IllegalArgumentException(
                "The system property 'file.separator' is not defined");
        }

        if (!"/".equals(sep) && fileName.indexOf(sep) != -1) {
            throw new
            IllegalArgumentException("Contains any path specification");
        }

        checkIllegalChars(fileName);

        // According to the spec, the current FileConnection object must refer
        // to a directory.
        // Check this right here in order to avoid IllegalModeException instead
        // of IOException.
        if (!fileHandler.isDirectory()) {
            throw new IOException("Not a directory");
        }

        String origPath = filePath, origName = this.fileName;

        // Note: security checks are performed before any object state changes
        if (fileName.equals("..")) {
            // go one directory up
            openPrim("//" + filePath, mode, false);
        } else {
            if (this.fileName.endsWith("/") || this.fileName.length() == 0) {
                sep = "";
            } else {
                sep = "/";
            }
            // go deeper in directory structure
            openPrim("//" + filePath
                     + this.fileName + sep + fileName,
                     mode, false);
        }

        // Old file connection must be a directory. It can not have open
        // streams so no need to close it. Just reset it to null
        fileHandler = null;

        // Reconnect to the new target
        ensureOpenAndConnected();

        // At this point we are already refer to the new file
        if (!fileHandler.exists()) {
            // Revert to an old file
            openPrim("//" + origPath + origName, mode, false);
            fileHandler = null;

            throw new IllegalArgumentException("New target does not exists");
        }
    }

    /**
     * Spec is not consistent: sometimes it requires IOException
     * and sometimes IllegalArgumentException in case of illegal chars
     * in the filename
     * @param name URL path fragment
     * @throws IOException if name contains unsupported characters
     */
    private void checkIllegalChars(String name) throws IOException {
        String illegalChars = fileHandler.illegalFileNameChars();
        for (int i = 0; i < illegalChars.length(); i++) {
            if (name.indexOf(illegalChars.charAt(i)) != -1) {
                throw new
                    IOException("Contains characters invalid for a filename");
            }
        }
    }

    // JAVADOC COMMENT ELIDED
    public String getName() {
        String name = fileName;

        try {
            if (exists()) {
                if (isDirectory()) {
                    if (!name.equals("") && !name.endsWith("/"))
                        name += "/";
                } else {
                    if (name.endsWith("/"))
                        name = name.substring(0, name.length()-1);
                }
            }
        } catch (SecurityException e) {
            // According to spec should silently ignore any exceptions
        } catch (IllegalModeException e) {
            // According to spec should silently ignore any exceptions
        } catch (ConnectionClosedException e) {
            // According to spec should silently ignore any exceptions
        }

        return name;
    }

    // JAVADOC COMMENT ELIDED
    public String getPath() {
        return filePath;
    }

    // JAVADOC COMMENT ELIDED
    public String getURL() {
        String url = EscapedUtil.getEscapedString(fileURL);

        try {
            if (exists()) {
                if (isDirectory()) {
                    if (!url.endsWith("/"))
                        url += "/";
                } else {
                    if (url.endsWith("/"))
                        url = url.substring(0, url.length()-1);
                }
            }
        } catch (SecurityException e) {
            // According to spec should silently ignore any exceptions
        } catch (IllegalModeException e) {
            // According to spec should silently ignore any exceptions
        } catch (ConnectionClosedException e) {
            // According to spec should silently ignore any exceptions
        }

        return url;
    }

    // JAVADOC COMMENT ELIDED
    public long lastModified() {
        long res = 0;

        try {
            checkReadPermission();

            ensureOpenAndConnected();

            res =  fileHandler.lastModified();
        } catch (IOException e) {
            res = 0;
        }

        return res;
    }

    // JAVADOC COMMENT ELIDED
    protected int readBytes(byte b[], int off, int len)
        throws IOException {

        checkReadPermission();

        ensureConnected();

        int readBytes = fileHandler.read(b, off, len);
        // return '-1' instead of '0' as stream specification requires
        // in case the end of the stream has been reached
        return (readBytes > 0) ? readBytes : -1;
    }

    // JAVADOC COMMENT ELIDED
    protected int writeBytes(byte b[], int off, int len)
        throws IOException {
        checkWritePermission();

        ensureConnected();

        return fileHandler.write(b, off, len);
    }

    // JAVADOC COMMENT ELIDED
    protected void flush() throws IOException {
        checkWritePermission();

        ensureConnected();

        fileHandler.flush();
    }

    // JAVADOC COMMENT ELIDED
    protected void closeInputStream() throws IOException {
        maxIStreams++;
        fileHandler.closeForRead();
        super.closeInputStream();
    }

    // JAVADOC COMMENT ELIDED
    protected void closeOutputStream() throws IOException {
        maxOStreams++;
        flush();
        fileHandler.closeForWrite();
        super.closeOutputStream();
    }

    // JAVADOC COMMENT ELIDED
    protected void disconnect() throws IOException {
        try {
            if (fileHandler != null) {
                fileHandler.close();
            }
        } finally {
            fileHandler = null;
        }
    }

    // In order to compile against MIDP's ConnectionBaseAdapter

    // JAVADOC COMMENT ELIDED
    protected void connect(String name, int mode, boolean timeouts)
        throws IOException {}

    // JAVADOC COMMENT ELIDED
    protected void ensureConnected() throws IOException {
        if (!isRoot(fileRoot)) {
            throw new IOException("Root is not accessible");
        }

        if (fileHandler == null) {
            fileHandler = getFileHandler();

            fileHandler.connect(fileRoot, filePath + fileName);

            fileHandler.createPrivateDir(fileRoot);
        }
    }

    /**
     * Checks if path is a root path.
     * @param root path to be checked
     * @return <code>true</code> if path is a root,
     *                <code>false</code> otherwise.
     */
    private boolean isRoot(String root) {
        Vector r = listRoots(); // retrieve up-to-date list of mounted roots
        for (int i = 0; i < r.size(); i++) {
            String name = (String)r.elementAt(i);
            if (name.equals(root)) {
                return true;
            }
        }
        return false;
    }

    // JAVADOC COMMENT ELIDED
    protected void ensureOpenAndConnected() throws IOException {
        if (!isOpen()) {
            throw new ConnectionClosedException("Connection is closed");
        }

        ensureConnected();
    }

    // JAVADOC COMMENT ELIDED
    private final void checkReadPermission(String fileURL, int mode)
            throws InterruptedIOException {

//        if (classSecurityToken == null) { // FC permission
//            MIDletSuite suite = Scheduler.getScheduler().getMIDletSuite();
//
//            try {
//                suite.checkForPermission(Permissions.FILE_CONNECTION_READ,
//                    fileURL);
//            } catch (InterruptedException ie) {
//                throw new InterruptedIOException(
//                    "Interrupted while trying to ask the user permission");
//            }
//        } else { // call from PIM
//            classSecurityToken.checkIfPermissionAllowed(
//                Permissions.FILE_CONNECTION_READ);
//        }

        if (mode == Connector.WRITE) {
            throw new IllegalModeException("Connection is write only");
        }
    }

    // JAVADOC COMMENT ELIDED
    protected final void checkReadPermission() throws InterruptedIOException {
        checkReadPermission(fileURL, mode);
    }

    // JAVADOC COMMENT ELIDED
    protected final void checkRootReadPermission()
            throws InterruptedIOException {

//        if (classSecurityToken == null) { // FC permission
//            MIDletSuite suite = Scheduler.getScheduler().getMIDletSuite();
//
//            try {
//                suite.checkForPermission(Permissions.FILE_CONNECTION_READ,
//                                     "file://" + fileRoot);
//            } catch (InterruptedException ie) {
//                throw new InterruptedIOException(
//                    "Interrupted while trying to ask the user permission");
//            }
//        } else { // call from PIM
//            classSecurityToken.checkIfPermissionAllowed
//                (Permissions.FILE_CONNECTION_READ);
//        }

        if (mode == Connector.WRITE) {
            throw new IllegalModeException("Connection is write only");
        }
    }

    // JAVADOC COMMENT ELIDED
    private final void checkWritePermission(String fileURL, int mode)
            throws InterruptedIOException {

//        if (classSecurityToken == null) { // FC permission
//            MIDletSuite suite = Scheduler.getScheduler().getMIDletSuite();
//
//            try {
//                suite.checkForPermission(Permissions.FILE_CONNECTION_WRITE,
//                                     fileURL);
//            } catch (InterruptedException ie) {
//                throw new InterruptedIOException(
//                    "Interrupted while trying to ask the user permission");
//            }
//        } else { // call from PIM
//            classSecurityToken.checkIfPermissionAllowed
//                (Permissions.FILE_CONNECTION_WRITE);
//        }

        if (mode == Connector.READ) {
            throw new IllegalModeException("Connection is read only");
        }
    }

    // JAVADOC COMMENT ELIDED
    protected final void checkWritePermission() throws InterruptedIOException {
        checkWritePermission(fileURL, mode);
    }

    // JAVADOC COMMENT ELIDED
    public static Vector listRoots() {
        BaseFileHandler fh = getFileHandler();
        return fh.listRoots();
    }

    // JAVADOC COMMENT ELIDED
    private Enumeration listInternal(String filter, boolean includeHidden)
        throws IOException {
        checkReadPermission();

        ensureOpenAndConnected();

        if (filter != null) {
            String sep = System.getProperty("file.separator");
            if (sep == null) {
                throw new IllegalArgumentException(
                    "The system property 'file.separator' is not defined");
            }

            if (filter.indexOf("/") != -1 || filter.indexOf(sep) != -1) {
                throw new IllegalArgumentException(
                    "Filter contains any path specification");
            }

            String illegalChars = fileHandler.illegalFileNameChars();
            for (int i = 0; i < illegalChars.length(); i++) {
                if (filter.indexOf(illegalChars.charAt(i)) != -1) {
                    throw new
                        IllegalArgumentException("Filter contains characters "
                            + "invalid for a filename");
                }
            }
        }

        return fileHandler.list(filter, includeHidden).elements();
    }

    /**
     * Gets the file handler.
     * @return handle to current file connection
     */
    private static BaseFileHandler getFileHandler() {
    	
    	return IOToolkit.getToolkit().createBaseFileHandler();
    	
//        String def = "com.sun.midp.io.j2me.file.DefaultFileHandler";
//        String n = null;
//        if (hasOtherFileHandler) {
//            n = Configuration.getProperty(
//                               "com.sun.midp.io.j2me.fileHandlerImpl");
//            if (n == null) {
//                hasOtherFileHandler = false;
//            }
//        }
//        if (hasOtherFileHandler) {
//            try {
//                return (BaseFileHandler) (Class.forName(n)).newInstance();
//            } catch (ClassNotFoundException e) {
//                hasOtherFileHandler = false;
//            } catch (Error e) {
//                hasOtherFileHandler = false;
//            } catch (IllegalAccessException e) {
//                hasOtherFileHandler = false;
//            } catch (InstantiationException e) {
//                hasOtherFileHandler = false;
//            }
//        }
//        try {
//            return (BaseFileHandler) (Class.forName(def)).newInstance();
//        } catch (ClassNotFoundException e) {
//        } catch (Error e) {
//        } catch (IllegalAccessException e) {
//        } catch (InstantiationException e) {
//        }
//        throw new Error("Unable to create FileConnection Handler");
    }
    
}
/**
 * Utility for escaped character handling.
 */
class EscapedUtil {
    /**
     * Gets the escaped string.
     * @param name string to be processed
     * @return escaped string
     * @throws IllegalArgumentException if encoding not supported
     */
    public static String getEscapedString(String name) {
        try {
            if (name == null) {
                return null;
            }
            byte newName[] = new byte[name.length()*12];
            int nextPlace = 0;
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (containsReserved(c)) {
                    char data[] = {c};
                    byte[] reservedBytes = new String(data).getBytes("utf-8");
                    for (int j = 0; j < reservedBytes.length; j++) {
                        newName[nextPlace++] = '%';
                        byte upper = (byte) ((reservedBytes[j] >> 4) & 0xF);
                        if (upper <= 9) {
                            newName[nextPlace++] = (byte) ('0' + upper);
                        } else {
                            newName[nextPlace++] = (byte) ('A' + (upper - 10));
                        }
                        byte lower = (byte) (reservedBytes[j] & 0xF);
                        if (lower <= 9) {
                            newName[nextPlace++] = (byte) ('0' + lower);
                        } else {
                            newName[nextPlace++] = (byte) ('A' + (lower - 10));
                        }
                    }
                } else {
                    newName[nextPlace++] = (byte)c;
                }
            }
            return new String(newName, 0, nextPlace);
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(uee.getMessage());
        }
    }


    /**
     * Gets the unescaped string.
     * <pre>
     *   escaped   = "%" hex hex
     *   hex       = digit | "A" | "B" | "C" | "D" | "E" | "F" |
     *                       "a" | "b" | "c" | "d" | "e" | "f"
     * </pre>
     * @param name string to be processed
     * @return escaped string
     * @throws IllegalArgumentException if encoding not supported
     *
     */
    public static String getUnescapedString(String name) {
        try {
            if (name == null) {
                return null;
            }
            if (name.indexOf("%") == -1) {
                return name;
            } else {
                byte newName[] = new byte[name.length()];
                int nextPlace = 0;
                for (int i = 0; i < name.length(); i++) {
                    char c = name.charAt(i);
                    if (c == '%') {
                        String hexNum = name.substring(i+1, i+3).toUpperCase();
                        if (isHexCharsLegal(hexNum)) {
                            c = hexToChar(hexNum);
                            i = i + 2;
                        } else {
                            throw new IllegalArgumentException("Bad format");
                        }
                    } else if (containsReserved(c)) {
                        throw
                            new IllegalArgumentException("Bad escaped format");
                    }
                    newName[nextPlace++] = (byte)c;
                }
                return new String(newName, 0, nextPlace,  "UTF-8");
            }
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(uee.getMessage());
        }
    }

    /**
     * Checks if the hexadecimal character is valid.
     * @param hexValue string to be checked
     * @return <code>true</code> if all characters are valid
     */
    private static boolean isHexCharsLegal(String hexValue) {
        if ((isDigit(hexValue.charAt(0)) || isABCDEF(hexValue.charAt(0))) &&
            (isDigit(hexValue.charAt(1)) || isABCDEF(hexValue.charAt(1)))) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Converts one hexadecimal char.
     * @param hexValue string to be processed
     * @return normalized hex value
     */
    private static char hexToChar(String hexValue) {
        char c = 0;
        if (isDigit(hexValue.charAt(0))) {
            c += (hexValue.charAt(0) - '0')*16;
        } else {
            c += (hexValue.charAt(0) - 'A' + 10)*16;
        }

        if (isDigit(hexValue.charAt(1))) {
            c += (hexValue.charAt(01) - '0');
        } else {
            c += (hexValue.charAt(1) - 'A' + 10);
        }
        return c;
    }

    /**
     * Checks if character is decimal digit.
     * @param c character to check
     * @return <code>true</code> if in the range 0..9
     */
    private static boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Checks if character is hexadecimal digit.
     * @param c character to check
     * @return  <code>true</code> if in the range A..F
     */
    private static boolean isABCDEF(char c) {
        return (c >= 'A' && c <= 'F');
    }

    /**
     * Checks if character is from the reserved character set.
     * @param c character to check
     * @return  <code>true</code> if not in the range A..Z,
     * a..z,..9, or punctuation (forward slash, colon, hyphen,
     * under score, period, exclamation, tilde, asterisk, single quote,
     * left paren or right paren).
     */
    private static boolean containsReserved(char c) {
        return !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ||
                 (c >= '0' && c <= '9') || ("/:-_.!~*'()".indexOf(c) != -1));
    }

} // End  of EscapeUtil
