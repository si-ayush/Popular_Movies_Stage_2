package com.example.android.popularmovies.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.android.popularmovies.api_manage.MovieService;
import com.example.android.popularmovies.api_manage.Movie_api;
import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.MovieResponse_first;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectRepository {
    private static ProjectRepository projectRepository;
    private final String VIDEOS = "videos,reviews,credits";
    private MovieService retrofit_interface;
    private String API_KEY = "369edc300248a29c50ce44c3bd21c220";
    private LiveData<List<MovieDetails>> moviesDB;
    private MutableLiveData<List<Movie>>movie;
    private MutableLiveData<MovieDetails> mMovieDetails;

    private ProjectRepository() {

        retrofit_interface = Movie_api.getClient().create(MovieService.class);
        mMovieDetails = new MutableLiveData<>();
        movie = new MutableLiveData<>();

    }

    public synchronized static ProjectRepository getInstance() {

        if (projectRepository == null) {
            Log.d("TAG", "Created NEW ProjectRepositpry");
            projectRepository = new ProjectRepository();
        }

        return projectRepository;
    }

    public LiveData<List<MovieDetails>> getAllMoviesFromDatabase(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        Log.d("TAG", "Actively retriving database in MainViewmodel");
        moviesDB = database.FavMoviesDao().loadAllMovies();
        return moviesDB;
    }

    public LiveData<MovieDetails> getMoviesByIdFromDb(AppDatabase appDatabase, int movieId) {

        return appDatabase.FavMoviesDao().loadMovieByID(movieId);
    }

    public LiveData<List<Movie>> getAllMoviesList(int page, int category) {

        Call<MovieResponse_first> call;

        Log.d("TAG", "RESPONSE DONE page" + page);
        Log.d("TAG", "RESPONSE DONE category" + category);
        if (category == 1) {
            call = retrofit_interface.getTopRatedMovies(API_KEY, page);
        } else {
            call = retrofit_interface.getPopularMovies(API_KEY, page);
        }
        call.enqueue(new Callback<MovieResponse_first>() {
            @Override
            public void onResponse(Call<MovieResponse_first> call, Response<MovieResponse_first> response) {
                movie.setValue(response.body().getReults());
                Log.d("TAG", "RESPONSE DONE");
            }

            @Override
            public void onFailure(Call<MovieResponse_first> call, Throwable t) {

            }
        });
        Log.d("TAG", "RESPONSE DONE end");
        return movie;
    }

    public LiveData<MovieDetails> getMovieDetails(int movieId) {
        Call<MovieDetails> call = retrofit_interface.getMovieDetails(movieId, API_KEY, VIDEOS);
        call.enqueue((new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful()) {

                    mMovieDetails.setValue(response.body());


                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                mMovieDetails.setValue(null);
            }
        }));
        return mMovieDetails;

    }

}


