package com.dk.androidstudyguide.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @create on 2020/8/28 23:07
 * @description 测试adapter
 * @author mrdonkey
 */
class FragmentAdapter(fm: FragmentManager, var datas: List<Fragment>, var titles: List<String>) :
    FragmentPagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    override fun getItem(position: Int): Fragment {
        return datas[position]
    }

    override fun getCount(): Int {
        return datas.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}