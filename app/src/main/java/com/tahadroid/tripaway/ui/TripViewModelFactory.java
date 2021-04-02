package com.tahadroid.tripaway.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tahadroid.tripaway.repository.TripRepository;

public class TripViewModelFactory implements ViewModelProvider.Factory {
  private TripRepository tripRepository;

    public TripViewModelFactory(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TripViewModel(tripRepository);
    }
}
