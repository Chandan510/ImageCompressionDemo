package com.era.imagecompressiondemo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedImage: Bitmap
    private lateinit var resizedBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        selectImg.setOnClickListener {
            getImageFromGallery()
        }

        _1080.setOnClickListener {
            if (selectedImage != null) {
                originalImg()
                compressImg(1080, 720)
            }
        }
        _720.setOnClickListener {
            if (selectedImage != null) {
                originalImg()
                compressImg(720, 360)
            }
        }
        _480.setOnClickListener {
            if (selectedImage != null) {
                originalImg()
                compressImg(480, 120)
            }
        }


    }

    private fun compressImg(targetWidth: Int, targetHeight: Int) {


        val bitMap = selectedImage


        val outputStream = ByteArrayOutputStream()

        val originalWidth = bitMap.width
        val originalHeight = bitMap.height

        try {

            Log.e("@@", "${originalWidth / targetWidth} -- ${originalHeight / targetHeight}")
            val scaleFactor = Math.min(originalWidth / targetWidth, originalHeight / targetHeight)

//        val resizedBitmap = Bitmap.createScaledBitmap(bitMap, 1080, 720, false)


            if (originalWidth != 0 && originalHeight != 0 && scaleFactor != 0 && scaleFactor != 1) {
                resizedBitmap = Bitmap.createScaledBitmap(
                    bitMap,
                    originalWidth / scaleFactor,
                    originalHeight / scaleFactor,
                    false
                )
            } else if (scaleFactor == 0 && scaleFactor != 1) {
                resizedBitmap = Bitmap.createScaledBitmap(
                    bitMap,
                    originalWidth/1,
                    originalHeight/1,
                    false
                )
            } else if (originalWidth==originalHeight && targetWidth == 1080) {
                resizedBitmap = Bitmap.createScaledBitmap(
                    bitMap,
                    1080,
                    1080,
                    false
                )
            } else if (originalWidth==originalHeight && targetWidth == 720) {
                resizedBitmap = Bitmap.createScaledBitmap(
                    bitMap,
                    720,
                    720,
                    false
                )
            } else if (originalWidth==originalHeight && targetWidth == 480) {
                resizedBitmap = Bitmap.createScaledBitmap(
                    bitMap,
                    480,
                    480,
                    false
                )
            }
            else if (scaleFactor==1){
                if(originalWidth==1080 || originalWidth==720 || originalWidth==480){
                    Log.e("@@@", "Here is it!")
                    resizedBitmap = Bitmap.createScaledBitmap(
                        bitMap,
                        originalWidth/(originalHeight/targetWidth),
                        originalHeight/(originalHeight/targetWidth),
                        false
                    )
                }
                else{
                    Log.e("@@@", "Here it is in else block!")
                    resizedBitmap = Bitmap.createScaledBitmap(
                        bitMap,
                        (originalWidth)/(originalHeight/targetHeight),
                        (originalHeight)/(originalHeight/targetHeight),
                        false
                    )
                }

            }
            else {
                resizedBitmap = bitMap
            }

            if (resizedBitmap != null) {
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            }


        } catch (e: IOException) {
            Toast.makeText(this, "Error:${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }


        val compressedImage = outputStream.toByteArray()

        removeMetaData(compressedImage)

        val bmp = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size)


        compressedImageView.setImageBitmap(bmp)

        compressedSize.text = "Compress: ${(compressedImage.size.toInt() / 1024)} KB"

        compressedResolution.text = "Compress : ${bmp.width} X ${bmp.height}"

        // For exporting compressed image to internal storage
//        val f = File(externalCacheDir?.absoluteFile.toString() + File.separator + "compressedImageWW" + System.currentTimeMillis()/1000 +".jpg")
//        val fo = FileOutputStream(f)
//        fo.write(compressedImage)
//        fo.close()
//        Toast.makeText(this@MainActivity, "File saved successfully:$f", Toast.LENGTH_SHORT).show()

    }

    private fun originalImg() {



        try{
            val bitMap = selectedImage

            val outputStream = ByteArrayOutputStream()
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            val originalImage = outputStream.toByteArray()

            originalSize.text = "Original: ${(originalImage.size.toInt() / 1024)} KB"

            originalResolution.text = "Original : ${bitMap.width} X ${bitMap.height}"

        }catch (e:IOException){
            e.printStackTrace()
        }




    }

    private fun removeMetaData(bitmap: ByteArray) {
        try {
            val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(ByteArrayInputStream(bitmap))
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            exif.setAttribute(ExifInterface.TAG_DATETIME, "")
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "")
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "")
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "")
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "")
            exif.setAttribute(ExifInterface.TAG_MAKE, "")
            exif.setAttribute(ExifInterface.TAG_MODEL, "")
            exif.setAttribute(ExifInterface.TAG_FLASH, "")
            exif.saveAttributes()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            val imageStream = contentResolver.openInputStream(imageUri!!)
            selectedImage = BitmapFactory.decodeStream(imageStream)
            selectImg.setImageBitmap(selectedImage)
        }
    }

}