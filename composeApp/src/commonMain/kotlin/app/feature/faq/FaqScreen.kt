package app.feature.faq

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ScreenBackground = Color(0xFFF8FAF7)
private val PrimaryGreen = Color(0xFF2D5A2D)
private val HeaderEndGreen = Color(0xFF43A047)
private val DarkGreen = Color(0xFF1A3A1A)
private val MutedGreen = Color(0xFF5A7A5A)
private val PaleGreen = Color(0xFFE8F5E9)
private val ContactCardEndGreen = Color(0xFFC8E6C9)

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    uiState: FaqUiState = FaqUiState(),
    onContactClick: () -> Unit = {},
) {
    var expandedQuestionId by rememberSaveable { mutableStateOf(uiState.initialExpandedQuestionId) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        FaqHeader()

        Column(
            modifier = Modifier
                .offset(y = (-16).dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.questions.forEach { question ->
                FaqAccordionItem(
                    question = question,
                    expanded = expandedQuestionId == question.id,
                    onClick = {
                        expandedQuestionId = if (expandedQuestionId == question.id) {
                            null
                        } else {
                            question.id
                        }
                    },
                )
            }

            FaqContactCard(
                onContactClick = onContactClick,
                modifier = Modifier.padding(top = 12.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun FaqHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryGreen, HeaderEndGreen),
                    start = Offset.Zero,
                    end = Offset(900f, 900f),
                ),
            )
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Häufige Fragen",
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun FaqAccordionItem(
    question: FaqQuestionUiModel,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val itemShape = RoundedCornerShape(16.dp)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .animateContentSize(),
        shape = itemShape,
        color = Color.White,
        shadowElevation = 6.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 72.dp)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FaqIconBubble(icon = question.icon.imageVector())

                Text(
                    text = question.question,
                    modifier = Modifier.weight(1f),
                    color = DarkGreen,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                )

                Icon(
                    imageVector = if (expanded) {
                        Icons.Outlined.KeyboardArrowUp
                    } else {
                        Icons.Outlined.KeyboardArrowDown
                    },
                    contentDescription = if (expanded) "Einklappen" else "Ausklappen",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp),
                )
            }

            if (expanded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = PaleGreen,
                ) {
                    Text(
                        text = question.answer,
                        modifier = Modifier.padding(16.dp),
                        color = DarkGreen,
                        fontSize = 14.sp,
                        lineHeight = 23.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun FaqIconBubble(
    icon: ImageVector,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(PaleGreen, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun FaqContactCard(
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PaleGreen, ContactCardEndGreen),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Weitere Fragen?",
                color = DarkGreen,
                fontSize = 18.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Kontaktieren Sie uns für weitere Informationen über Windenergie in Deutschland",
                modifier = Modifier.padding(top = 8.dp),
                color = MutedGreen,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )

            Surface(
                onClick = onContactClick,
                modifier = Modifier.padding(top = 16.dp),
                shape = CircleShape,
                color = PrimaryGreen,
            ) {
                Text(
                    text = "Kontakt aufnehmen",
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun FaqQuestionIcon.imageVector(): ImageVector = when (this) {
    FaqQuestionIcon.Wind -> Icons.Outlined.Air
    FaqQuestionIcon.Co2 -> Icons.Outlined.Eco
    FaqQuestionIcon.Operator -> Icons.Outlined.Business
    FaqQuestionIcon.Participation -> Icons.Outlined.Groups
}
