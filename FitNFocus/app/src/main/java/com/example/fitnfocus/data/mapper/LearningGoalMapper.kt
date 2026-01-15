package com.example.fitnfocus.data.mapper

import android.os.Build
import com.example.fitnfocus.data.local.LearningGoalEntity
import com.example.fitnfocus.domain.LearningGoal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mapper für LearningGoal ↔ LearningGoalEntity.
 * Zentralisiert die Konvertierung zwischen Domain und Data-Layer.
 */
object LearningGoalMapper {

    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    /**
     * Konvertiert ein LearningGoalEntity zu einem LearningGoal Domain-Modell.
     */
    fun entityToDomain(entity: LearningGoalEntity): LearningGoal {
        val topicsList: List<String> = if (entity.topics.isNotBlank()) {
            entity.topics.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }

        val examDate: LocalDate? = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            entity.examDate != null &&
            entity.examDate.isNotBlank()
        ) {
            try {
                LocalDate.parse(entity.examDate, dateFormatter)
            } catch (e: Exception) {
                null
            }
        } else {
            null
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
    fun domainToEntity(goal: LearningGoal): LearningGoalEntity {
        val examDateString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            goal.examDate?.format(dateFormatter)
        } else {
            null
        }

        return LearningGoalEntity(
            id = goal.id,
            moduleName = goal.moduleName,
            topics = goal.topics.joinToString(","),
            examDate = examDateString,
            isCompleted = goal.isCompleted
        )
    }

    /**
     * Extension-Funktion für bequeme Konvertierung.
     */
    fun LearningGoalEntity.toDomain(): LearningGoal = entityToDomain(this)

    /**
     * Extension-Funktion für bequeme Konvertierung.
     */
    fun LearningGoal.toEntity(): LearningGoalEntity = domainToEntity(this)
}

/**
 * Extension-Funktionen für Listen.
 */
fun List<LearningGoalEntity>.toDomainList(): List<LearningGoal> =
    map { LearningGoalMapper.entityToDomain(it) }

fun List<LearningGoal>.toEntityList(): List<LearningGoalEntity> =
    map { LearningGoalMapper.domainToEntity(it) }

