package com.example.flowsinjetpackcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch


/**.
 * collect will execute for every emission but if in .collectLatest takes too long and next emission
happened then previous one will be cancelled

Cold Flow emits data only when there is a collector, doesn't store data, and each collector receives all the values from the beginning.
Hot Flow emits data continuously, can store data, and each collector receives data from where they started collecting.


shared flow: for events e.g. showing a snackbar.
state flow: for keeping a state value, e.g. updating the value of a text composable

 **/

class MainViewModel : ViewModel() {
    // Cold Flow - Events will be emitted only in the listener's presence.
    val countDownFlow = flow<Int> {
        var startValue = 5
        emit(startValue)
        var currentvalue = startValue
        while (currentvalue > 0) {
            delay(1000L)
            currentvalue--
            emit(currentvalue)
        }
    }

    init {
        //collectFlow()
        //  collectFlowWithCount()
        //  collectFlowWithReduce()
        // collectFlowWithFold()
        collectFlowWithoutBuffer()
    }

    private fun collectFlow() {
        viewModelScope.launch {
            countDownFlow.filter { time -> time % 2 == 0 }.map { time ->
                time * 2
            }.onEach {
                println(it)
            }.collect { time ->
                println("Time is $time")
            }

        }
    }

    /**
     * Terminal flow operator - .count , .reduce, .fold
     **/
    private fun collectFlowWithCount() {
        var count = viewModelScope.launch {
            countDownFlow.filter { time -> time % 2 == 0 }.map { time ->
                time * 2
            }.onEach {
                println(it)
            }.count { time ->
                time % 2 == 0
            }
        }
        println("The count is $count")
    }

    private fun collectFlowWithReduce() {
        viewModelScope.launch {
            var value = countDownFlow.reduce { accumulator, value ->
                accumulator + value

            }
            // first 2 emiison value is 5 ,(5-1) 4 so accumator will be 9 .accululator is the sum of the values
//            5+4 =9
//            9+3
//            12+2
//            14+1 here o/p will be 15

            println("The count is $value")
        }
    }

    private fun collectFlowWithFold() {
        viewModelScope.launch {
            var value = countDownFlow.fold(10) { accumulator, value ->
                accumulator + value

            }
//            first 2 emiison value is 5, (5-1) 4 so accumator will be 9 .accululator is the sum of the values
//            5 + 4 = 9
//            9 + 3
//            12 + 2
//            14 + 1 here o / p will be 15
//            add initially 10

            println("The count is $value")
        }
    }

    /**
     * to work flow and collect in different corountines .buffer is used. Another strategy .conflate which will collect only latest emissions
    delay() function is used to suspend the corountines for that much time
     **/
    private fun collectFlowWithoutBuffer() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }.conflate().
                //.buffer()
            collectLatest {
                println("FLOW: Now eating $it")
                delay(1500L)
                println("FLOW: Finished eating $it")
            }
        }
    }
    //Stateflow is the livedata without lifecycle awareness.
//Hot flow : Events will be emitted irrespective of listener's presence.
//Cold flow : Events will be emitted only in the listener's presence.
//So stateflow cannot detect when activity goes in the background,otherwise same like livedata.Stateflow is hot flow,irrrespective of the listner it emits

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()
    fun incrementCounter() {
        _stateFlow.value += 1
    }


    //SharedFlow  work with multiple collectors while stateflow works with single collector.Shared flow
//    SharedFlow has the ability to keep values after they have been emitted, depending on how it's configured.
//
//    SharedFlow provides options for controlling how emitted values are stored and whether they are buffered for
//        future collectors. By default, SharedFlow does keep emitted values until they are collected by active
//    collectors. This means that if values are emitted by a SharedFlow and there are no active collectors at that
//    moment,the values will be buffered internally and stored until a collector subscribes to the SharedFlow
//    we cann't send theses events incase these numbers to the shared flow as it is one time event channel but if there
//    is no collectors it will lose
    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun incrementCounterInSHaredFlow() {
        viewModelScope.launch(Dispatchers.Main) {
            for (i in 0..10) {
                _sharedFlow.emit(i)


            }

            _stateFlow.value += 1
        }
    }
}
