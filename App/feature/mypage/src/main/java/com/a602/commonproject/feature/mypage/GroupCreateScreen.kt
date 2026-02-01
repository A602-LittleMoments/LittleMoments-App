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
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.mypage.viewmodel.GroupCreateUiState
import com.a602.commonproject.feature.mypage.viewmodel.GroupCreateViewModel
import com.a602.commonproject.navigation.Navigator

@Composable
fun GroupCreateContainer(
    navigator: Navigator,
    viewModel: GroupCreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isCreateSuccess) {
        if (uiState.isCreateSuccess) {
            Toast.makeText(context, "그룹이 생성되었습니다!", Toast.LENGTH_SHORT).show()
            viewModel.onCreateSuccessConsumed()
            navigator.goBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    GroupCreateScreen(
        uiState = uiState,
        onGroupNameChanged = viewModel::onGroupNameChanged,
        onRelationChanged = viewModel::onRelationChanged,
        onCreateClick = viewModel::createGroup,
        onBackClick = { navigator.goBack() }
    )
}

@Composable
fun GroupCreateScreen(
    uiState: GroupCreateUiState,
    onGroupNameChanged: (String) -> Unit,
    onRelationChanged: (String) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = { LMTopAppBar(title = "그룹 만들기", onNavigationClick = onBackClick) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(56.dp))
                CreateGroupHeader()
                Spacer(modifier = Modifier.height(40.dp))
                LMEditInputField(
                    label = "그룹 이름",
                    value = uiState.groupName,
                    onValueChange = onGroupNameChanged,
                    placeholder = "예) 우리 가족, 사랑하는 가족"
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "그룹에서 내가 불릴 호칭",
                    value = uiState.relation,
                    onValueChange = onRelationChanged,
                    placeholder = "예) 아빠, 엄마, 삼촌"
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledButton(
                    text = "생성하기",
                    onClick = onCreateClick,
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
private fun CreateGroupHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.GroupAdd,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = main
        )
        Text(
            text = "새로운 그룹을 만들고\n소중한 사람들을 초대하세요!",
            style = AppTypography.headlineSmall,
            textAlign = TextAlign.Center,
            color = color3
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupCreateScreenPreview() {
    LMTheme {
        GroupCreateScreen(
            uiState = GroupCreateUiState(),
            onGroupNameChanged = {},
            onRelationChanged = {},
            onCreateClick = {},
            onBackClick = {}
        )
    }
}
