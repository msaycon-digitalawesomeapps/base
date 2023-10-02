package com.digitalawesome.clearchoice.home.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.digitalawesome.clearchoice.AchievementBindingModel_
import com.digitalawesome.clearchoice.R
import com.digitalawesome.clearchoice.UserViewModel
import com.digitalawesome.clearchoice.category
import com.digitalawesome.clearchoice.databinding.FragmentAccountBinding
import com.digitalawesome.clearchoice.orderHistory
import com.digitalawesome.clearchoice.profile
import com.digitalawesome.clearchoice.rewards
import com.digitalawesome.clearchoice.setting
import com.digitalawesome.clearchoice.space
import com.digitalawesome.dispensary.domain.models.AchievementsModel
import com.digitalawesome.dispensary.domain.models.UserModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.UUID


class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }


    private lateinit var viewModel: AccountViewModel

    lateinit var binding: FragmentAccountBinding

    private val userViewModel: UserViewModel by sharedViewModel()
    private var percentage: Int = 0
    private var userData: UserModel? = null
    private var achievements: List<AchievementsModel?> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewEvents()
        setupDataEvents()

        initialServiceCalls()
    }

    private fun initialServiceCalls() {
        userViewModel.getCurrentUser()
        userViewModel.getOrders(1)
    }

    private fun setupViews() {
        binding.rvProfile.withModels {

            val userData = userViewModel.userData.value ?: return@withModels

            profile {
                id(userData.id)
                name(userData.attributes.fullname)
                subHeader("Bud Club Member")
                profileImage(userData.attributes.avatar)
                onClick { _ ->
                    findNavController().navigate(R.id.go_to_profile)
                }
            }

            repeat(2) {
                space {
                    id(UUID.randomUUID().toString())
                }
            }

            category {
                id(UUID.randomUUID().toString())
                category("Club Rewards")
            }

            rewards {
                id("rewards")
                percent("${percentage}%")
                nextReward("60pts")
            }

            repeat(2) {
                space {
                    id(UUID.randomUUID().toString())
                }
            }

            category {
                id(UUID.randomUUID().toString())
                category("Achievements")
            }

            carousel {
                id(UUID.randomUUID().toString())
                padding(
                    Carousel.Padding.dp(
                        8, //left
                        0, //top
                        8, //right
                        0, //bottom
                        16 //itemspacing
                    )
                )
                numViewsToShowOnScreen(2f)
                models(achievements.map {
                    return@map AchievementBindingModel_().id(UUID.randomUUID().toString())
                        .title(it?.attributes?.title ?: "")
                        .image(if (it?.attributes?.isComplete == true) it.attributes.image else it?.attributes?.imageUnearned)
                })
            }

            repeat(2) {
                space {
                    id(UUID.randomUUID().toString())
                }
            }

            category {
                id(UUID.randomUUID().toString())
                category("My Orders")
            }

            (1..3).forEach {
                orderHistory {
                    id("cartItem$it")
                    title("item $it")
                    description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. $it")
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                    onClick { _ -> }
                }
            }

            repeat(2) {
                space {
                    id(UUID.randomUUID().toString())
                }
            }

            setting {
                id("settings")
                icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings))
                title("Settings")
            }

            space {
                id(UUID.randomUUID().toString())
            }

            setting {
                id("invite")
                icon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_add_friends
                    )
                )
                title("Invite Friends")
            }

            space {
                id(UUID.randomUUID().toString())
            }

            setting {
                id("review")
                icon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_review
                    )
                )
                title("Review this App")
                onClick { _ ->
                }
            }

            space {
                id(UUID.randomUUID().toString())
            }

            setting {
                id("logout")
                icon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_logout
                    )
                )
                title("Logout")
            }

            space {
                id(UUID.randomUUID().toString())
            }
        }
    }

    private fun setupViewEvents() {


    }

    private fun setupDataEvents() {
        userViewModel.userData.observe(viewLifecycleOwner) {
            userData = userViewModel.userData.value
            val userData = userData ?: return@observe
            achievements = userData.relationships?.achievements ?: listOf()
            val numberOfAchievements = achievements.size
            if (numberOfAchievements != 0) {
                percentage =
                    (achievements.count { achievementsModel -> achievementsModel?.attributes?.isComplete == true } / numberOfAchievements) * 100
            }

            binding.rvProfile.requestModelBuild()
        }

    }
}