package com.example.fitnfocus.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.LearningGoalEntity
import com.example.fitnfocus.domain.LearningGoal
import java.time.LocalDate

/**
 * Mapper für LearningGoal ↔ LearningGoalEntity.
 * Zentralisiert die Konvertierung zwischen Domain und Data-Layer.
 */
object LearningGoalMapper {

    /**
     * Konvertiert ein LearningGoalEntity zu einem LearningGoal Domain-Modell.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun entityToDomain(entity: LearningGoalEntity): LearningGoal {
        val topicsList: List<String> = if (entity.topics.isNotBlank()) {
            entity.topics.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }

        val examDate: LocalDate? = entity.examEpochDay?.let {
            LocalDate.ofEpochDay(it)
        }

        return LearningGoal(
            id = entity.id,
            moduleName = entity.moduleName,
            topics = topicsList,
            examDate = examDate,
            isCompleted = entity.isCompleted
        )
    }

    /**
     * Konvertiert ein LearningGoal Domain-Modell zu einem LearningGoalEntity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun domainToEntity(goal: LearningGoal): LearningGoalEntity {
        return LearningGoalEntity(
            id = goal.id,
            moduleName = goal.moduleName,
            topics = goal.topics.joinToString(","),
            examEpochDay = goal.examDate?.toEpochDay(),
            isCompleted = goal.isCompleted
        )
    }
}