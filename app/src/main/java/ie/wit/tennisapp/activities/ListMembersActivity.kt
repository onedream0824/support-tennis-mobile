package ie.wit.tennisapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.tennisapp.R
import ie.wit.tennisapp.databinding.ActivityListMembersBinding
import ie.wit.tennisapp.main.MainApp
import ie.wit.tennisapp.adapters.MemberAdapter
import ie.wit.tennisapp.adapters.MembersListener
import ie.wit.tennisapp.models.MemberModel

class ListMembersActivity : AppCompatActivity(), MembersListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityListMembersBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MemberAdapter(app.members.findAll(), this)
        binding.toolbar.title = title

        loadMembers()
        registerRefreshCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onEditMemberClick(member: MemberModel) {
        val launcherIntent = Intent(this, RegisterActivity::class.java)
        launcherIntent.putExtra("member_edit", member)
        refreshIntentLauncher.launch(launcherIntent)
    }

    override fun onDeleteMemberClick(member: MemberModel) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this member?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                app.members.delete(member)
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadMembers() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.recyclerView.adapter?.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_view_results -> {
                startActivity(Intent(this, ListResultsActivity::class.java))
                true
            }
            R.id.item_view_members -> {
                startActivity(Intent(this, ListMembersActivity::class.java))
                true
            }
            R.id.item_contact -> {
                startActivity(Intent(this, ContactActivity::class.java))
                true
            }
            R.id.item_logout -> {
                startActivity(Intent(this, WelcomeActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadMembers() {
        showMembers(app.members.findAll())
    }

    private fun showMembers(members: List<MemberModel>) {
        binding.recyclerView.adapter = MemberAdapter(members, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}