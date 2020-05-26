package org.thenesis.pjogles;

import org.thenesis.pjogles.states.GLAccumState;
import org.thenesis.pjogles.states.GLColorBufferState;
import org.thenesis.pjogles.states.GLColorTableState;
import org.thenesis.pjogles.states.GLCurrentState;
import org.thenesis.pjogles.states.GLDepthState;
import org.thenesis.pjogles.states.GLDisplayListState;
import org.thenesis.pjogles.states.GLEnableState;
import org.thenesis.pjogles.states.GLEvaluatorState;
import org.thenesis.pjogles.states.GLFogState;
import org.thenesis.pjogles.states.GLHintState;
import org.thenesis.pjogles.states.GLLightState;
import org.thenesis.pjogles.states.GLLineState;
import org.thenesis.pjogles.states.GLPixelState;
import org.thenesis.pjogles.states.GLPointState;
import org.thenesis.pjogles.states.GLPolygonState;
import org.thenesis.pjogles.states.GLPolygonStippleState;
import org.thenesis.pjogles.states.GLScissorState;
import org.thenesis.pjogles.states.GLSharedState;
import org.thenesis.pjogles.states.GLStencilState;
import org.thenesis.pjogles.states.GLTextureState;
import org.thenesis.pjogles.states.GLTransformState;
import org.thenesis.pjogles.states.GLViewportState;
import org.thenesis.pjogles.texture.GLTextureUnitState;

/**
 * Represents the state variables (server side) in OpenGL that can be pushed and
 * popped via <strong>glPushAttrib</strong> and <strong>glPopAttrib</strong>.
 *
 * @see GL10#glPushAttrib glPushAttrib
 * @see GL10#glPopAttrib glPopAttrib
 *
 * @author tdinneen
 */
public final class GLAttribute implements GLConstants {
	/**
	 * Mask of which fields in this structure are valid.
	 */
	public int mask;

	public GLCurrentState current;
	public GLPointState point;
	public GLLineState line;
	public GLPolygonState polygon;
	public GLPolygonStippleState polygonStipple;
	public GLPixelState pixel;
	public GLLightState lighting;
	public GLFogState fog;
	public GLDepthState depth;
	public GLAccumState accum;
	public GLStencilState stencil;
	public GLViewportState viewport;
	public GLTransformState transform;
	public GLEnableState enables;
	public GLColorBufferState colorBuffer;
	public GLHintState hints;
	public GLEvaluatorState evaluator;
	public GLDisplayListState list;
	public GLTextureState texture;
	public GLScissorState scissor;
	public GLColorTableState colorTable;

	public GLSharedState shared;

	/**
	 * Initialize this GLAttribute.
	 *
	 * @param gc the GLSoftwareContext.
	 * @param state the GLAttribute state to copy the GLSharedState from.
	 */
	public final void initialize(final GLSoftwareContext gc, final GLAttribute state) {
		if (gc == null)
			throw new IllegalArgumentException(
					"GLAttribute.initialize(GLSoftwareContext, GLAttribute) - The supplied GLSoftwareContext is null.");

		// state is allowed to be null

		current = new GLCurrentState();
		point = new GLPointState();
		line = new GLLineState();
		polygon = new GLPolygonState();
		polygonStipple = new GLPolygonStippleState();
		pixel = new GLPixelState(gc);
		lighting = new GLLightState();
		fog = new GLFogState();
		depth = new GLDepthState();
		accum = new GLAccumState();
		stencil = new GLStencilState();
		viewport = new GLViewportState();
		transform = new GLTransformState();
		enables = new GLEnableState();
		colorBuffer = new GLColorBufferState();
		hints = new GLHintState();
		evaluator = new GLEvaluatorState();
		list = new GLDisplayListState();
		texture = new GLTextureState();
		scissor = new GLScissorState();
		colorTable = new GLColorTableState();

		if (state != null && state.shared != null)
			shared = state.shared;
		else
			shared = new GLSharedState();

		// we support multi texture units (OpenGL 1.1+)
		// default to OpenGL 1.0, ie. essentially the same
		// as calling glBindTexture(..., 0);

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			GLTextureUnitState textureUnit = texture.unit[i];

			textureUnit.current1D = shared.default1D;
			textureUnit.current2D = shared.default2D;
			textureUnit.current3D = shared.default3D;
		}
	}
}
