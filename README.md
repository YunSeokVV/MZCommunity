# <p align="center">   MZ 커뮤니티 </p>

##### 

<p align="center">   MZ 커뮤니티는 사용자들이 자신의 일상을 공유하기 위한 커뮤니티 앱입니다. </p>


개발 인원 : 1명 <br>
개발 기간 : 4개월


**목차**

1. 앱의기능

2. 사용한 기술들

3. Architecture

4. 시연영상 링크

5. 실행파일



#  1. 앱의 기능




![](https://raw.githubusercontent.com/YunSeokVV/MZCommunity/master/previews/Slice1.png)

![](https://raw.githubusercontent.com/YunSeokVV/MZCommunity/master/previews/Slice2.png)

![](https://raw.githubusercontent.com/YunSeokVV/MZCommunity/master/previews/Slice3.png)

# 기능 상세설명

**1.로그인**

 이메일 또는 소셜네트워크로 로그인합니다.

**2.회원가입**

 이메일을 통해 회원가입을 합니다.

**3.일상 게시글**

 서로의 일상을 공유합니다. 게시글은 동영상, 다중 이미지, 텍스트 등이 있습니다.

**4.밸런스 게임게시판**

 밸런스게임을 할 수 있습니다. 어떤 안건이 몇표를 받았는지 확인할 수 있고 백분율로도 확인할 수 있습니다.

**5.밸런스게임 생성**

 원하는 주제로 밸런스 게임을 생성할 수 있습니다.

**6.마이 페이지**

 자신의 프로필 사진과 닉네임을 수정할 수 있습니다.

**7.게시글 좋아요/싫어요**

 일상 게시글에 대해 좋아요, 싫어요를 설정할 수 있습니다.

**8.댓글/대댓글**

 일상 게시글과 밸런스 게임 게시글에 댓글과 대댓글을 작성할 수 있습니다.

**9.일상 게시글 작성**

 일상 게시글을 작성할 수 있습니다. (동영상, 다중 이미지, 텍스트)



# 2. 사용한 기술 & 라이브러리



Coroutine + Flow를 활용해서 비동기 처리 작업을 진행

- Language : Kotlin
- Jetpack
  - ViewModel
  - ViewBinding
  - Hilt
  - ViewPager2
  - Swiperefreshlayout
- Architecture
  - MVVM Architecture
  - Domain Layer
  - Repository Pattern
- Glide
- Firebase Firestore
- Firebase Storage
- Firebase Authentication
- Logger



# 3. Architecture


**MZ 커뮤니티는** MVVM 아키텍쳐를 기반으로 하고 있습니다. View, Domain, Repository 계층으로 나뉩니다.

![](https://raw.githubusercontent.com/YunSeokVV/MZCommunity/master/previews/AppArchitecture.png)

MZ 커뮤니티는 세가지 계층으로 구성되어 있습니다.

**UI 계층** 
- 앱을 동작하기 위해 비즈니스 로직에서 받아온 데이터를 적절하게 사용자에게 표현해주는 역할을 합니다. 이 계층은 비즈니스 로직의 데이터를 알아선 안됩니다.
ViewModel의 LiveData가 외부에서 받아오는 데이터를 관찰하고 있다가 변화가 감지되면 이를 사용자가 봐야할 UI에 적용시켜줍니다.

**Domain 계층**
- 도메인 계층을 도입함으로서 향후 앱의 기능이 추가되거나 수정이 될경우 유지보수에 용이한 이점을 얻었습니다. 사용자의 동작을 최소한의 기능단위로 나누었습니다.

**Data 계층**
- 외부의 데이터를 받아오는 역할을 해줍니다. 여기서 외부는 서버, 로컬 디비를 의미합니다. Repository(저장소)를 포함하고 있고 이 계층은 다른 어떠한 계층에도 의존하지 않습니다.

각 계층은 자신의 역할에만 집중하고 있고 자신과 무관한 역할의 일은 하지 않습니다.
상위계층에서 하위계층(UI -> Domain -> Data)으로 Hilt라이브러리를 사용해서 의존성 주입을 했습니다.


# 4. 시연영상 링크
https://www.youtube.com/watch?v=UAquv6Xn-Xw

# 5. 실행파일
[https://github.com/YunSeokVV/MZCommunity/releases/tag/v1.0.0](https://github.com/YunSeokVV/MZCommunity/releases/tag/v1.0.0)

