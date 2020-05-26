/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.svg;

import java.io.IOException;
import java.io.InputStream;

/**
 * The <code>ProgressInputStream</code> class provides a way to notify the 
 * application of progress when loading a resource, given an expected resource
 * size. 
 *
 */
public class ProgressiveInputStream extends InputStream {
    public interface Listener {
        /**
         * Notifies the listener of the progress in reading the input stream
         * content.
         * 
         * @param progress the current estimated progress, as a penetration factor in the [0,1] interval.
         */
        public void streamProgress(float progress);
    }
    /**
     * The proxied InputStream instance.
     */
    private InputStream pis;
    
    /**
     * The estimated input stream length.
     */
    private float lengthEstimate;
    
    /**
     * The associated listener instance.
     */
    private Listener listener;
    
    /**
     * The current byteCount.
     */
    private float byteCount;
    
    /**
     * The last time a notification was sent.
     */
    private long lastNotification;

    private static final long MIN_NOTIFICATION_LENGTH = 0;
    
    /** 
     * Creates a new instance of ProgressiveInputStream 
     * @param pis the proxied <code>InputStream</code> instance. Should not be null
     * @param lengthEstimate the anticipated input stream length. Should be strictly positive.
     * @param listener the ProgressiveInputStream.Listener instance to notify when the input 
     *        stream has been fully loaded.
     */
    public ProgressiveInputStream(final InputStream pis, final int lengthEstimate, final Listener listener) {
        if (pis == null) {
            throw new NullPointerException();
        }
        
        if (lengthEstimate <= 0 || listener == null) {
            throw new IllegalArgumentException();
        }
        
        this.pis = pis;
        this.lengthEstimate = lengthEstimate;
        this.listener = listener;
        
        // Initial notification.
        lastNotification = System.currentTimeMillis();
        listener.streamProgress(0);
    }

    public final int read() throws IOException {
        int r = pis.read();
        
        if (r == -1) {
            // The end of the stream has been reached.
            listener.streamProgress(1);
        } else {
            byteCount++;
            long t = System.currentTimeMillis();
            if ((t - lastNotification) >= MIN_NOTIFICATION_LENGTH) {
                listener.streamProgress(byteCount / lengthEstimate);
                lastNotification = t;
            }
        }
        
        return r;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        int n = pis.read(b, off, len);
            
        if (n == -1) {
            // The end of the stream has been reached.
            listener.streamProgress(1);
        } else {
            byteCount += n;
            long t = System.currentTimeMillis();
            if ((t - lastNotification) >= MIN_NOTIFICATION_LENGTH) {
                listener.streamProgress(byteCount / lengthEstimate);
                lastNotification = t;
            }
        }
        
        return n;
    }

    public int available() throws IOException {
        return pis.available();
    }

    public void close() throws IOException {
        pis.close();
    }

    public void mark(int readlimit) {
        pis.mark(readlimit);
    }

    public boolean markSupported() {
        return pis.markSupported();
    }

    public void reset() throws IOException {
        pis.reset();
    }

    public long skip(long n) throws IOException {
        return pis.skip(n);
    } 
}
