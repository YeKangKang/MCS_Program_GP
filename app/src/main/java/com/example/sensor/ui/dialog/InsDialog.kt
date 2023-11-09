package com.example.sensor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.sensor.R
import com.example.sensor.databinding.DialogInsBinding
import com.example.sensor.persistence.ins.UserIns
import java.util.Date

class InsDialog( context: Context, private val userIns: UserIns): Dialog(context) {

    private val viewBinding: DialogInsBinding by lazy {
        DialogInsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        val windowManager = window?.windowManager
        val disPlay = windowManager?.getDefaultDisplay()
        val attr = window?.attributes
        val size = Point()
        disPlay?.getSize(size)
        attr?.width = (((size.x)*0.75).toInt())     //设置为屏幕的0.7倍宽度
        window?.attributes = attr

        viewBinding.insUserName.text = userIns.userName
        viewBinding.insContent.text = userIns.content
        viewBinding.insTime.text = "Published in: ${userIns.date}"
        val bitmap = BitmapFactory.decodeFile(userIns.imagePath)
        viewBinding.insImg.setImageBitmap(bitmap)
        viewBinding.insUserAvatar.setAnimation(userIns.userAvatar)
        viewBinding.insUserAvatar.playAnimation()
    }
}