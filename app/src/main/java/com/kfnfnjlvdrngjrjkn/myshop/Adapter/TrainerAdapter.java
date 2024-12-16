package com.kfnfnjlvdrngjrjkn.myshop.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kfnfnjlvdrngjrjkn.myshop.Model.Trainer;
import com.kfnfnjlvdrngjrjkn.myshop.R;

import java.io.File;
import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {
    private List<Trainer> trainerList;
    private OnTrainerSelectedListener listener; // Слушатель для обработки выбора тренера


    public TrainerAdapter(List<Trainer> trainerList, OnTrainerSelectedListener listener) {
        this.trainerList = trainerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание нового представления для элемента списка
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trainer, parent, false);
        return new ViewHolder(view); // Возврат нового ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trainer trainer = trainerList.get(position);
        holder.trainerName.setText(trainer.getName());
        holder.trainerSpecialty.setText(trainer.getSpecialty());

        // Загрузка изображения тренера
        String imagePath = trainer.getImageUrl();
        File imgFile = new File(imagePath); // Создание объекта File для изображения
        if (imgFile.exists()) {

            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.trainerImage.setImageBitmap(bitmap); // Установка изображения
        } else {

            holder.trainerImage.setImageResource(R.drawable.chinuk_2);
        }


        holder.buttonBookTraining.setOnClickListener(v -> listener.onTrainerSelected(trainer));
    }

    @Override
    public int getItemCount() {
        return trainerList.size(); // Возврат количества элементов в списке
    }

    //  ViewHolder для хранения ссылок на элементы представления
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView trainerName;
        public TextView trainerSpecialty;
        public ImageView trainerImage;
        public Button buttonBookTraining;

        public ViewHolder(View itemView) {
            super(itemView);

            trainerName = itemView.findViewById(R.id.trainer_name);
            trainerSpecialty = itemView.findViewById(R.id.trainer_specialty);
            trainerImage = itemView.findViewById(R.id.trainer_image);
            buttonBookTraining = itemView.findViewById(R.id.button_book_training);
        }
    }

    // Интерфейс для обработки выбора тренера
    public interface OnTrainerSelectedListener {
        void onTrainerSelected(Trainer trainer); // Метод, который будет вызван при выборе тренера
    }
}