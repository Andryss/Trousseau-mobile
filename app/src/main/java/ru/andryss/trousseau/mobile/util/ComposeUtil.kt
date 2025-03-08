package ru.andryss.trousseau.mobile.util

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> SnapshotStateList<T>.replaceAllFrom(items: Collection<T>) {
    clear()
    addAll(items)
}