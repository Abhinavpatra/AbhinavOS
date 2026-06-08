package com.example.athleteos.data

import android.content.Context
import com.example.athleteos.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

@Serializable
data class JsonProgram(
    val programName: String,
    val totalWeeks: Int,
    val days: List<JsonDay>,
    val weekSpecific: JsonWeekSpecific? = null
)

@Serializable
data class JsonWeekSpecific(
    val accelerations: JsonAccelerationVariation? = null,
    val conditioning: JsonConditioningVariation? = null
)

@Serializable
data class JsonAccelerationVariation(
    val setsByWeek: Map<String, Int> = emptyMap(),
    val notesByWeek: Map<String, String> = emptyMap()
)

@Serializable
data class JsonConditioningVariation(
    val intervalsByWeek: Map<String, Int> = emptyMap(),
    val hardSecByWeek: Map<String, Int> = emptyMap(),
    val easySecByWeek: Map<String, Int> = emptyMap()
)

@Serializable
data class JsonDay(
    val dayNumber: Int,
    val name: String,
    val durationMinutes: Int,
    val exercises: List<JsonExercise>
)

@Serializable
data class JsonExercise(
    val name: String,
    val session: String? = null,
    val sets: Int,
    val targetReps: Int? = null,
    val targetWeight: Double? = null,
    val restSeconds: Int,
    val notes: String? = null
)

interface DataRepository {
    suspend fun checkAndSeedDatabase()
    fun getAllWorkouts(): Flow<List<WorkoutLog>>
    fun getWorkoutsForWeek(week: Int): Flow<List<WorkoutLog>>
    fun getWorkoutByIdFlow(id: Long): Flow<WorkoutLog?>
    suspend fun getWorkoutById(id: Long): WorkoutLog?
    fun getWorkoutByWeekAndDayFlow(week: Int, day: Int): Flow<WorkoutLog?>
    suspend fun updateWorkout(workout: WorkoutLog)
    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseLog>>
    suspend fun updateExercise(exercise: ExerciseLog)
    fun getSetsForExercise(exerciseId: String): Flow<List<ExerciseSetLog>>
    suspend fun updateSet(set: ExerciseSetLog)
    suspend fun updateSets(sets: List<ExerciseSetLog>)
    fun getAllDailyArmorLogs(): Flow<List<DailyArmorLog>>
    suspend fun getDailyArmorLog(date: String): suspend () -> DailyArmorLog? // Wait, let's use standard signature
    suspend fun getDailyArmorLogDirect(date: String): DailyArmorLog?
    suspend fun saveDailyArmorLog(date: String, completedItems: List<String>)
    fun getAllMetricLogs(): Flow<List<MetricLog>>
    fun getMetricHistoryFlow(name: String): Flow<List<MetricLog>>
    suspend fun saveMetric(date: String, category: String, name: String, value: Double, unit: String)
    suspend fun deleteMetric(log: MetricLog)
    suspend fun clearAllData()
    suspend fun importBackup(workouts: List<WorkoutLog>, armorLogs: List<DailyArmorLog>, metricLogs: List<MetricLog>)
}

