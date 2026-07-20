# Walkthrough - Relocated Profile and Notifications

I have moved the **Profile** and **Notifications** access points from the bottom navigation bar to the top right of the Home screen header. This resolves the 5-item limit crash and provides a cleaner main navigation interface.

## Changes Made

### Main Navigation Uncluttered
- **Bottom Navigation:** Removed the Profile and Notifications tabs. The bar now features **Home**, **Search**, **Anime**, and **Manga** (4 items), which is within the system's 5-item limit.
- **Fixed Crash:** The `java.lang.IllegalArgumentException` caused by having 6 items in the `BottomNavigationView` is now resolved.

### Home Header Enhancements
- **Top-Right Icons:** Added a **Notification Bell** icon and the **User Avatar** to the top right of the Home screen header.
- **Unread Badge:** Implemented a red notification badge on the bell icon that updates in real-time to show your unread notification count.
- **Interactive:** Both the Avatar and Bell icons are clickable and will open your full Profile and Notifications screens, respectively.

### Technical Refactoring
- **Full-Page Navigation:** Notifications and Profile now open as standard full-page fragments (stacking on top) rather than being part of the persistent tab ViewPager.
- **State Management:** Updated `HomeViewModel` to observe unread notification counts from the `UserRepository`, ensuring the badge stays accurate.
- **Deep Linking:** Updated the app's deep linking logic to correctly open these screens even though they are no longer persistent tabs.

## Verification Results

### Manual Test Scenarios
1.  **Launch & Stability:** Verified the app launches without crashing and correctly displays the 4 bottom tabs.
2.  **Header Actions:** Verified that clicking the top-right avatar opens the user's profile and clicking the bell opens notifications.
3.  **Notification Badge:** Verified that when a new notification is received, the red badge appears on the Home screen's bell icon.
4.  **Back Navigation:** Verified that navigating to Profile or Notifications from Home allows for easy "back" navigation to return to the front page.

> [!TIP]
> Your main discovery and management tools (Home, Search, Lists) are now front-and-center, while your personal activity (Notifications, Profile) is tucked neatly into the header!
