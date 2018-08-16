/*
* Copyright (C) 2015 Vincent Mi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ventum.zephyr.soundboardtemplate.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.makeramen.roundedimageview.Corner
import com.makeramen.roundedimageview.R
import com.makeramen.roundedimageview.RoundedDrawable

class NonMutableRoundedImageView : ImageView {

    private val mCornerRadii = floatArrayOf(DEFAULT_RADIUS, DEFAULT_RADIUS, DEFAULT_RADIUS, DEFAULT_RADIUS)

    private var mBackgroundDrawable: Drawable? = null
    var borderColors: ColorStateList? = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR)
        private set
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mColorFilter: ColorFilter? = null
    private var mColorMod = false
    private var mDrawable: Drawable? = null
    private var mHasColorFilter = false
    private var mIsOval = false
    private var mMutateBackground = false
    private var mResource: Int = 0
    private var mBackgroundResource: Int = 0
    private var mScaleType: ImageView.ScaleType? = null
    var tileModeX: Shader.TileMode? = DEFAULT_TILE_MODE
        set(tileModeX) {
            if (this.tileModeX == tileModeX) {
                return
            }

            field = tileModeX
            updateDrawableAttrs()
            updateBackgroundDrawableAttrs(false)
            invalidate()
        }
    var tileModeY: Shader.TileMode? = DEFAULT_TILE_MODE
        set(tileModeY) {
            if (this.tileModeY == tileModeY) {
                return
            }

            field = tileModeY
            updateDrawableAttrs()
            updateBackgroundDrawableAttrs(false)
            invalidate()
        }

    /**
     * @return the largest corner radius.
     */
    /**
     * Set the corner radii of all corners in px.
     *
     * @param radius the radius to set.
     */
    var cornerRadius: Float
        get() = maxCornerRadius
        set(radius) = setCornerRadius(radius, radius, radius, radius)

    /**
     * @return the largest corner radius.
     */
    val maxCornerRadius: Float
        get() {
            var maxRadius = 0f
            for (r in mCornerRadii) {
                maxRadius = Math.max(r, maxRadius)
            }
            return maxRadius
        }

    var borderColor: Int
        @ColorInt
        get() = borderColors!!.defaultColor
        set(@ColorInt color) = setBorderColor(ColorStateList.valueOf(color))

    var isOval: Boolean
        get() = mIsOval
        set(oval) {
            mIsOval = oval
            updateDrawableAttrs()
            updateBackgroundDrawableAttrs(false)
            invalidate()
        }

    constructor(context: Context) : super(context) {}

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyle, 0)

        val index = a.getInt(R.styleable.RoundedImageView_android_scaleType, -1)
        if (index >= 0) {
            scaleType = SCALE_TYPES[index]
        } else {
            // default scaletype to FIT_CENTER
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        var cornerRadiusOverride = a.getDimensionPixelSize(R.styleable.RoundedImageView_riv_corner_radius, -1).toFloat()

        mCornerRadii[Corner.TOP_LEFT] = a.getDimensionPixelSize(R.styleable.RoundedImageView_riv_corner_radius_top_left, -1).toFloat()
        mCornerRadii[Corner.TOP_RIGHT] = a.getDimensionPixelSize(R.styleable.RoundedImageView_riv_corner_radius_top_right, -1).toFloat()
        mCornerRadii[Corner.BOTTOM_RIGHT] = a.getDimensionPixelSize(R.styleable.RoundedImageView_riv_corner_radius_bottom_right, -1).toFloat()
        mCornerRadii[Corner.BOTTOM_LEFT] = a.getDimensionPixelSize(R.styleable.RoundedImageView_riv_corner_radius_bottom_left, -1).toFloat()

        var any = false
        run {
            var i = 0
            val len = mCornerRadii.size
            while (i < len) {
                if (mCornerRadii[i] < 0) {
                    mCornerRadii[i] = 0f
                } else {
                    any = true
                }
                i++
            }
        }

        if (!any) {
            if (cornerRadiusOverride < 0) {
                cornerRadiusOverride = DEFAULT_RADIUS
            }
            var i = 0
            val len = mCornerRadii.size
            while (i < len) {
                mCornerRadii[i] = cornerRadiusOverride
                i++
            }
        }

        mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_riv_border_width, -1).toFloat()
        if (mBorderWidth < 0) {
            mBorderWidth = DEFAULT_BORDER_WIDTH
        }

        borderColors = a.getColorStateList(R.styleable.RoundedImageView_riv_border_color)
        if (borderColors == null) {
            borderColors = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR)
        }

        mMutateBackground = a.getBoolean(R.styleable.RoundedImageView_riv_mutate_background, false)
        mIsOval = a.getBoolean(R.styleable.RoundedImageView_riv_oval, false)

        val tileMode = a.getInt(R.styleable.RoundedImageView_riv_tile_mode, TILE_MODE_UNDEFINED)
        if (tileMode != TILE_MODE_UNDEFINED) {
            tileModeX = parseTileMode(tileMode)
            tileModeY = parseTileMode(tileMode)
        }

        updateDrawableAttrs()
        updateBackgroundDrawableAttrs(true)

        if (mMutateBackground) {
            // when setBackground() is called by View constructor, mMutateBackground is not loaded from the attribute,
            // so it's false by default, what doesn't allow to create the RoundedDrawable. At this point, after load
            // mMutateBackground and updated BackgroundDrawable to RoundedDrawable, the View's background drawable needs to
            // be changed to this new drawable.

            super.setBackgroundDrawable(mBackgroundDrawable)
        }

        a.recycle()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

    override fun getScaleType(): ImageView.ScaleType? {
        return mScaleType
    }

    override fun setScaleType(scaleType: ImageView.ScaleType?) {
        assert(scaleType != null)

        if (mScaleType != scaleType) {
            mScaleType = scaleType

            when (scaleType) {
                ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_END, ImageView.ScaleType.FIT_XY -> super.setScaleType(ImageView.ScaleType.FIT_XY)
                else -> super.setScaleType(scaleType)
            }

            updateDrawableAttrs()
            updateBackgroundDrawableAttrs(false)
            invalidate()
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        mResource = 0
        mDrawable = RoundedDrawable.fromDrawable(drawable)
        updateDrawableAttrs()
        super.setImageDrawable(mDrawable)
    }

    override fun setImageBitmap(bm: Bitmap) {
        mResource = 0
        mDrawable = RoundedDrawable.fromBitmap(bm)
        updateDrawableAttrs()
        super.setImageDrawable(mDrawable)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        if (mResource != resId) {
            mResource = resId
            mDrawable = resolveResource()
            updateDrawableAttrs()
            super.setImageDrawable(mDrawable)
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setImageDrawable(drawable)
    }

    private fun resolveResource(): Drawable? {
        val rsrc = resources ?: return null

        var d: Drawable? = null

        if (mResource != 0) {
            try {
                d = rsrc.getDrawable(mResource)
            } catch (e: Exception) {
                Log.w(TAG, "Unable to find resource: $mResource", e)
                // Don't try again.
                mResource = 0
            }

        }
        return RoundedDrawable.fromDrawable(d)
    }

    override fun setBackground(background: Drawable) {
        setBackgroundDrawable(background)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        if (mBackgroundResource != resId) {
            mBackgroundResource = resId
            mBackgroundDrawable = resolveBackgroundResource()
            setBackgroundDrawable(mBackgroundDrawable)
        }
    }

    override fun setBackgroundColor(color: Int) {
        mBackgroundDrawable = ColorDrawable(color)
        setBackgroundDrawable(mBackgroundDrawable)
    }

    private fun resolveBackgroundResource(): Drawable? {
        val rsrc = resources ?: return null

        var d: Drawable? = null

        if (mBackgroundResource != 0) {
            try {
                d = rsrc.getDrawable(mBackgroundResource)
            } catch (e: Exception) {
                Log.w(TAG, "Unable to find resource: $mBackgroundResource", e)
                // Don't try again.
                mBackgroundResource = 0
            }

        }
        return RoundedDrawable.fromDrawable(d)
    }

    private fun updateDrawableAttrs() {
        updateAttrs(mDrawable, mScaleType)
    }

    private fun updateBackgroundDrawableAttrs(convert: Boolean) {
        if (mMutateBackground) {
            if (convert) {
                mBackgroundDrawable = RoundedDrawable.fromDrawable(mBackgroundDrawable)
            }
            updateAttrs(mBackgroundDrawable, ImageView.ScaleType.FIT_XY)
        }
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (mColorFilter !== cf) {
            mColorFilter = cf
            mHasColorFilter = true
            mColorMod = true
            applyColorMod()
            invalidate()
        }
    }

    private fun applyColorMod() {
        // Only mutate and apply when modifications have occurred. This should
        // not reset the mColorMod flag, since these filters need to be
        // re-applied if the Drawable is changed.
        if (mDrawable != null && mColorMod) {
            //mDrawable = mDrawable!!.mutate()
            if (mHasColorFilter) {
                mDrawable!!.colorFilter = mColorFilter
            }
            // TODO: support, eventually...
            //mDrawable.setXfermode(mXfermode);
            //mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);
        }
    }

    private fun updateAttrs(drawable: Drawable?, scaleType: ImageView.ScaleType?) {
        if (drawable == null) {
            return
        }

        if (drawable is RoundedDrawable) {
            drawable
                    .setScaleType(scaleType)
                    .setBorderWidth(mBorderWidth)
                    .setBorderColor(borderColors)
                    .setOval(mIsOval)
                    .setTileModeX(tileModeX).tileModeY = tileModeY

            if (mCornerRadii != null) {
                drawable.setCornerRadius(
                        mCornerRadii[Corner.TOP_LEFT],
                        mCornerRadii[Corner.TOP_RIGHT],
                        mCornerRadii[Corner.BOTTOM_RIGHT],
                        mCornerRadii[Corner.BOTTOM_LEFT])
            }

            applyColorMod()
        } else if (drawable is LayerDrawable) {
            // loop through layers to and set drawable attrs
            val ld = drawable as LayerDrawable?
            var i = 0
            val layers = ld!!.getNumberOfLayers()
            while (i < layers) {
                updateAttrs(ld!!.getDrawable(i), scaleType)
                i++
            }
        }
    }

    @Deprecated("")
    override fun setBackgroundDrawable(background: Drawable?) {
        mBackgroundDrawable = background
        updateBackgroundDrawableAttrs(true)

        super.setBackgroundDrawable(mBackgroundDrawable)
    }

    /**
     * Set the corner radius of a specific corner in px.
     *
     * @param corner the corner to set.
     * @param radius the corner radius to set in px.
     */
    fun setCornerRadius(@Corner corner: Int, radius: Float) {
        if (mCornerRadii[corner] == radius) {
            return
        }
        mCornerRadii[corner] = radius

        updateDrawableAttrs()
        updateBackgroundDrawableAttrs(false)
        invalidate()
    }

    /**
     * Set the corner radii of each corner individually. Currently only one unique nonzero value is
     * supported.
     *
     * @param topLeft radius of the top left corner in px.
     * @param topRight radius of the top right corner in px.
     * @param bottomRight radius of the bottom right corner in px.
     * @param bottomLeft radius of the bottom left corner in px.
     */
    fun setCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        if (mCornerRadii[Corner.TOP_LEFT] == topLeft
                && mCornerRadii[Corner.TOP_RIGHT] == topRight
                && mCornerRadii[Corner.BOTTOM_RIGHT] == bottomRight
                && mCornerRadii[Corner.BOTTOM_LEFT] == bottomLeft) {
            return
        }

        mCornerRadii[Corner.TOP_LEFT] = topLeft
        mCornerRadii[Corner.TOP_RIGHT] = topRight
        mCornerRadii[Corner.BOTTOM_LEFT] = bottomLeft
        mCornerRadii[Corner.BOTTOM_RIGHT] = bottomRight

        updateDrawableAttrs()
        updateBackgroundDrawableAttrs(false)
        invalidate()
    }

    fun setBorderWidth(width: Float) {
        if (mBorderWidth == width) {
            return
        }

        mBorderWidth = width
        updateDrawableAttrs()
        updateBackgroundDrawableAttrs(false)
        invalidate()
    }

    fun setBorderColor(colors: ColorStateList?) {
        if (borderColors == colors) {
            return
        }

        borderColors = colors ?: ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR)
        updateDrawableAttrs()
        updateBackgroundDrawableAttrs(false)
        if (mBorderWidth > 0) {
            invalidate()
        }
    }

    companion object {

        // Constants for tile mode attributes
        private val TILE_MODE_UNDEFINED = -2
        private val TILE_MODE_CLAMP = 0
        private val TILE_MODE_REPEAT = 1
        private val TILE_MODE_MIRROR = 2

        val TAG = "RoundedImageView"
        val DEFAULT_RADIUS = 0f
        val DEFAULT_BORDER_WIDTH = 0f
        val DEFAULT_TILE_MODE: Shader.TileMode = Shader.TileMode.CLAMP
        private val SCALE_TYPES = arrayOf(ImageView.ScaleType.MATRIX, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.FIT_END, ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE)

        private fun parseTileMode(tileMode: Int): Shader.TileMode? {
            when (tileMode) {
                TILE_MODE_CLAMP -> return Shader.TileMode.CLAMP
                TILE_MODE_REPEAT -> return Shader.TileMode.REPEAT
                TILE_MODE_MIRROR -> return Shader.TileMode.MIRROR
                else -> return null
            }
        }
    }
}
