package contact;

/**
 * Copyright 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */


import java.awt.AWTKeyStroke;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.KeyboardFocusManager;
import java.awt.geom.NoninvertibleTransformException;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;

import javax.media.nativewindow.NativeWindow;
import javax.media.nativewindow.OffscreenLayerOption;
import javax.media.nativewindow.WindowClosingProtocol;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.MenuSelectionManager;

import jogamp.nativewindow.awt.AWTMisc;
import jogamp.nativewindow.jawt.JAWTUtil;
import jogamp.newt.Debug;
import jogamp.newt.WindowImpl;
import jogamp.newt.awt.NewtFactoryAWT;
import jogamp.newt.awt.event.AWTParentWindowAdapter;
import jogamp.newt.driver.DriverClearFocus;
import jogamp.opengl.awt.AWTTilePainter;

import com.jogamp.common.util.awt.AWTEDTExecutor;
import com.jogamp.nativewindow.awt.AWTPrintLifecycle;
import com.jogamp.nativewindow.awt.AWTWindowClosingProtocol;
import com.jogamp.nativewindow.awt.JAWTWindow;
import com.jogamp.newt.Display;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.awt.AWTAdapter;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.opengl.util.GLDrawableUtil;
import com.jogamp.opengl.util.TileRenderer;
/**
 * AWT {@link java.awt.Canvas Canvas} containing a NEWT {@link Window} using native parenting.
 *
 * <h5><A NAME="java2dgl">Offscreen Layer Remarks</A></h5>
 *
 * {@link OffscreenLayerOption#setShallUseOffscreenLayer(boolean) setShallUseOffscreenLayer(true)}
 * maybe called to use an offscreen drawable (FBO or PBuffer) allowing
 * the underlying JAWT mechanism to composite the image, if supported.
 */
@SuppressWarnings("serial")
public class NewtCanvasAWT extends GLJPanel implements WindowClosingProtocol, OffscreenLayerOption, AWTPrintLifecycle {
    public static final boolean DEBUG = Debug.debug("Window");

    private final Object sync = new Object();
    private JAWTWindow jawtWindow = null;
    private boolean shallUseOffscreenLayer = false;
    private Window newtChild = null;
    private boolean newtChildAttached = false;
    private boolean isOnscreen = true;
    private WindowClosingMode newtChildCloseOp;
    private final AWTParentWindowAdapter awtAdapter;
    private final AWTAdapter awtMouseAdapter;
    private final AWTAdapter awtKeyAdapter;

    private final AWTWindowClosingProtocol awtWindowClosingProtocol =
          new AWTWindowClosingProtocol(this, new Runnable() {
                @Override
                public void run() {
                    NewtCanvasAWT.this.destroyImpl(false /* removeNotify */, true /* windowClosing */);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    if( newtChild != null ) {
                        newtChild.sendWindowEvent(WindowEvent.EVENT_WINDOW_DESTROY_NOTIFY);
                    }
                }
            } );

    /**
     * Instantiates a NewtCanvas without a NEWT child.<br>
     */
    public NewtCanvasAWT() {
        super();
        awtMouseAdapter = new AWTMouseAdapter().addTo(this);
        awtKeyAdapter = new AWTKeyAdapter().addTo(this);
        awtAdapter = (AWTParentWindowAdapter) new AWTParentWindowAdapter().addTo(this);
        awtAdapter.removeWindowClosingFrom(this); // we utilize AWTWindowClosingProtocol triggered destruction!
    }

    /**
     * Instantiates a NewtCanvas without a NEWT child.<br>
     */
    public NewtCanvasAWT(GraphicsConfiguration gc) {
    	super();
//        super(gc);
        awtMouseAdapter = new AWTMouseAdapter().addTo(this);
        awtKeyAdapter = new AWTKeyAdapter().addTo(this);
        awtAdapter = (AWTParentWindowAdapter) new AWTParentWindowAdapter().addTo(this);
        awtAdapter.removeWindowClosingFrom(this); // we utilize AWTWindowClosingProtocol triggered destruction!
    }

    /**
     * Instantiates a NewtCanvas with a NEWT child.
     */
    public NewtCanvasAWT(Window child) {
        super();
        awtMouseAdapter = new AWTMouseAdapter().addTo(this);
        awtKeyAdapter = new AWTKeyAdapter().addTo(this);
        awtAdapter = (AWTParentWindowAdapter) new AWTParentWindowAdapter().addTo(this);
        awtAdapter.removeWindowClosingFrom(this); // we utilize AWTWindowClosingProtocol triggered destruction!
        setNEWTChild(child);
    }

