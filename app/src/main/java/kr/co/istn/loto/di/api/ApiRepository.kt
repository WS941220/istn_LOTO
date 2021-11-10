package kr.co.istn.loto.di.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kr.co.istn.loto.di.Result
import kr.co.istn.loto.di.model.TEST


class ApiRepository @Inject constructor(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ApiInterface {
    companion object {
        const val DATA_NOT_FOUND = "DATA_NOT_FOUND"
    }

    override suspend fun getTest(test: String): Result<String> = withContext(ioDispatcher) {
        try {
            val sTest = apiService.getTest(test)
            if(sTest == "")
                return@withContext Result.Success(sTest)
            else
                return@withContext Result.Error(Exception(DATA_NOT_FOUND))
        }  catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

}