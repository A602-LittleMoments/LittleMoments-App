package com.a602.commonproject.feature.mypage

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.mypage.viewmodel.GroupJoinUiState
import com.a602.commonproject.feature.mypage.viewmodel.GroupJoinViewModel
import com.a602.commonproject.navigation.Navigator

@Composable
fun GroupJoinContainer(
    navigator: Navigator,
    viewModel: GroupJoinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isJoinSuccess) {
        if (uiState.isJoinSuccess) {
            Toast.makeText(context, "그룹에 참여했습니다!", Toast.LENGTH_SHORT).show()
            viewModel.onJoinSuccessConsumed()
            navigator.goBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    GroupJoinScreen(
        uiState = uiState,
        onCodeChanged = viewModel::onCodeChanged,
        onRelationChanged = viewModel::onRelationChanged,
        onJoinClick = viewModel::joinGroup,
        onBackClick = { navigator.goBack() }
    )
}

@Composable
fun GroupJoinScreen(
    uiState: GroupJoinUiState,
    onCodeChanged: (String) -> Unit,
    onRelationChanged: (String) -> Unit,
    onJoinClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = { LMTopAppBar(title = "그룹 참여하기", onNavigationClick = onBackClick,) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(56.dp))
                JoinGroupHeader()
                Spacer(modifier = Modifier.height(40.dp))
                LMEditInputField(
                    label = "초대코드",
                    value = uiState.code,
                    onValueChange = onCodeChanged,
                    placeholder = "공유받은 초대코드를 입력하세요"
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "관계",
                    value = uiState.relation,
                    onValueChange = onRelationChanged,
                    placeholder = "예) 아빠, 엄마, 삼촌"
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledButton(
                    text = "참여하기",
                    onClick = onJoinClick,
                    size = ButtonSize.Full,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.padding(bottom = 50.dp)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun JoinGroupHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = main
        )
        Text(
            text = "초대코드를 입력하고\n가족 그룹에 참여하세요!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = color3
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupJoinScreenPreview() {
    LMTheme {
        GroupJoinScreen(
            uiState = GroupJoinUiState(),
            onCodeChanged = {},
            onRelationChanged = {},
            onJoinClick = {},
            onBackClick = {}
        )
    }
}
