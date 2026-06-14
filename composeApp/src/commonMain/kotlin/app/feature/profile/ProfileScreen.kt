package app.feature.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
private val DisabledSwitchGreen = Color(0xFFC8E6C9)
private val LogoutRed = Color(0xFFD32F2F)

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: ProfileUiState = ProfileUiState(),
    onNotificationsChanged: (Boolean) -> Unit = {},
    onDarkModeChanged: (Boolean) -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        ProfileHeader(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .offset(y = (-32).dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SettingsCard(
                uiState = uiState,
                onNotificationsChanged = onNotificationsChanged,
                onDarkModeChanged = onDarkModeChanged,
                onLanguageClick = onLanguageClick,
                onPrivacyClick = onPrivacyClick,
            )

            AboutCard(uiState = uiState)

            LogoutButton(onClick = onLogoutClick)

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryGreen, HeaderEndGreen),
                    start = Offset.Zero,
                    end = Offset(900f, 900f),
                ),
            )
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 48.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = "Profil",
                color = Color.White,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        Text(
            text = "Willkommen zurück!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SettingsCard(
    uiState: ProfileUiState,
    onNotificationsChanged: (Boolean) -> Unit,
    onDarkModeChanged: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    onPrivacyClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 10.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Einstellungen",
                modifier = Modifier.padding(16.dp),
                color = DarkGreen,
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Medium,
            )

            SettingsRowDivider()

            SettingsToggleRow(
                icon = Icons.Outlined.NotificationsNone,
                label = "Benachrichtigungen",
                checked = uiState.notificationsEnabled,
                onCheckedChange = onNotificationsChanged,
            )

            SettingsRowDivider()

            SettingsToggleRow(
                icon = Icons.Outlined.DarkMode,
                label = "Dunkelmodus",
                checked = uiState.darkModeEnabled,
                onCheckedChange = onDarkModeChanged,
            )

            SettingsRowDivider()

            SettingsActionRow(
                icon = Icons.Outlined.Language,
                label = "Sprache",
                trailingText = uiState.language,
                onClick = onLanguageClick,
            )

            SettingsRowDivider()

            SettingsActionRow(
                icon = Icons.Outlined.Security,
                label = "Datenschutz",
                onClick = onPrivacyClick,
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowLabel(
            icon = icon,
            label = label,
            labelFontWeight = FontWeight.Normal,
        )

        CompactSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    trailingText: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 73.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 17.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowLabel(
            icon = icon,
            label = label,
            labelFontWeight = FontWeight.Medium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = MutedGreen,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MutedGreen,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun SettingsRowLabel(
    icon: ImageVector,
    label: String,
    labelFontWeight: FontWeight,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
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

        Text(
            text = label,
            color = DarkGreen,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = labelFontWeight,
        )
    }
}

@Composable
private fun CompactSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val knobOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
    )

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(24.dp)
            .background(
                color = if (checked) PrimaryGreen else DisabledSwitchGreen,
                shape = CircleShape,
            )
            .clickable { onCheckedChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 2.dp)
                .size(20.dp)
                .background(Color.White, CircleShape),
        )
    }
}

@Composable
private fun SettingsRowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(PaleGreen),
    )
}

@Composable
private fun AboutCard(
    uiState: ProfileUiState,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PaleGreen, DisabledSwitchGreen),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(20.dp),
        ) {
            Text(
                text = uiState.aboutTitle,
                color = DarkGreen,
                fontSize = 18.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = uiState.aboutText,
                modifier = Modifier.padding(top = 8.dp),
                color = MutedGreen,
                fontSize = 14.sp,
                lineHeight = 23.sp,
            )

            Text(
                text = uiState.version,
                modifier = Modifier.padding(top = 16.dp),
                color = MutedGreen,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, LogoutRed, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Abmelden",
            color = LogoutRed,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
