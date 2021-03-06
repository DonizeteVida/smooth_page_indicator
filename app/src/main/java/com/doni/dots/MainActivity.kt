package com.doni.dots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        findViewById<FillDots>(R.id.fillDots).currentOffset = position + positionOffset
        findViewById<SlideDots>(R.id.slideDots).currentOffset = position + positionOffset
        findViewById<WormDots>(R.id.wormDots).currentOffset = position + positionOffset
        findViewById<ExpandingDots>(R.id.expandingDots).currentOffset = position + positionOffset
        findViewById<WormDots>(R.id.wormDots2).currentOffset = position + positionOffset
        findViewById<ExpandingDots>(R.id.expandingDots2).currentOffset = position + positionOffset
        findViewById<SwapDots>(R.id.swapDots).currentOffset = position + positionOffset
    }

    override fun onPageSelected(position: Int) = Unit
}

class ViewPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = BlankFragment()

    override fun getCount() = 5
}

