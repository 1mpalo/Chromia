package com.impalo.media

// TODO: при дальнейшем улучшении можно строить
// TODO: совместную тональную кривую или вычислять маски из общего опорного сигнала
// в шейдере
// TODO: изучить и добавить определение белого относительно анализы сцены
// в шейдере

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.GlUtil
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram
import com.impalo.media.color.TemperatureAdaptation

@OptIn(UnstableApi::class)
class IdentityGlEffect(
    private val settingsHolder: GradingSettingsHolder
) : GlEffect {
    @OptIn(UnstableApi::class)
    override fun toGlShaderProgram(
        context: Context,
        useHdr: Boolean,
    ): GlShaderProgram {
        return IdentityShaderProgram(
            context = context,
            useHdr = useHdr,
            settings = settingsHolder
        )
    }
}

@OptIn(UnstableApi::class)
private class IdentityShaderProgram(
    context: Context,
    useHdr: Boolean,
    private val settings: GradingSettingsHolder
) : BaseGlShaderProgram(
    /* useHighPrecisionColorComponents = */ useHdr,
    /* texturePoolCapacity = */ 1
) {

    private var lastTemperature = Float.NaN
    private var temperatureLmsScale = floatArrayOf(1f, 1f, 1f)

    private val glProgram: GlProgram = try {
        GlProgram(
            context,
            "shaders/identity.vert",
            "shaders/identity.frag"
        )
    } catch (e: Exception) {
        throw VideoFrameProcessingException(e)
    }

    init {
        glProgram.setBufferAttribute(
            "aFramePosition",
            GlUtil.getNormalizedCoordinateBounds(),
            GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE
        )
    }

    override fun configure(
        inputWidth: Int,
        inputHeight: Int
    ): Size {
        return Size(inputWidth, inputHeight)
    }
    private var glVersionLogged = false
    override fun drawFrame(
        inputTexId: Int,
        presentationTimeUs: Long
    ) {
        try {

            if (!glVersionLogged) {
                Log.d("ChromiaGL", "GL: ${GLES20.glGetString(GLES20.GL_VERSION)}")
                Log.d(
                    "ChromiaGL",
                    "GLSL: ${GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)}"
                )
                glVersionLogged = true
            }

            glProgram.use()

            val grading = settings.snapshot()

            if(grading.temp != lastTemperature) {
                temperatureLmsScale =
                    TemperatureAdaptation.calculateLmsScale(
                        temperature = grading.temp
                    )

                lastTemperature = grading.temp
            }

            glProgram.setFloatsUniform(
                "uWhiteBalanceLmsScale",
                temperatureLmsScale
            )

            glProgram.setSamplerTexIdUniform(
                "uTexSampler",
                inputTexId,
                0
            )

            glProgram.setFloatUniform(
                "uExposure",
                grading.exposure
            )

            glProgram.setFloatUniform(
                "uContrast",
                grading.contrast
            )

            glProgram.setFloatUniform(
                "uHighlights",
                grading.highlights
            )

            glProgram.setFloatUniform(
                "uShadows",
                grading.shadows
            )

            glProgram.setFloatUniform(
                "uWhites",
                grading.whites
            )

            glProgram.setFloatUniform(
                "uBlacks",
                grading.blacks
            )

            glProgram.bindAttributesAndUniforms()

            GLES20.glDrawArrays(
                GLES20.GL_TRIANGLE_STRIP,
                0,
                4
            )

            GlUtil.checkGlError()
        } catch (e: GlUtil.GlException) {
            throw VideoFrameProcessingException(e, presentationTimeUs)
        }
    }

    override fun release() {
        super.release()

        try {
            glProgram.delete()
        } catch (e: GlUtil.GlException) {
            throw VideoFrameProcessingException(e)
        }
    }
}