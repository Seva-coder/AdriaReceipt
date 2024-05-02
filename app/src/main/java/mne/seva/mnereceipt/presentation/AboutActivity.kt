package mne.seva.mnereceipt.presentation

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val aboutText = getString(R.string.text)

        val formattedText = HtmlCompat.fromHtml(aboutText, HtmlCompat.FROM_HTML_MODE_COMPACT)
        binding.textAbout.movementMethod = LinkMovementMethod.getInstance()
        binding.textAbout.text = formattedText
    }


    // back arrow handling
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}