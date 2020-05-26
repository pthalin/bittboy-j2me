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

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGEventListener;
import javax.microedition.m2g.SVGImage;

import javax.microedition.lcdui.Canvas;

public final class DefaultSVGAnimator extends SVGAnimator 
                                      implements SVGEventListener {
    /**
     * State constants. These are used to track the animator's state.
     */
    private static final int STATE_STOPPED = 0;
    private static final int STATE_PAUSED = 1;
    private static final int STATE_PLAYING = 2;

    /**
     * Indicates the interruption while in the playing state (for example by an 
     * incomming call).
     */     
    private static final int STATE_INTERRUPTED = 3;

    /** The decorated animator. */
    private final SVGAnimator innerAnimator;
    
    /** The svg image. */
    private final SVGImage svgImage;
    
    /**
     * One of STATE_STOPPED, STATE_PAUSED, STATE_PLAYING or 
     * STATE_INTERRUPTED.
     */
    private int animatorState = STATE_INTERRUPTED;

    /**
     * The SVGEventListener to which to forward event notifications from 
     * innerAnimator.
     */     
    private SVGEventListener svgEventListener;

    private DefaultSVGAnimator(SVGAnimator innerAnimator, SVGImage svgImage) {
        this.innerAnimator = innerAnimator;
        this.svgImage = svgImage;
        innerAnimator.setSVGEventListener(this);

        Canvas targetCanvas = (Canvas)innerAnimator.getTargetComponent();
        targetCanvas.setFullScreenMode(true);
        
        sizeChanged(targetCanvas.getWidth(), targetCanvas.getHeight());
    }

    public static SVGAnimator createAnimator(SVGImage svgImage) {
        SVGAnimator innerAnimator = SVGAnimator.createAnimator(svgImage);
        SVGAnimator outerAnimator = new DefaultSVGAnimator(innerAnimator,
                                                           svgImage);
        
        return outerAnimator;
    }

    public Object getTargetComponent() {
        return innerAnimator.getTargetComponent();
    }

    public float getTimeIncrement() {
        return innerAnimator.getTimeIncrement();
    }

    public void setTimeIncrement(float timeIncrement) {
        innerAnimator.setTimeIncrement(timeIncrement);
    }
    
    public void invokeAndWait(Runnable runnable) throws InterruptedException {
        innerAnimator.invokeAndWait(runnable);
    }
    
    public void invokeLater(Runnable runnable) {
        innerAnimator.invokeLater(runnable);
    }
    
    public void play() {
        if (animatorState != STATE_PLAYING) {
            innerAnimator.play();
            animatorState = STATE_PLAYING;
        } 
    }

    public void pause() {
        if (animatorState == STATE_PLAYING) {
            innerAnimator.pause();
            animatorState = STATE_PAUSED;
        }
    }
    
    public void stop() {
        if (animatorState != STATE_STOPPED) {
            innerAnimator.stop();
            animatorState = STATE_STOPPED;
        }
    }
    
    public void setSVGEventListener(SVGEventListener svgEventListener) {
        this.svgEventListener = svgEventListener;
    }
        
    public void hideNotify() {
        if (animatorState == STATE_PLAYING) {
            innerAnimator.pause();
            animatorState = STATE_INTERRUPTED;
        }
        if (svgEventListener != null) {
            svgEventListener.hideNotify();
        }
    }

    public void showNotify() {
        if (animatorState == STATE_INTERRUPTED) {
            innerAnimator.play();
            animatorState = STATE_PLAYING;   
        }
        if (svgEventListener != null) {
            svgEventListener.showNotify();
        }
    }
        
    public void keyPressed(int keyCode) {
        if (svgEventListener != null) {
            svgEventListener.keyPressed(keyCode);
        }
    }

    public void keyReleased(int keyCode) {
        if (svgEventListener != null) {
            svgEventListener.keyReleased(keyCode);
        }
    }

    public void pointerPressed(int x, int y) {
        if (svgEventListener != null) {
            svgEventListener.pointerPressed(x, y);
        }
    }
    
    public void pointerReleased(int x, int y) {
        if (svgEventListener != null) {
            svgEventListener.pointerReleased(x, y);
        }
    }
    
    public void sizeChanged(int width, int height) {
        svgImage.setViewportWidth(width);
        svgImage.setViewportHeight(height);
            
        if (svgEventListener != null) {
            svgEventListener.sizeChanged(width, height);
        }
    }
}
