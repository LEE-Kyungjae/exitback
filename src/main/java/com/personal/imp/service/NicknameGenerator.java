package com.personal.imp.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of("행복한", "슬픈", "큰", "작은", "빠른", "느린", "밝은", "어두운", "뜨거운", "차가운", "젊은", "늙은", "강한", "약한", "부드러운", "딱딱한", "부유한", "가난한", "깨끗한", "더러운", "아름다운", "못생긴", "쉬운", "어려운", "신선한", "신선하지 않은", "무거운", "가벼운", "두꺼운", "얇은");
    private static final List<String> NOUNS = List.of("책", "사람", "학교", "자동차", "집", "나무", "강아지", "고양이", "공원", "도시", "바다", "산", "하늘", "별", "달", "꽃", "친구", "컴퓨터", "전화", "음악", "영화", "음식", "물", "불", "바람", "구름", "비", "눈", "태양", "시계");
    private static final Random RANDOM = new SecureRandom();

    public static String generateNickname() {
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        int number = RANDOM.nextInt(10000);
        return String.format("%s%s#%04d", adjective, noun, number);
    }

    public static void main(String[] args) {
        System.out.println(generateNickname());
    }
}
