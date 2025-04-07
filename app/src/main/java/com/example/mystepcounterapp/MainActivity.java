package com.example.mystepcounterapp; // <-- ЗАМЕНИ НА ТВОЙ ПАКЕТ

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest; // Важно!
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Для показа сообщений пользователю

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnStartService;
    private Button btnStopService;
    private TextView tvSteps;

    // Приемник для получения данных от сервиса
    private BroadcastReceiver stepUpdateReceiver;

    // Код запроса разрешения
    private static final int ACTIVITY_RECOGNITION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Убедись, что layout называется activity_main.xml

        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        tvSteps = findViewById(R.id.tvSteps);

        // Устанавливаем обработчики нажатий
        btnStartService.setOnClickListener(v -> tryStartStepService()); // Измененный вызов
        btnStopService.setOnClickListener(v -> stopStepService());

        // Инициализация приемника для сообщений от сервиса
        stepUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && StepService.ACTION_STEP_UPDATE.equals(intent.getAction())) {
                    int steps = intent.getIntExtra(StepService.EXTRA_STEP_COUNT, 0);
                    Log.d(TAG, "Received step update via Broadcast: " + steps);
                    // Обновляем TextView на UI потоке
                    tvSteps.setText(String.format(Locale.getDefault(), "Шагов: %d", steps));
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Регистрируем приемник при возобновлении активности
        LocalBroadcastManager.getInstance(this).registerReceiver(
                stepUpdateReceiver,
                new IntentFilter(StepService.ACTION_STEP_UPDATE) // Используем тот же action, что и в сервисе
        );
        Log.d(TAG, "BroadcastReceiver registered");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Отписываемся от приемника при сворачивании активности, чтобы не было утечек
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepUpdateReceiver);
        Log.d(TAG, "BroadcastReceiver unregistered");
    }

    /**
     * Пытается запустить сервис подсчета шагов, проверяя разрешения.
     */
    private void tryStartStepService() {
        // Разрешение ACTIVITY_RECOGNITION требуется начиная с Android 10 (API 29)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Проверяем, есть ли уже разрешение
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Разрешение есть, запускаем сервис
                Log.d(TAG, "ACTIVITY_RECOGNITION permission already granted.");
                startStepServiceActual();
            } else {
                // Разрешения нет. Проверяем, нужно ли показывать объяснение.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                    // Показываем объяснение пользователю (например, в диалоге)
                    // В этом примере просто выводим Toast и запрашиваем снова
                    Log.w(TAG, "Showing rationale for ACTIVITY_RECOGNITION permission.");
                    Toast.makeText(this, "Разрешение на физическую активность нужно для подсчета шагов", Toast.LENGTH_LONG).show();
                    // После показа объяснения, можно снова запросить разрешение
                    requestActivityRecognitionPermission();
                } else {
                    // Объяснение не требуется (первый запрос или пользователь выбрал "Don't ask again")
                    // Просто запрашиваем разрешение
                    Log.d(TAG, "Requesting ACTIVITY_RECOGNITION permission...");
                    requestActivityRecognitionPermission();
                }
            }
        } else {
            // Для версий Android ниже 10 разрешение ACTIVITY_RECOGNITION не требуется, запускаем сразу
            Log.d(TAG, "No need to request ACTIVITY_RECOGNITION permission (API < 29).");
            startStepServiceActual();
        }
    }

    /**
     * Выполняет фактический запрос разрешения ACTIVITY_RECOGNITION.
     */
    private void requestActivityRecognitionPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                ACTIVITY_RECOGNITION_PERMISSION_CODE
        );
    }


    /**
     * Метод, который фактически запускает сервис (вызывается после получения разрешений).
     */
    private void startStepServiceActual() {
        // Дополнительно проверим разрешение на уведомления для Android 13+
        // Хотя сервис сам запросит его при startForeground, если нет,
        // лучше запросить заранее для более гладкого UX.
        // Эту проверку можно добавить сюда или оставить как есть.

        Log.d(TAG, "Starting StepService...");
        Intent serviceIntent = new Intent(this, StepService.class);
        // Используем startForegroundService для Android 8.0 (API 26) и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    /**
     * Останавливает сервис подсчета шагов.
     */
    private void stopStepService() {
        Log.d(TAG, "Stopping StepService...");
        Intent serviceIntent = new Intent(this, StepService.class);
        stopService(serviceIntent);
        // Можно сбросить текст шагов здесь при остановке
        // tvSteps.setText(R.string.steps_initial);
    }

    /**
     * Обработка результата запроса разрешений от пользователя.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_CODE) {
            // Проверяем результат запроса на ACTIVITY_RECOGNITION
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Пользователь предоставил разрешение
                Log.d(TAG, "ACTIVITY_RECOGNITION permission granted by user.");
                // Теперь можно безопасно запускать сервис
                startStepServiceActual();
            } else {
                // Пользователь отклонил разрешение
                Log.w(TAG, "ACTIVITY_RECOGNITION permission denied by user.");
                // Показываем сообщение, что функция недоступна без разрешения
                Toast.makeText(this, "Без разрешения подсчет шагов невозможен", Toast.LENGTH_LONG).show();
                // Можно предложить пользователю перейти в настройки приложения,
                // если он выбрал "Don't ask again".
            }
        }
        // Здесь можно добавить обработку других кодов запросов разрешений, если они будут
    }
}