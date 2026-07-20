package com.zen.alchan.ui.main

import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentMainBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.helper.utils.PushNotificationUtil
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.explore.ExploreFragment
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.medialist.MediaListFragment
import com.zen.alchan.ui.notifications.NotificationsFragment
import com.zen.alchan.ui.profile.ProfileFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var viewPagerAdapter: MainViewPagerAdapter? = null

    private var fragments: List<Fragment?>? = null
    private var homeFragment: HomeFragment? = null
    private var exploreFragment: ExploreFragment? = null
    private var animeListFragment: MediaListFragment? = null
    private var mangaListFragment: MediaListFragment? = null

    private var deepLink: DeepLink? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            val isViewerAuthenticated = viewModel.isViewerAuthenticated

            homeFragment = HomeFragment.newInstance()
            exploreFragment = ExploreFragment.newInstance(com.zen.alchan.helper.enums.SearchCategory.ANIME, isTopLevel = true)
            animeListFragment = MediaListFragment.newInstance(MediaType.ANIME)
            mangaListFragment = MediaListFragment.newInstance(MediaType.MANGA)

            fragments = if (isViewerAuthenticated) {
                listOf(
                    homeFragment,
                    exploreFragment,
                    animeListFragment,
                    mangaListFragment
                )
            } else {
                listOf(
                    homeFragment,
                    exploreFragment
                )
            }

            viewPagerAdapter = MainViewPagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                fragments?.filterNotNull() ?: listOf()
            )

            binding.mainViewPager.adapter = viewPagerAdapter
            mainViewPager.isUserInputEnabled = false
            mainViewPager.offscreenPageLimit = 4

            binding.mainBottomNavigation.menu.findItem(R.id.menuAnime).isVisible = isViewerAuthenticated
            binding.mainBottomNavigation.menu.findItem(R.id.menuManga).isVisible = isViewerAuthenticated

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val itemId = fragments?.getOrNull(position)?.let { fragment ->
                        when (fragment) {
                            homeFragment -> R.id.menuHome
                            exploreFragment -> R.id.menuExplore
                            animeListFragment -> R.id.menuAnime
                            mangaListFragment -> R.id.menuManga
                            else -> null
                        }
                    }
                    itemId?.let { mainBottomNavigation.menu.findItem(it).isChecked = true }
                }
            })

            mainBottomNavigation.setOnItemSelectedListener {
                val index = fragments?.indexOfFirst { fragment ->
                    when (it.itemId) {
                        R.id.menuHome -> fragment == homeFragment
                        R.id.menuExplore -> fragment == exploreFragment
                        R.id.menuAnime -> fragment == animeListFragment
                        R.id.menuManga -> fragment == mangaListFragment
                        else -> false
                    }
                } ?: 0

                if (index != -1) {
                    mainViewPager.setCurrentItem(index, true)
                }
                true
            }

            mainBottomNavigation.setOnItemReselectedListener {
                sharedViewModel.scrollToTop(it.order)
            }
        }
    }

    override fun setUpObserver() {
        viewModel.loadData(Unit)

        if (!sharedDisposablesAdded) {
            sharedDisposables.add(
                incomingDeepLink.subscribe {
                    handleDeepLinkNavigation(it)
                }
            )

            sharedDisposables.add(
                sharedViewModel.bottomSheetNavigation.subscribe {
                    binding.mainViewPager.setCurrentItem(it, true)
                }
            )

            sharedDisposablesAdded = true
        }

        deepLink?.let {
            handleDeepLinkNavigation(it)
        }
    }

    private fun handleDeepLinkNavigation(deepLink: DeepLink) {
        val isViewerAuthenticated = viewModel.isViewerAuthenticated

        when {
            deepLink.isHome() -> binding.mainViewPager.currentItem = 0
            deepLink.isAnimeList() && isViewerAuthenticated -> {
                val animeListIndex = fragments?.indexOfFirst { it == animeListFragment  }
                if (animeListIndex != null && animeListIndex != -1) {
                    changeTabWithDelay(animeListIndex)
                }
            }
            deepLink.isMangaList() && isViewerAuthenticated -> {
                val mangaListIndex = fragments?.indexOfFirst { it == mangaListFragment  }
                if (mangaListIndex != null && mangaListIndex != -1) {
                    changeTabWithDelay(mangaListIndex)
                }
            }
            deepLink.isNotifications() && isViewerAuthenticated -> {
                context?.let { PushNotificationUtil.clearAllPushNotification(it) }
                navigation.navigateToNotifications()
            }
            deepLink.isProfile() && isViewerAuthenticated -> {
                navigation.navigateToUser(id = null, username = null)
            }
            deepLink.isAppSettings() && isViewerAuthenticated -> {
                navigation.navigateToUser(id = null, username = null)
                navigation.navigateToSettings()
                navigation.navigateToAppSettings()
            }
            deepLink.isAniListSettings() && isViewerAuthenticated -> {
                navigation.navigateToUser(id = null, username = null)
                navigation.navigateToSettings()
                navigation.navigateToAniListSettings()
            }
            deepLink.isListSettings() && isViewerAuthenticated -> {
                navigation.navigateToUser(id = null, username = null)
                navigation.navigateToSettings()
                navigation.navigateToListSettings()
            }
            deepLink.isSpoiler() -> {
                dialog.showSpoilerDialog(deepLink.getQueryParamOfOrNull("data") ?: "", null)
            }
            deepLink.isAnime() || deepLink.isManga() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToMedia(it.toInt()) }
            }
            deepLink.isCharacter() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToCharacter(it.toInt()) }
            }
            deepLink.isStaff() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToStaff(it.toInt()) }
            }
            deepLink.isStudio() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToStudio(it.toInt()) }
            }
            deepLink.isUser() -> {
                deepLink.getAniListPageId()?.let {
                    val isUsername = it.toIntOrNull() == null
                    if (isUsername)
                        navigation.navigateToUser(username = it)
                    else
                        navigation.navigateToUser(id = it.toInt())
                }
            }
            deepLink.isActivity() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToActivityDetail(it.toInt()) { _, _ -> } }
            }
        }

        this.deepLink = null
    }

    private fun changeTabWithDelay(index: Int) {
        Single.timer(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.mainViewPager.currentItem = index
                },
                {
                }
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPagerAdapter = null
        fragments = null
        homeFragment = null
        exploreFragment = null
        animeListFragment = null
        mangaListFragment = null
    }

    companion object {
        @JvmStatic
        fun newInstance(deepLink: DeepLink?) = MainFragment().apply {
            this.deepLink = deepLink
        }
    }
}