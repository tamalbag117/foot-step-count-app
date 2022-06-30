package com.example.wear.tiles

import kotlinx.coroutines.delay
import kotlin.random.Random

data class GoalProgress(
    val current: Int,
    val goal: Int
) {
    val percentage get() = current.toFloat() / goal.toFloat()
}

/**
 * A fake repo
 */
object GoalsRepository {
    suspend fun getGoalProgress(): GoalProgress {
        delay(200)
        return goalProgress.copy(current = Random.nextInt(8000))
    }
}

val goalProgress = GoalProgress(current = 0, goal = 8000)

