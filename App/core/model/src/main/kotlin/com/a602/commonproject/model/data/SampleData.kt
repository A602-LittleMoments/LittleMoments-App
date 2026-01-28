package com.a602.commonproject.model.data

/**
 * 마이페이지 통합 테스트를 위한 전용 샘플 데이터입니다.
 * User, Baby, Group, GroupMember 형식
 */
object SampleData {

    // 1. 내 정보 (User)
    val user = User(
        id = "ads123",
        username = "홍길동",
        email = "abc@univ.ac.kr",
        nickname = "가나다",
        password = "password123!",
        profileImageUrl = null
    )

    // 2. 아이 정보 (Baby)
    val baby = Baby(
        babyId = "baby_01",
        babyName = "튼튼이",
        birthDate = "2025-01-01",
        gender = Baby.Gender.MALE,
        imageUrl = null
    )

    // 3. 그룹 멤버들 (GroupMember)
    val groupMembers = listOf(
        GroupMember(
            userId = "user_123",
            nickname = "길동이",
            relation = "GrandMother",
            role = GroupRole.OWNER
        ),
        GroupMember(
            userId = "user_456",
            nickname = "abc_123",
            relation = "Mother",
            role = GroupRole.MEMBER
        ),
        GroupMember(
            userId = "user_789",
            nickname = "아빠",
            relation = "Father",
            role = GroupRole.VIEWER
        )
    )

    // 4. 전체 그룹 정보 (Group)
    val group = Group(
        id = "group_602",
        name = "길동이네 가족",
        role = GroupRole.OWNER,
        relation = "나",
        members = groupMembers
    )
}
