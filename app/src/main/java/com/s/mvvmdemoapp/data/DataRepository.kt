package com.s.mvvmdemoapp.data

import com.s.mvvmdemoapp.model.MoviesResponse
import io.reactivex.rxjava3.core.Observable


interface DataRepository {
    fun getMovies(page:Int): Observable<MoviesResponse>
}