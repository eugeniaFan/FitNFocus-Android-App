package com.example.fitnfocus.data.mapper

import com.example.fitnfocus.data.local.LearningGoalEntity
import com.example.fitnfocus.domain.LearningGoal
import java.time.LocalDate

/**
 * Mapper between LearningGoal domain model and LearningGoalEntity.
 * Handles conversion of LocalDate to epochDay for database storage.
 */
object LearningGoalMapper {

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