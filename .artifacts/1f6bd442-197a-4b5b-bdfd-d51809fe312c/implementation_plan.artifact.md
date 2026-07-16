# Implementation Plan - Unify Search and Explore

This plan details how to redirect the home page search bar to the "Explore" experience, providing filtering and detailed results for all search categories, including users.

## User Review Required

> [!NOTE]
> This change will effectively make `SearchFragment` unused in the main app flow, as its functionality is superseded by the more featured `ExploreFragment`. I will also enable "User" search within the Explore flow to maintain parity with the previous search bar functionality.

## Proposed Changes

### [Home Page]

#### [MODIFY] [HomeFragment.kt](file:///Users/yellp1/Documents/GitHub/Personal%20Projects/AL-chan/app/src/main/java/com/zen/alchan/ui/home/HomeFragment.kt)
- Update the `searchCategoryList` observer to call `navigation.navigateToExplore(data)` instead of `navigation.navigateToSearch(data)`.

#### [MODIFY] [HomeViewModel.kt](file:///Users/yellp1/Documents/GitHub/Personal%20Projects/AL-chan/app/src/main/java/com/zen/alchan/ui/home/HomeViewModel.kt)
- Update `loadExploreCategories()` to include `SearchCategory.USER` using `R.string.search_users`.
- (Optional) Clean up `loadSearchCategories()` to use the same logic/list as `loadExploreCategories()` to ensure consistency.

---

### [Explore Page]

#### [MODIFY] [ExploreViewModel.kt](file:///Users/yellp1/Documents/GitHub/Personal%20Projects/AL-chan/app/src/main/java/com/zen/alchan/ui/explore/ExploreViewModel.kt)
- Update `updateSelectedSearchCategory()`: Remove `// should not be used` comments for `SearchCategory.USER`.
- Update `loadSearchCategories()` (the category selection inside Explore) to include `SearchCategory.USER`.

---

## Verification Plan

### Manual Verification
1.  **Search Bar:** Click the search bar on the Home page. Select "Anime". Verify it opens the `ExploreFragment` (with the filter icon and detailed cards).
2.  **Explore Button:** Click "Explore" in the menu. Select "Anime". Verify it still works as expected.
3.  **User Search:** Click the search bar, select "Users". Verify it opens the Explore page and correctly searches for users (even if filtering is disabled for this category).
4.  **Filtering:** Ensure that after searching via the search bar, you can still open and apply filters.
