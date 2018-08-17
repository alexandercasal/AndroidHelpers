/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexandercasal.androidhelpers.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Force LiveData to only emmit distinct object updates from its source. This avoids false
 * positives. An example might be when observing a row in a Room database table and the table
 * is updated elsewhere but the observed row didn't change. Observed objects must properly
 * implement [equals].
 *
 * https://gist.github.com/florina-muntenescu/fea9431d0151ce0afd2f5a0b8834a6c7
 */
fun <T> LiveData<T>.getDistinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T> {
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if (!initialized) {
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else if (obj != lastObj) {
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            }
        }
    })
    return distinctLiveData
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (t: T) -> Unit) {
    observe(owner, Observer {
        observer(it)
    })
}

inline fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, crossinline observer: (t: T) -> Unit) {
    observe(owner) {
        it?.let {
            observer(it)
        }
    }
}

fun <T> MutableLiveData<T>.updateIfChanged(newValue: T) {
    if (value != newValue) {
        value = newValue
    }
}

fun <T> MutableLiveData<T>.postIfChanged(newValue: T) {
    if (value != newValue) {
        postValue(newValue)
    }
}