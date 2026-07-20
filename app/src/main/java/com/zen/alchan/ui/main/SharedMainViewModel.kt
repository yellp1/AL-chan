package com.zen.alchan.ui.main

import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class SharedMainViewModel : BaseViewModel<Unit>() {

    private val _scrollHomeToTop = PublishSubject.create<Unit>()
    private val _scrollExploreToTop = PublishSubject.create<Unit>()
    private val _scrollAnimeToTop = PublishSubject.create<Unit>()
    private val _scrollMangaToTop = PublishSubject.create<Unit>()
    private val _scrollNotificationsToTop = PublishSubject.create<Unit>()

    private val scrollEvents = linkedMapOf(
        Page.HOME to _scrollHomeToTop,
        Page.EXPLORE to _scrollExploreToTop,
        Page.ANIME to _scrollAnimeToTop,
        Page.MANGA to _scrollMangaToTop,
        Page.NOTIFICATIONS to _scrollNotificationsToTop
    )

    private val _bottomSheetNavigation = PublishSubject.create<Int>()
    val bottomSheetNavigation: Observable<Int>
        get() = _bottomSheetNavigation

    private val _searchCategoryUpdate = PublishSubject.create<com.zen.alchan.helper.enums.SearchCategory>()
    val searchCategoryUpdate: Observable<com.zen.alchan.helper.enums.SearchCategory>
        get() = _searchCategoryUpdate

    override fun loadData(param: Unit) = Unit

    fun scrollToTop(pageIndex: Int) {
        scrollEvents.toList()[pageIndex].second.onNext(Unit)
    }

    fun getScrollToTopObservable(page: Page): Observable<Unit> {
        return scrollEvents[page] ?: _scrollHomeToTop
    }

    fun getPageFromMediaType(mediaType: MediaType): Page {
        return when (mediaType) {
            MediaType.ANIME -> Page.ANIME
            MediaType.MANGA -> Page.MANGA
        }
    }

    fun navigateTo(page: Page, category: com.zen.alchan.helper.enums.SearchCategory? = null) {
        category?.let { _searchCategoryUpdate.onNext(it) }
        _bottomSheetNavigation.onNext(Page.values().indexOfFirst { it == page })
    }

    enum class Page {
        HOME,
        EXPLORE,
        ANIME,
        MANGA,
        NOTIFICATIONS
    }
}