package com.ufoioio.mlkit.labelertest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.ufoioio.mlkit.labelertest.GraphicOverlay
import com.ufoioio.mlkit.labelertest.GraphicOverlay.Graphic
import com.google.mlkit.vision.label.ImageLabel
import java.util.Locale

/** Graphic instance for rendering a label within an associated graphic overlay view.  */
class LabelGraphic(
    private val overlay: GraphicOverlay,
    private val labels: List<ImageLabel>
) : Graphic(overlay) {
    private val textPaint: Paint = Paint()
    private val labelPaint: Paint

    init {
        textPaint.color = Color.WHITE
        textPaint.textSize = TEXT_SIZE
        labelPaint = Paint()
        labelPaint.color = Color.BLACK
        labelPaint.style = Paint.Style.FILL
        labelPaint.alpha = 0
    }

    @Synchronized
    override fun draw(canvas: Canvas) {
        // First try to find maxWidth and totalHeight in order to draw to the center of the screen.
        var maxWidth = 0f
        val totalHeight = labels.size * 2 * TEXT_SIZE
        for (label in labels) {
            val line1Width = textPaint.measureText(label.text)
            val line2Width =
                textPaint.measureText(
                    String.format(
                        Locale.US,
                        LABEL_FORMAT,
                        label.confidence * 100,
                        label.index
                    )
                )

            maxWidth = Math.max(maxWidth, Math.max(line1Width, line2Width))
        }

        val x = Math.max(0f, overlay.width / 2.0f - maxWidth / 2.0f)
        var y = Math.max(200f, overlay.height / 2.0f - totalHeight / 2.0f)

        if (!labels.isEmpty()) {
            val padding = 20f
            canvas.drawRect(
                x - padding,
                y - padding,
                x + maxWidth + padding,
                y + totalHeight + padding,
                labelPaint
            )
        }

        for (label in labels) {
            if (y + TEXT_SIZE * 2 > overlay.height) {
                break
            }
            canvas.drawText(label.text, x, y + TEXT_SIZE, textPaint)
            y += TEXT_SIZE
            canvas.drawText(
                String.format(
                    Locale.US,
                    LABEL_FORMAT,
                    label.confidence * 100,
                    label.index
                ),
                x, y + TEXT_SIZE, textPaint
            )
            y += TEXT_SIZE
        }
    }

    companion object {
        private const val TEXT_SIZE = 70.0f
        private const val LABEL_FORMAT = "%.2f%% confidence (index: %d)"
    }
}
