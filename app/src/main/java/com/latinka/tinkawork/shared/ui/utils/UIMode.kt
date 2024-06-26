package com.latinka.tinkawork.shared.ui.utils

import android.content.Context
import android.content.res.Configuration

abstract class UIMode {
    companion object {
        fun isDarkMode(context: Context): Boolean {
            val nightModeFlag = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
            return nightModeFlag == Configuration.UI_MODE_NIGHT_YES
        }
    }
}