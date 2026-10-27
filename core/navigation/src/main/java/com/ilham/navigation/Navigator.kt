package com.ilham.navigation

import androidx.navigation3.runtime.NavBackStack

interface Navigator {

    fun setBackState(backStack: NavBackStack)

    fun navigateTo(route: AppRoute)

    fun navigateBack(): Boolean

    fun navigateAndPopUpTo(route: AppRoute)

}