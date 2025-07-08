package com.example.nookstyle.model

data class Villager(
    val name: String,
    val imagePath: String, // 빌라저 기본 이미지 파일 경로
    val remainImagePath: String, // 빌라저 remain 이미지 파일 경로
    val headImagePath: String, // 빌라저 head 이미지 파일 경로
    val hatImagePath: String, // 기본 모자 이미지 파일 경로
    val topImagePath: String, // 기본 상의 이미지 파일 경로
    val bottomImagePath: String, // 기본 하의 이미지 파일 경로
    val shoesImagePath: String, // 기본 신발 이미지 파일 경로
    val width: Int = 400, // 빌라저 이미지 너비
    val height: Int = 400, // 빌라저 이미지 높이
    val hatPosition: ClothingPosition, // 모자 위치
    val topPosition: ClothingPosition, // 상의 위치
    val bottomPosition: ClothingPosition, // 하의 위치
    val shoesPosition: ClothingPosition // 신발 위치
)

// 의류 아이템의 위치 정보를 담는 데이터 클래스
// scaleXTop: 윗변 길이 배율, scaleXBottom: 아랫변 길이 배율
// scaleYLeft: 왼쪽 높이 배율, scaleYRight: 오른쪽 높이 배율
// (x, y)는 변형 후 사다리꼴의 중심 상대좌표

data class ClothingPosition(
    val x: Float, // 중심 X (0.0~1.0)
    val y: Float, // 중심 Y (0.0~1.0)
    val scaleX: Float = 1.0f, // X축 배율
    val scaleY: Float = 1.0f, // Y축 배율
    val rotation: Float = 0.0f // 회전 각도
) 