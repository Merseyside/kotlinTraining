package com.kotlintraining.merseyside.kotlintraining.base.model

abstract class BaseSortedAdapterViewModel : BaseAdapterViewModel(), Comparable<Any> {
    abstract var item: Any

    abstract fun isContentTheSame(obj: Any): Boolean
    abstract fun isItemsTheSame(obj: Any): Boolean

}
