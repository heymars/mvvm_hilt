package com.s.mvvmdemoapp.ui

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.s.mvvmdemoapp.data.DataRepository
import com.s.mvvmdemoapp.model.Content
import com.s.mvvmdemoapp.model.MoviesResponse
import com.s.mvvmdemoapp.utils.Resource
import com.s.mvvmdemoapp.utils.ResourceState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class MovieListingViewModel @ViewModelInject constructor(
     private  val repository: DataRepository
):ViewModel(), LifecycleObserver{
    val movieListLiveData: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    fun getMovies(page:Int) {
        movieListLiveData.postValue(Resource(ResourceState.LOADING, null, null))
        repository.getMovies(page = page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe(object : DisposableObserver<MoviesResponse>() {
                override fun onNext(t: MoviesResponse) {
                    movieListLiveData.postValue(Resource(ResourceState.SUCCESS, t, null))
                }

                override fun onComplete() {

                }

                override fun onError(e: Throwable) {
                    movieListLiveData.postValue(
                        Resource(
                            ResourceState.ERROR,
                            null,
                            e.message
                        )
                    )
                    Timber.d("--------${e.message}----------")
                }
            })
    }
}