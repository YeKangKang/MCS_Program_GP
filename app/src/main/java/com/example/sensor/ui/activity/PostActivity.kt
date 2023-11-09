package com.example.sensor.ui.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.sensor.databinding.ActivityPostBinding
import com.example.sensor.persistence.ins.UserIns
import com.example.sensor.persistence.ins.UserInsDatabase
import com.example.sensor.persistence.user.UsersDatabase
import com.example.sensor.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date

class PostActivity: AppCompatActivity() {

    private val viewBinding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    private val PHOTO_PICKED_WITH_DATA = 1000

    private var photoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = ImageUtils.createImageFile(applicationContext)
        if(photoFile == null) {
            Toast.makeText(this, "Description Failed to create the image file", Toast.LENGTH_SHORT).show()
            return
        }
        photoPath = photoFile.absolutePath
        val photoUri = FileProvider.getUriForFile(applicationContext, "com.example.sensor.provider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        requestLauncher.launch(intent)

        viewBinding.publish.setOnClickListener {
            val content = viewBinding.content.text.trim().toString()
            if(content.isEmpty()) {
                Toast.makeText(this, "Please input ideas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(photoPath.isEmpty()) {
                Toast.makeText(this, "Please take a photo to record it", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("Maizi", "publish --- photoPath = $photoPath")
            runBlocking(Dispatchers.IO) {
                val loginUser = UsersDatabase.getInstance(applicationContext).getLoginUser()
                val timestamp = System.currentTimeMillis()
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val ins = UserIns(loginUser.name, content, photoPath, loginUser.avatar, format.format(Date(timestamp)))
                UserInsDatabase.getInstance(applicationContext).insertUserIns(ins)
                finish()
            }
        }
    }

    private val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(photoPath)
            viewBinding.image.setImageBitmap(bitmap)
        }
    }
}