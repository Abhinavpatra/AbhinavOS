package com.example.athleteos.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "workout_log",
    indices = [Index(value = ["weekNumber", "dayNumber"], unique = true)]
)
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekNumber: Int,
    val dayNumber: Int,
    val name: String,
    val estimatedDurationMinutes: Int,
    val isCompleted: Boolean = false,
    val completionPercentage: Int = 0,
    val notes: String? = null,
    val dateCompleted: Long? = null
)

@Entity(
    tableName = "exercise_log",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutLog::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class ExerciseLog(
    @PrimaryKey val id: String, // format: "${workoutId}_${name}"
    val workoutId: Long,
    val name: String,
    val orderIndex: Int,
    val isCompleted: Boolean = false
)

@Entity(
    tableName = "exercise_set_log",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLog::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
)
data class ExerciseSetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val setNumber: Int,
    val targetWeight: Double? = null,
    val targetReps: Int? = null,
    val targetRestSeconds: Int = 90,
    val actualWeight: Double? = null,
    val actualReps: Int? = null,
    val isCompleted: Boolean = false
)

@Entity(
    tableName = "daily_armor_log",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyArmorLog(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val completedItemsJson: String // List of completed item names as JSON string
)

@Entity(tableName = "metric_log")
data class MetricLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val category: String, // BODY, RECOVERY, STRENGTH, ATHLETICISM
    val name: String, // e.g., Weight, Squat
    val value: Double,
    val unit: String
)

@Dao
interface AthleteDao {
    @Query("SELECT * FROM workout_log ORDER BY weekNumber, dayNumber")
    fun getAllWorkouts(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_log WHERE weekNumber = :week ORDER BY dayNumber")
    fun getWorkoutsForWeek(week: Int): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_log WHERE weekNumber = :week AND dayNumber = :day LIMIT 1")
    suspend fun getWorkoutByWeekAndDay(week: Int, day: Int): WorkoutLog?

    @Query("SELECT * FROM workout_log WHERE weekNumber = :week AND dayNumber = :day LIMIT 1")
    fun getWorkoutByWeekAndDayFlow(week: Int, day: Int): Flow<WorkoutLog?>

    @Query("SELECT * FROM workout_log WHERE id = :id LIMIT 1")
    fun getWorkoutByIdFlow(id: Long): Flow<WorkoutLog?>

    @Query("SELECT * FROM workout_log WHERE id = :id LIMIT 1")
    suspend fun getWorkoutById(id: Long): WorkoutLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutLog>): List<Long>

    @Update
    suspend fun updateWorkout(workout: WorkoutLog)

    // Exercises
    @Query("SELECT * FROM exercise_log WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun getExercisesForWorkoutFlow(workoutId: Long): Flow<List<ExerciseLog>>

    @Query("SELECT * FROM exercise_log WHERE workoutId = :workoutId ORDER BY orderIndex")
    suspend fun getExercisesForWorkout(workoutId: Long): List<ExerciseLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseLog>)

    @Update
    suspend fun updateExercise(exercise: ExerciseLog)

    // Sets
    @Query("SELECT * FROM exercise_set_log WHERE exerciseId = :exerciseId ORDER BY setNumber")
    fun getSetsForExerciseFlow(exerciseId: String): Flow<List<ExerciseSetLog>>

    @Query("SELECT * FROM exercise_set_log WHERE exerciseId = :exerciseId ORDER BY setNumber")
    suspend fun getSetsForExercise(exerciseId: String): List<ExerciseSetLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<ExerciseSetLog>)

    @Update
    suspend fun updateSet(set: ExerciseSetLog)

    // Daily Armor Checklist Logs
    @Query("SELECT * FROM daily_armor_log ORDER BY date DESC")
    fun getAllDailyArmorLogs(): Flow<List<DailyArmorLog>>

    @Query("SELECT * FROM daily_armor_log WHERE date = :date LIMIT 1")
    suspend fun getDailyArmorLog(date: String): DailyArmorLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyArmorLog(log: DailyArmorLog)

    // Metrics
    @Query("SELECT * FROM metric_log ORDER BY date DESC")
    fun getAllMetricLogs(): Flow<List<MetricLog>>

    @Query("SELECT * FROM metric_log WHERE name = :name ORDER BY date ASC")
    fun getMetricHistoryFlow(name: String): Flow<List<MetricLog>>

    @Query("SELECT * FROM metric_log WHERE category = :category ORDER BY date DESC")
    fun getMetricsByCategory(category: String): Flow<List<MetricLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(log: MetricLog)

    @Delete
    suspend fun deleteMetric(log: MetricLog)

    // Helper to clear database for data import
    @Query("DELETE FROM workout_log")
    suspend fun clearWorkouts()
    @Query("DELETE FROM daily_armor_log")
    suspend fun clearDailyArmor()
    @Query("DELETE FROM metric_log")
    suspend fun clearMetrics()
}

@Database(
    entities = [WorkoutLog::class, ExerciseLog::class, ExerciseSetLog::class, DailyArmorLog::class, MetricLog::class],
    version = 1,
    exportSchema = false
)
abstract class AthleteDatabase : RoomDatabase() {
    abstract fun athleteDao(): AthleteDao
}
