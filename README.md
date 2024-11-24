## 팀원

| 이름 | 이메일 | 깃허브 주소 | 역할 |
| --- | --- | --- | --- |
| 김동권 | dafeg3@ajou.ac.kr | https://github.com/kwon204 | 백엔드 |
| 신지항 | tlswlgkd565@ajou.ac.kr | https://github.com/zihang98 | 백엔드 |
| 이정태 | sms3025@ajou.ac.kr | https://github.com/sms3025 | 백엔드 |

## 기술 스택

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)

![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-a08021?style=for-the-badge&logo=firebase&logoColor=ffcd34)

![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)

## 패키지 구조
```
├── admin
│   ├── controller
│   ├── dto
│   └── service
├── controller
├── domain
├── dto
│   └── CollectionResponse
├── global
│   ├── auth
│   ├── config
│   ├── error
│   └── jwt
├── repository
└── service
```

- admin: 관리자 기능
- item: 중고 거래 게시글 관리
- chat: 거래 채팅 기능
- timetable: 개인 시간표 기능
- review: 리뷰 기능
- user: 로그인, 학교 인증, 프로필 기능
- keyword: 키워드를 통한 알림 기능
- report: 신고 기능
- qa: 문의 기능
- global: 에러 핸들링, jwt 기능, configuration 파일

위 기능들을 위 패키지 구조에 맞춰 구현

## 코드 컨벤션

https://google.github.io/styleguide/javaguide.html

https://github.com/spring-projects/spring-framework/wiki/Code-Style

## 깃 컨벤션

<img width="1098" alt="gitflow" src="https://github.com/user-attachments/assets/05b57047-01a2-4cc5-aabc-07a34fdb8946">
