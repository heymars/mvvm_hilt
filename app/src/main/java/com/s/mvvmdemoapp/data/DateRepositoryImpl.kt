package com.s.mvvmdemoapp.data

import android.content.Context
import com.google.gson.Gson
import com.s.mvvmdemoapp.model.MoviesResponse
import com.s.mvvmdemoapp.utils.JsonHelper
import io.reactivex.rxjava3.core.Observable

class DateRepositoryImpl(private val context: Context) : DataRepository {
    override fun getMovies(page: Int): Observable<MoviesResponse> {
        val fileName = "CONTENTLISTINGPAGE-PAGE${page+1}.json"
        val jsonFileString: String? =
            JsonHelper.getJsonFromAssets(context, fileName)
        val gSon = Gson()
        var moviesResponse: MoviesResponse? = null
        if (jsonFileString != null) {
            moviesResponse = gSon.fromJson(jsonFileString, MoviesResponse::class.java)
        }
        return Observable.just(moviesResponse)
    }

}