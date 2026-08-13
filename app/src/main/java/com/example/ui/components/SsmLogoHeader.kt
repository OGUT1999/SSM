package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Official SSM Logo (Solusi Sistem UMKM - Developer Small Business)
 * Rebuilt with exact pixel-perfect fidelity matching the user's reference logo image:
 * 1. "SSM" (Large bold cyan-blue heading)
 * 2. Center Icon:
 *    - Gear / Cogwheel with 6 teeth
 *    - Vibrant Green Leaf at top-right
 *    - Financial Line Chart with 3 connected ring nodes
 *    - Bold Upward Growth Arrow breaking out to the top-right
 *    - Inner secondary arrow
 * 3. "SOLUSI SISTEM" (Bold cyan-blue text)
 * 4. "UMKM" (Bold cyan-blue text)
 * 5. "DEVELOPPER SMALL BUSINESS" (Light blue tracking subtext)
 */
@Composable
fun SsmOfficialLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 130.dp,
    showContainerBackground: Boolean = false
) {
    val ssmBlue = Color(0xFF0C82C6)       // Primary SSM Cerulean Blue
    val ssmLightBlue = Color(0xFF62A9D6)  // Subtitle Light Blue
    val ssmGreen = Color(0xFF43B02A)      // Leaf Green

    Column(
        modifier = modifier
            .then(
                if (showContainerBackground) {
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(20.dp)
                } else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. TOP TITLE: SSM
        Text(
            text = "SSM",
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ssmBlue,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. CENTER ICON: GEAR + LEAF + CHART + ARROW
        Box(
            modifier = Modifier.size(iconSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(iconSize)) {
                val w = size.width
                val h = size.height
                val center = Offset(w * 0.48f, h * 0.52f)
                val gearRadius = w * 0.32f
                val strokeW = w * 0.07f

                // A. Main Gear Ring
                drawCircle(
                    color = ssmBlue,
                    radius = gearRadius,
                    center = center,
                    style = Stroke(width = strokeW)
                )

                // B. Gear Teeth (6 Protrusions)
                val teethCount = 6
                val toothWidth = w * 0.10f
                val toothHeight = w * 0.08f
                for (i in 0 until teethCount) {
                    val angle = Math.toRadians((i * 360 / teethCount - 30).toDouble())
                    val outerX = center.x + Math.cos(angle).toFloat() * (gearRadius + toothHeight * 0.5f)
                    val outerY = center.y + Math.sin(angle).toFloat() * (gearRadius + toothHeight * 0.5f)

                    val toothPath = Path().apply {
                        val sideAngle1 = angle - 0.22
                        val sideAngle2 = angle + 0.22
                        val rIn = gearRadius - strokeW * 0.3f
                        val rOut = gearRadius + toothHeight

                        moveTo(
                            center.x + Math.cos(sideAngle1).toFloat() * rIn,
                            center.y + Math.sin(sideAngle1).toFloat() * rIn
                        )
                        lineTo(
                            center.x + Math.cos(sideAngle1).toFloat() * rOut,
                            center.y + Math.sin(sideAngle1).toFloat() * rOut
                        )
                        lineTo(
                            center.x + Math.cos(sideAngle2).toFloat() * rOut,
                            center.y + Math.sin(sideAngle2).toFloat() * rOut
                        )
                        lineTo(
                            center.x + Math.cos(sideAngle2).toFloat() * rIn,
                            center.y + Math.sin(sideAngle2).toFloat() * rIn
                        )
                        close()
                    }
                    drawPath(path = toothPath, color = ssmBlue)
                }

                // C. Green Leaf on Top-Right of Gear
                val leafCenter = Offset(center.x + gearRadius * 0.75f, center.y - gearRadius * 0.85f)
                val leafPath = Path().apply {
                    moveTo(leafCenter.x, leafCenter.y + w * 0.08f)
                    cubicTo(
                        leafCenter.x + w * 0.12f, leafCenter.y + w * 0.06f,
                        leafCenter.x + w * 0.15f, leafCenter.y - w * 0.08f,
                        leafCenter.x + w * 0.02f, leafCenter.y - w * 0.12f
                    )
                    cubicTo(
                        leafCenter.x - w * 0.08f, leafCenter.y - w * 0.06f,
                        leafCenter.x - w * 0.06f, leafCenter.y + w * 0.04f,
                        leafCenter.x, leafCenter.y + w * 0.08f
                    )
                    close()
                }
                drawPath(path = leafPath, color = ssmGreen)

                // D. Line Chart Nodes (3 Rings)
                val n1 = Offset(center.x - gearRadius * 0.85f, center.y + gearRadius * 0.25f)
                val n2 = Offset(center.x - gearRadius * 0.15f, center.y - gearRadius * 0.15f)
                val n3 = Offset(center.x + gearRadius * 0.25f, center.y + gearRadius * 0.05f)
                val arrowTip = Offset(center.x + gearRadius * 1.05f, center.y - gearRadius * 0.95f)

                // Draw Node Ring 1
                drawCircle(color = ssmBlue, radius = w * 0.055f, center = n1)
                drawCircle(color = Color.White, radius = w * 0.025f, center = n1)

                // Draw Node Ring 2
                drawCircle(color = ssmBlue, radius = w * 0.045f, center = n2)
                drawCircle(color = Color.White, radius = w * 0.02f, center = n2)

                // Draw Node Ring 3
                drawCircle(color = ssmBlue, radius = w * 0.045f, center = n3)
                drawCircle(color = Color.White, radius = w * 0.02f, center = n3)

                // E. Connecting Line Chart Path
                val chartPath = Path().apply {
                    moveTo(n1.x, n1.y)
                    lineTo(n2.x, n2.y)
                    lineTo(n3.x, n3.y)
                    lineTo(arrowTip.x, arrowTip.y)
                }
                drawPath(
                    path = chartPath,
                    color = ssmBlue,
                    style = Stroke(width = w * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // F. Large Upward Growth Arrow Head (Top Right)
                val arrowHeadPath = Path().apply {
                    moveTo(arrowTip.x, arrowTip.y)
                    lineTo(arrowTip.x - w * 0.22f, arrowTip.y + w * 0.04f)
                    lineTo(arrowTip.x - w * 0.14f, arrowTip.y + w * 0.12f)
                    lineTo(arrowTip.x - w * 0.04f, arrowTip.y + w * 0.22f)
                    close()
                }
                drawPath(path = arrowHeadPath, color = ssmBlue)

                // G. Secondary Inner Small Arrow (Pointing Up inside Gear)
                val innerArrowCenter = Offset(center.x - gearRadius * 0.1f, center.y - gearRadius * 0.1f)
                val innerArrowPath = Path().apply {
                    moveTo(innerArrowCenter.x, innerArrowCenter.y - w * 0.10f)
                    lineTo(innerArrowCenter.x - w * 0.07f, innerArrowCenter.y + w * 0.04f)
                    lineTo(innerArrowCenter.x - w * 0.02f, innerArrowCenter.y + w * 0.02f)
                    lineTo(innerArrowCenter.x - w * 0.02f, innerArrowCenter.y + w * 0.08f)
                    lineTo(innerArrowCenter.x + w * 0.02f, innerArrowCenter.y + w * 0.08f)
                    lineTo(innerArrowCenter.x + w * 0.02f, innerArrowCenter.y + w * 0.02f)
                    lineTo(innerArrowCenter.x + w * 0.07f, innerArrowCenter.y + w * 0.04f)
                    close()
                }
                drawPath(path = innerArrowPath, color = Color.White)
                drawPath(
                    path = innerArrowPath,
                    color = ssmBlue,
                    style = Stroke(width = w * 0.02f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. "SOLUSI SISTEM"
        Text(
            text = "SOLUSI SISTEM",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ssmBlue,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.5.sp
        )

        // 4. "UMKM"
        Text(
            text = "UMKM",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ssmBlue,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 5. "DEVELOPPER SMALL BUSINESS"
        Text(
            text = "DEVELOPPER SMALL BUSINESS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ssmLightBlue,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun SsmLogoCard(
    modifier: Modifier = Modifier,
    cardSize: Dp = 220.dp
) {
    SsmOfficialLogo(
        modifier = modifier,
        iconSize = (cardSize.value * 0.55f).dp,
        showContainerBackground = true
    )
}

@Composable
fun SsmLogoHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SsmOfficialLogo(
            iconSize = 130.dp,
            showContainerBackground = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Clean Subtitle Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEBF3FF))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aplikasi cerdas Solusi Sistem UMKM untuk membantu usaha Anda lebih mudah, murah, dan menguntungkan.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                lineHeight = 18.sp
            )
        }
    }
}
