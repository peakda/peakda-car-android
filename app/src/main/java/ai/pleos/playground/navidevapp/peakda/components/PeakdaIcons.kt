package ai.pleos.playground.navidevapp.peakda.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Figma 에 쓰인 선 아이콘(24 격자, 둥근 끝). 색은 Icon tint 로 바꾼다. */
object PeakdaIcons {

    private fun circle(cx: Float, cy: Float, r: Float) =
        "M${cx - r},$cy a$r,$r 0 1,0 ${2 * r},0 a$r,$r 0 1,0 ${-2 * r},0"

    private fun stroke(name: String, width: Float, vararg d: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            d.forEach {
                addPath(
                    pathData = PathParser().parsePathString(it).toNodes(),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = width,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()

    private fun fill(name: String, vararg d: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            d.forEach {
                addPath(pathData = PathParser().parsePathString(it).toNodes(), fill = SolidColor(Color.Black))
            }
        }.build()

    val ChevronLeft = stroke("chevron_left", 2.4f, "M15 18 9 12 15 6")
    val ChevronRight = stroke("chevron_right", 2f, "M9 18 15 12 9 6")
    val More = fill("more", circle(4.5f, 12f, 2.4f), circle(12f, 12f, 2.4f), circle(19.5f, 12f, 2.4f))
    val Clock = stroke("clock", 2f, circle(12f, 12f, 10f), "M12 6v6l4 2")
    val Users = stroke(
        "users", 2f,
        "M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2", circle(9f, 7f, 4f),
        "M22 21v-2a4 4 0 0 0-3-3.87", "M16 3.13a4 4 0 0 1 0 7.75",
    )
    val Sun = stroke(
        "sun", 2f, circle(12f, 12f, 4f),
        "M12 2v2", "M12 20v2", "M4.93 4.93l1.41 1.41", "M17.66 17.66l1.41 1.41",
        "M2 12h2", "M20 12h2", "M6.34 17.66l-1.41 1.41", "M19.07 4.93l-1.41 1.41",
    )
    val Car = stroke(
        "car", 2f,
        "M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2",
        circle(7f, 17f, 2f), "M9 17h6", circle(17f, 17f, 2f),
    )
    private const val HEART =
        "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"
    val Heart = stroke("heart", 2f, HEART)
    val HeartFilled = fill("heart_filled", HEART)
    val Mic = stroke(
        "mic", 2f,
        "M12 2a3 3 0 0 0-3 3v7a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3Z", "M19 10v2a7 7 0 0 1-14 0v-2", "M12 19v3",
    )
}
