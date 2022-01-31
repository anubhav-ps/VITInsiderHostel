package com.anubhav.vitinsiderhostel.appviewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.anubhav.vitinsiderhostel.database.LocalRepository;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;

import java.util.List;


public class AppViewModel extends AndroidViewModel {


    private LocalRepository localRepository;

    // for tenant related data
    private LiveData<List<Tenant>> allTenantData;

    public AppViewModel(@NonNull Application application) {
        super(application);

        localRepository = new LocalRepository(application);

    }

    public void insertCurrentUser(User user){
        localRepository.insertCurrentUser(user);
    }

   public LiveData<List<User>> retrieveAllUsers(){
        return localRepository.retrieveAllUsers();
   }

   public void deleteAllUsers(){
        localRepository.deleteAllUsers();
   }

}
