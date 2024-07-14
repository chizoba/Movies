package com.remote.ghibli.android.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.remote.ghibli.android.R

@Composable
fun FilmListItem(
    title: String,
    description: String,
    favoriteIcon: ImageVector,
    favoriteTintColor: Color,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = favoriteIcon,
                    contentDescription = stringResource(R.string.content_description_favorite),
                    tint = favoriteTintColor,
                )
            }
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.body1,
            color = Color.Black.copy(alpha = 0.75f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilmListItem_Preview() {
    val context = LocalContext.current
    Box(Modifier.padding(16.dp)) {
        FilmListItem(
            title = "Castle in the Sky",
            description = "The orphan Sheeta inherited a mysterious crystal that links her to the " +
                    "mythical sky-kingdom of Laputa. With the help of resourceful Pazu and a " +
                    "rollicking band of sky pirates, she makes her way to the ruins of the " +
                    "once-great civilization. Sheeta and Pazu must outwit the evil Muska, who " +
                    "plans to use Laputa's science to make himself ruler of the world.",
            favoriteIcon = Icons.Filled.Favorite,
            favoriteTintColor = Color.Red,
            onFavoriteClick = {
                Toast.makeText(context, "Favorite clicked", Toast.LENGTH_SHORT).show()
            },
        )
    }
}
