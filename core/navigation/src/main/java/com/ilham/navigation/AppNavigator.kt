package com.ilham.navigation

import androidx.navigation3.runtime.NavBackStack


class AppNavigator : Navigator {

    private var backStack: NavBackStack? = null

    override fun setBackState(backStack: NavBackStack) {
        this.backStack = backStack
    }

    override fun navigateTo(route: AppRoute) {
        requireBackStack().add(route)
    }

    override fun navigateBack(): Boolean {
        val stack = requireBackStack()
        return if (stack.size > 1){
            stack.removeAt(stack.lastIndex)
            true
        }else{
            false
        }
    }

    override fun navigateAndPopUpTo(route: AppRoute) {
        val stack = requireBackStack()
        stack.clear()
        stack.add(route)
    }

    fun requireBackStack(): NavBackStack{
        return checkNotNull(backStack){ "Navigator back stack has not been attached." }
    }
}