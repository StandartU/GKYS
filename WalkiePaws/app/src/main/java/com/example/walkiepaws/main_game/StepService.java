package com.example.walkiepaws.main_game;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager; // Для отправки данных в Activity

import com.example.walkiepaws.R;
import com.example.walkiepaws.backend.ApiService;
import com.example.walkiepaws.backend.RetrofitClient;
import com.example.walkiepaws.backend.model.dto.request.UserAddCashDTO;

import java.util.LinkedList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepService extends Service implements SensorEventListener {

    // --- Константы и Теги ---
    private ApiService apiService;
    private static final String TAG = "StepServiceCustom";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "StepServiceCustomChannel";

    // --- Константы для Broadcast ---
    public static final String ACTION_STEP_UPDATE = "com.yourcompany.mystepcounterapp.STEP_UPDATE"; // <-- ЗАМЕНИ НА ТВОЙ ПАКЕТ
    public static final String EXTRA_STEP_COUNT = "stepCount";

    // --- Параметры алгоритма (ТРЕБУЮТ НАСТРОЙКИ!) ---
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;
    private static final int SMOOTHING_WINDOW_SIZE = 10;
    private static final float PEAK_THRESHOLD = 10.3f;
    private static final float VALLEY_THRESHOLD = 9.7f;
    private static final float GYRO_THRESHOLD = 1.2f;
    private static final long MIN_STEP_INTERVAL_NS = 200_000_000;
    private static final long MAX_STEP_INTERVAL_NS = 2500_000_000L;

    // --- Сенсоры и менеджер ---
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    // --- Переменные состояния алгоритма ---
    private LinkedList<Float> accelMagnitudeBuffer = new LinkedList<>();
    private float lastSmoothedMagnitude = 0f;
    private long lastStepTimestampNs = 0;
    private int stepCount = 0;
    private boolean isPeakDetectionPhase = true;
    private float lastGyroMagnitude = 0f;

    // --- Для отправки данных ---
    private LocalBroadcastManager broadcaster;


    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        apiService = RetrofitClient.getApiService();
        Log.i(TAG, "onCreate: Initializing Step Service...");
        broadcaster = LocalBroadcastManager.getInstance(this); // Инициализация менеджера бродкастов


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        boolean sensorsInitialized = initializeSensors();

        if (!sensorsInitialized) {
            Log.e(TAG, "onCreate: Failed to initialize critical sensors. Stopping service.");
            stopSelf();
            return;
        }

        resetAlgorithmState();



        startForeground(NOTIFICATION_ID, createNotification("Служба шагомера запущена"));
        Log.i(TAG, "onCreate: Service started in foreground.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand received.");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: Stopping Step Service...");
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "onDestroy: All sensor listeners unregistered.");
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTimestampNs = event.timestamp;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

                accelMagnitudeBuffer.add(magnitude);
                if (accelMagnitudeBuffer.size() > SMOOTHING_WINDOW_SIZE) {
                    accelMagnitudeBuffer.poll();
                }
                float smoothedMagnitude = calculateAverage(accelMagnitudeBuffer);

                if (Float.isNaN(smoothedMagnitude)) return;

                detectStep(smoothedMagnitude, currentTimestampNs);
                lastSmoothedMagnitude = smoothedMagnitude;
                break;

            case Sensor.TYPE_GYROSCOPE:
                float rotX = event.values[0];
                float rotY = event.values[1];
                float rotZ = event.values[2];
                lastGyroMagnitude = (float) Math.sqrt(rotX * rotX + rotY * rotY + rotZ * rotZ);
                break;
        }
    }

    private boolean initializeSensors() {
        if (sensorManager == null) return false;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) return false;
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope == null) Log.w(TAG, "Gyroscope not available");

        boolean accelRegistered = sensorManager.registerListener(this, accelerometer, SENSOR_DELAY);
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SENSOR_DELAY);
        }
        return accelRegistered; // Достаточно акселерометра для базовой работы
    }

    /**
     * Основной алгоритм обнаружения шагов на основе анализа пиков и впадин
     * сглаженного сигнала акселерометра. Включает проверку гироскопа.
     *
     * @param smoothedMagnitude Сглаженное значение магнитуды ускорения.
     * @param currentTimestampNs Текущая временная метка события сенсора.
     */
    private void detectStep(float smoothedMagnitude, long currentTimestampNs) {

        if (isPeakDetectionPhase) {
            // --- Фаза 1: Ищем ПИК (превышение верхнего порога) ---
            if (smoothedMagnitude > PEAK_THRESHOLD) {
                isPeakDetectionPhase = false; // <-- Меняем фазу
            }
        } else {
            // --- Фаза 2: Ищем ВПАДИНУ (падение ниже нижнего порога) ---
            if (smoothedMagnitude < VALLEY_THRESHOLD) {
                long timeSinceLastStepNs = currentTimestampNs - lastStepTimestampNs;

                // Проверка временного интервала (ИГНОРИРУЕМ ДЛЯ ПЕРВОГО ШАГА!)
                boolean timeIntervalOk = (lastStepTimestampNs == 0) || // Первый шаг всегда проходит временную проверку
                        (timeSinceLastStepNs >= MIN_STEP_INTERVAL_NS &&
                                timeSinceLastStepNs <= MAX_STEP_INTERVAL_NS);

                // Проверка активности гироскопа (ключ к фильтрации тряски)
                boolean gyroActivityOk = (gyroscope == null || lastGyroMagnitude > GYRO_THRESHOLD);

                // --- Принятие решения ---
                if (timeIntervalOk && gyroActivityOk) {
                    // Все проверки пройдены - засчитываем ШАГ!
                    stepCount++;
                    lastStepTimestampNs = currentTimestampNs; // !!! ОБНОВЛЯЕМ ВРЕМЯ !!!

                    updateNotification("Шагов: " + stepCount);
                    sendStepUpdate(stepCount);

                } else {
                    // Шаг отфильтрован, логируем причину
                    String reason = "";
                    // Проверяем причину фильтрации - ВРЕМЯ
                    if (lastStepTimestampNs != 0 && !timeIntervalOk) {
                        reason += String.format(Locale.US,"Time Interval: %dms", timeSinceLastStepNs / 1_000_000);
                        // !!! СБРАСЫВАЕМ ВРЕМЯ, ЧТОБЫ СЛЕДУЮЩИЙ ШАГ НЕ СРАВНИВАЛСЯ СО СТАРЫМ !!!
                        lastStepTimestampNs = 0;
                    }
                    // Проверяем причину фильтрации - ГИРОСКОП
                    if (!gyroActivityOk) {
                        reason += (reason.isEmpty() ? "" : " | ") + String.format(Locale.US,"Gyro Low: %.2f", lastGyroMagnitude);
                        // Здесь НЕ сбрасываем время, т.к. это может быть просто короткая тряска
                    }
                }
                // !!! ВОЗВРАТ В ФАЗУ ПИКА !!!
                isPeakDetectionPhase = true;
                if (stepCount % 25 == 0) {
                    apiService.addCash(
                            ((App) getApplication().getApplicationContext()).getToken(),
                            new UserAddCashDTO(stepCount, true)
                    ).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) { stepCount = 0; }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                    });
                }
            }
        }
    }

    private float calculateAverage(LinkedList<Float> buffer) {
        if (buffer == null || buffer.isEmpty()) return Float.NaN;
        float sum = 0; for (float val : buffer) { sum += val; }
        return sum / buffer.size();
    }

    private void resetAlgorithmState() {
        stepCount = 0;
        lastStepTimestampNs = 0;
        accelMagnitudeBuffer.clear();
        lastSmoothedMagnitude = 0f;
        isPeakDetectionPhase = true;
        lastGyroMagnitude = 0f;
        sendStepUpdate(stepCount); // Отправляем 0 при сбросе
    }

    // --- Метод для отправки Broadcast ---
    private void sendStepUpdate(int steps) {
        Intent intent = new Intent(ACTION_STEP_UPDATE);
        intent.putExtra(EXTRA_STEP_COUNT, steps);
        broadcaster.sendBroadcast(intent);
    }


    // --- Методы для Foreground Service (createNotification, updateNotification, createNotificationChannel) ---
    private Notification createNotification(String text) {
        createNotificationChannel();
        // !!! ВАЖНО: Замени MainActivity.class, если твоя активность называется иначе !!!
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Шагомер Активен")
                .setContentText(text)
                // !!! ВАЖНО: Убедись, что иконка @mipmap/ic_launcher существует или замени ее !!!
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void updateNotification(String text) {
        Notification notification = createNotification(text);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Канал Службы Шагомера";
            String description = "Уведомления от фоновой службы шагомера";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
