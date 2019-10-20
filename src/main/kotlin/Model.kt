import kotlinx.serialization.Serializable

@Serializable
data class Model(var name: String,
            var vars:MutableList<Variable>)


//class User(var name: String = "dsds") {
//
//}
//
//
//enum class Method {
//    post,get,put,delete
//}

//interface APIRequest {
//    val method: Method
//    val urlPath: String
//}
//
//class RequestSample(var name: String = "dsds"): APIRequest {
//    override val method: Method = Method.post
//    override var urlPath: String = "dad${name}dada"
//}
