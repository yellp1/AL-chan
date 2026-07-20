# Tasks - Move Notifications/Profile to Home Header

- [x] Clean up Bottom Navigation
    - [x] Update `menu_bottom_navigation.xml` (Remove Notifications/Profile)
    - [x] Update `SharedMainViewModel.kt` (Update Page enum and indices)
    - [x] Update `MainFragment.kt` (Handle 4-tab mapping and ViewPager)
- [x] Implement Top-Level Navigation
    - [x] Update `NavigationManager.kt` (Add `navigateToNotifications`)
    - [x] Update `DefaultNavigationManager.kt` (Implement `navigateToNotifications`)
- [x] Home Header UI Changes
    - [x] Update `layout_home_header.xml` (Add Bell and Avatar to top-right)
    - [x] Update `HomeRvAdapter.kt` (Bind header clicks and badge)
    - [x] Update `HomeListener.kt` (Add callbacks for Notifications/Profile)
- [x] Home Logic & Badge Management
    - [x] Update `HomeViewModel.kt` (Observe unread notification count)
    - [x] Update `HomeFragment.kt` (Handle new listener callbacks)
- [x] Verification & Testing
