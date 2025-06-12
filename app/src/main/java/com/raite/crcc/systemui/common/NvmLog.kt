package com.raite.crcc.systemui.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nvm_log")
data class NvmLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long,
    val type: Int, //0 normal; 1 alarm; 2 fault;
    val action: String,
    val domain: String?,
    val reason: String?,
)
