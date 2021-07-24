package com.example.pomodoro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundService: Service() {

    private var isServiceStarted = false //флаг, определяет запущен ли сервис или нет, чтобы не стартовать повторно.
    private var notificationManager: NotificationManager? = null //мы будем обращаться к NotificationManager, когда нам нужно показать нотификацию или обновить её состояние.
    private var job: Job? = null //тут будет хранится Job нашей корутины, в которой мы запускаем обновление секундомера в нотификации.




    /*Notification Builder понадобиться нам всякий раз когда мы будем обновлять нотификацию, но некоторые значения Builder остаются неизменными.
    Поэтому мы создаем Builder при первом обращении к нему с этими параметрами. Теперь при каждом повторном обращении к builder
    он вернет нам готовую реализацию.*/
    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent()) //при нажатии на нотификацию мы будем возвращаться в MainActivity.
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
    }

    override fun onCreate() { //создаём экземпляр NotificationManager
        super.onCreate()
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    // обрабатываем Intent. Этот метод вызывается когда сервис запускается. Мы будем передавать параметры для запуска и остановки сервиса через Intent.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT

    }

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    // получаем данные из Intent и определяем что делаем дальше: стартуем или останавливаем сервис.
    private fun processCommand(intent: Intent?) {
        when(intent?.extras?.getString(COMMAND_ID)?: INVALID){
            COMMAND_START -> {
                //val startTime = intent?.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return

                val startTime = intent?.extras?.getLong(TIME_SYSTEM) ?: return
                val runningTimer = intent.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return

                commandStart(startTime,runningTimer) // Если получили команду на старт сервиса
            }
            COMMAND_STOP -> commandStop() //останавливаем обновление секундомера job?.cancel()
            INVALID -> return
        }
    }

    private fun commandStart(startTime: Long, countdown: Long) {
        if (isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStart()")
        try {

            moveToStartedState() //старт сервиса

            startForegroundAndShowNotification() // создаем канал, если API >= Android O. Создаем нотификацию и вызываем startForeground()

            /*продолжаем отсчитывать секундомер. Тут мы запускаем корутину, которую кэнсельнем, когда сервис будет стопаться.
            В корутине каждую секунду обновляем нотификацию. И как уже было сказано, обновлять чаще будет проблематично.*/
            continueTimer(startTime, countdown)
        } finally {
            isServiceStarted = true
        }
    }

    //останавливаем обновление секундомера job?.cancel()
    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStop()")
        try {
            job?.cancel()

            stopForeground(true)//убираем сервис из форегроунд стейта stopForeground(true)
            stopSelf()// останавливаем сервис stopSelf()
        } finally {
            isServiceStarted = false
        }
    }


    private fun continueTimer(startTime: Long, runningTimer: Long) {


        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                val interval = (System.currentTimeMillis() - startTime)

                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(
                    (runningTimer - interval).displayTime()   //.dropLast(6)




                    )
                )
                delay(INTERVAL)
            }
        }
    }

    private fun moveToStartedState() {
        //вызываем startForegroundService() или startService() в зависимости от текущего API.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            Log.d("TAG", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(NOTIFICATION_ID, notification)
    }


    private fun getNotification(content: String) = builder.setContentText(content).build()


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }


    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    /*private companion object {
        private const val CHANNEL_ID = "Channel_ID"
        private const val NOTIFICATION_ID = 777
        private const val INTERVAL = 1000L
    }*/
}