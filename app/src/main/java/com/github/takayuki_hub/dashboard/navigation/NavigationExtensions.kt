package com.github.takayuki_hub.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

// トップレベル画面（ボトムナビなどで切り替える主要画面）への遷移処理をカプセル化
fun NavController.navigateToTopLevelDestination(route: Any) {
    this.navigate(route) {
        popUpTo(this@navigateToTopLevelDestination.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}