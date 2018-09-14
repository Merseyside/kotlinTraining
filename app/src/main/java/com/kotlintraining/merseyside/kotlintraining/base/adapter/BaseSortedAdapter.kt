package com.kotlintraining.merseyside.kotlintraining.base.adapter

import android.os.Handler
import android.os.Looper
import android.support.v7.util.SortedList
import android.text.TextUtils
import com.kotlintraining.merseyside.kotlintraining.base.model.BaseSortedAdapterViewModel

import java.lang.reflect.ParameterizedType
import java.util.ArrayList
import java.util.Comparator
import java.util.ConcurrentModificationException

abstract class BaseSortedAdapter<M, T : BaseSortedAdapterViewModel> protected constructor(private val threadExecutor: ThreadExecutor) : BaseAdapter<T>() {

    private val TAG = "BaseSortedAdapter"

    private val persistentClass: Class<T>

    private var addThread: Thread? = null
    private var updateThread: Thread? = null

    private val fullList: MutableList<T>
    protected val list: SortedList<T>

    private val comparator = { o1, o2 -> o1.compareTo(o2) }

    private val lock = Any()

    private var isFiltered = false
    private var isOnlyUpdateWithoutAdd = true

    init {

        this.persistentClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<T>
        fullList = ArrayList()

        list = SortedList(persistentClass, object : SortedList.Callback<T>() {
            override fun onInserted(position: Int, count: Int) {
                /* This bug google don't wanna fix for a long long time!*/
                Handler(Looper.getMainLooper()).post { notifyItemRangeInserted(position, count) }
            }

            override fun onRemoved(position: Int, count: Int) {
                Handler(Looper.getMainLooper()).post { notifyItemRangeRemoved(position, count) }

            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                Handler(Looper.getMainLooper()).post { notifyItemMoved(fromPosition, toPosition) }
            }

            override fun compare(o1: T, o2: T): Int {
                return comparator.compare(o1, o2)

            }

            override fun onChanged(position: Int, count: Int) {
                Handler(Looper.getMainLooper()).post { notifyItemRangeChanged(position, count) }

            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.equals(newItem)
            }

            override fun areItemsTheSame(item1: T, item2: T): Boolean {
                return item1 === item2
            }
        })
    }

    fun add(obj: M) {
        val listItem = createItemViewModel(obj)
        fullList.add(listItem)
    }

    fun add(list: List<M>) {
        addThread = Thread {
            synchronized(lock) {
                for (obj in list) {
                    add(obj)
                }
                Handler(Looper.getMainLooper()).post {
                    this.list.beginBatchedUpdates()
                    this.list.addAll(fullList)
                    this.list.endBatchedUpdates()
                }

            }
        }
        addThread!!.start()

    }

    private fun update(obj: M): Boolean {
        var isFound = false
        for (i in 0 until list.size()) {

            val model = list.get(i)
            if (!updateThread!!.isInterrupted) {
                if (model.isItemsTheSame(obj)) {
                    if (!list.get(i).isContentTheSame(obj)) {
                        Handler(Looper.getMainLooper()).post { notifyItemChanged(i, obj) }
                    }
                    isFound = true
                    break
                }
            } else {
                break
            }
        }
        return isFound
    }

    fun update(list: List<M>) {
        updateThread = Thread {
            synchronized(lock) {
                if (!isFiltered) {
                    val removeList = ArrayList<T>()
                    for (i in 0 until this.list.size()) {
                        var isFound = false
                        val model = this.list.get(i)
                        for (obj in list) {
                            if (model.isItemsTheSame(obj)) {
                                isFound = true
                                break
                            }
                        }
                        if (!isFound)
                            removeList.add(model)
                    }
                    for (removeItem in removeList) {
                        remove(removeItem)
                    }
                }

                val addList = ArrayList<M>()
                for (obj in list) {
                    if (!updateThread!!.isInterrupted)
                        if (!isFiltered && !update(obj)) {
                            addList.add(obj)
                        }
                }

                if (!isOnlyUpdateWithoutAdd)
                    add(addList)
            }
        }
        updateThread!!.start()
    }

    private fun replaceAll(models: List<T>) {
        synchronized(lock) {
            list.beginBatchedUpdates()
            for (i in list.size() - 1 downTo 0) {
                val model = list.get(i)
                if (!models.contains(model)) {
                    list.remove(model)
                }
            }

            list.addAll(models)
            list.endBatchedUpdates()
        }
    }

    fun setFilter(query: String) {
        threadExecutor.execute({
            try {
                if (!TextUtils.isEmpty(query)) {
                    isFiltered = true
                    val filteredList = ArrayList<T>()
                    for (obj in fullList) {
                        if (filter(obj, query))
                            filteredList.add(obj)
                    }
                    replaceAll(filteredList)
                } else {
                    isFiltered = false
                    replaceAll(fullList)
                }

            } catch (ignored: ConcurrentModificationException) {
            }
        })

    }

    protected abstract fun filter(obj: T, query: String): Boolean

    protected abstract fun createItemViewModel(obj: M): T

    override fun getItemCount(): Int {
        return list.size()
    }

    fun removeAll() {
        interruptThread(addThread)
        interruptThread(updateThread)
        synchronized(lock) {
            list.beginBatchedUpdates()
            list.clear()
            list.endBatchedUpdates()
            fullList.clear()
        }
    }

    private fun remove(obj: T) {
        list.remove(obj)
        fullList.remove(obj)
    }

    private fun interruptThread(thread: Thread?) {
        if (thread != null && !thread.isInterrupted)
            thread.interrupt()
    }

    fun hasItems(): Boolean {
        return fullList.size != 0
    }

    protected fun setOnlyUpdateWithoutAdd(isOnlyUpdate: Boolean) {
        this.isOnlyUpdateWithoutAdd = isOnlyUpdate
    }
}