    /**
     * Instantiates a NewtCanvas with a NEWT child.
     */
    public NewtCanvasAWT(GraphicsConfiguration gc, Window child) {
//        super(gc);
        awtMouseAdapter = new AWTMouseAdapter().addTo(this);
        awtKeyAdapter = new AWTKeyAdapter().addTo(this);
        awtAdapter = (AWTParentWindowAdapter) new AWTParentWindowAdapter().addTo(this);
        awtAdapter.removeWindowClosingFrom(this); // we utilize AWTWindowClosingProtocol triggered destruction!
        setNEWTChild(child);
    }

    @Override
    public void setShallUseOffscreenLayer(boolean v) {
        shallUseOffscreenLayer = v;
    }

    @Override
    public final boolean getShallUseOffscreenLayer() {
        return shallUseOffscreenLayer;
    }

    @Override
    public final boolean isOffscreenLayerSurfaceEnabled() {
        final JAWTWindow w = jawtWindow;
        return null != w && w.isOffscreenLayerSurfaceEnabled();
    }

    /**
     * Returns true if the AWT component is parented to an {@link java.applet.Applet},
     * otherwise false. This information is valid only after {@link #addNotify()} is issued,
     * ie. before adding the component to the AWT tree and make it visible.
     */
    public boolean isApplet() {
        final JAWTWindow w = jawtWindow;
        return null != w && w.isApplet();
    }

    boolean isParent() {
        return null!=newtChild && jawtWindow == newtChild.getParent();
    }

    boolean isFullscreen() {
        return null != newtChild && newtChild.isFullscreen();
    }

    class FocusAction implements Window.FocusRunnable {
        @Override
        public boolean run() {
            final boolean isParent = isParent();
            final boolean isFullscreen = isFullscreen();
            if(DEBUG) {
                System.err.println("NewtCanvasAWT.FocusAction: "+Display.getThreadName()+", isOnscreen "+isOnscreen+", hasFocus "+hasFocus()+", isParent "+isParent+", isFS "+isFullscreen);
            }
            if( isParent && !isFullscreen ) { // must be parent of newtChild _and_ newtChild not fullscreen
                if( isOnscreen ) {
                    // Remove the AWT focus in favor of the native NEWT focus
                    AWTEDTExecutor.singleton.invoke(false, awtClearGlobalFocusOwner);
                } else if( !hasFocus() ) {
                    // In offscreen mode we require the focus!
                    // Newt-EDT -> AWT-EDT may freeze Window's native peer requestFocus.
                    NewtCanvasAWT.super.requestFocus();
                }
            }
            return false; // NEWT shall proceed requesting the native focus
        }
    }
    private final FocusAction focusAction = new FocusAction();

    private static class ClearFocusOwner implements Runnable {
        @Override
        public void run() {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        }
    }
    private static final Runnable awtClearGlobalFocusOwner = new ClearFocusOwner();

    /** Must run on AWT-EDT non-blocking, since it invokes tasks on AWT-EDT w/ waiting otherwise. */
    private final Runnable awtClearSelectedMenuPath = new Runnable() {
        @Override
        public void run() {
            MenuSelectionManager.defaultManager().clearSelectedPath();
        }
    };
    private final WindowListener clearAWTMenusOnNewtFocus = new WindowAdapter() {
          @Override
          public void windowResized(WindowEvent e) {
              updateLayoutSize();
          }
          @Override
          public void windowGainedFocus(WindowEvent arg0) {
              if( isParent() && !isFullscreen() ) {
                  AWTEDTExecutor.singleton.invoke(false, awtClearSelectedMenuPath);
              }
          }
    };

    class FocusTraversalKeyListener implements KeyListener {
         @Override
         public void keyPressed(KeyEvent e) {
             if( isParent() && !isFullscreen() ) {
                 handleKey(e, false);
             }
         }
         @Override
         public void keyReleased(KeyEvent e) {
             if( isParent() && !isFullscreen() ) {
                 handleKey(e, true);
             }
         }