class DefaultDataRepository(
    private val context: Context,
    private val athleteDao: AthleteDao
) : DataRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open("program.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()
            inputStream.close()

            val program = json.decodeFromString<JsonProgram>(jsonString)
            val expectedWorkoutCount = program.totalWeeks * program.days.size
            val expectedExerciseCount = program.totalWeeks * program.days.sumOf { it.exercises.size }
            val expectedSetCount = program.totalWeeks * program.days.sumOf { day -> day.exercises.sumOf { it.sets } }
            val workoutCount = athleteDao.getWorkoutCount()
            val exerciseCount = athleteDao.getExerciseCount()
            val setCount = athleteDao.getSetCount()
            if (workoutCount == expectedWorkoutCount && exerciseCount >= expectedExerciseCount && setCount >= expectedSetCount) return@withContext

            athleteDao.clearWorkouts()

            val weekKey = { w: Int -> w.toString() }

            val allWeeks = (1..program.totalWeeks).toList()
            for (w in allWeeks) {
                for (d in program.days) {
                    val workoutLog = WorkoutLog(
                        weekNumber = w,
                        dayNumber = d.dayNumber,
                        name = d.name,
                        estimatedDurationMinutes = d.durationMinutes
                    )
                    val workoutId = athleteDao.insertWorkouts(listOf(workoutLog)).first()

                    val exercises = mutableListOf<ExerciseLog>()
                    for ((index, jsonExercise) in d.exercises.withIndex()) {
                        val exerciseId = "${workoutId}_${index}_${jsonExercise.name}"
                        exercises.add(
                            ExerciseLog(
                                id = exerciseId,
                                workoutId = workoutId,
                                name = jsonExercise.name,
                                orderIndex = index,
                                session = jsonExercise.session ?: "Session",
                                notes = buildExerciseNote(w, d.dayNumber, jsonExercise, program)
                            )
                        )

                        // Determine sets count from JSON or week-specific variations
                        val setAmt = when {
                            // Accelerations (day 1, exercise "Accelerations")
                            d.dayNumber == 2 && jsonExercise.name == "Accelerations" && program.weekSpecific?.accelerations != null ->
                                program.weekSpecific.accelerations.setsByWeek[weekKey(w)] ?: jsonExercise.sets
                            d.dayNumber == 4 && jsonExercise.name == "Interval Conditioning" && program.weekSpecific?.conditioning != null ->
                                program.weekSpecific.conditioning.intervalsByWeek[weekKey(w)] ?: jsonExercise.sets
                            else -> jsonExercise.sets
                        }

                        // Determine conditioning-specific rest seconds and target reps
                        val conditioning = program.weekSpecific?.conditioning
                        val restSec = if (d.dayNumber == 4 && jsonExercise.name == "Interval Conditioning" && conditioning != null) {
                            conditioning.easySecByWeek[weekKey(w)] ?: jsonExercise.restSeconds
                        } else {
                            jsonExercise.restSeconds
                        }
                        val targetRep = if (d.dayNumber == 4 && jsonExercise.name == "Interval Conditioning") 1 else jsonExercise.targetReps

                        val sets = mutableListOf<ExerciseSetLog>()
                        for (s in (1..setAmt)) {
                            sets.add(
                                ExerciseSetLog(
                                    exerciseId = exerciseId,
                                    setNumber = s,
                                    targetWeight = jsonExercise.targetWeight,
                                    targetReps = targetRep,
                                    targetRestSeconds = restSec
                                )
                            )
                        }
                        athleteDao.insertExercises(exercises.takeLast(1))
                        athleteDao.insertSets(sets)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildExerciseNote(
        week: Int,
        dayNumber: Int,
        exercise: JsonExercise,
        program: JsonProgram
    ): String? {
        val weekKey = week.toString()
        return when {
            dayNumber == 2 && exercise.name == "Accelerations" && program.weekSpecific?.accelerations != null ->
                program.weekSpecific.accelerations.notesByWeek[weekKey] ?: exercise.notes
            dayNumber == 4 && exercise.name == "Interval Conditioning" && program.weekSpecific?.conditioning != null -> {
                val conditioning = program.weekSpecific.conditioning
                val intervals = conditioning.intervalsByWeek[weekKey] ?: exercise.sets
                val hard = conditioning.hardSecByWeek[weekKey] ?: 30
                val easy = conditioning.easySecByWeek[weekKey] ?: 90
                "Week $week: $intervals intervals, ${hard}s hard / ${easy}s easy. Modalities: running, treadmill, or bike."
            }
            else -> exercise.notes
        }
    }

    override fun getAllWorkouts(): Flow<List<WorkoutLog>> = athleteDao.getAllWorkouts()

    override fun getWorkoutsForWeek(week: Int): Flow<List<WorkoutLog>> = athleteDao.getWorkoutsForWeek(week)

    override fun getWorkoutByIdFlow(id: Long): Flow<WorkoutLog?> = athleteDao.getWorkoutByIdFlow(id)

    override fun getWorkoutByWeekAndDayFlow(week: Int, day: Int): Flow<WorkoutLog?> =
        athleteDao.getWorkoutByWeekAndDayFlow(week, day)

    override suspend fun getWorkoutById(id: Long): WorkoutLog? = athleteDao.getWorkoutById(id)

    override suspend fun updateWorkout(workout: WorkoutLog) {
        athleteDao.updateWorkout(workout)
    }

    override fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseLog>> =
        athleteDao.getExercisesForWorkoutFlow(workoutId)

    override suspend fun updateExercise(exercise: ExerciseLog) {
        athleteDao.updateExercise(exercise)
    }

    override fun getSetsForExercise(exerciseId: String): Flow<List<ExerciseSetLog>> =
        athleteDao.getSetsForExerciseFlow(exerciseId)

    override suspend fun updateSet(set: ExerciseSetLog) {
        athleteDao.updateSet(set)
        updateWorkoutCompletionState(set.exerciseId)
    }

    override suspend fun updateSets(sets: List<ExerciseSetLog>) {
        if (sets.isEmpty()) return
        sets.forEach { athleteDao.updateSet(it) }
        updateWorkoutCompletionState(sets.first().exerciseId)
    }

    private suspend fun updateWorkoutCompletionState(exerciseId: String) {
        val parts = exerciseId.split("_")
        val workoutId = parts.firstOrNull()?.toLongOrNull() ?: return

        val exercises = athleteDao.getExercisesForWorkout(workoutId)
        if (exercises.isEmpty()) return

        var totalSets = 0
        var completedSets = 0

        val updatedExercises = mutableListOf<ExerciseLog>()
        for (exercise in exercises) {
            val sets = athleteDao.getSetsForExercise(exercise.id)
            val exerciseCompleted = sets.isNotEmpty() && sets.all { it.isCompleted }
            if (exercise.isCompleted != exerciseCompleted) {
                val updatedEx = exercise.copy(isCompleted = exerciseCompleted)
                athleteDao.updateExercise(updatedEx)
                updatedExercises.add(updatedEx)
            } else {
                updatedExercises.add(exercise)
            }
            totalSets += sets.size
            completedSets += sets.count { it.isCompleted }
        }

        val workout = athleteDao.getWorkoutById(workoutId) ?: return
        val pct = if (totalSets > 0) (completedSets * 100) / totalSets else 0
        val isCompleted = pct == 100

        athleteDao.updateWorkout(
            workout.copy(
                isCompleted = isCompleted,
                completionPercentage = pct,
                dateCompleted = if (isCompleted) System.currentTimeMillis() else null
            )
        )
    }

    override fun getAllDailyArmorLogs(): Flow<List<DailyArmorLog>> = athleteDao.getAllDailyArmorLogs()

    override suspend fun getDailyArmorLogDirect(date: String): DailyArmorLog? = athleteDao.getDailyArmorLog(date)

    override suspend fun getDailyArmorLog(date: String): suspend () -> DailyArmorLog? {
        return { athleteDao.getDailyArmorLog(date) }
    }

    override suspend fun saveDailyArmorLog(date: String, completedItems: List<String>) {
        val jsonStr = json.encodeToString(completedItems)
        athleteDao.insertDailyArmorLog(DailyArmorLog(date, jsonStr))
    }

    override fun getAllMetricLogs(): Flow<List<MetricLog>> = athleteDao.getAllMetricLogs()

    override fun getMetricHistoryFlow(name: String): Flow<List<MetricLog>> = athleteDao.getMetricHistoryFlow(name)

    override suspend fun saveMetric(date: String, category: String, name: String, value: Double, unit: String) {
        athleteDao.insertMetric(MetricLog(date = date, category = category, name = name, value = value, unit = unit))
    }

    override suspend fun deleteMetric(log: MetricLog) {
        athleteDao.deleteMetric(log)
    }

    override suspend fun clearAllData() {
        athleteDao.clearWorkouts()
        athleteDao.clearDailyArmor()
        athleteDao.clearMetrics()
    }

    override suspend fun importBackup(workouts: List<WorkoutLog>, armorLogs: List<DailyArmorLog>, metricLogs: List<MetricLog>) {
        athleteDao.clearWorkouts()
        athleteDao.clearDailyArmor()
        athleteDao.clearMetrics()

        athleteDao.insertWorkouts(workouts)
        armorLogs.forEach { athleteDao.insertDailyArmorLog(it) }
        metricLogs.forEach { athleteDao.insertMetric(it) }
    }
}
