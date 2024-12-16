package com.kfnfnjlvdrngjrjkn.myshop.Users;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfnfnjlvdrngjrjkn.myshop.Adapter.TrainerAdapter;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Trainer;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TrainerAdapter trainerAdapter;
    private List<Trainer> trainerList;

    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewConfirmation;
    private Button buttonSelectDate;
    private Button buttonSelectTime;
    private Button buttonBookTraining;

    private String selectedDate;
    private String selectedTime;
    private Trainer selectedTrainer;
    private String currentUserId; // ID текущего пользователя
    private String currentTrainingId; // ID текущей тренировки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        recyclerView = findViewById(R.id.recycler_view_trainers);
        trainerList = new ArrayList<>();

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainerAdapter = new TrainerAdapter(trainerList, this::onTrainerSelected);
        recyclerView.setAdapter(trainerAdapter);

        loadTrainers(); // Загрузка списка тренеров


        textViewDate = findViewById(R.id.text_view_date);
        textViewTime = findViewById(R.id.text_view_time);
        textViewConfirmation = findViewById(R.id.text_view_confirmation);
        buttonSelectDate = findViewById(R.id.button_select_date);
        buttonSelectTime = findViewById(R.id.button_select_time);
        buttonBookTraining = findViewById(R.id.button_book_training);


        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        buttonSelectTime.setOnClickListener(v -> showTimePickerDialog());
        buttonBookTraining.setOnClickListener(v -> {
            if (selectedTrainer != null && selectedDate != null && selectedTime != null) {
                bookOrUpdateTraining();
            } else {
                Toast.makeText(this, "Пожалуйста, выберите тренера, дату и время", Toast.LENGTH_SHORT).show();
            }
        });

        loadUserTrainingInfo(); // Загрузка информации о текущей тренировке пользователя
    }

    private void loadTrainers() {
        DatabaseReference trainersRef = FirebaseDatabase.getInstance().getReference("Trainers");
        trainersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trainerList.clear(); // Очистка списка перед загрузкой новых данных
                for (DataSnapshot trainerSnapshot : snapshot.getChildren()) {
                    Trainer trainer = trainerSnapshot.getValue(Trainer.class);
                    if (trainer != null) {
                        trainerList.add(trainer); // Добавление тренера в список
                        Log.d("TrainingActivity", "Тренер добавлен: " + trainer.getName());
                    } else {
                        Log.d("TrainingActivity", "Тренер равен null");
                    }
                }
                trainerAdapter.notifyDataSetChanged(); // Уведомление адаптера об изменении данных
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TrainingActivity.this, "Ошибка загрузки тренеров", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onTrainerSelected(Trainer trainer) {
        selectedTrainer = trainer; // Сохранение выбранного тренера
        if (selectedTrainer != null) {
            Toast.makeText(this, "Вы выбрали тренера: " + selectedTrainer.getName(), Toast.LENGTH_SHORT).show();
            updateTrainingInfo(); // Обновление информации о тренировке
        } else {
            Toast.makeText(this, "Ошибка: тренер не выбран", Toast.LENGTH_SHORT).show();
        }
    }

    private void bookOrUpdateTraining() {
        Users currentUser = Prevalent.getCurrentOnlineUser();
        currentUserId = currentUser.getPhone();

        DatabaseReference trainingRef = FirebaseDatabase.getInstance().getReference("Trainings").child(currentUserId);

        // Проверка, есть ли уже запись
        trainingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Если запись существует, удаляем старую запись
                    for (DataSnapshot trainingSnapshot : snapshot.getChildren()) {
                        trainingSnapshot.getRef().removeValue();
                    }
                }

                // Создание новой записи
                HashMap<String, Object> trainingInfo = new HashMap<>();
                currentTrainingId = trainingRef.push().getKey();
                trainingInfo.put("trainerId", selectedTrainer.getTrainerId());
                trainingInfo.put("trainingDate", selectedDate);
                trainingInfo.put("trainingTime", selectedTime);
                trainingInfo.put("bookingConfirmation", "Вы записаны на тренировку к " + selectedTrainer.getName() + " в " + selectedTime + " на " + selectedDate);

                // Запись информации о тренировке в базу данных
                trainingRef.child(currentTrainingId).setValue(trainingInfo).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(TrainingActivity.this, "Записались на тренировку с " + selectedTrainer.getName(), Toast.LENGTH_SHORT).show();
                        updateTrainingInfo(); // Обновление информации о тренировке
                    } else {
                        Toast.makeText(TrainingActivity.this, "Ошибка записи. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TrainingActivity.this, "Ошибка проверки записи", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTrainingInfo() {
        if (selectedTrainer != null && selectedDate != null && selectedTime != null) {
            // Формирование сообщения о подтверждении записи
            String confirmationMessage = "Вы записаны на тренировку к " + selectedTrainer.getName() + " в " + selectedTime + " на " + selectedDate;
            textViewConfirmation.setText(confirmationMessage);
            textViewConfirmation.setVisibility(View.VISIBLE);
        } else {
            Log.d("TrainingActivity", "selectedTrainer равен null");
        }
    }

    private void loadUserTrainingInfo() {//загружаем информацию о текущих тренировках пользователя из бд
        Users currentUser = Prevalent.getCurrentOnlineUser();
        if (currentUser != null) {
            currentUserId = currentUser.getPhone();
            DatabaseReference trainingRef = FirebaseDatabase.getInstance().getReference("Trainings").child(currentUserId);
            trainingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Загружаем данные о тренировке
                        for (DataSnapshot trainingSnapshot : snapshot.getChildren()) {
                            selectedDate = trainingSnapshot.child("trainingDate").getValue(String.class);
                            selectedTime = trainingSnapshot.child("trainingTime").getValue(String.class);
                            String trainerId = trainingSnapshot.child("trainerId").getValue(String.class);

                            // Обновление полей с информацией о тренировке
                            textViewDate.setText(selectedDate);
                            textViewTime.setText(selectedTime);
                            textViewConfirmation.setText("Вы записаны на тренировку к тренеру с ID: " + trainerId + " в " + selectedTime + " на " + selectedDate);
                            textViewConfirmation.setVisibility(View.VISIBLE); // Отображение сообщения
                        }
                    } else {
                        Log.d("TrainingActivity", "Нет записей о тренировках");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TrainingActivity.this, "Ошибка загрузки информации о тренировках", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Диалог выбора даты
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    textViewDate.setText(selectedDate);
                    updateTrainingInfo();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    textViewTime.setText(selectedTime);
                    updateTrainingInfo();
                }, hour, minute, true);
        timePickerDialog.show();
    }
}