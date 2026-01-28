package com.a602.commonproject.feature.memory


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * feature 쪽에서 "상세 화면 엔트리"를 통일하고 싶을 때 쓰는 Wrapper
 * - 내부는 MediaDetailRoute 호출
 * - Preview도 여기서 보기 편함
 */
@Composable
fun MediaDetailScreenEntry(
    keywordId: String,
    mediaId: String,
    onBack: () -> Unit,
) {
    MediaDetailRoute(
        keywordId = keywordId,
        mediaId = mediaId,
        onBack = onBack
    )
}

/* -------------------- Preview -------------------- */

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "Detail - Photo")
@Composable
private fun Preview_Detail_Photo() {
    MediaDetailScreenEntry(
        keywordId = "k1",
        mediaId = "m1",
        onBack = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "Detail - Video")
@Composable
private fun Preview_Detail_Video() {
    MediaDetailScreenEntry(
        keywordId = "k1",
        mediaId = "m2",
        onBack = {}
    )
}
