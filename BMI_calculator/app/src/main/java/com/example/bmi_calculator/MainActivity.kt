package com.example.bmi_calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // R이 주소값
        setContentView(R.layout.activity_main)
        val heightEditText: EditText = findViewById(R.id.heightEditText) // type 명시
        val weightEditText = findViewById<EditText>(R.id.weightEditText) // findViewById 반환하면서 type 추론

        val resultButton = findViewById<Button>(R.id.resultButton)

        resultButton.setOnClickListener {
            // 비어있으면 앱이 종료되기 때문에 예외처리
            if(heightEditText.text.isEmpty() || weightEditText.text.isEmpty()) {
                Toast.makeText(this, "빈 값이 있습니다", Toast.LENGTH_SHORT)
                // 함수가 중첩되어있기 때문에 어느 함수에서 return 할 것인지 명시
                return@setOnClickListener
            }
            // String 에서 Int 로 바꿔줘야함
            val height: Int = heightEditText.text.toString().toInt()
            val weight: Int = weightEditText.text.toString().toInt()

            val intent = Intent(this, ResultActivity::class.java)

            intent.putExtra("height", height)
            intent.putExtra("weight", weight)

            startActivity(intent)
        }
    }
}