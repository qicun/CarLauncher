package com.raite.crcc.systemui.ui.launcher

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * ViewPager2 的适配器，用于管理桌面(DesktopPageFragment)。
 *
 * @param activity The hosting activity.
 */
class DesktopPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    /**
     * 返回桌面页面的总数。
     * 此处硬编码为2，代表有两个桌面屏幕。
     * TODO: 未来可以根据用户配置动态返回页面数量。
     */
    override fun getItemCount(): Int = 2

    /**
     * 根据位置创建对应的桌面 Fragment。
     *
     * @param position 页面位置索引。
     * @return 一个新的 DesktopPageFragment 实例。
     */
    override fun createFragment(position: Int): Fragment {
        // 所有的页面都使用同一个 Fragment 布局，
        // 未来可以根据 position 创建不同类型的桌面。
        return DesktopPageFragment.newInstance()
    }
} 