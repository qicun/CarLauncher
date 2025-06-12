package com.raite.crcc.systemui.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.raite.crcc.systemui.data.repository.LauncherRepository

/**
 * 一个监听系统通知的服务。
 * 当有新的通知发出或移除时，它会接收到回调，
 * 并据此更新 LauncherRepository 中对应应用的角标状态。
 */
class LauncherNotificationListenerService : NotificationListenerService() {

    /**
     * 当系统中有新的通知发布时被调用。
     * @param sbn 代表新通知的 StatusBarNotification 对象。
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.packageName?.let {
            // 当收到通知时，通知 Repository 为该应用显示角标。
            LauncherRepository.updateBadge(it, true)
        }
    }

    /**
     * 当系统中的一个通知被移除时被调用。
     * @param sbn 代表被移除通知的 StatusBarNotification 对象。
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.packageName?.let {
            // 注意：此处的逻辑被简化了。一个更完整的实现需要检查
            // 该应用是否还有其他活动通知，仅在所有通知都被清除时才移除角标。
            // 为简单起见，此处在任何通知被移除时都直接隐藏角标。
            LauncherRepository.updateBadge(it, false)
        }
    }
} 