         void handleKey(KeyEvent evt, boolean onRelease) {
             if(null == keyboardFocusManager) {
                 throw new InternalError("XXX");
             }
             final AWTKeyStroke ks = AWTKeyStroke.getAWTKeyStroke(evt.getKeyCode(), evt.getModifiers(), onRelease);
             boolean suppress = false;
             if(null != ks) {
                 final Set<AWTKeyStroke> fwdKeys = keyboardFocusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
                 final Set<AWTKeyStroke> bwdKeys = keyboardFocusManager.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
                 if(fwdKeys.contains(ks)) {
                     final Component nextFocus = AWTMisc.getNextFocus(NewtCanvasAWT.this, true /* forward */);
                     if(DEBUG) {
                         System.err.println("NewtCanvasAWT.focusKey (fwd): "+ks+", current focusOwner "+keyboardFocusManager.getFocusOwner()+", hasFocus: "+hasFocus()+", nextFocus "+nextFocus);
                     }
                     // Newt-EDT -> AWT-EDT may freeze Window's native peer requestFocus.
                     nextFocus.requestFocus();
                     suppress = true;
                 } else if(bwdKeys.contains(ks)) {
                     final Component prevFocus = AWTMisc.getNextFocus(NewtCanvasAWT.this, false /* forward */);
                     if(DEBUG) {
                         System.err.println("NewtCanvasAWT.focusKey (bwd): "+ks+", current focusOwner "+keyboardFocusManager.getFocusOwner()+", hasFocus: "+hasFocus()+", prevFocus "+prevFocus);
                     }
                     // Newt-EDT -> AWT-EDT may freeze Window's native peer requestFocus.
                     prevFocus.requestFocus();
                     suppress = true;
                 }
             }
             if(suppress) {
                 evt.setConsumed(true);
             }
             if(DEBUG) {
                 System.err.println("NewtCanvasAWT.focusKey: XXX: "+ks);
             }
         }
    }
    private final FocusTraversalKeyListener newtFocusTraversalKeyListener = new FocusTraversalKeyListener();

