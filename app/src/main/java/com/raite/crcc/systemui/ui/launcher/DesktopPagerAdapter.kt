package com.raite.crcc.systemui.ui.launcher

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * ViewPager2 的适配器，用于管理桌面(DesktopPageFragment)。
 *
 * @param activity The hosting activity.
 */
class DesktopPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    /**
     * 返回桌面页面的总数。
     * 此处修改为3，以实现三个桌面的需求。
     * TODO: 未来可以根据用户配置动态返回页面数量。
     */
    override fun getItemCount(): Int = 3

    /**
     * 根据位置创建对应的桌面 Fragment。
     *
     * @param position 页面位置索引。
     * @return 一个新的 DesktopPageFragment 实例。
     */
    override fun createFragment(position: Int): Fragment {
        // 为每个页面创建一个新的 DesktopPageFragment 实例
        return DesktopPageFragment.newInstance(position)
    }
} 