package com.example.tesprojek.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Shape segienam (flat-top: sisi datar di atas & bawah, sudut di kiri & kanan)
 * dengan sudut membulat supaya terlihat "cute".
 * Rasio ideal lebar : tinggi = 1 : 0.866
 */
class HexagonShape(private val cornerRadius: Dp = 14.dp) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radiusPx = with(density) { cornerRadius.toPx() }
        return Outline.Generic(roundedPolygonPath(hexagonPoints(size), radiusPx))
    }
}

/** Enam titik sudut hexagon di dalam area [size]. */
fun hexagonPoints(size: Size): List<Offset> {
    val w = size.width
    val h = size.height
    return listOf(
        Offset(w * 0.25f, 0f),
        Offset(w * 0.75f, 0f),
        Offset(w, h / 2f),
        Offset(w * 0.75f, h),
        Offset(w * 0.25f, h),
        Offset(0f, h / 2f)
    )
}

/** Membuat Path poligon dengan sudut membulat (quadratic dikonversi ke cubic). */
fun roundedPolygonPath(points: List<Offset>, cornerRadius: Float): Path {
    val path = Path()
    val n = points.size
    for (i in 0 until n) {
        val prev = points[(i - 1 + n) % n]
        val curr = points[i]
        val next = points[(i + 1) % n]

        val toPrev = prev - curr
        val toNext = next - curr
        val r = minOf(cornerRadius, toPrev.getDistance() / 2f, toNext.getDistance() / 2f)

        val start = curr + toPrev / toPrev.getDistance() * r
        val end = curr + toNext / toNext.getDistance() * r

        if (i == 0) path.moveTo(start.x, start.y) else path.lineTo(start.x, start.y)

        // Quadratic (control = curr) -> Cubic
        val c1 = start + (curr - start) * (2f / 3f)
        val c2 = end + (curr - end) * (2f / 3f)
        path.cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)
    }
    path.close()
    return path
}