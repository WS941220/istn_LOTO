package kr.co.istn.loto.di.api

import kr.co.istn.loto.di.Result
import kr.co.istn.loto.di.model.TEST

interface ApiInterface {

    suspend fun getTest(tesst: String) : Result<String>

}