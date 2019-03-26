package id.airmas.airmas


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var toolbar: ActionBar? = null
    private var content: FrameLayout? = null
    private var slideUp: SlideUp? = null
    private var dim: View? = null
    private var slideView: View? = null
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId){
            R.id.navigation_edukasi -> {
                val fragment = EdukasiFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_informasi -> {
                val fragment = InformasiFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_promo -> {
                val fragment = PromoFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun addFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.frame_container, fragment, fragment.javaClass.simpleName)
            .commit()
    }
    private fun FragmentAnimateUp(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations( R.animator.slide_up, 0, 0, R.animator.slide_down)
            .replace(R.id.frame_container, fragment, fragment.javaClass.simpleName)
            .commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fragment = HomeOrderFragment()
        addFragment(fragment)
        val btnUp : Button = btn_up
        btnUp.setOnClickListener {
           FragmentAnimateUp(ProfileFragment())
        }



    }
}
