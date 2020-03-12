import com.yt8492.grpcsample.protobuf.*
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    val channel = ManagedChannelBuilder.forAddress("localhost", 6565)
        .usePlaintext()
        .build()
    val client = MessageServiceGrpc.newStub(channel)
    runBlocking {
        println("--- Unary Call start ---")
        val request = MessageRequest.newBuilder()
            .setMessage("hoge")
            .build()
        val response = client.unary(request)
        println(response.message)
        println("--- Unary Call finish ---")
    }
    runBlocking {
        println("--- Client Stream start ---")
        val call = client.clientStream()
        listOf("hoge", "fuga", "piyo").forEach {
            val request = MessageRequest.newBuilder()
                .setMessage(it)
                .build()
            call.send(request)
        }
        call.close()
        val response = call.await()
        println(response.message)
        println("--- Client Stream finish ---")
    }
    runBlocking {
        println("--- Server Stream start ---")
        val request = MessageRequest.newBuilder()
            .setMessage("hoge")
            .build()
        val call = client.serverStream(request)
        call.consumeEach {
            println(it.message)
        }
        println("--- Server Stream finish ---")
    }
    runBlocking {
        println("--- Bidirectional Stream start ---")
        val call = client.bidirectionalStream()
        listOf("hoge", "fuga", "piyo").forEach {
            val request = MessageRequest.newBuilder()
                .setMessage(it)
                .build()
            call.send(request)
        }
        call.close()
        call.consumeEach {
            println(it.message)
        }
        println("--- Bidirectional Stream finish ---")
    }
}