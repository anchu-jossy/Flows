package com.example.flowsinjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.flowsinjetpackcompose.ui.theme.FlowsInJetpackComposeTheme

//Flow it is a koitlin program,,I’m fetaure that serve as a reactive programming framework
//Its all about notififying the changes in your code and sending them through a pipelein that potentially modifies them
//
//A flow is a couroutine that emit multiple values  over a perios of time
//https://www.youtube.com/watch?v=sk3svS_fzZM


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowsInJetpackComposeTheme {
                var viewModel=viewModel<MainViewModel>()
                val time=viewModel.countDownFlow.collectAsState(initial =10 )
              val stateflowCount=  viewModel.stateFlow.collectAsState(10)
                // A surface container using the 'background' color from the theme
                Box(modifier = Modifier.fillMaxSize()){
                  Button(onClick = { viewModel.incrementCounter()
                  }) {
                      Text(text ="stateflowCount  ${stateflowCount.value}" )
                  }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlowsInJetpackComposeTheme {
        Greeting("Android")
    }
}