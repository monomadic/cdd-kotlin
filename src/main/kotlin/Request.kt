import kotlinx.serialization.*
import kotlinx.serialization.json.*
@Serializable
class Request(var name: String,
              var vars: MutableList<Variable>,
              var method: String,
              var path: String,
              var response_type:String,
              var error_type:String)


