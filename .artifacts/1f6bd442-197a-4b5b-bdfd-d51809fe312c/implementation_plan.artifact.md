# Implementation Plan - Fix Crash and UI Bugs in Persistent Search

This plan addresses the crash reported after adding the Persistent Search tab and fixes several UI/navigation bugs.

## User Review Required

> [!IMPORTANT]
> - **Crash Root Cause:** `ExploreFragment` was adding shared observers without checking if they were already added. These observers were accessing `binding` after the view was destroyed, leading to a `NullPointerException`.
> - **Navigation Bug:** Tab switching (specifically to the Profile tab when unauthenticated) was checking the wrong menu item because it assumed the viewpager index matched the menu index.

## Proposed Changes

### [UI Components - Explore]

#### [MODIFY] [ExploreFragment.kt](file:///Users/yellp1/Documents/GitHub/Personal%20Projects/AL-chan/app/src/main/java/com/zen/alchan/ui/explore/ExploreFragment.kt)
- Wrap `sharedViewModel` subscription in `if (!sharedDisposablesAdded)`.
- Use a safe check for `_binding` inside the subscription to avoid NPE if the view is destroyed.
- Remove redundant `viewModel.loadData` call in `setUpObserver`.
- Read `isTopLevel` from `arguments` in `setUpLayout`.

### [UI Components - Main Navigation]

#### [MODIFY] [MainFragment.kt](file:///Users/yellp1/Documents/GitHub/Personal%20Projects/AL-chan/app/src/main/java/com/zen/alchan/ui/main/MainFragment.kt)
- Update `onPageSelected` to correctly map the `ViewPager2` position to the `BottomNavigationView` item ID, even when items are hidden (unauthenticated mode).

#### [MODIFY] [SharedMainViewModel.kt](file:///Users/yellp1/Documents/GitHub/Personal%20Projects/AL-chan/app/src/main/java/com/zen/alchan/ui/main/SharedMainViewModel.kt)
- Fix the `scrollEvents` map: map `Page.HOME` to `_scrollHomeToTop` instead of `_scrollAnimeToTop`.

---

## Verification Plan

### Automated Tests
- No automated tests available for UI lifecycle, will verify manually.

### Manual Verification
1.  **Crash Fix:** Navigate between tabs, background the app, and switch categories from Home. Ensure no crashes occur.
2.  **Unauthenticated Navigation:** Log out, and ensure that clicking "Profile" in the bottom navigation correctly highlights the Profile icon and doesn't switch to a hidden tab.
3.  **Scroll to Top:** Click the Home icon while on the Home tab to ensure it scrolls to top (verifying the `SharedMainViewModel` fix).
4.  **Top-Level Back Button:** Verify the Search tab correctly hides its back button when it's part of the bottom navigation.
