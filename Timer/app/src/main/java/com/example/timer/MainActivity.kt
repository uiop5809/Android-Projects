package com.example.timer

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import com.example.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var currentCountDownTimer: CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        bindViews()
        initSounds() // 사운드 로드
    }

    override fun onResume() { // 다시 돌아왔을 경우
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() { // 화면에 안 보일 경우
        super.onPause()
        soundPool.autoPause() // 모든 활성화된 소리 멈춤
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // 사운드 사용하지 않을 때 메모리 해제
    }
    
    private fun bindViews() {
        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if(p2) { // 사용자가 실제로 건드렸을 때만 업데이트
                        updateRemainTimes(p1 * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    binding.seekBar ?: return

                    if(binding.seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }
            }
        )
    }

    private fun initSounds() {
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object: CountDownTimer(initialMillis, 1000L) {
            override fun onTick(p0: Long) {
                updateRemainTimes(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(binding.seekBar.progress * 60 * 1000L)
        // 바로 위에 생성해도 코드 사이에 변화가 생길 수 있기 때문에 nullable
        currentCountDownTimer?.start()
        // 인자가 nullable 할 경우, null 이 아닌 경우에만 let 으로
        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun completeCountDown() {
        updateRemainTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    private fun updateRemainTimes(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        binding.remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        binding.remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        binding.seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}