
# Social_Media_App
A social media application made with Kotlin and Firebase Firestore.

- [x] User authentication
- [x] Upload post
     - [ ] Post comment
     - [ ] Post like
- [x] Follow functionality
- [x] Search users
- [ ] Feed home page with the posts of users I follow (can be done in Home fragment)
- [ ] Upload profile pic (can be done in Profile fragment)

## Structure of project

1. There are 2 activities
  - MainActivity.kt associated with [activity_main.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/res/layout/activity_main.xml)
      - It is opened only after authentication to allow users access all the functionalities
        ![image](https://github.com/yogendra131994/Social_Media_App/assets/87609565/106baf59-0d6d-420b-8216-41a21258d9b3)

  - Authentication.kt associated with [activity_authentication.xml]( https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/res/layout/activity_authentication.xml)
      - When someone newly installs this application, this activity is opened where we have Sign in, Sign up, Explore buttons.
        ![image](https://github.com/yogendra131994/Social_Media_App/assets/87609565/9efc5e8f-8141-48fc-a4a4-a373a356756a)

The MainActivity.kt uses bottom navigation. All the .kt files are within folder [bottomnavigation](https://github.com/yogendra131994/Social_Media_App/tree/main/app/src/main/java/com/example/myapplication/bottomnavigation) folder.

> CreateFragment.kt associated with [fragment_create.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/bottomnavigation/CreateFragment.kt).
> 
> ExploreFragment.kt associated with [fragment_explore.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/bottomnavigation/ExploreFragment.kt)
> 
> HomeFragment.kt associated with [fragment_home.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/bottomnavigation/HomeFragment.kt)
> 
> ProfileFragment.kt associated with [fragment_profile.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/bottomnavigation/ProfileFragment.kt)
> 
> SearchFragment.kt associated with [fragment_search.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/bottomnavigation/SearchFragment.kt)

The Authentication.kt contains 3 fragments as following
> [fragment_sign_up.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/res/layout/fragment_sign_up.xml)  handled by [SignUpFragment.kt](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/SignUpFragment.kt)
> 
> [fragment_sign_in.xml](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/res/layout/fragment_sign_in.xml) handled by [SignInFragment.kt](https://github.com/yogendra131994/Social_Media_App/blob/main/app/src/main/java/com/example/myapplication/SignInFragment.kt)
