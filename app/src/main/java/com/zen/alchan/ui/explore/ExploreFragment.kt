package com.zen.alchan.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.FragmentExploreBinding
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.main.SharedMainViewModel
import com.zen.alchan.ui.search.SearchRvAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class ExploreFragment : BaseFragment<FragmentExploreBinding, ExploreViewModel>() {

    override val viewModel: ExploreViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var adapter: SearchRvAdapter? = null

    private var listener: ExploreListener? = null
    private var mediaFilter: MediaFilter? = null
    private var isTopLevel: Boolean = false

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExploreBinding {
        return FragmentExploreBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        isTopLevel = arguments?.getBoolean(IS_TOP_LEVEL) ?: false
        with(binding) {
            exploreBackButton.show(!isTopLevel)
            exploreBackButton.clicks {
                goBack()
            }

            exploreCategoryButton.clicks {
                exploreEditText.clearFocus()
                viewModel.loadSearchCategories()
            }

            exploreSettingButton.clicks {
                exploreEditText.clearFocus()
                viewModel.loadMediaFilterComponent()
            }

            adapter = SearchRvAdapter(requireContext(), listOf(), AppSetting(), true, getSearchListener())
            exploreRecyclerView.adapter = adapter

            exploreSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            exploreRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy != 0) exploreEditText.clearFocus()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        viewModel.loadNextPage()
                    }
                }
            })

            exploreEditText.setOnFocusChangeListener { _, hasFocus ->
                toggleKeyboard(hasFocus)
            }
        }
    }

    override fun setUpInsets() {
        binding.exploreLayout.applyTopPaddingInsets()
        binding.exploreRecyclerView.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        if (!sharedDisposablesAdded) {
            sharedDisposables.add(
                sharedViewModel.searchCategoryUpdate.subscribe {
                    if (view != null) {
                        binding.exploreRecyclerView.scrollToPosition(0)
                        viewModel.updateSelectedSearchCategory(it, true)
                    }
                }
            )
            sharedDisposablesAdded = true
        }

        disposables.add(
            binding.exploreEditText.textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.doSearch(it.toString()) }
        )

        disposables.addAll(
            viewModel.loading.subscribe {
                if (view != null) binding.exploreSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.appSetting.subscribe {
                if (view != null) {
                    adapter = SearchRvAdapter(requireContext(), listOf(), it, true, getSearchListener())
                    binding.exploreRecyclerView.adapter = adapter
                }
            },
            viewModel.searchItems.subscribe {
                if (view != null) {
                    if (binding.exploreEditText.text.isNullOrEmpty() && viewModel.userManager.exploreSearchQuery?.isNotEmpty() == true) {
                        binding.exploreEditText.setText(viewModel.userManager.exploreSearchQuery)
                    }
                    adapter?.updateData(it, true)
                }
            },
            viewModel.emptyLayoutVisibility.subscribe {
                if (view != null) binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.searchCategoryList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    if (view != null) {
                        binding.exploreRecyclerView.scrollToPosition(0)
                        viewModel.updateSelectedSearchCategory(data, true)
                    }
                }
            },
            viewModel.searchPlaceholderText.subscribe {
                if (view != null) binding.exploreEditText.hint = getString(it)
            },
            viewModel.filterVisibility.subscribe {
                if (view != null) binding.exploreSettingButton.show(it)
            },
            viewModel.mediaFilterComponent.subscribe {
                navigation.navigateToFilter(it.mediaFilter, it.mediaType, it.scoreFormat, it.isUserList, it.hasBigList, it.isViewer) {
                    viewModel.updateMediaFilter(it)
                }
            },
            viewModel.scrollToTopTrigger.subscribe {
                if (view != null) binding.exploreRecyclerView.scrollToPosition(0)
            }
        )

        viewModel.loadData(ExploreParam(SearchCategory.valueOf(arguments?.getString(SEARCH_CATEGORY) ?: SearchCategory.ANIME.name), mediaFilter))
        mediaFilter = null
    }

    private fun getSearchListener(): SearchRvAdapter.SearchListener {
        return object : SearchRvAdapter.SearchListener {
            override fun navigateToMedia(media: Media) {
                binding.exploreEditText.clearFocus()
                navigateToBrowseScreen {
                    navigation.navigateToMedia(media.getId())
                }
            }

            override fun navigateToCharacter(character: Character) {
                binding.exploreEditText.clearFocus()
                navigateToBrowseScreen {
                    navigation.navigateToCharacter(character.id)
                }
            }

            override fun navigateToStaff(staff: Staff) {
                binding.exploreEditText.clearFocus()
                navigateToBrowseScreen {
                    navigation.navigateToStaff(staff.id)
                }
            }

            override fun navigateToStudio(studio: Studio) {
                binding.exploreEditText.clearFocus()
                navigateToBrowseScreen {
                    navigation.navigateToStudio(studio.id)
                }
            }

            override fun navigateToUser(user: User) {
                binding.exploreEditText.clearFocus()
                navigateToBrowseScreen {
                    navigation.navigateToUser(user.id)
                }
            }

            override fun showQuickDetail(media: Media) {
                dialog.showMediaQuickDetailDialog(media)
            }
        }
    }

    private fun navigateToBrowseScreen(navigation: () -> Unit) {
        listener?.let {
            goBack()
            it.doNavigation { navigation() }
        } ?: kotlin.run {
            navigation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        private const val SEARCH_CATEGORY = "searchCategory"
        private const val IS_TOP_LEVEL = "isTopLevel"

        @JvmStatic
        fun newInstance(searchCategory: SearchCategory, mediaFilter: MediaFilter? = null, listener: ExploreListener? = null, isTopLevel: Boolean = false) = ExploreFragment().apply {
            arguments = Bundle().apply {
                putString(SEARCH_CATEGORY, searchCategory.name)
                putBoolean(IS_TOP_LEVEL, isTopLevel)
            }
            this.mediaFilter = mediaFilter
            this.listener = listener
            this.isTopLevel = isTopLevel
        }
    }

    interface ExploreListener {
        fun doNavigation(navigation: () -> Unit)
    }
}