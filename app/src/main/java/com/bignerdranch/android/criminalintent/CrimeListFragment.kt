package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.CrimePagerActivity.Companion.newIntent
import java.util.*

class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null
    private var subtitleVisible: Boolean = false

    companion object {
        private const val REQUEST_CRIME: Int = 1
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
        val subtitleItem = menu.findItem(R.id.show_subtitle)
        subtitleItem.setTitle(if (subtitleVisible) R.string.hide_subtitle else R.string.show_subtitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime(false)
                CrimeLab.addCrime(crime)
                val intent = activity?.let { newIntent(it, crime.id) }
                startActivity(intent)
                true
            }
            R.id.show_subtitle -> {
                subtitleVisible = !subtitleVisible
                activity?.invalidateOptionsMenu()
                updateSubtitle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSubtitle() {
        val crimeCount = CrimeLab.crimes.size
        val subtitle = if (subtitleVisible) getString(R.string.subtitle_format, crimeCount) else null
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.subtitle = subtitle
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }
        updateUI()
        return view
    }

    private fun updateUI() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        } else {
            adapter = CrimeAdapter(CrimeLab.crimes)
            crimeRecyclerView.adapter = adapter
        }
        updateSubtitle()
    }

    private abstract inner class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }


        protected var crime = Crime(false)
        protected val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        protected val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        abstract fun bind(crime: Crime)
        override fun onClick(v: View?) {
            val intent = activity?.let {
                newIntent(
                    it, crime.id
                )
            }
            startActivityForResult(intent, REQUEST_CRIME)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CRIME) {
            if (resultCode == Activity.RESULT_OK)
                Toast.makeText(activity, "Element was changed", Toast.LENGTH_SHORT).show()
//            adapter.notifyItemChanged()
        }
    }

    private inner class LightCrimeHolder(itemView: View) : CrimeHolder(itemView) {
        val solvedImageView: ImageView = itemView.findViewById((R.id.crime_solved))
        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }
    }

    private inner class HeavyCrimeHolder(itemView: View) : CrimeHolder(itemView) {
        val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        val policeButton: ImageButton = itemView.findViewById(R.id.police_img)
        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
//            policeButton.setOnClickListener {
//                try {
//                    val intent = Intent(Intent.ACTION_CALL)
//                    intent.data = Uri.parse("tel:$911")
//                    startActivity(intent)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
        }

    }

    private inner class CrimeAdapter(private val crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        override fun getItemViewType(position: Int): Int {
            val crime = crimes[position]
            return when (crime.requiresPolice) {
                false -> 0
                true -> 1
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val inflater = LayoutInflater.from(activity)
            return when (viewType) {
                0 -> {
                    val view = inflater.inflate(R.layout.list_item_crime, parent, false)
                    LightCrimeHolder(view)
                }
                else -> {
                    val view = inflater.inflate(R.layout.list_item_heavy_crime, parent, false)
                    HeavyCrimeHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

    }
}