package com.a602.commonproject.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a602.commonproject.designsystem.R

object LMicons {
    val Close = Icons.Default.Close //닫기


    // 네비게이션에서 사용하는 것들------------------------------
    val Home_Unselected = Icons.Outlined.Home //메인페이지
    val Home_Selected = Icons.Filled.Home //메인페이지

    val Memory_Unselected = Icons.Outlined.AutoAwesome
    val Memory_Selected = Icons.Filled.AutoAwesome


    val Gallery_Unselected = Icons.Outlined.Photo // 갤러리
    val Gallery_Selected = Icons.Filled.Photo

    val my_page_Unselected = Icons.Outlined.AccountCircle
    val my_page_Selected = Icons.Filled.AccountCircle

    val Person = Icons.Outlined.Person //회원가입 인물 아이콘

    // 일반 화면에서 사용하는 아이콘들 -------------------------

    val Back = Icons.Outlined.ArrowBackIosNew //뒤로가기

    val Download = Icons.Outlined.FileDownload

    val Delete = Icons.Outlined.Delete


    val PhotoLibrary = Icons.Outlined.PhotoLibrary //사진 선택에 들어가는 갤러리 아이콘

    val Camera = Icons.Outlined.PhotoCamera

    val Edit = Icons.Default.Edit

    val Calendar = Icons.Default.CalendarToday

    val Notifications = Icons.Outlined.Notifications

    val Email = Icons.Outlined.Email

    // 비밀번호 표시
    val Visibility = Icons.Outlined.Visibility

    val VisibilityOff = Icons.Outlined.VisibilityOff

    val Group = Icons.Outlined.ChildCare //그룹 상단 아이콘이었지만 일단 drawable에 기존 아이콘을 추가했습니다

    val Menu = Icons.Outlined.Menu

    val Shield = Icons.Outlined.Shield //그룹권한 멤버 아이콘

    val AddCircle = Icons.Outlined.AddCircle // 원형 +

    val Add = Icons.Outlined.Add // +

    val Heart = Icons.Rounded.Favorite

    val logout = Icons.AutoMirrored.Outlined.Logout
    val edit_outline = Icons.Outlined.ModeEdit
    val edit_note = Icons.Outlined.EditNote
    val person_add = Icons.Outlined.PersonAdd

    // 알림 타입 정하고 사용
//    fun notification(type: LMNotificationType): ImageVector {
//        return when (type) {
//            LMNotificationType.FAMILY -> Notifications
//            LMNotificationType.LOVE -> Heart
//            LMNotificationType.HIGHLIGHT -> Star
//            LMNotificationType.MEMORY -> Calendar
//        }
//    }

    // 임시용 알림창 아이콘
    @Composable
    fun notification(type: String): ImageVector {
        return when (type) {
            "FAMILY" -> Notifications
            "LOVE" -> Heart
            "HIGHLIGHT" -> Memory_Selected
            "Date" -> ImageVector.vectorResource(id = R.drawable.plant)
            else -> Memory_Unselected
        }
    }


}
