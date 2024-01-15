package com.eloix.anikiwi.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eloix.anikiwi.model.StatisticsResponse;
import com.eloix.anikiwi.repositories.UserRepository;

public class StatisticsViewModel extends ViewModel {
    private final MutableLiveData<StatisticsResponse> statisticsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final UserRepository userRepository;

    public StatisticsViewModel() {
        userRepository = UserRepository.getInstance();
    }

    public void getUserStatistics(String userId) {
        loadingLiveData.setValue(true);

        userRepository.getUserStatistics(userId, new UserRepository.OnStatisticsResponseListener() {
            @Override
            public void onSuccess(StatisticsResponse statistics) {
                statisticsLiveData.setValue(statistics);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onError() {
                errorLiveData.setValue("Failed to fetch statistics");
                loadingLiveData.setValue(false);
            }
        });
    }

    public LiveData<StatisticsResponse> getStatistics() {
        return statisticsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
}

