# Walkthrough - Persistent Search Tab

I have added a "Search" tab to the bottom navigation bar and implemented state persistence to ensure your search queries and filters are remembered across app sessions and navigation switches.

## Changes Made

### Bottom Navigation
- Added a new **Search** (Explore) tab as the second item in the bottom navigation menu.
- Updated `MainFragment` to include the `ExploreFragment` in its tab layout.
- Increased the `offscreenPageLimit` to **5**, which natively preserves the scroll position and view state when switching between any of the main tabs.

### State Persistence
- **Query & Category:** Your current search text and selected category (Anime, Manga, etc.) are now saved to local storage as you type or change them.
- **Filters:** All search filters (including the "Hide watched series" filter we added earlier) are now persisted.
- **Session Persistence:** When you close and reopen the app, the Search tab will automatically restore your last active query and filter settings.

### Unified Experience
- The search bar on the **Home** page now switches you to the **Search** tab and updates the category to your selection, providing a seamless and unified discovery experience.
- Removed the back button from the Search tab when accessed via the bottom navigation to reflect its status as a root-level screen.

## Verification Results

### Manual Test Scenarios
1.  **Tab Switching:** Searched for "One Piece", scrolled down, switched to the Profile tab, and then back to Search. Verified that the scroll position and results remained unchanged.
2.  **Home Bar Redirection:** Clicked the Home search bar, selected "Manga". Verified the app switched to the Search tab and automatically performed a search for Manga.
3.  **App Restart:** Set a specific filter (e.g., Year: 2024), closed the app, and reopened it. Verified that the Search tab still had the 2024 filter applied.

> [!TIP]
> You can now find the Search icon (magnifying glass) at the bottom of your screen next to the Home button!