    class FocusPropertyChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final Object oldF = evt.getOldValue();
            final Object newF = evt.getNewValue();
            final boolean isParent = isParent();
            final boolean isFullscreen = isFullscreen();
            if(DEBUG) {
                System.err.println("NewtCanvasAWT.FocusProperty: "+evt.getPropertyName()+", src "+evt.getSource()+", "+oldF+" -> "+newF+", isParent "+isParent+", isFS "+isFullscreen);
            }
            if(isParent && !isFullscreen) {
                if(newF == NewtCanvasAWT.this) {
                    if(DEBUG) {
                        System.err.println("NewtCanvasAWT.FocusProperty: AWT focus -> NEWT focus traversal");
                    }
                    requestFocusNEWTChild();
                } else if(oldF == NewtCanvasAWT.this && newF == null) {
                    // focus traversal to NEWT - NOP
                    if(DEBUG) {
                        System.err.println("NewtCanvasAWT.FocusProperty: NEWT focus");
                    }
                } else if(null != newF && newF != NewtCanvasAWT.this) {
                    // focus traversal to another AWT component
                    if(DEBUG) {
                        System.err.println("NewtCanvasAWT.FocusProperty: lost focus - clear focus");
                    }
                    if(newtChild.getDelegatedWindow() instanceof DriverClearFocus) {
                        ((DriverClearFocus)newtChild.getDelegatedWindow()).clearFocus();
                    }
                }
            }
        }
    }
    private final FocusPropertyChangeListener focusPropertyChangeListener = new FocusPropertyChangeListener();
    private volatile KeyboardFocusManager keyboardFocusManager = null;

    private final void requestFocusNEWTChild() {
        if(null!=newtChild) {
            newtChild.setFocusAction(null);
            if(isOnscreen) {
                AWTEDTExecutor.singleton.invoke(false, awtClearGlobalFocusOwner);
            }
            newtChild.requestFocus();
            newtChild.setFocusAction(focusAction);
        }
    }

    /**
     * Sets a new NEWT child, provoking reparenting.
     * <p>
     * A previously detached <code>newChild</code> will be released to top-level status
     * and made invisible.
     * </p>
     * <p>
     * Note: When switching NEWT child's, detaching the previous first via <code>setNEWTChild(null)</code>
     * produced much cleaner visual results.
     * </p>
     * @return the previous attached newt child.
     */
    public Window setNEWTChild(Window newChild) {
        synchronized(sync) {
            final Window prevChild = newtChild;
            if(DEBUG) {
                System.err.println("NewtCanvasAWT.setNEWTChild.0: win "+newtWinHandleToHexString(prevChild)+" -> "+newtWinHandleToHexString(newChild));
            }
            final java.awt.Container cont = AWTMisc.getContainer(this);
            // remove old one
            if(null != newtChild) {
                detachNewtChild( cont );
                newtChild = null;
            }
            // add new one, reparent only if ready
            newtChild = newChild;

            updateLayoutSize();
            // will be done later at paint/display/..: attachNewtChild(cont);

            return prevChild;
        }
    }

    private final void updateLayoutSize() {
        final Window w = newtChild;
        if( null != w ) {
            // use NEWT child's size for min/pref size!
            java.awt.Dimension minSize = new java.awt.Dimension(w.getWidth(), w.getHeight());
            setMinimumSize(minSize);
            setPreferredSize(minSize);
        }
    }

    /** @return the current NEWT child */
    public Window getNEWTChild() {
        return newtChild;
    }

    /** @return this AWT Canvas NativeWindow representation, may be null in case {@link #removeNotify()} has been called,
     * or {@link #addNotify()} hasn't been called yet.*/
    public NativeWindow getNativeWindow() { return jawtWindow; }

    @Override
    public WindowClosingMode getDefaultCloseOperation() {
        return awtWindowClosingProtocol.getDefaultCloseOperation();
    }

    @Override
    public WindowClosingMode setDefaultCloseOperation(WindowClosingMode op) {
        return awtWindowClosingProtocol.setDefaultCloseOperation(op);
    }

    @Override
    public void addNotify() {
        if( Beans.isDesignTime() ) {
            super.addNotify();
        } else {
            // before native peer is valid: X11
            disableBackgroundErase();

            // creates the native peer
            super.addNotify();

            // after native peer is valid: Windows
            disableBackgroundErase();

            synchronized(sync) {
                jawtWindow = NewtFactoryAWT.getNativeWindow(this, null != newtChild ? newtChild.getRequestedCapabilities() : null);
                jawtWindow.setShallUseOffscreenLayer(shallUseOffscreenLayer);

                if(DEBUG) {
                    // if ( isShowing() == false ) -> Container was not visible yet.
                    // if ( isShowing() == true  ) -> Container is already visible.
                    System.err.println("NewtCanvasAWT.addNotify: win "+newtWinHandleToHexString(newtChild)+
                                       ", comp "+this+", visible "+isVisible()+", showing "+isShowing()+
                                       ", displayable "+isDisplayable()+", cont "+AWTMisc.getContainer(this));
                }
            }
        }
        awtWindowClosingProtocol.addClosingListener();
    }

    @Override
    public void removeNotify() {
        awtWindowClosingProtocol.removeClosingListener();

        if( Beans.isDesignTime() ) {
            super.removeNotify();
        } else {
            destroyImpl(true /* removeNotify */, false /* windowClosing */);
            super.removeNotify();
        }
    }

    /**
     * Destroys this resource:
     * <ul>
     *   <li> Make the NEWT Child invisible </li>
     *   <li> Disconnects the NEWT Child from this Canvas NativeWindow, reparent to NULL </li>
     *   <li> Issues <code>destroy()</code> on the NEWT Child</li>
     *   <li> Remove reference to the NEWT Child</li>
     * </ul>
     * @see Window#destroy()
     */
    public final void destroy() {
        AWTEDTExecutor.singleton.invoke(true, new Runnable() {
            @Override
            public void run() {
                destroyImpl(false /* removeNotify */, false /* windowClosing */);
            } } );
    }

    private final void destroyImpl(boolean removeNotify, boolean windowClosing) {
        synchronized(sync) {
            if( null !=newtChild ) {
                java.awt.Container cont = AWTMisc.getContainer(this);
                if(DEBUG) {
                    System.err.println("NewtCanvasAWT.destroy(removeNotify "+removeNotify+", windowClosing "+windowClosing+"): nw "+newtWinHandleToHexString(newtChild)+", from "+cont);
                }
                detachNewtChild(cont);

                if( !removeNotify ) {
                    final Window cWin = newtChild;
                    final Window dWin = cWin.getDelegatedWindow();
                    newtChild=null;
                    if( windowClosing && dWin instanceof WindowImpl ) {
                        ((WindowImpl)dWin).windowDestroyNotify(true);
                    } else {
                        cWin.destroy();
                    }
                }
            }
            if( ( removeNotify || windowClosing ) && null!=jawtWindow ) {
                NewtFactoryAWT.destroyNativeWindow(jawtWindow);
                jawtWindow=null;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        synchronized(sync) {
            if( validateComponent(true) && !printActive ) {
                newtChild.windowRepaint(0, 0, getWidth(), getHeight());
            }
        }
    }
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void reshape(int x, int y, int width, int height) {
        synchronized (getTreeLock()) { // super.reshape(..) claims tree lock, so we do extend it's lock over reshape
            synchronized(sync) {
                super.reshape(x, y, width, height);
                if(DEBUG) {
                    System.err.println("NewtCanvasAWT.reshape: "+x+"/"+y+" "+width+"x"+height);
                }
                if( validateComponent(true) ) {
                    // newtChild.setSize(width, height);
                }
            }
        }
    }

    private volatile boolean printActive = false;
    private GLAnimatorControl printAnimator = null;
    private GLAutoDrawable printGLAD = null;
    private AWTTilePainter printAWTTiles = null;

    private final GLAutoDrawable getGLAD() {
        if( null != newtChild && newtChild instanceof GLAutoDrawable ) {
            return (GLAutoDrawable)newtChild;
        }
        return null;
    }

    @Override
    public void setupPrint(double scaleMatX, double scaleMatY, int numSamples, int tileWidth, int tileHeight) {
        printActive = true;
        final int componentCount = isOpaque() ? 3 : 4;
        final TileRenderer printRenderer = new TileRenderer();
        printAWTTiles = new AWTTilePainter(printRenderer, componentCount, scaleMatX, scaleMatY, numSamples, tileWidth, tileHeight, DEBUG);
        AWTEDTExecutor.singleton.invoke(getTreeLock(), true /* allowOnNonEDT */, true /* wait */, setupPrintOnEDT);
    }
    private final Runnable setupPrintOnEDT = new Runnable() {
        @Override
        public void run() {
            synchronized(sync) {
                if( !validateComponent(true) ) {
                    if(DEBUG) {
                        System.err.println(getThreadName()+": Info: NewtCanvasAWT setupPrint - skipped GL render, drawable not valid yet");
                    }
                    printActive = false;
                    return; // not yet available ..
                }
                if( !isVisible() ) {
                    if(DEBUG) {
                        System.err.println(getThreadName()+": Info: NewtCanvasAWT setupPrint - skipped GL render, drawable visible");
                    }
                    printActive = false;
                    return; // not yet available ..
                }
                final GLAutoDrawable glad = getGLAD();
                if( null == glad ) {
                    if( DEBUG ) {
                        System.err.println("AWT print.setup exit, newtChild not a GLAutoDrawable: "+newtChild);
                    }
                    printActive = false;
                    return;
                }
                printAnimator =  glad.getAnimator();
                if( null != printAnimator ) {
                    printAnimator.remove(glad);
                }
                printGLAD = glad; // _not_ default, shall be replaced by offscreen GLAD
                final GLCapabilities caps = (GLCapabilities)glad.getChosenGLCapabilities().cloneMutable();
                final int printNumSamples = printAWTTiles.getNumSamples(caps);
                GLDrawable printDrawable = printGLAD.getDelegatedDrawable();
                final boolean reqNewGLADSamples = printNumSamples != caps.getNumSamples();
                final boolean reqNewGLADSize = printAWTTiles.customTileWidth != -1 && printAWTTiles.customTileWidth != printDrawable.getWidth() ||
                                               printAWTTiles.customTileHeight != -1 && printAWTTiles.customTileHeight != printDrawable.getHeight();
                final boolean reqNewGLADOnscrn = caps.isOnscreen();

                // It is desired to use a new offscreen GLAD, however Bug 830 forbids this for AA onscreen context.
                // Bug 830: swapGLContextAndAllGLEventListener and onscreen MSAA w/ NV/GLX
                final boolean reqNewGLAD = !caps.getSampleBuffers() && ( reqNewGLADOnscrn || reqNewGLADSamples || reqNewGLADSize );
                if( DEBUG ) {
                    System.err.println("AWT print.setup: reqNewGLAD "+reqNewGLAD+"[ onscreen "+reqNewGLADOnscrn+", samples "+reqNewGLADSamples+", size "+reqNewGLADSize+"], "+
                            ", drawableSize "+printDrawable.getWidth()+"x"+printDrawable.getHeight()+
                            ", customTileSize "+printAWTTiles.customTileWidth+"x"+printAWTTiles.customTileHeight+
                            ", scaleMat "+printAWTTiles.scaleMatX+" x "+printAWTTiles.scaleMatY+
                            ", numSamples "+printAWTTiles.customNumSamples+" -> "+printNumSamples+", printAnimator "+printAnimator);
                }
                if( reqNewGLAD ) {
                    caps.setDoubleBuffered(false);
                    caps.setOnscreen(false);
                    if( printNumSamples != caps.getNumSamples() ) {
                        caps.setSampleBuffers(0 < printNumSamples);
                        caps.setNumSamples(printNumSamples);
                    }
                    final GLDrawableFactory factory = GLDrawableFactory.getFactory(caps.getGLProfile());
                    printGLAD = factory.createOffscreenAutoDrawable(null, caps, null,
                            printAWTTiles.customTileWidth != -1 ? printAWTTiles.customTileWidth : DEFAULT_PRINT_TILE_SIZE,
                            printAWTTiles.customTileHeight != -1 ? printAWTTiles.customTileHeight : DEFAULT_PRINT_TILE_SIZE);
                    GLDrawableUtil.swapGLContextAndAllGLEventListener(glad, printGLAD);
                    printDrawable = printGLAD.getDelegatedDrawable();
                }
                printAWTTiles.setGLOrientation(printGLAD.isGLOriented(), printGLAD.isGLOriented());
                printAWTTiles.renderer.setTileSize(printDrawable.getWidth(), printDrawable.getHeight(), 0);
                printAWTTiles.renderer.attachAutoDrawable(printGLAD);
                if( DEBUG ) {
                    System.err.println("AWT print.setup "+printAWTTiles);
                    System.err.println("AWT print.setup AA "+printNumSamples+", "+caps);
                    System.err.println("AWT print.setup printGLAD: "+printGLAD.getWidth()+"x"+printGLAD.getHeight()+", "+printGLAD);
                    System.err.println("AWT print.setup printDraw: "+printDrawable.getWidth()+"x"+printDrawable.getHeight()+", "+printDrawable);
                }
            }
        }
    };

    @Override
    public void releasePrint() {
        if( !printActive || null == printGLAD ) {
            throw new IllegalStateException("setupPrint() not called");
        }
        // sendReshape = false; // clear reshape flag
        AWTEDTExecutor.singleton.invoke(getTreeLock(), true /* allowOnNonEDT */, true /* wait */, releasePrintOnEDT);
        newtChild.sendWindowEvent(WindowEvent.EVENT_WINDOW_RESIZED); // trigger a resize/relayout to listener
    }
    private final Runnable releasePrintOnEDT = new Runnable() {
        @Override
        public void run() {
            synchronized(sync) {
                if( DEBUG ) {
                    System.err.println("AWT print.release "+printAWTTiles);
                }
                final GLAutoDrawable glad = getGLAD();
                printAWTTiles.dispose();
                printAWTTiles= null;
                if( printGLAD != glad ) {
                    GLDrawableUtil.swapGLContextAndAllGLEventListener(printGLAD, glad);
                    printGLAD.destroy();
                }
                printGLAD = null;
                if( null != printAnimator ) {
                    printAnimator.add(glad);
                    printAnimator = null;
                }
                printActive = false;
            }
        }
    };

    @Override
    public void print(Graphics graphics) {
        synchronized(sync) {
            if( !printActive || null == printGLAD ) {
                throw new IllegalStateException("setupPrint() not called");
            }
            if(DEBUG && !EventQueue.isDispatchThread()) {
                System.err.println(getThreadName()+": Warning: GLCanvas print - not called from AWT-EDT");
                // we cannot dispatch print on AWT-EDT due to printing internal locking ..
            }

            final Graphics2D g2d = (Graphics2D)graphics;
            try {
                printAWTTiles.setupGraphics2DAndClipBounds(g2d, getWidth(), getHeight());
                final TileRenderer tileRenderer = printAWTTiles.renderer;
                if( DEBUG ) {
                    System.err.println("AWT print.0: "+tileRenderer);
                }
                if( !tileRenderer.eot() ) {
                    try {
                        do {
                            tileRenderer.display();
                        } while ( !tileRenderer.eot() );
                        if( DEBUG ) {
                            System.err.println("AWT print.1: "+printAWTTiles);
                        }
                        tileRenderer.reset();
                    } finally {
                        printAWTTiles.resetGraphics2D();
                    }
                }
            } catch (NoninvertibleTransformException nte) {
                System.err.println("Catched: Inversion failed of: "+g2d.getTransform());
                nte.printStackTrace();
            }
            if( DEBUG ) {
                System.err.println("AWT print.X: "+printAWTTiles);
            }
        }
    }

    private final boolean validateComponent(boolean attachNewtChild) {
        if( Beans.isDesignTime() || !isDisplayable() ) {
            return false;
        }
        if ( null == newtChild || null == jawtWindow ) {
            return false;
        }
        if( 0 >= getWidth() || 0 >= getHeight() ) {
            return false;
        }

        if( attachNewtChild && !newtChildAttached && null != newtChild ) {
            attachNewtChild();
        }

        return true;
    }

    private final void configureNewtChild(boolean attach) {
        awtAdapter.clear();
        awtMouseAdapter.clear();
        awtKeyAdapter.setConsumeAWTEvent(false);
        awtMouseAdapter.clear();
        awtKeyAdapter.setConsumeAWTEvent(false);

        if(null != keyboardFocusManager) {
            keyboardFocusManager.removePropertyChangeListener("focusOwner", focusPropertyChangeListener);
            keyboardFocusManager = null;
        }

        if( null != newtChild ) {
            newtChild.setKeyboardFocusHandler(null);
            if(attach) {
                if(null == jawtWindow.getGraphicsConfiguration()) {
                    throw new InternalError("XXX");
                }
                isOnscreen = jawtWindow.getGraphicsConfiguration().getChosenCapabilities().isOnscreen();
                awtAdapter.setDownstream(jawtWindow, newtChild);
                newtChild.addWindowListener(clearAWTMenusOnNewtFocus);
                newtChild.setFocusAction(focusAction); // enable AWT focus traversal
                newtChildCloseOp = newtChild.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
                keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                keyboardFocusManager.addPropertyChangeListener("focusOwner", focusPropertyChangeListener);
                // force this AWT Canvas to be focus-able,
                // since this it is completely covered by the newtChild (z-order).
                setFocusable(true);
                if(isOnscreen) {
                    // onscreen newt child needs to fwd AWT focus
                    newtChild.setKeyboardFocusHandler(newtFocusTraversalKeyListener);
                } else {
                    // offscreen newt child requires AWT to fwd AWT key/mouse event
                    awtMouseAdapter.setDownstream(newtChild);
                    // We cannot consume AWT mouse click, since it would disable focus via mouse click!
                    // awtMouseAdapter.setConsumeAWTEvent(true);
                    awtKeyAdapter.setDownstream(newtChild);
                    // We manually transfer the focus via NEWT KeyListener, hence we can mark AWT keys as consumed!
                    awtKeyAdapter.setConsumeAWTEvent(true);
                }
            } else {
                newtChild.removeWindowListener(clearAWTMenusOnNewtFocus);
                newtChild.setFocusAction(null);
                newtChild.setDefaultCloseOperation(newtChildCloseOp);
                setFocusable(false);
            }
        }
    }

    /**
     * Returns <code>true</code> if Key and Mouse input events will be passed through AWT,
     * otherwise only the {@link #getNEWTChild() NEWT child} will receive them.
     * <p>
     * Normally only the {@link #getNEWTChild() NEWT child} will receive Key and Mouse input events.
     * In offscreen mode, e.g. OSX/CALayer, the AWT events will be received and translated into NEWT events
     * and delivered to the NEWT child window.<br/>
     * Note: AWT key events will {@link java.awt.event.InputEvent#consume() consumed} in pass-through mode.
     * </p>
     */
    public final boolean isAWTEventPassThrough() {
        return !isOnscreen;
    }

    private final void attachNewtChild() {
      if( null == newtChild || null == jawtWindow || newtChildAttached ) {
          return; // nop
      }
      if(DEBUG) {
          // if ( isShowing() == false ) -> Container was not visible yet.
          // if ( isShowing() == true  ) -> Container is already visible.
          System.err.println("NewtCanvasAWT.attachNewtChild.0 @ "+Thread.currentThread().getName()+": win "+newtWinHandleToHexString(newtChild)+
                             ", EDTUtil: cur "+newtChild.getScreen().getDisplay().getEDTUtil()+
                             ", comp "+this+", visible "+isVisible()+", showing "+isShowing()+", displayable "+isDisplayable()+
                             ", cont "+AWTMisc.getContainer(this));
      }

      newtChildAttached = true;
      newtChild.setFocusAction(null); // no AWT focus traversal ..
      if(DEBUG) {
        System.err.println("NewtCanvasAWT.attachNewtChild.1: newtChild: "+newtChild);
      }
      final int w = getWidth();
      final int h = getHeight();
      if(DEBUG) {
          System.err.println("NewtCanvasAWT.attachNewtChild.2: size "+w+"x"+h);
      }
      newtChild.setVisible(false);
      newtChild.setSize(w, h);
      newtChild.reparentWindow(jawtWindow);
      newtChild.addSurfaceUpdatedListener(jawtWindow);
      if( jawtWindow.isOffscreenLayerSurfaceEnabled() &&
          0 != ( JAWTUtil.JAWT_OSX_CALAYER_QUIRK_POSITION & JAWTUtil.getOSXCALayerQuirks() ) ) {
          AWTEDTExecutor.singleton.invoke(false, forceRelayout);
      }
      newtChild.setVisible(true);
      configureNewtChild(true);
      newtChild.sendWindowEvent(WindowEvent.EVENT_WINDOW_RESIZED); // trigger a resize/relayout to listener

      if(DEBUG) {
          System.err.println("NewtCanvasAWT.attachNewtChild.X: win "+newtWinHandleToHexString(newtChild)+", EDTUtil: cur "+newtChild.getScreen().getDisplay().getEDTUtil()+", comp "+this);
      }
    }
    private final Runnable forceRelayout = new Runnable() {
        @Override
        public void run() {
            if(DEBUG) {
                System.err.println("NewtCanvasAWT.forceRelayout.0");
            }
            // Hack to force proper native AWT layout incl. CALayer components on OSX
            final java.awt.Component component = NewtCanvasAWT.this;
            final int cW = component.getWidth();
            final int cH = component.getHeight();
            component.setSize(cW+1, cH+1);
            component.setSize(cW, cH);
            if(DEBUG) {
                System.err.println("NewtCanvasAWT.forceRelayout.X");
            }
        }  };

    private final void detachNewtChild(java.awt.Container cont) {
      if( null == newtChild || null == jawtWindow || !newtChildAttached ) {
          return; // nop
      }
      if(DEBUG) {
          // if ( isShowing() == false ) -> Container was not visible yet.
          // if ( isShowing() == true  ) -> Container is already visible.
          System.err.println("NewtCanvasAWT.detachNewtChild.0: win "+newtWinHandleToHexString(newtChild)+
                             ", EDTUtil: cur "+newtChild.getScreen().getDisplay().getEDTUtil()+
                             ", comp "+this+", visible "+isVisible()+", showing "+isShowing()+", displayable "+isDisplayable()+
                             ", cont "+cont);
      }

      newtChild.removeSurfaceUpdatedListener(jawtWindow);
      newtChildAttached = false;
      newtChild.setFocusAction(null); // no AWT focus traversal ..
      configureNewtChild(false);
      newtChild.setVisible(false);

      newtChild.reparentWindow(null); // will destroy context (offscreen -> onscreen) and implicit detachSurfaceLayer

      if(DEBUG) {
          System.err.println("NewtCanvasAWT.detachNewtChild.X: win "+newtWinHandleToHexString(newtChild)+", EDTUtil: cur "+newtChild.getScreen().getDisplay().getEDTUtil()+", comp "+this);
      }
    }

  // Disables the AWT's erasing of this Canvas's background on Windows
  // in Java SE 6. This internal API is not available in previous
  // releases, but the system property
  // -Dsun.awt.noerasebackground=true can be specified to get similar
  // results globally in previous releases.
  private static boolean disableBackgroundEraseInitialized;
  private static Method  disableBackgroundEraseMethod;
  private void disableBackgroundErase() {
    if (!disableBackgroundEraseInitialized) {
      try {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
              try {
                Class<?> clazz = getToolkit().getClass();
                while (clazz != null && disableBackgroundEraseMethod == null) {
                  try {
                    disableBackgroundEraseMethod =
                      clazz.getDeclaredMethod("disableBackgroundErase",
                                              new Class[] { Canvas.class });
                    disableBackgroundEraseMethod.setAccessible(true);
                  } catch (Exception e) {
                    clazz = clazz.getSuperclass();
                  }
                }
              } catch (Exception e) {
              }
              return null;
            }
          });
      } catch (Exception e) {
      }
      disableBackgroundEraseInitialized = true;
      if(DEBUG) {
        System.err.println("NewtCanvasAWT: TK disableBackgroundErase method found: "+
                (null!=disableBackgroundEraseMethod));
      }
    }
    if (disableBackgroundEraseMethod != null) {
      Throwable t=null;
      try {
        disableBackgroundEraseMethod.invoke(getToolkit(), new Object[] { this });
      } catch (Exception e) {
        t = e;
      }
      if(DEBUG) {
        System.err.println("NewtCanvasAWT: TK disableBackgroundErase error: "+t);
      }
    }
  }

  protected static String getThreadName() { return Thread.currentThread().getName(); }

  static String newtWinHandleToHexString(Window w) {
      return null != w ? toHexString(w.getWindowHandle()) : "nil";
  }
  static String toHexString(long l) {
      return "0x"+Long.toHexString(l);
  }
}

