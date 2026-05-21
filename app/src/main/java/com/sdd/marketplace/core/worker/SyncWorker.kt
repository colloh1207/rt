package com.sdd.marketplace.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sdd.marketplace.domain.repository.ProductRepository
import com.sdd.marketplace.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("SyncWorker: Starting background sync")
            productRepository.getFeaturedProducts().first()
            userRepository.setOnlineStatus(true)
            Timber.d("SyncWorker: Background sync completed")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker: Background sync failed")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "sdd_background_sync"
    }
}
